package io.ebinar.infolder.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import io.ebinar.infolder.R;
import io.ebinar.infolder.path.animation.ResizeAnimation;


/**
 * Creado por jorgeacostaalvarado on 25-06-15.
 */
public class VideoFloatView extends VideoView {


    public Bitmap b;
    private float mPosX;
    private float mPosY;
    private  float dx = 0;
    private  float dy = 0;

    private int nWidth = 0;
    private int nHeight = 0;
    private int dHeight = 0;
    private int oWidth = 0;
    private int oHeight = 0;

    private float mLastTouchX;
    private float mLastTouchY;

    private static final int INVALID_POINTER_ID = -1;
    private static final int DOWN       = 0;
    private static final int UP         = 1;
    private static final int LEFT       = 2;
    private static final int RIGHT      = 3;

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private int movingType = -1;

    private VideoView videoView;
    private RelativeLayout fakeContent;
    private DisplayMetrics metrics;

    public VideoFloatView(Context context) {
        this(context, null, 0);
    }

    public VideoFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoFloatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


       // this.setVideoURI(Uri.parse("android.resource://" + this.getContext().getPackageName() + "/" + R.raw.test));
        this.start();

        metrics = getResources().getDisplayMetrics();


        oWidth = getWidth();
        oHeight = getHeight();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        assert getLayoutParams() != null;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();


        dHeight = ((int) Math.floor(metrics.heightPixels));

        lp.topMargin = dHeight - (int) Math.floor((140 + 16) * metrics.density);


        setLayoutParams(lp);

    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void move(){

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
        //RelativeLayout.LayoutParams fakeContentLayoutParams = (RelativeLayout.LayoutParams) fakeContent.getLayoutParams();

        int left = (int)Math.floor(lp.rightMargin -  mPosX);
        int top =(int) Math.floor(lp.topMargin + mPosY);

        nWidth =(int) (Math.floor(oWidth - mPosY));
        nHeight = (int) Math.floor(0.75 * nWidth);

        int dWidth = ((int) Math.floor(metrics.widthPixels)) - 32;
        int dHeight = ((int) Math.floor(metrics.heightPixels));

        nWidth  = Math.min( Math.max(nWidth, 160),dWidth);
        nHeight = Math.max(nHeight, 120);

        //lp.rightMargin = Math.max(16,left);
        lp.topMargin = Math.max(16,top);

        if(nWidth <= oWidth){
            movingType = DOWN;
        }else{
            movingType = UP;
        }

        oWidth = nWidth;
        oHeight = nHeight;


        float a = oWidth;
        float b = metrics.widthPixels * 0.75f;
        float p = Math.min(1.0f, a / b);

        int lWidth =(int) Math.floor( dWidth * p);
        int lHeight = (int) Math.floor(0.75 * lWidth);


        lp.width = lWidth;
        lp.height = lHeight;


        Log.d("INFOLDER", "moving: " + String.valueOf(movingType));



        setLayoutParams(lp);

    }

    private void launchDetail(){
        /*
        Intent intent = new Intent(this.getContext(), VideoDetailActivity.class);
        this.getContext().startActivity(intent);
        */

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();

        ResizeAnimation anim = new ResizeAnimation(this,(int)(160 * metrics.density),(int)(140 * metrics.density));
        anim.addTop(dHeight - 140 - 16, lp.topMargin);
        anim.setDuration(500);
        this.startAnimation(anim);
    }

    private void openActivity(){

        int dWidth = ((int) Math.floor(metrics.widthPixels * metrics.density));
        int dHeight = ((int) Math.floor(metrics.heightPixels * metrics.density));

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();

        ResizeAnimation anim = new ResizeAnimation(this,dWidth,dHeight);
        anim.addTop(0, lp.topMargin);
        anim.setDuration(500);
        this.startAnimation(anim);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                //

                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                dx = x - mLastTouchX;
                dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

                mLastTouchX = x;
                mLastTouchY = y;

                move();
                invalidate();

                break;
            }

            case MotionEvent.ACTION_UP: {

                if(movingType == UP) {
                    openActivity();
                }else{
                    launchDetail();
                }
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if(movingType == UP) {
                    openActivity();
                }else{
                    launchDetail();
                }
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                if(movingType == UP) {
                    openActivity();
                }else{
                    launchDetail();
                }

                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {

                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    public void setFakeContent(RelativeLayout fakeContent) {
        this.fakeContent = fakeContent;
    }

    /**/



}
