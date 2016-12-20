package io.ebinar.infolder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.object.BPObject;

import io.ebinar.infolder.R;

public class ImageActivity extends AppCompatActivity {

    private  Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        toolbar = (Toolbar)findViewById(R.id.toolbar);


        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");

        BPObject object = (BPObject) getIntent().getExtras().get("object");

        BPUIBinding binding = new BPUIBinding(this);

        binding.add(R.id.image)
                .addRule("media_url", BPBindType.IMAGE);

        binding.setData(object);
        binding.draw();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);
    }

}
