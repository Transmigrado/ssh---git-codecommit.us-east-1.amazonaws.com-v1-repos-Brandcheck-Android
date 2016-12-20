package com.blueprint.blueprint.object;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.blueprint.blueprint.database.BPDatabaseManager;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Creado por jorgeacostaalvarado on 09-04-15.
 */
public class BPCollection extends ArrayList<BPObject> {

    private BPSqlQuery query;
    private Callback callback;
    public String className;
    public String sortField = null;


    public BPCollection() {
        super();
    }

    public void dataFromSQLQuery(BPSqlQuery query){

        Log.d("brandcheck",">> DATA fROM");

        this.className = query.getTableName();

        this.query = query;
        if(this.query==null){
            return;
        }

        String limit = "";

        if(query.getLimit() > -1){
            if(query.getSkip()>-1){
                limit = Integer.toString(query.getSkip())+","+Integer.toString(query.getLimit());
            }else{
                limit = Integer.toString(query.getLimit());
            }
        }else{
            limit = "1000";
        }


        ArrayList<BPObject> temp = BPDatabaseManager.getInstance().getFromTable(query.getTableName(), new ArrayList<String>(), query.where().getRawString(), query.getGroupBy(), query.getOrderBy(), limit);
        for(int i = 0 ; i < temp.size(); i++){
            BPObject o = temp.get(i);
            o.className =   this.className;
            add(o);
        }

        if(sortField!=null){
            sortBy(sortField);
        }
    }

    public void save(){
        SaveInBackgroundTask task = new SaveInBackgroundTask(BPDatabaseManager.getContext());
        task.execute();
    }

    public void setClassName(String className) {
        this.className = className;
        for (int i = 0 ; i < size(); i++){
            get(i).className = this.className;
        }
    }

    public void appendFromLastObject(String field) {

        BPObject lastObject = this.get(this.size()-1);


        if(lastObject!=null){

            this.resetQuery(null);
            this.query.where().lessThan(field,lastObject.getLong(field));
        }

        dataFromSQLQuery(this.query);
        //this.sortBy(field);
    }

    public void resetQuery(BPSqlQuery query) {

        if(query!=null){
            this.query = query;
            return;
        }

        this.query = new BPSqlQuery(this.query.getTableName());

    }



    public class SaveInBackgroundTask extends AsyncTask<Void, Void, String> {
        private final Context mContext;

        public SaveInBackgroundTask(Context context) {
            super();
            this.mContext = context;
        }

        protected String doInBackground(Void... params) {
            for(int i = 0 ; i < size(); i++){
                BPObject o = BPCollection.this.get(i);
                BPCollection.this.get(i).save();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(String value) {
            BPCollection.this.getCallback().saveComplete();
        }
    }

    @Override
    public String toString(){
        String result = "[";
        for (int i = 0 ; i < size();i++){
            result += "{";
            result += get(i).toString()+"";
            if(i < size() - 1) {
                result += "},";
            }else{
                result += "}";
            }
        }
        result += "]";
        return result;
    }

    public void refresh() {
        while(this.size()>0){
            remove(0);
        }
        dataFromSQLQuery(this.query);
    }

    public void refreshQuery(BPSqlQuery query){

        this.query = query;

        while(this.size()>0){
            remove(0);
        }

        dataFromSQLQuery(this.query);
    }


    public void refreshAndApendQuery(){
        dataFromSQLQuery(this.query);
    }


    public void sortBy(final String field){

        this.sortField = field;
        Collections.sort(this,
                new Comparator<BPObject>() {
                    @Override
                    public int compare(BPObject o2, BPObject o1)
                    {
                        if(o2.getLong(field) < o1.getLong(field))
                        {
                            return 1;
                        }else if(o2.getLong(field) > o1.getLong(field)){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });



    }


    public Callback getCallback(){
        return this.callback;
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public interface Callback{
        void saveComplete();
    }
}
