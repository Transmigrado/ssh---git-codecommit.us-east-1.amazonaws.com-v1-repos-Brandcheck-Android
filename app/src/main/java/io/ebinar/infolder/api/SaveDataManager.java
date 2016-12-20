package io.ebinar.infolder.api;

import android.os.Handler;
import android.util.Log;

import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import io.ebinar.infolder.event.MediaEvent;

/**
 * Creado Por jorgeacostaalvarado el 16-11-16.
 */
public class SaveDataManager {

    private BPCollection collection;
    private int filterId = 0;
    public int i = 0 ;
    Callback callback;

    public int filter = 0;
    private String tag;
    BPCollection current;

    public SaveDataManager(String tag,BPCollection collection, int filterId, Callback callback){
        this.collection = collection;
        this.filterId = filterId;
        this.callback = callback;
        this.tag = tag;
    }

    public void saveObject(){

        if(collection.size()==0){
            callback.done();
            return;
        }

        current = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Media");
        query.where().equalTo("tag",tag);
        query.setLimit(1);
        query.setOrderBy("updatedAt DESC");

        current.dataFromSQLQuery(query);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                BPObject media = collection.get(i);
                media.className = "Media";
                media.setInt("filterId", filterId);
                media.setString("tag", tag);



                if(current.size() > 0){

                    Date d1 = getDateFromString(media.getString("createdAt"));
                    Date d2 =  getDateFromString(current.get(0).getString("createdAt"));

                    if(d2.getTime() > d1.getTime()){
                        Log.d("brandcheck","--> SAVE");
                        Log.d("brandcheck", ">> " + media.toString());
                        media.save();
                    }

                } else {
                    Log.d("brandcheck","--> SAVE");
                    media.save();
                }

                i++;

                if(i>0 && i % 100 == 0){
                   callback.done();
                }


                if(i < collection.size()){


                    saveObject();



                }else{
                   callback.done();
                }

            }
        }, 100);

    }

    private Date getDateFromString(String d){

        Date date = null;
        try {
            String dd = d.split(".000")[0];

            String[] da = dd.split("T");
            String[] dates = da[0].split("-");
            String[] hours = da[1].split(":");

            date = new Date();
            date.setDate(Integer.parseInt(dates[2]));
            date.setMonth(Integer.parseInt(dates[1]) - 1);
            date.setYear(Integer.parseInt(dates[0]) - 1900);
            date.setSeconds(Integer.parseInt(hours[2]));
            date.setMinutes(Integer.parseInt(hours[1]));
            date.setHours(Integer.parseInt(hours[0]));
        }catch (Exception e){
            return new Date();
        }

        return date;
    }

    public interface Callback {
        void done();
    }

}
