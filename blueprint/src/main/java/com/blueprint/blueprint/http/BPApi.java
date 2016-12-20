package com.blueprint.blueprint.http;


import android.os.Debug;
import android.util.Log;

import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.util.BPJsonUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Creado Por jorgeacostaalvarado el 25-09-15.
 */
public class BPApi {

    private BPApiCallback callback;
    private BPHttp bpHttp;

    public BPApi(){
        bpHttp = new BPHttp();
    }

    public void addHeader(String key, String value){
        bpHttp.addHeader(key, value);
    }

    public void requestFromUrl(String url, BPHttpType type){

        bpHttp.requestURL(url, type);
        bpHttp.addCallback(new BPHttp.BeeRequestCallback() {
            @Override
            public void done(BPObject data) {

        Log.d("infolder",">> " + data.toString());

                if(callback!=null){
                    if(data.getString("data").equalsIgnoreCase("")){
                        callback.error();
                    }else {
                        try {
                            JSONObject json = new JSONObject(data.getString("data"));
                            BPObject object = BPJsonUtil.jsonToBPObject(json);

                            object.setInt("responseCode", data.getInt("responseCode"));

                            callback.actionDone(object, null);

                        } catch (JSONException e) {
                            try {
                                callback.json(new JSONObject(data.getString("data")));
                            } catch (JSONException e1) {
                                callback.error();
                            }
                        }
                    }
                }

            }

            @Override
            public void doneBytes(byte[] data) {

            }


        });
    }

    public void setEntity(String message){
        bpHttp.setEntity(message);
    }

    public void setCallback(BPApiCallback callback) {
        this.callback = callback;
    }

    public BPApiCallback getCallback() {
        return callback;
    }

    public interface BPApiCallback{
        void queryDone(BPCollection collection);
        void actionDone(BPObject object, BPObject responseConfig);
        void json(JSONObject json);
        void error();
    }

}
