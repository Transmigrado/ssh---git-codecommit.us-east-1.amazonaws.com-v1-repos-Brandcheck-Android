package com.blueprint.blueprint.util;

import android.content.Context;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Creado Por jorgeacostaalvarado el 18-09-15.
 */
public class BPJsonUtil {

    public static String loadJSONFromAsset(Context context, String path) {
        String json;
        try {

            InputStream is =context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {

            return null;
        }

        return json;

    }

    public static BPObject jsonToBPObject(JSONObject jsonObj) throws JSONException {
        BPObject o = new BPObject();

        Iterator<?> keys = jsonObj.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            Object value = jsonObj.get(key);

            if (value instanceof JSONObject ) {
                BPObject bpObject = BPJsonUtil.jsonToBPObject((JSONObject) value);
                o.setBPObject(key, bpObject);
            } else if(value instanceof JSONArray){
                BPCollection bpCollection = BPJsonUtil.jsonToBPCollection((JSONArray) value);
                o.setBPCollection(key,bpCollection);
            } else {
                o.setObject(key,value);
            }
        }
        return o;
    }

    public static BPCollection jsonToBPCollection(JSONArray array) throws JSONException {
        BPCollection collection = new BPCollection();

        for(int i = 0 ; i < array.length() ; i++){
            JSONObject o = array.getJSONObject(i);
            collection.add(BPJsonUtil.jsonToBPObject(o));
        }

        return collection;
    }

    public static JSONObject bpObjectToJSON(BPObject object){
        try{
            return new JSONObject(object.toString());
        }catch (JSONException e){
            try {
                return new JSONObject();
            } catch (Exception e1) {
                return null;
            }
        }
    }
}
