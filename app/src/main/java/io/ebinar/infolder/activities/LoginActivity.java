package io.ebinar.infolder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.event.BPEvent;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.object.BPValidate;

import org.json.JSONObject;

import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.api.MTUser;
import io.ebinar.infolder.utils.AlertManager;


public class LoginActivity extends AppCompatActivity implements BPEvent.BPOnEvent {

    private BPUIBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Iniciar sesión");
        toolbar.setTitleTextColor(Color.WHITE);


        binding = new BPUIBinding(this);
        binding.setCallbackEvent(this);

        binding.add(R.id.login_password);
        binding.add(R.id.login_email);


        ((EditText) binding.get(R.id.login_email).getView()).setTypeface(Typeface.DEFAULT);
        ((EditText) binding.get(R.id.login_password).getView()).setTypeface(Typeface.DEFAULT);


        binding.add(R.id.config_logout)
                .addRule("", BPBindType.EVENT);
        binding.add(R.id.login_signup)
                .addRule("", BPBindType.EVENT);
        binding.add(R.id.login_recover)
                .addRule("", BPBindType.EVENT);


        binding.draw();
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


    private void sendForm() {


        String email = (String) binding.get(R.id.login_email).getValue();
        String password = (String) binding.get(R.id.login_password).getValue();

       if(BPValidate.validateEmail(email)){
           AlertManager.loading(this);
           MTApi api = new MTApi();
           api.login(email.toLowerCase(), password, new BPApi.BPApiCallback() {
               @Override
               public void queryDone(BPCollection collection) {

               }

               @Override
               public void actionDone(BPObject object, BPObject responseConfig) {
                   AlertManager.closeCurrentDialog();

                   MTUser.saveCurrentUser(object, LoginActivity.this);

                   finishAffinity();

                   Intent intent = new Intent(LoginActivity.this,MediaActivity.class);
                   startActivity(intent);
               }

               @Override
               public void json(JSONObject jsonObject){

               }

               @Override
               public void error() {
                   AlertManager.closeCurrentDialog();
                   AlertManager.message(LoginActivity.this,"Error","Ha ocurrido un error.");
               }
           });
       }else if(!BPValidate.validateEmail(email)){
           AlertManager.message(LoginActivity.this,"Error","Debes ingresar un correo válido.");
       }else if(password.length()<=6){
           AlertManager.message(LoginActivity.this,"Error","la contraseña debe tener al menos 6 carácteres.");
       }

        /*
        if (BPValidate.validateEmail(email) && password.length() > 6) {

            AlertManager.loading(this);

            MTApi api = new MTApi();
            api.login(email, password, new BPApi.BPApiCallback() {
                @Override
                public void queryDone(BPCollection collection) {

                }

                @Override
                public void actionDone(BPObject object, BPObject responseConfig) {

                    AlertManager.closeCurrentDialog();

                    int code = object.getInt("responseCode");

                    if (code == 200) {
                        // Guardamos al usuario

                        try {
                            BPObject user = BPJsonUtil.jsonToBPObject(new JSONObject(object.getString("data")));
                            user.saveAsSharedObject(LoginActivity.this, "currentUser");
                        } catch (JSONException e) {

                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } else if (code == 401) {
                        //las credenciales son erroneas
                        AlertManager.message(LoginActivity.this,
                                getResources().getString(R.string.login_error_title),
                                getResources().getString(R.string.login_bad_credential));
                    } else if (code == 405) {
                        // error desconocido
                        AlertManager.message(LoginActivity.this,
                                getResources().getString(R.string.login_error_title),
                                getResources().getString(R.string.login_other_405));
                    } else {
                        // error desconocido
                        AlertManager.message(LoginActivity.this,
                                getResources().getString(R.string.login_error_title),
                                getResources().getString(R.string.login_other));
                    }


                }
            });
        } else {
            AlertManager.message(LoginActivity.this, getResources().getString(R.string.login_error_title), getResources().getString(R.string.login_data_error));
        }
        */

    }

    @Override
    public void onEvent(BPEvent event) {
        Intent intent;
        switch (event.getObjectId()){
            case R.id.config_logout:
                sendForm();
            break;
            case R.id.login_signup:
                intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
             break;
            case R.id.login_recover:
                intent = new Intent(this, RequestPasswordActivity.class);
                startActivity(intent);
            break;
        }

    }
}