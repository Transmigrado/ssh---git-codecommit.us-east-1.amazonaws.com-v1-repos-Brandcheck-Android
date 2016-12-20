package com.blueprint.blueprint.object;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by jorgeacostaalvarado on 07-07-15.
 */
public class BPObjectUtil {


    public static final String RED = "#F7402D";
    public static final String PINK = "#EC1460";
    public static final String PURPLE = "#9D1AB2";
    public static final String DEEP_PURPLE = "#6732B9";
    public static final String INDIGO = "#3D4DB7";
    public static final String BLUE = "#0F93F5";
    public static final String LIGHT_BLUE = "#00A6F7";
    public static final String CYAN = "#00BBD6";
    public static final String TEAL = "#009687";
    public static final String GREEN = "#46B04A";
    public static final String LIGHT_GREEN = "#89C441";
    public static final String LIME = "#CCDD1E";

    public static int getColorFromString(String stringColor){

        if(stringColor.indexOf("#") == 0){
            if(stringColor.length()==7 || stringColor.length()==9){
                try{
                    return Color.parseColor(stringColor);
                }catch (NumberFormatException e){
                    return Color.BLACK;
                }

            }else{
                return Color.BLACK;
            }
        }

        String[] colorArray = stringColor.split(",");

        if(colorArray.length < 3){
            return Color.BLACK;
        }

        int r;
        int g;
        int b;
        int a = 255;

        try{
            r = Integer.parseInt(colorArray[0]);
            g = Integer.parseInt(colorArray[1]);
            b = Integer.parseInt(colorArray[2]);
        }catch (NumberFormatException e){
            return Color.BLACK;
        }

        if(colorArray.length > 3){
            a =  Integer.parseInt(colorArray[3]);
        }

        return Color.argb(a,r,g,b);
    }

    public static int getMaterialColor(Context context, String str){
       str = "material_" + str.toLowerCase();
       int colorid = context.getResources().getIdentifier(str,"color",context.getPackageName());
       return  context.getResources().getColor(colorid);
    }

    public static int getMaterialColor(String str){
        if(str.equalsIgnoreCase(RED)){
            return getColorFromString(RED);
        } else if(str.equalsIgnoreCase(PINK)){
            return getColorFromString(PINK);
        } else if(str.equalsIgnoreCase(PURPLE)){
            return getColorFromString(PURPLE);
        }else if(str.equalsIgnoreCase(DEEP_PURPLE)){
            return getColorFromString(DEEP_PURPLE);
        }else if(str.equalsIgnoreCase(INDIGO)){
            return getColorFromString(INDIGO);
        }else if(str.equalsIgnoreCase(BLUE)){
            return getColorFromString(BLUE);
        }else if(str.equalsIgnoreCase(LIGHT_BLUE)){
            return getColorFromString(LIGHT_BLUE);
        }else if(str.equalsIgnoreCase(CYAN)){
            return getColorFromString(CYAN);
        }else if(str.equalsIgnoreCase(TEAL)){
            return getColorFromString(TEAL);
        }

        return getColorFromString(RED);
    }
}
