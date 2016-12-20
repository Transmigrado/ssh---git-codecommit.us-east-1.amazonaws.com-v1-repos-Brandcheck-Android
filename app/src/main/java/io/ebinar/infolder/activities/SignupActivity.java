package io.ebinar.infolder.activities;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import io.ebinar.infolder.utils.AlertManager;


public class SignupActivity extends AppCompatActivity implements BPEvent.BPOnEvent {

    private BPUIBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getResources().getString(R.string.signup_title));
        toolbar.setTitleTextColor(Color.WHITE);

        binding = new BPUIBinding(this);
        binding.setCallbackEvent(this);
        binding.add(R.id.home_login)
                .addRule("", BPBindType.EVENT);


        binding.add(R.id.signup_email);
        binding.add(R.id.signup_firstname);
        binding.add(R.id.signup_lastname);
        binding.add(R.id.signup_company);
        binding.add(R.id.signup_phone);

        binding.draw();

    }


    @Override
    public void onEvent(BPEvent event) {
        switch (event.getObjectId())
        {
            case R.id.home_login:
                sendform();
            break;
        }
    }

    public void sendform(){
        MTApi api = new MTApi();
        BPObject object = new BPObject();

        String email = (String) binding.get(R.id.signup_email).getValue();
        String firstName = (String) binding.get(R.id.signup_firstname).getValue();
        String lastName = (String) binding.get(R.id.signup_lastname).getValue();
        String phone = (String) binding.get(R.id.signup_phone).getValue();
        String company = (String) binding.get(R.id.signup_company).getValue();


        if(email.length() > 0 && firstName.length() > 0 && lastName.length() > 0 && phone.length()>0 && company.length()>0) {

            if(BPValidate.validateEmail(email))
            {
                object.setString("email", email);
                object.setString("firstName", firstName);
                object.setString("lastName", lastName);
                object.setString("phone", phone);
                object.setString("company", company);

                AlertManager.loading(this);


                api.signup(object, new BPApi.BPApiCallback() {

                    @Override
                    public void json(JSONObject jsonObject){

                    }
                    @Override
                    public void queryDone(BPCollection collection) {

                    }

                    @Override
                    public void actionDone(BPObject object, BPObject responseConfig) {


                        AlertManager.closeCurrentDialog();

                        int responseCode = object.getInt("responseCode");

                        if (responseCode == 201) {
                            Intent intent = new Intent(SignupActivity.this, SignupSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            // error desconocido
                            AlertManager.message(SignupActivity.this,
                                    getResources().getString(R.string.signup_error_title),
                                    getResources().getString(R.string.signup_problem));
                        }
                    }

                    @Override
                    public void error(){
                        AlertManager.closeCurrentDialog();
                        AlertManager.message(SignupActivity.this,"Error","Ha ocurrido un error.");
                    }
                });

            }else{
                // no es correo
                AlertManager.message(this, getResources().getString(R.string.login_error_title), getResources().getString(R.string.signup_email_error));
            }
        }else{
            AlertManager.message(this, getResources().getString(R.string.login_error_title), getResources().getString(R.string.login_data_error));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}

