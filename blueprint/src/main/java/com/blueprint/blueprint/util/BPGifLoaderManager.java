package com.blueprint.blueprint.util;

import android.util.Log;

import com.blueprint.blueprint.http.BPHttp;
import com.blueprint.blueprint.http.BPHttpFile;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.view.GifImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Creado Por jorgeacostaalvarado el 28-12-15.
 */
public class BPGifLoaderManager {

    private static BPGifLoaderManager instance;
    private static ArrayList<BPObject> list = null;
    public static String value = "";
    private static HashMap<String, byte[]> gifdata;

    public static BPGifLoaderManager getInstance() {

        if(instance == null) {
            instance = new BPGifLoaderManager();
            instance.list = new ArrayList<>();
            instance.gifdata = new HashMap<>();
        }

        return instance;
    }

    public static void add(BPObject o){


        getInstance().list.add(o);
    }

    public static byte[] get(String objectId){
        return getInstance().gifdata.get(objectId);
    }

    public static void startLoad(){
        getInstance().load();
    }

    private static void load(){
        if(getInstance().list!=null && getInstance().list.size()>0){
            final BPObject object = getInstance().list.get(0);
            getInstance().list.remove(0);

            BPHttpFile httpFile = new BPHttpFile();
            httpFile.requestURL(getInstance().value);

            httpFile.addCallback(new BPHttp.BeeRequestCallback() {
                @Override
                public void done(BPObject data) {

                }

                @Override
                public void doneBytes(byte[] data) {
                    getInstance().gifdata.put(object.getString(value), data);
                    getInstance().load();
                }
            });

        }
    }

}
