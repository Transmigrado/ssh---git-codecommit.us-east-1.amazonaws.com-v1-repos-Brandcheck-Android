package io.ebinar.infolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blueprint.blueprint.object.BPObject;

import io.ebinar.infolder.activities.HomeActivity;
import io.ebinar.infolder.activities.MediaActivity;
import io.ebinar.infolder.api.MTUser;
import io.ebinar.infolder.mobile.Topic;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Topic topic = new Topic(this);
        topic.subscribeToTopic("", new Topic.Callback() {
            @Override
            public void done() {

            }

            @Override
            public void error() {

            }
        });

        BPObject user = MTUser.getCurrenUser(this);

        if(user==null){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, MediaActivity.class);
            startActivity(intent);
            finish();
        }

    }
}