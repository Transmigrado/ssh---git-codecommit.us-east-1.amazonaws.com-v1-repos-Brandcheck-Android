package io.ebinar.infolder.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.blueprint.blueprint.object.BPObject;

import io.ebinar.infolder.R;

public class VideoLandscapeActivity extends AppCompatActivity {

    private int position = 0;
    private BPObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_landscape);


        int time = getIntent().getExtras().getInt("time");
        object =  getIntent().getExtras().getParcelable("object");

        String media_url = object.getString("media_url");

        MediaController mediaController = new MediaController(this);
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(media_url));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaController.setElevation(0);
        }

        videoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {


            public void onPrepared(MediaPlayer mediaPlayer) {


                videoView.seekTo(position);

                if (position == 0) {

                    videoView.start();
                } else {

                    //if we come from a resumed activity, video playback will be paused
                    videoView.pause();

                }
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
           finish();
        }
    }

}
