package io.ebinar.infolder;

import android.app.Application;

import com.blueprint.blueprint.database.BPDatabaseManager;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

/**
 * Creado Por jorgeacostaalvarado el 09-05-16.
 */
public class MTApp  extends Application {

    public static JSONObject jsonObject = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());


        BPDatabaseManager.init(this,"Infolder_v1",1);    //iniciamos Blueprint framework


    }

}
