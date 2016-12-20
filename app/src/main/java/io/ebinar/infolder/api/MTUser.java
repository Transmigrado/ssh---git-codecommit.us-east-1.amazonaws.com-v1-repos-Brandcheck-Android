package io.ebinar.infolder.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.blueprint.blueprint.object.BPObject;

/**
 * Creado Por jorgeacostaalvarado el 09-05-16.
 */
public class MTUser {


    public static final String PREFS_NAME = "CurrentUser";

    public static void saveCurrentUser(BPObject user, Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("objectId",user.getString("objectId"));
        editor.putString("company",user.getString("company"));
        editor.putString("createdAt",user.getString("createdAt"));
        editor.putString("email",user.getString("email"));
        editor.putString("firstName",user.getString("firstName"));
        editor.putString("lastName",user.getString("lastName"));
        editor.putString("phone",user.getString("phone"));
        editor.putString("sessionToken",user.getString("sessionToken"));
        editor.putString("updateAt",user.getString("updateAt"));
        editor.putString("username",user.getString("username"));
        try {
            editor.putBoolean("notifications", user.getBoolean("notifications"));
        }catch (Exception e){
            editor.putBoolean("notifications",true);
        }

        editor.commit();
    }

    @Nullable
    public static BPObject getCurrenUser(Context context){
        BPObject user = new BPObject();
        SharedPreferences userSharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);

        String objectId = userSharedPreferences.getString("objectId",null);

        if(objectId==null){
            return null;
        }

        user.setString("objectId",userSharedPreferences.getString("objectId",null));
        user.setString("company",userSharedPreferences.getString("company",null));
        user.setString("createdAt",userSharedPreferences.getString("createdAt",null));
        user.setString("email",userSharedPreferences.getString("email",null));
        user.setString("firstName",userSharedPreferences.getString("firstName",null));
        user.setString("lastName",userSharedPreferences.getString("lastName",null));
        user.setString("phone",userSharedPreferences.getString("phone",null));
        user.setString("sessionToken",userSharedPreferences.getString("sessionToken",null));
        user.setString("updateAt",userSharedPreferences.getString("updateAt",null));
        user.setString("username",userSharedPreferences.getString("username",null));
        user.setBoolean("notifications",userSharedPreferences.getBoolean("notifications",true));


        return user;
    }

    public static void logout(Context context){
        SharedPreferences userSharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = userSharedPreferences.edit();
        editor.clear();
        editor.commit();

    }
}
