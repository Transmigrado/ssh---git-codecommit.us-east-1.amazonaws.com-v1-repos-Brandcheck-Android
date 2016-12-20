package com.blueprint.blueprint.builder;

import android.content.Context;
import android.util.Log;

import com.blueprint.blueprint.database.BPDatabaseManager;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.util.BPJsonUtil;
import com.blueprint.blueprint.util.BPStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creado Por jorgeacostaalvarado el 18-09-15.
 */
public class BPAppBuilder {


    private static BPAppBuilder instance = null;
    private static Context context = null;
    private static String configPath = null;
    private static String modelPath = null;

    private static HashMap<String, BPObject> views;
    private static HashMap<String, BPObject> model;
    private static HashMap<String, Integer> uiIds;

    private static BPObject dataModel;

    public BPAppBuilder(Context context) {

    }

    public static void init(Context context,String configPath, String modelPath){

        BPAppBuilder.context = context;
        BPAppBuilder.configPath = configPath;
        BPAppBuilder.modelPath = modelPath;

        BPAppBuilder.views = new HashMap<>();
        BPAppBuilder.model = new HashMap<>();
        BPAppBuilder.uiIds = new HashMap<>();

        getInstance().loadConfig();
        getInstance().loadModel();

    }

    public static BPObject getApiAction(String name){
        BPCollection models = getInstance().dataModel.getBPCollection("models");

        for(int i = 0 ; i < models.size(); i++){
            BPObject model = models.get(i);

            BPCollection actions = model.getBPCollection("actions");
            if(actions!=null) {
                for (int k = 0; k < actions.size(); k++) {
                    BPObject action = actions.get(k);
                    if (action.getString("name").equalsIgnoreCase(name)) {
                        return action;
                    }
                }
            }
        }

        return new BPObject();
    }

    public static void addUIID(String key, Integer id){
        getInstance().uiIds.put(key, id);
    }

    public static int getUIID(String key){
       if(getInstance().uiIds.get(key)==null){
           return 0;
       }
        return getInstance().uiIds.get(key);
    }

    public static void loadConfig(){

        String data = BPJsonUtil.loadJSONFromAsset(BPAppBuilder.context, BPAppBuilder.configPath);
        try {
            JSONObject config = new JSONObject(data);
            JSONArray views = config.getJSONArray("views");
            for(int i = 0 ; i < views.length();i++){
                BPObject o = BPJsonUtil.jsonToBPObject(views.getJSONObject(i));
                getInstance().views.put(o.getString("name"), o);
            }
        } catch (JSONException e){

        }

    }

    public static void loadStyle(String stylePath){
        String data = BPJsonUtil.loadJSONFromAsset(BPAppBuilder.context, stylePath);

        try{

            JSONObject json = new JSONObject(data);
            JSONObject style = json.getJSONObject("style");

            Iterator<?> keys = style.keys();

            BPStyle.init(BPAppBuilder.context);

            while( keys.hasNext() ) {
                String key = (String)keys.next();

                BPStyle.getInstance().addObject(key, BPJsonUtil.jsonToBPObject(style.getJSONObject(key)));
            }

        }catch (JSONException e){

        }
    }

    public static void loadModel(){
        String data = BPJsonUtil.loadJSONFromAsset(BPAppBuilder.context, BPAppBuilder.modelPath);

        try {
            JSONObject config = new JSONObject(data);
            getInstance().dataModel = BPJsonUtil.jsonToBPObject(config);

            BPDatabaseManager.init(getInstance().context,getInstance().dataModel.getBPObject("database").getString("name"), getInstance().dataModel.getBPObject("database").getInt("version"));

        } catch (JSONException e){
            Log.d("universe",e.toString());
        }
    }

    public static BPObject getDataModel(){
        return getInstance().dataModel;
    }

    public static BPObject getInitialView(){

        Iterator it =  getInstance().views.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            BPObject o = (BPObject) e.getValue();
            if(o.getBoolean("isInitial")){
                return o;
            }
        }

        return null;
    }

    public static BPObject getView(String view){
        BPObject o = getInstance().views.get(view);
        return o;
    }

    public static BPAppBuilder getInstance() {

        if(instance == null) {
            instance = new BPAppBuilder(BPAppBuilder.context);
        }

        return instance;
    }

    public static Context getContext(){
        return context;
    }
}
