package com.blueprint.blueprint.object;

import android.content.res.Resources;
import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.blueprint.blueprint.R;
import com.blueprint.blueprint.database.BPDatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.TimeZone;

/**
 * Creado por jorgeacostaalvarado on 09-04-15.
 */
public class BPObject extends Observable implements BPIObject, Parcelable {

    private HashMap<String, Object> map;
    public String className = null;

    public BPObject(){
        map = new HashMap<>();
    }

    public BPObject(HashMap<String, Object> map){
        this.map = map;
    }


    @Override
    public String getString(String key) {

        /*
            Ejemplo generico
            user/firstName,user/lastName|%@ %@
         */

        if(key.indexOf("|") > 0){
            String[] keys = key.split("\\|");
            String[] keys2 = keys[0].split(",");
            String result = keys[1];

            for (String key1 : keys2) {
                String keyResult1 = getString(key1);
                if(keyResult1 == null){
                    return "";
                }
                result = result.replaceFirst("%@", keyResult1);
            }

            return result;

        }else {
            if (key.indexOf("/") > 0) {
                String[] objectKey = key.split("/");
                if (objectKey.length > 0) {
                    JSONObject json;
                    try {
                        String path = (String) map.get(objectKey[0]);
                        if (path == null) {
                            return null;
                        }
                        json = new JSONObject(path);
                    } catch (JSONException e) {
                        return null;
                    }

                    for (int i = 1; i < objectKey.length; i++) {
                        try {

                            if (json == null) {
                                return null;
                            }
                            if (i == objectKey.length - 1) {
                                return json.getString(objectKey[i]);
                            } else {
                                json = json.getJSONObject(objectKey[i]);
                            }


                        } catch (JSONException e) {
                            return null;
                        }
                    }
                }

            }
        }

        return (String) String.valueOf(map.get(key));
    }

    @Override
    public void setString(String key, String value) {
        map.put(key,value);
        this.setChanged();
        this.notifyObservers(key);
    }

    @Override
    public double getDouble(String key) {
        return (double) map.get(key);
    }

    @Override
    public void setDouble(String key, double value) {
        Double v = value;
        map.put(key, v);
    }


    public BPGeoObject getGeoPoint(String key){
        try {
            JSONObject json = new JSONObject((String) map.get(key));
            return new BPGeoObject(json.getDouble(key+"/latitude"),json.getDouble(key+"/longitude"));
        }catch (JSONException e){
            return null;
        }
    }

    @Override
    public int getInt(String key) {
        Object o = map.get(key);

        if(o instanceof String){
            return Integer.parseInt((String) o);
        }else if(o == null){
            return 0;
        }

        return (int) map.get(key);
    }


    public void setInt(String key, int value) {
        map.put(key,value);
    }


    public BPObject getBPObject(String key) {
        return (BPObject) map.get(key);
    }


    public void setBPObject(String key, BPIObject value) {

    }


    public void setColor(String key, String value) {
           map.put(key,value);
    }


    public int getColor(String key) {
        String stringColor = getString(key).toUpperCase();
        return BPObjectUtil.getColorFromString(stringColor);
    }

    @Override
    public boolean getBoolean(String key) {
        Object value = map.get(key);

        if(value instanceof Integer){
            if(((int) value) > 0){
                return true;
            } else{
                return false;
            }
        }else if(value instanceof String){
            if(((String) value).equalsIgnoreCase("0") || ((String) value).equalsIgnoreCase("false")){
                return false;
            }else{
                return true;
            }
        }

        return (boolean) map.get(key);
    }

    @Override
    public BPGeoObject getGeoObject(String key) {
        return null;
    }

    @Override
    public BPResourceArray getResourceArray(String key) {
        return null;
    }

    @Override
    public void setBoolean(String key, boolean value) {
        map.put(key,value);
    }

    @Override
    public void setGeoObject(String key, BPGeoObject value) {

    }

    @Override
    public void setResourceArray(String key, BPResourceArray value) {

    }

    @Override
    public void setBPObject(String key, BPObject value) {
        map.put(key,value);
    }


    public String getStringTimeAgo(String key) {

        String d = (String)map.get(key);

        String dd = d.split(".000")[0];

        String[] da = dd.split("T");
        String[] dates = da[0].split("-");
        String[] hours = da[1].split(":");

        Date date = new Date();
        date.setDate(Integer.parseInt(dates[2]));
        date.setMonth(Integer.parseInt(dates[1]) - 1);
        date.setYear(Integer.parseInt(dates[0]) - 1900);
        date.setSeconds(Integer.parseInt(hours[2]));
        date.setMinutes(Integer.parseInt(hours[1]));
        date.setHours(Integer.parseInt(hours[0]));

        return timeAgo(date);
    }


