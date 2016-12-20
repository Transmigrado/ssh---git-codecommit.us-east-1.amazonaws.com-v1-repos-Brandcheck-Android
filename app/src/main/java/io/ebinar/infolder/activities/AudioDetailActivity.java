package io.ebinar.infolder.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.json.JSONObject;

import io.ebinar.infolder.R;
import io.ebinar.infolder.adapter.MediaAdapter;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.path.animation.ResizeAnimation;
import io.ebinar.infolder.utils.AlertManager;
import io.ebinar.infolder.view.VisualizerView;


public class AudioDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private static final float VISUALIZER_HEIGHT_DIP = 50f;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;

    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;

    private MediaAdapter mAdapter;
    private BPObject object;
    private FloatingActionButton fab;
    private int position = 0;
    private RelativeLayout drawerLayout;

    private int widthWindow = 0;
    private int heightWindow = 0;
    private float density = 1.0f;
    private RelativeLayout content_video;
    private RelativeLayout overlay;
    private  Toolbar toolbar;

    private Handler customHandler = new Handler();
    private Handler customUIHandler = new Handler();
    private Runnable updateTimerThread;
    private boolean seekBarControl = false;
    private boolean isStartVideo = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detail);

        //getWindow().addFlags(WindowManager.Layout Params.FLAG_NOT_TOUCHABLE);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthWindow = size.x;
        heightWindow = size.y;
        density = metrics.density;


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (RelativeLayout) findViewById(R.id.drawerLayout);


        overlay = (RelativeLayout) findViewById(R.id.overlay);
        content_video = (RelativeLayout) findViewById(R.id.content_video);



        content_video.setOnClickListener(this);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");

        object =  getIntent().getExtras().getParcelable("video");


        String media_url = object.getString("cover_url");



        BPUIBinding activityBinding = new BPUIBinding(this);

        activityBinding.add(R.id.image)
                .addRule("thumbnail_url", BPBindType.IMAGE);

        activityBinding.setData(object);

        activityBinding.draw();


        BPCollection collection = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Calendar");


        query.where().equalTo("media_id",object.getString("objectId"));

        mAdapter =  new MediaAdapter(collection, query, object);


        RecyclerView mListView = (RecyclerView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));



        if(collection.size()<=1) {

            AlertManager.loading(this);

            MTApi api = new MTApi();
            api.getHistorial(object.getString("objectId"), new BPApi.BPApiCallback() {
                @Override
                public void queryDone(BPCollection collection) {

                }

                @Override
                public void actionDone(BPObject oo, BPObject responseConfig) {

                    BPObject element = oo.getBPObject("element");
                    BPCollection history = null;

                    if(element!=null){
                        history = element.getBPCollection("history");
                    }

                    if(history!=null){

                        Log.d("infolder",">> " + history.toString());

                        for (int i = 0; i < history.size(); i++) {
                            BPObject o = history.get(i);
                            BPObject new_o = new BPObject();
                            new_o.className = "Calendar";
                            String[] date = o.getString("date").split(" ");
                            new_o.setString("Support", o.getString("support"));
                            new_o.setString("Value", o.getString("price"));
                            new_o.setString("Schedule",date[0]);
                            new_o.setString("DateAppear",date[1]);
                            new_o.setString("media_id", object.getString("objectId"));
                            new_o.save();

                        }
                    }

                    mAdapter.refresh();
                    AlertManager.closeCurrentDialog();
                }

                @Override
                public void json(JSONObject json) {

                }

                @Override
                public void error() {
                    AlertManager.closeCurrentDialog();
                    Log.d("brand","error >>");
                }
            });
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleFav();
            }
        });



        setFav(object.getBoolean("is_favorite"));



        setmMediaPlayer();
    }

    private void toggleFav() {
        boolean isFav = object.getBoolean("is_favorite");
        setFav(!isFav);
        object.className = "Media";
        object.setBoolean("is_favorite", !isFav);

        BPObject historial = new BPObject();
        historial.className = "Historial";


        if(!isFav){
            historial.setString("action", "ADD_FAV"); //le dimos Fav
        }else{
            historial.setString("action", "REMOVE_FAV"); //le sacamos el Fav
        }

        historial.setString("title",object.getString("description"));
        historial.setString("thumb", object.getString("cover_url"));
        historial.setInt("action_count",1);
        historial.setInt("video_id", object.getInt("id"));
        historial.save();

        MTApi api = new MTApi();
        api.setFav(object.getString("objectId"), false, new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {

            }

            @Override
            public void json(JSONObject json) {

            }

            @Override
            public void error() {

            }
        });
    }

    private void initPlay(){



        playAudio();
        
        BPCollection historialCollection = new BPCollection();


        BPObject lastHistorial = null;

        if(historialCollection.size() > 0){
            lastHistorial = historialCollection.get(0);

            if(lastHistorial.getInt("video_id") == object.getInt("id") && lastHistorial.getString("action").equalsIgnoreCase("MEDIA_VIDEO_COMPLETE")){
                int action_count = lastHistorial.getInt("action_count");
                action_count += 1;
                lastHistorial.setInt("action_count",action_count);
                lastHistorial.save();
            }else{
                BPObject historial = new BPObject();
                historial.className = "Historial";
                historial.setString("action", "MEDIA_VIDEO_COMPLETE"); //le sacamos el Fav
                historial.setInt("action_count", 1);
                historial.setString("title",object.getString("description"));
                historial.setString("thumb", object.getString("cover_url"));
                historial.setInt("video_id", object.getInt("id"));
                historial.save();
            }
        } else {

            BPObject historial = new BPObject();
            historial.className = "Historial";
            historial.setString("action", "MEDIA_VIDEO_COMPLETE"); //le sacamos el Fav
            historial.setInt("action_count", 1);
            historial.setString("title",object.getString("description"));
            historial.setString("thumb", object.getString("cover_url"));
            historial.setInt("video_id", object.getInt("id"));
            historial.save();
        }

    }

    private void uiAutoHide(){


        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(4000);
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // show fab

                CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                p.setAnchorId(R.id.app_bar_layout);
                p.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT | Gravity.END;

                fab.setLayoutParams(p);
                fab.show();
                fab.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }

    private void minimizeWindow(){


        toolbar.setVisibility(View.GONE);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) drawerLayout.getLayoutParams();

        ResizeAnimation anim = new ResizeAnimation(drawerLayout, widthWindow, (800 * 2));
        anim.mode = ResizeAnimation.FRAMELAYOUT;
        anim.addTop(heightWindow - (int) Math.floor((90 + 32) * density), lp.topMargin);
        anim.setDuration(500);
        drawerLayout.startAnimation(anim);

        //RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) content_video.getLayoutParams();

        ResizeAnimation anim2 = new ResizeAnimation(content_video, (int) Math.floor(160 * density), (int) Math.floor(90 * density));
        anim2.addRight(16,0);
        anim2.mode = ResizeAnimation.RELATIVE_LAYOUT;
        anim2.setDuration(500);
        content_video.startAnimation(anim2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }


    private void setFav(boolean isFav){

        Log.d("infolder","is fav :" + String.valueOf(isFav));

        if(isFav) {
            fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav_over));
        }else {
            fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        minimizeWindow();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {




        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();


        if (true) {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            final View view = getWindow().getDecorView();
            final WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();

            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;

            lp.width = width;
            lp.height = height;
            getWindowManager().updateViewLayout(view, lp);

        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // minimizeWindow();
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {


        if (!isStartVideo) {

            overlay.setVisibility(View.GONE);
            initPlay();
            isStartVideo = true;
        } else {

            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);

            fab.setVisibility(View.GONE);


            /*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    p.setAnchorId(View.NO_ID);
                    fab.setLayoutParams(p);

                    fab.setVisibility(View.GONE);
                }
            }, 100);
            */


            uiAutoHide();

        }
    }

    private void setmMediaPlayer(){
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);

        String media_url = object.getString("media_url");

        mMediaPlayer = MediaPlayer.create(this, Uri.parse(media_url));



        Log.d("brandcheck", ">> SET MEDIA PLAYER");

/*
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        setupVisualizerFxAndUI();

        // enable the visualizer
        mVisualizer.setEnabled(true);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });
        */
    }

    private void playAudio(){


        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        setupVisualizerFxAndUI();

        // enable the visualizer
        mVisualizer.setEnabled(true);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);

            }
        });

        mMediaPlayer.start();
    }


    /*displays the audio waveform*/
    private void setupVisualizerFxAndUI() {

        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}



