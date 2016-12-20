package io.ebinar.infolder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.event.BPEvent;

import io.ebinar.infolder.R;

public class HomeActivity extends AppCompatActivity implements BPEvent.BPOnEvent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BPUIBinding bind = new BPUIBinding(this);
        bind.add(R.id.home_login)
                .addRule("", BPBindType.EVENT);

        bind.add(R.id.config_logout)
                .addRule("",BPBindType.EVENT);

        bind.setCallbackEvent(this);

        bind.draw();

    }

    @Override
    public void onEvent(BPEvent event) {
        Intent intent;
        switch (event.getObjectId())
        {
            case R.id.home_login:
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
            break;
            case R.id.config_logout:
                intent = new Intent(this,SignupActivity.class);
                startActivity(intent);
            break;
            default:
            break;
        }
    }
}