    public Date getDate(String key) {
        return null;
    }

    public void setDate(String key, Date value) {

    }

    public HashMap<String, Object> getRaw()
    {
        return map;
    }

    public void save() {
        int id = getInt("id");

        if(id>0) {
            BPDatabaseManager.getInstance().updateTable(className, getMap(), "id = " + id);
        }else {
            long rowId = BPDatabaseManager.getInstance().addToTable(className, getMap(), null);
            setInt("id",(int) Math.floor(rowId));
        }
    }


    public void remove(){
        BPDatabaseManager.getInstance().remove(className,"id = " + getString("id"));
    }

    public void removeValue(String key){
        getRaw().remove(getString(key));
    }

    public HashMap<String,Object> getMap(){
        return map;
    }

    @Override
    public String toString(){
        JSONObject json = new JSONObject();

        for (Object o : map.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            try {
                json.put((String) e.getKey(), e.getValue());
            } catch (JSONException ignored) {

            }
        }

        return json.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Object> pairs = it.next();
            keys.add(pairs.getKey());
            Object value = pairs.getValue();

            try{
                values.add(String.valueOf(value));
            }catch(Exception e){
                values.add(null);
            }


        }

        if(className!=null) {
            keys.add("table");
            values.add(className);
        }

        dest.writeStringList(keys);
        dest.writeStringList(values);

    }

    public void setBPCollection(String key, BPCollection value){
        map.put(key,value);
    }

    public BPCollection getBPCollection(String key){
        return (BPCollection) map.get(key);
    }

    @Override
    public long getLong(String key) {
        Object o = map.get(key);

        if(o instanceof String){
            return Long.parseLong((String) o);
        }else if(o == null){
            return 0;
        }else if(o instanceof  Integer){
            Long myLong= new Long((int) o);
            return myLong;
        }else if(o instanceof  Long){
            return ((long) map.get(key));
        }else if(o instanceof Double){
            long x = Math.round((double) o);
            return x;
        }

        return 0L;
    }

    @Override
    public void setLong(String key, long value) {
        map.put(key,value);
    }

    public static final Creator<BPObject> CREATOR = new Creator<BPObject>() {
        public BPObject createFromParcel(Parcel in) {
            return new BPObject(in);
        }

        public BPObject[] newArray(int size) {
            return new BPObject[size];
        }
    };

    public BPObject(Parcel in){

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        in.readStringList(keys);
        in.readStringList(values);

        map = new HashMap<>();

        for(int i = 0; i < keys.size();i++){
            String key = keys.get(i);
            Object value = values.get(i);

            if(key.equalsIgnoreCase("className")){
                className = (String) value;
            }else{
                map.put(key, value);
            }
        }

    }

    public void setObject(String field, Object value){
        map.put(field,value);
    }

    public Object getObject(String field){
       return map.get(field);
    }

    public void toggle(String field) {
      setBoolean(field, !getBoolean(field));
    }

    public String timeAgo(Date date) {
        return timeAgo(date.getTime());
    }

    public String timeAgo(long millis) {
        long diff = new Date().getTime() - millis;

        Resources r = BPDatabaseManager.getContext().getResources();

        String prefix = r.getString(R.string.time_ago_prefix);
        String suffix = r.getString(R.string.time_ago_suffix);

        double seconds = Math.abs(diff) / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        double days = hours / 24;
        double years = days / 365;

        String words;

        if (seconds < 45) {
            words = r.getString(R.string.time_ago_seconds, Math.round(seconds));
        } else if (seconds < 90) {
            words = r.getString(R.string.time_ago_minute, 1);
        } else if (minutes < 45) {
            words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
        } else if (minutes < 90) {
            words = r.getString(R.string.time_ago_hour, 1);
        } else if (hours < 24) {
            words = r.getString(R.string.time_ago_hours, Math.round(hours));
        } else if (hours < 42) {
            words = r.getString(R.string.time_ago_day, 1);
        } else if (days < 30) {
            words = r.getString(R.string.time_ago_days, Math.round(days));
        } else if (days < 45) {
            words = r.getString(R.string.time_ago_month, 1);
        } else if (days < 365) {
            words = r.getString(R.string.time_ago_months, Math.round(days / 30));
        } else if (years < 1.5) {
            words = r.getString(R.string.time_ago_year, 1);
        } else {
            words = r.getString(R.string.time_ago_years, Math.round(years));
        }

        StringBuilder sb = new StringBuilder();

        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix).append(" ");
        }

        sb.append(words);

        if (suffix != null && suffix.length() > 0) {
            sb.append(" ").append(suffix);
        }

        return sb.toString().trim();
    }
}


