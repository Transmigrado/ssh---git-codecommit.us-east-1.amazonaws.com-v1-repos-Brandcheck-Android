package io.ebinar.infolder.view;

import android.content.Context;
import android.util.AttributeSet;

import io.ebinar.infolder.utils.shaders.ShaderHelper;
import io.ebinar.infolder.utils.shaders.SvgShader;


public class ShapeImageView extends ShaderImageView {

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader();
    }
}
