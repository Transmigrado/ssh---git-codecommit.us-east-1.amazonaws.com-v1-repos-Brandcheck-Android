package io.ebinar.infolder.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;


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
import io.ebinar.infolder.font.FontelloTextView;
import io.ebinar.infolder.path.animation.ResizeAnimation;
import io.ebinar.infolder.utils.AlertManager;

public class MediaDetailActivity extends AppCompatActivity implements View.OnClickListener {

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
    private VideoView videoView;

    private SeekBar indicator;
    private Handler customHandler = new Handler();
    private Handler customUIHandler = new Handler();
    private Runnable updateTimerThread;
    private boolean seekBarControl = false;
    private boolean isStartVideo = false;
    private LinearLayout mediacontroller_content;

    private ImageButton mc_play_button;
    private ImageButton mc_fav_button;
    private double total = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthWindow = size.x;
        heightWindow = size.y;
        density = metrics.density;


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (RelativeLayout) findViewById(R.id.drawerLayout);
        indicator = (SeekBar) findViewById(R.id.seekBar);
        videoView = (VideoView) findViewById(R.id.videoView);
        mediacontroller_content = (LinearLayout) findViewById(R.id.mediacontroller_content);
        mc_play_button = (ImageButton) findViewById(R.id.mc_play_button);
        mc_fav_button = (ImageButton) findViewById(R.id.mc_fav_button);
        overlay = (RelativeLayout) findViewById(R.id.overlay);
        content_video = (RelativeLayout) findViewById(R.id.content_video);




