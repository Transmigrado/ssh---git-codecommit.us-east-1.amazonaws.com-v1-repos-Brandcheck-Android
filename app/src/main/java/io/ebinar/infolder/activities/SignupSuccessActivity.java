package io.ebinar.infolder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.ebinar.infolder.R;

public class SignupSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_success);


        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {
            int icon = bundle.getInt("icon");
            String message = bundle.getString("message");
            ImageView successAlertLogo = (ImageView) findViewById(R.id.success_alert_logo);
            TextView messageText = (TextView) findViewById(R.id.success_alert_text);
            successAlertLogo.setImageDrawable(getResources().getDrawable(icon));
            messageText.setText(message);

        }

        Button ok_button = (Button) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
}
