package com.blueprint.blueprint.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.util.BPGifLoaderManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creado por jorgeacostaalvarado on 12-03-15.
 */
public class BPDatabaseManager extends SQLiteAssetHelper {

    private static BPDatabaseManager instance = null;
    private static Context context = null;
    private static String database_name = null;
    private static int database_version = 0;
    private static SQLiteDatabase db = null;
    private static String[] internalString = {"id","view","parent"};

    public BPDatabaseManager(Context context, String database_name, int database_version) {
        super(context, database_name, null, database_version);
    }

    public static BPDatabaseManager getInstance() {

        if(instance == null) {
            instance = new BPDatabaseManager(BPDatabaseManager.context,BPDatabaseManager.database_name,BPDatabaseManager.database_version);
        }

        return instance;
    }

    public static Context getContext(){
        return context;
    }

    //inicializamos la clase manager, generalmente desde la clase Application
    public static void init(Context context,String database_name, int database_version){
        BPDatabaseManager.context = context;
        BPDatabaseManager.database_name = database_name;
        BPDatabaseManager.database_version = database_version;
        getInstance().openDatabase();
        BPGifLoaderManager.getInstance();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
    }

    // abre la base de datos
    private void openDatabase(){
        getInstance().db = getReadableDatabase();
    }

    // actualizar los datos
    public void updateTable(String tableName, HashMap<String,Object> raw, String where){

        ContentValues update = new ContentValues();

        Iterator<Map.Entry<String, Object>> it = raw.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Object> pairs = it.next();
            Object value = pairs.getValue();


            if(!pairs.getKey().equalsIgnoreCase("table")  &&!pairs.getKey().equalsIgnoreCase("parent")  && !pairs.getKey().equalsIgnoreCase("view")  && !pairs.getKey().equalsIgnoreCase("id") && !pairs.getKey().equalsIgnoreCase("bpIndex")) {
                if (value instanceof Integer) {

                    update.put(pairs.getKey(), (int) pairs.getValue());
                } else if (value instanceof Boolean) {


                    update.put(pairs.getKey(), (boolean) pairs.getValue());
                } else {
                    update.put(pairs.getKey(), String.valueOf(pairs.getValue()));
                }
            }
        }


        getInstance().db.update(tableName, update, where, null);


    }

    // Obtiene un Array desde la tabla
    public ArrayList<BPObject> getFromTable(String tableName, ArrayList<String> properties, ArrayList<String> wheres, String group_by, String sort_by, String limit) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        properties.add(0, "*");
        String[] select = new String[ properties.size() ];
        select = properties.toArray(select);

        qb.setTables(tableName);



        if(wheres!=null) {
            if(wheres.size()>0) {
                String where = "";
                for (int i = 0; i < wheres.size(); i++) {
                    where = where + wheres.get(i);
                    Log.d("dynamodb", "WHERE: " + wheres.get(i));
                }

                qb.appendWhere(where);
            }


        }


        Cursor c = qb.query(BPDatabaseManager.db, select, null, null, group_by, null, sort_by,limit);


        if(c!=null) {
            return cursorToArray(c,tableName);
        }

        return null;
    }


    // remueve registro en la tabla
    public void remove(String tableName, String where){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(tableName, where, null);

    }


    //agrega un registro en la tabla
    public Long addToTable(String tableName, HashMap<String,Object> data, String[] modelMap){

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);
        ContentValues register = new ContentValues();

        if(modelMap != null){

            for(int i = 0 ; i < modelMap.length;i++){
                String key = modelMap[i];
                Object value = data.get(key);


                if(!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("view") && !key.equalsIgnoreCase("parent") ){
                    register.put(key,String.valueOf(value));
                }
            }

        }else{

            Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Object> pairs = it.next();
                String key =  pairs.getKey();
                Object value = pairs.getValue();


                if(!isInternalString(key)){
                    if(value instanceof Integer){
                        register.put(key, (int) value);
                    } else if(value instanceof Boolean){
                        register.put(key, (boolean) value);
                    } else if(value instanceof String){
                        register.put(key, (String) value);
                    }else if(value instanceof JSONObject){
                        register.put(key, value.toString());
                    }else if(value instanceof JSONArray){
                        register.put(key, value.toString());
                    }else if(value instanceof Double){
                        register.put(key, (double) value);
                    }else if(value instanceof Float){
                        register.put(key, (float) value);
                    }else if(value instanceof Long){
                        register.put(key, (long) value);
                    }else{
                        register.put(key, String.valueOf(value));
                    }

                }

            }
        }


        return getInstance().db.insert(tableName, null, register);
    }
    //devuelve true si el key esta dentro de los strings prohibidos
    private boolean isInternalString(String value){
        boolean isInternal = false;

        for(int i = 0 ; i < internalString.length;i++){
            if(internalString[i].equalsIgnoreCase(value)){
                return true;
            }
        }
        return isInternal;
    }
    // obtiene un ArrayList desde un cursor
    private ArrayList<BPObject> cursorToArray(Cursor cursor,String tableName){

        String[] names = cursor.getColumnNames();

        ArrayList<BPObject> list = new ArrayList<>();

        for(int i = 0; i < cursor.getCount();i++){
            cursor.moveToPosition(i);

            BPObject o = new BPObject();

            for(int j = 0 ; j < names.length;j++){

                int type = cursor.getType(j);

                switch(type){
                    case Cursor.FIELD_TYPE_NULL:

                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        o.setDouble(names[j], cursor.getFloat(j));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        o.setInt(names[j], cursor.getInt(j));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        o.setString(names[j], cursor.getString(j));
                        break;
                    default:
                        o.setString(names[j], cursor.getString(j));
                        break;
                }


            }

            list.add(o);

        }

        return list;

    }

}
