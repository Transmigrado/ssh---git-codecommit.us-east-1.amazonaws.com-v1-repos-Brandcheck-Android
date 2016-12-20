package io.ebinar.infolder.path.animation;

import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Creado por jorgeacostaalvarado on 30-06-15.
 */
public class ResizeAnimation extends Animation
{
    private int mWidth;
    private int mStartWidth;
    private int mHeight;
    private int mStartHeight;
    private int mTop =0;
    private int mStartTop = 0;
    private int mRight = 0;
    private int mStartRight = 0;
    private View mView;

    public int mode = 0;
    public static final int RELATIVE_LAYOUT = 0;
    public static final int COLLAPSINGTOOLBARLAYOUT = 1;
    public static final int FRAMELAYOUT = 2;


    public ResizeAnimation(View view, int width, int height)
    {
        mView = view;
        mWidth = width;
        mHeight = height;

        mStartWidth = view.getWidth();
        mStartHeight = view.getHeight();
    }

    public void addTop(int top, int startTop){
        mTop = top;
        mStartTop = startTop;
    }

    public void addRight(int right, int startRight){
        mRight = right;
        mStartRight = startRight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
        int newHeight = mStartHeight+ (int) ((mHeight - mStartHeight) * interpolatedTime);
        int newTop = mStartTop+ (int) ((mTop - mStartTop) * interpolatedTime);
        int newRight = mStartRight+ (int) ((mRight - mStartRight) * interpolatedTime);

        mView.getLayoutParams().width = newWidth;
        mView.getLayoutParams().height = newHeight;

        if(mode == FRAMELAYOUT) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mView.getLayoutParams();

            lp.topMargin = newTop;
            lp.rightMargin = newRight;
        } else if(mode == RELATIVE_LAYOUT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mView.getLayoutParams();

            lp.topMargin = newTop;
            lp.rightMargin = newRight;
        }else if(mode == COLLAPSINGTOOLBARLAYOUT) {
            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) mView.getLayoutParams();

            lp.topMargin = newTop;
            lp.rightMargin = newRight;
        }

        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
