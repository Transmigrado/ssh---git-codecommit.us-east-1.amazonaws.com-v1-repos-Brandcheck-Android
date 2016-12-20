package com.blueprint.blueprint.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blueprint.blueprint.object.BPObject;

import java.util.HashMap;

/**
 * Creado Por jorgeacostaalvarado el 03-10-15.
 */
public class BPStyle extends HashMap<String,BPObject> {


    private static BPStyle instance = null;
    private static Context context = null;

    public BPStyle(Context context) {

    }

    public static void init(Context context) {
        BPStyle.context = context;
    }

    public static BPStyle getInstance() {

        if(instance == null) {
            instance = new BPStyle(BPStyle.context);
        }

        return instance;
    }

    public static void addObject(String key, BPObject object){
        getInstance().put(key, object);
    }

    public static Context getContext(){
        return context;
    }

    public static void styleObject(View v, BPObject o) {

        BPObject style = getInstance().get(o.getString("ui"));


        if(style != null && v!=null){

            BPObject background = style.getBPObject("background");

            if(background != null){

                int topLeftRadius = background.getInt("topLeftRadius");
                int topRightRadius = background.getInt("topRightRadius");
                int bottomLeftRadius = background.getInt("bottomLeftRadius");
                int bottomRightRadius = background.getInt("bottomRightRadius");

                GradientDrawable gradient = new GradientDrawable();
                gradient.setOrientation(GradientDrawable.Orientation.valueOf(background.getString("orientation")));
                int[] colors = new int[2];
                colors[0] = Color.parseColor(background.getString("color1"));
                colors[1] = Color.parseColor(background.getString("color2"));
                gradient.setColors(colors);
                gradient.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,bottomLeftRadius,bottomLeftRadius,bottomRightRadius,bottomRightRadius});
                v.setBackground(gradient);



            }

            int paddingLeft = style.getInt("paddingLeft");
            int paddingTop = style.getInt("paddingTop");
            int paddingRight = style.getInt("paddingRight");
            int paddingBottom = style.getInt("paddingBottom");

            int marginLeft = style.getInt("marginLeft");
            int marginTop = style.getInt("marginTop");
            int marginRight = style.getInt("marginRight");
            int marginBottom = style.getInt("marginBottom");

            float elevation = style.getInt("elevation");

            v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);



            if(v.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                v.setLayoutParams(params);
            } else if(v.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                v.setLayoutParams(params);
            }



        }

    }
}