        videoView.requestFocus();



        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mc_play_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_play));
            }
        });

        mc_play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (videoView.isPlaying()) {
                    videoView.pause();
                    mc_play_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_play));
                } else {
                    videoView.start();
                    mc_play_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_pause));
                }
            }
        });


        indicator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBarControl) {
                    double d = videoView.getDuration();
                    double progressDouble = progress;


                    int p = (int) Math.floor(d * progressDouble / 100.0f);

                    videoView.seekTo(p);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                customHandler.removeCallbacks(updateTimerThread);
                seekBarControl = true;
                videoView.pause();
                mc_play_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_play));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBarControl) {
                    seekBarControl = false;
                    videoView.start();
                    videoPlaying();
                    mc_play_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_pause));
                }
            }
        });

        mediacontroller_content.setVisibility(View.GONE);

        content_video.setOnClickListener(this);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");

        object =  getIntent().getExtras().getParcelable("video");


        String mediaId = "";

        if(object!=null){
           fillData();
        }else{

            //AlertManager.loading(this);

            mediaId = getIntent().getExtras().getString("media_id");


            MTApi api = new MTApi();
            api.refreshMedia(mediaId, new BPApi.BPApiCallback() {
                @Override
                public void queryDone(BPCollection collection) {

                }

                @Override
                public void actionDone(BPObject object, BPObject responseConfig) {
                    MediaDetailActivity.this.object = object.getBPObject("element");

                    Log.d("brandcheck",">> OBJECT " + MediaDetailActivity.this.object);

                    fillData();
                    AlertManager.closeCurrentDialog();
                }

                @Override
                public void json(JSONObject json) {

                }

                @Override
                public void error() {
                    finish();
                }
            });


        }




    }

    private void fillData(){

        String media_url = object.getString("media_url");


        if(object.getString("type").equalsIgnoreCase("Video")) {
            videoView.setVideoURI(Uri.parse(media_url));
        }else{
            mc_play_button.setVisibility(View.GONE);
        }

        BPUIBinding activityBinding = new BPUIBinding(this);

        activityBinding.add(R.id.image)
                .addRule("cover_url", BPBindType.IMAGE);

        activityBinding.setData(object);

        activityBinding.draw();

        videoView.setVisibility(View.GONE);


        BPCollection collection = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Calendar");


        query.where().equalTo("media_id",object.getString("objectId"));

        mAdapter =  new MediaAdapter(collection, query, object);


        RecyclerView mListView = (RecyclerView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));


        mListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("brandcheck",">> " +  String.valueOf(scrollY));
            }
        });

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


                        for (int i = 0; i < history.size(); i++) {
                            BPObject o = history.get(i);
                            BPObject new_o = new BPObject();
                            new_o.className = "Calendar";
                            String[] date = o.getString("date").split("T");
                            new_o.setString("Support", o.getString("support"));
                            new_o.setString("Value", o.getString("price"));
                            new_o.setString("Schedule",date[0]);
                            new_o.setString("DateAppear",date[1]);
                            new_o.setString("media_id", object.getString("objectId"));
                            new_o.save();
                            try {
                                String price = o.getString("price");
                                price = price.replace(".","");
                                price = price.replace(".","");
                                price = price.replace("$","");

                                Log.d("brandcheck", ">> price : " + price);

                                total += Double.parseDouble(price);

                            }catch (Exception e){

                            }
                        }


                        Log.d("brandcheck", ">> total : " + String.valueOf(total));

                        mAdapter.total = total;
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

                }
            });
        } else {

            for(int i = 0 ; i < collection.size();i++){
                BPObject o = collection.get(i);
                try {
                    String price = o.getString("Value");
                    price = price.replace(".","");
                    price = price.replace(".","");
                    price = price.replace("$","");


                    Log.d("brandcheck", ">> total : " + price);
                    total += Double.parseDouble(price);
                }catch (Exception e){

                }
            }

            Log.d("brandcheck", ">> total : " + String.valueOf(total));
        }

        mAdapter.total = total;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleFav();
            }
        });

        mc_fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFav();
            }
        });

        setFav(object.getBoolean("is_favorite"));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.seekTo(position);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                videoView.setVisibility(View.GONE);
                overlay.setVisibility(View.VISIBLE);


                mediacontroller_content.setVisibility(View.GONE);

                // show fab

                CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                p.setAnchorId(R.id.app_bar_layout);
                p.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT | Gravity.END;

                fab.setLayoutParams(p);
                fab.show();
                fab.setVisibility(View.VISIBLE);

                isStartVideo = false;
            }
        });
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
        historial.setInt("video_id", object.getInt("objectId"));
        historial.save();

        MTApi api = new MTApi();
        api.setFav(object.getString("objectId"),!isFav, new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {

                Log.d("brandcheck",">> " + object.toString());
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


        if (position == 0) {

            videoView.start();
            videoPlaying();
        } else {

            videoView.pause();

        }

        BPCollection historialCollection = new BPCollection();


        BPObject lastHistorial = null;

        if(historialCollection.size() > 0){
            lastHistorial = historialCollection.get(0);

            if(lastHistorial.getInt("video_id") == object.getInt("objectId") && lastHistorial.getString("action").equalsIgnoreCase("MEDIA_VIDEO_COMPLETE")){
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
                historial.setInt("video_id", object.getInt("objectId"));
                historial.save();
            }
        } else {

            BPObject historial = new BPObject();
            historial.className = "Historial";
            historial.setString("action", "MEDIA_VIDEO_COMPLETE"); //le sacamos el Fav
            historial.setInt("action_count", 1);
            historial.setString("title",object.getString("description"));
            historial.setString("thumb", object.getString("cover_url"));
            historial.setInt("video_id", object.getInt("objectId"));
            historial.save();
        }

    }

    private void videoPlaying(){


          updateTimerThread = new Runnable() {

            public void run() {

                double p = videoView.getCurrentPosition();
                double t = videoView.getDuration();
                int pp = (int)  Math.floor(100 * p / t);

                indicator.setProgress(pp);
                customHandler.postDelayed(this, 1000);

            }

        };

        updateTimerThread.run();
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
                mediacontroller_content.setVisibility(View.GONE);

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


        mediacontroller_content.setAnimation(fadeOut);
    }

    private void minimizeWindow(){

        if(!videoView.isPlaying()){
            finish();
            return;
        }
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

        if(isFav) {
            fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav_over));
            mc_fav_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_fav_on));
        }else {
            fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav));
            mc_fav_button.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mc_fav_off));
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

            if(object.getString("type").equalsIgnoreCase("Video")) {
                Intent intent = new Intent(this, VideoLandscapeActivity.class);
                intent.putExtra("time", videoView.getCurrentPosition());
                intent.putExtra("object", object);
                startActivity(intent);
            }


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
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


        Log.d("brandcheck", ">> " + object.toString());

        String type = object.getString("type");

        if(type.equalsIgnoreCase("Imagen")){

            Intent intent = new Intent(MediaDetailActivity.this, ImageActivity.class);
            intent.putExtra("object",object);
            startActivity(intent);
            return;
        }

        videoView.setVisibility(View.VISIBLE);



        if(!isStartVideo){

            overlay.setVisibility(View.GONE);
            initPlay();
            isStartVideo = true;
        }else {

            mediacontroller_content.setVisibility(View.VISIBLE);
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



}
