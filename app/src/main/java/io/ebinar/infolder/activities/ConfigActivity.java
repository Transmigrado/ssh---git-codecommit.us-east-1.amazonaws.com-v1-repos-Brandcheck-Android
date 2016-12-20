package io.ebinar.infolder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.event.BPEvent;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.json.JSONObject;

import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.api.MTUser;
import io.ebinar.infolder.utils.AlertManager;

public class ConfigActivity extends AppCompatActivity implements BPEvent.BPOnEvent {

    private  BPUIBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Configuraciones");
        toolbar.setTitleTextColor(Color.WHITE);


        binding = new BPUIBinding(this);
        binding.setCallbackEvent(this);
        BPObject user = MTUser.getCurrenUser(this);

        Log.d("brandcheck", ">> " + user.toString());

        binding.add(R.id.first_name)
                .addRule("firstName", BPBindType.TEXT);
        binding.add(R.id.last_name)
                .addRule("lastName", BPBindType.TEXT);
        binding.add(R.id.email)
                .addRule("email", BPBindType.TEXT);

        binding.add(R.id.notify_new_brand);
        binding.add(R.id.notify_new_media);

        binding.add(R.id.config_send)
                .addRule("",BPBindType.EVENT);

        binding.add(R.id.config_logout)
                .addRule("",BPBindType.EVENT);

        binding.setData(user);
        binding.draw();

        Log.d("brandcheck", ">> LLAMAMOS");

        MTApi api = new MTApi();
        api.getMe(new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {
                Boolean notify_new_brand = object.getBoolean("notify_new_brand");
                Boolean notify_new_media = object.getBoolean("notify_new_media");

                Log.d("brandcheck", ">> CHECK" + String.valueOf(notify_new_brand));
                Log.d("brandcheck", ">> CHECK" + String.valueOf(notify_new_media));

                ((CheckBox) binding.get(R.id.notify_new_brand).getView()).setChecked(notify_new_brand);
                ((CheckBox) binding.get(R.id.notify_new_media).getView()).setChecked(true);

            }

            @Override
            public void json(JSONObject json) {
                try {
                    Boolean notify_new_brand = json.getBoolean("notify_new_brand");
                    Boolean notify_new_media = json.getBoolean("notify_new_media");

                    Log.d("brandcheck", ">> CHECK" + String.valueOf(notify_new_brand));
                    Log.d("brandcheck", ">> CHECK" + String.valueOf(notify_new_media));

                    ((CheckBox) binding.get(R.id.notify_new_brand).getView()).setChecked(notify_new_brand);
                    ((CheckBox) binding.get(R.id.notify_new_media).getView()).setChecked(notify_new_media);
                }catch (Exception e){}
            }

            @Override
            public void error() {
                Log.d("brandcheck", ">> ERROR");
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEvent(BPEvent event) {

        if(event.getObjectId() == R.id.config_logout){
            MTUser.logout(ConfigActivity.this);
            Intent intent = new Intent(ConfigActivity.this,HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }else {
            AlertManager.loading(this);

            BPObject user = MTUser.getCurrenUser(this);
            user.setString("firstName", (String) binding.get(R.id.first_name).getValue());
            user.setString("lastName", (String) binding.get(R.id.last_name).getValue());
            user.setString("email", (String) binding.get(R.id.email).getValue());

            MTUser.saveCurrentUser(user, this);

            MTApi api = new MTApi();

            BPObject object = new BPObject();
            object.setString("firstName", (String) binding.get(R.id.first_name).getValue());
            object.setString("lastName", (String) binding.get(R.id.last_name).getValue());
            object.setString("email", (String) binding.get(R.id.email).getValue());
            object.setString("phone", user.getString("phone"));
            object.setString("password","");
            object.setInt("notify_new_brand", ((CheckBox) binding.get(R.id.notify_new_brand).getView()).isChecked()? 1 : 0);
            object.setInt("notify_new_media",((CheckBox) binding.get(R.id.notify_new_media).getView()).isChecked() ? 1 : 0);

            api.syncUser(object, new BPApi.BPApiCallback() {
                @Override
                public void queryDone(BPCollection collection) {
                    AlertManager.closeCurrentDialog();
                }

                @Override
                public void actionDone(BPObject object, BPObject responseConfig) {
                    AlertManager.closeCurrentDialog();
                    Log.d("megatime", ">> " + object);
                    AlertManager.message(ConfigActivity.this,"Exito","Tus datos han sido almacenados correctamente");
                }

                @Override
                public void json(JSONObject jsonObject) {
                    AlertManager.closeCurrentDialog();
                }

                @Override
                public void error(){
                    AlertManager.closeCurrentDialog();
                }
            });

        }

    }
}
