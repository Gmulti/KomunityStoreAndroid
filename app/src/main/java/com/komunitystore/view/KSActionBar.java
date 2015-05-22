package com.komunitystore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.komunitystore.R;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class KSActionBar extends FrameLayout {

    private ImageButton _leftButton, _rightButton1, _rightButton2;
    private TextView _title;

    public KSActionBar(Context context) {
        super(context);
        init();
    }

    public KSActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KSActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KSActionBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.ks_action_bar, null);
        _leftButton = (ImageButton) root.findViewById(R.id.left_image);
        _leftButton.setColorFilter(getResources().getColor(android.R.color.white));
        _rightButton1 = (ImageButton) root.findViewById(R.id.right_button1);
        _rightButton1.setColorFilter(getResources().getColor(android.R.color.white));
        _rightButton2 = (ImageButton) root.findViewById(R.id.right_button2);
        _rightButton2.setColorFilter(getResources().getColor(android.R.color.white));
        _title = (TextView) root.findViewById(R.id.title);
        addView(root);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setTitle(String title) {
        _title.setText(title);
    }

    public void setLeftButton(KSActionBarButton button) {
        if (button == null) {
            _leftButton.setVisibility(INVISIBLE);
            _leftButton.setOnClickListener(null);
        } else {
            _leftButton.setImageResource(button.getResource());
            _leftButton.setVisibility(VISIBLE);
            _leftButton.setOnClickListener(button.getListener());
        }
    }

    public ImageButton getLeftButton() {
        return _leftButton;
    }

    public void setRightButton1(KSActionBarButton button) {
        if (button == null) {
            _rightButton1.setVisibility(GONE);
            _rightButton1.setOnClickListener(null);
        } else {
            _rightButton1.setImageResource(button.getResource());
            _rightButton1.setVisibility(VISIBLE);
            _rightButton1.setOnClickListener(button.getListener());
        }
    }

    public ImageButton getRightButton1() {
        return _rightButton1;
    }

    public void setRightButton2(KSActionBarButton button) {
        if (button == null) {
            _rightButton2.setVisibility(GONE);
            _rightButton2.setOnClickListener(null);
        } else {
            _rightButton2.setImageResource(button.getResource());
            _rightButton2.setVisibility(VISIBLE);
            _rightButton2.setOnClickListener(button.getListener());
        }
    }

    public ImageButton getRightButton2() {
        return _rightButton2;
    }
}
