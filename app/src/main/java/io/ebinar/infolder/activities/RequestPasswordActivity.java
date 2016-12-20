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

public class RequestPasswordActivity extends AppCompatActivity implements BPEvent.BPOnEvent {

    private BPUIBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_password);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getResources().getString(R.string.recoverpassword_title));
        toolbar.setTitleTextColor(Color.WHITE);

        binding = new BPUIBinding(this);
        binding.setCallbackEvent(this);

        binding.add(R.id.recover_email);
        binding.add(R.id.recover_send)
                .addRule("", BPBindType.EVENT);

        binding.draw();
    }

    @Override
    public void onEvent(BPEvent event) {
        switch (event.getObjectId())
        {
            case R.id.recover_send:
                sendForm();
            break;
        }
    }

    private void sendForm(){

        MTApi api = new MTApi();

        String email = (String) binding.get(R.id.recover_email).getValue();

        if(BPValidate.validateEmail(email)){
            //es un correo
            AlertManager.loading(this);

            api.requestPassword(email, new BPApi.BPApiCallback() {
                @Override
                public void queryDone(BPCollection collection) {

                }

                @Override
                public void actionDone(BPObject object, BPObject responseConfig) {
                    AlertManager.closeCurrentDialog();

                    Intent intent = new Intent(RequestPasswordActivity.this,SignupSuccessActivity.class);
                    intent.putExtra("icon", R.mipmap.ic_big_email);
                    intent.putExtra("message","Su clave ha sido enviada a su correo electrónico.");
                    startActivity(intent);
                    finish();
                }

                @Override
                public void json(JSONObject jsonObject){

                }

                @Override
                public void error(){
                    AlertManager.closeCurrentDialog();
                    AlertManager.message(RequestPasswordActivity.this,"Error","Ha ocurrido un error, intentalo más tarde.");
                }
            });
        }else{
            AlertManager.message(this,"Error","Debes ingresar un correo.");
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
