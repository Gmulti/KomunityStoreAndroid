package com.komunitystore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.komunitystore.R;
import com.komunitystore.utils.Singleton;

/**
 * Created by G3ck0z9 on 19/05/2015.
 */
public class KSSearchView extends FrameLayout {

    private boolean isExpanded = false, isAnimated = false;

    private OnSearchViewAnimated _listener;

    public KSSearchView(Context context) {
        super(context);
        init();
    }

    public KSSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KSSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KSSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.ks_search_view, null);
        addView(root);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setY(getMeasuredHeight());
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void expand(final boolean expand) {
        if (!isAnimated) {
            isAnimated = true;
            final float startY = getY();
            SpringSystem springSystem = SpringSystem.create();
            Spring spring = springSystem.createSpring();
            spring.setVelocity(5);
            //spring.setSpringConfig(new SpringConfig(100, 10));
            spring.addListener(new SimpleSpringListener() {

                @Override
                public void onSpringUpdate(Spring spring) {
                    float value = (float) spring.getCurrentValue();
                    if (_listener != null) {
                        _listener.onAnimated(value);
                    }
                    if (value > 1) {
                        value = 1 - (value - 1);
                    }
                    if (expand) {
                        value = value * -1;
                    }
                    setY(startY + (getMeasuredHeight() * value));
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    isExpanded = expand;
                    isAnimated = false;
                }
            });
            spring.setEndValue(1);
        }
    }

    public void setOnSearchViewAnimatedListener(OnSearchViewAnimated listener) {
        _listener = listener;
    }

    public interface OnSearchViewAnimated {
        void onAnimated(float animationValue);
    }
}
