package com.blueprint.blueprint.object;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

/**
 * Creado por Jorge Acosta Alvarado on 04-08-15.
 */
public class BPResourceArray extends HashMap<String, Integer> {

    private Context context;

    BPResourceArray(Context context){
        this.context = context;
    }

    public Drawable getDrawable(String key){
        return context.getResources().getDrawable(get(key));
    }

    public String getString(String key){
        return context.getResources().getString(get(key));
    }

    public int getColor(String key){
        return context.getResources().getColor(get(key));
    }

}
