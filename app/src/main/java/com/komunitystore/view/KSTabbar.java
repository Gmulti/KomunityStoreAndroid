package com.komunitystore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.komunitystore.R;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class KSTabbar extends FrameLayout {

    private LinearLayout _leftLayout, _rightLayout, _middleLayout;
    private TextView _leftText, _rightText, _middleText;
    private ImageView _leftImage, _rightImage, _middleImage;

    private ButtonType _selectedButton = ButtonType.MIDDLE;

    private enum ButtonType {
        LEFT, RIGHT, MIDDLE
    }

    public KSTabbar(Context context) {
        super(context);
        init();
    }

    public KSTabbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KSTabbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KSTabbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.ks_tabbar, null);
        _leftLayout = (LinearLayout) root.findViewById(R.id.left_layout);
        _leftText = (TextView) root.findViewById(R.id.left_text);
        _leftImage = (ImageView) root.findViewById(R.id.left_button);

        _middleLayout = (LinearLayout) root.findViewById(R.id.middle_layout);
        _middleText = (TextView) root.findViewById(R.id.middle_text);
        _middleImage = (ImageView) root.findViewById(R.id.middle_image);

        _rightLayout = (LinearLayout) root.findViewById(R.id.right_layout);
        _rightText = (TextView) root.findViewById(R.id.right_text);
        _rightImage = (ImageView) root.findViewById(R.id.right_image);
        addView(root);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        displaySelectedButton();
    }

    private void displaySelectedButton() {
        _leftImage.setColorFilter(getResources().getColor(R.color.dark_grey));
        _middleImage.setColorFilter(getResources().getColor(R.color.dark_grey));
        _rightImage.setColorFilter(getResources().getColor(R.color.dark_grey));
        _leftText.setTextColor(getResources().getColor(R.color.dark_grey));
        _middleText.setTextColor(getResources().getColor(R.color.dark_grey));
        _rightText.setTextColor(getResources().getColor(R.color.dark_grey));
        switch (_selectedButton) {
            case LEFT:
                _leftImage.setColorFilter(getResources().getColor(R.color.darker_grey));
                _leftText.setTextColor(getResources().getColor(R.color.darker_grey));
                break;
            case MIDDLE:
                _middleImage.setColorFilter(getResources().getColor(R.color.darker_grey));
                _middleText.setTextColor(getResources().getColor(R.color.darker_grey));
                break;
            case RIGHT:
                _rightImage.setColorFilter(getResources().getColor(R.color.darker_grey));
                _rightText.setTextColor(getResources().getColor(R.color.darker_grey));
                break;
        }
    }

    public void setLeftButton(final KSTabbarButton button) {
        _leftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _selectedButton = ButtonType.LEFT;
                displaySelectedButton();
                button.getListener().onClick(_leftLayout);
            }
        });
        _leftText.setText(button.getText());
        _leftImage.setImageResource(button.getResource());
    }

    public void setMiddleButton(final KSTabbarButton button) {
        _middleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _selectedButton = ButtonType.MIDDLE;
                displaySelectedButton();
                button.getListener().onClick(_middleLayout);
            }
        });
        _middleText.setText(button.getText());
        _middleImage.setImageResource(button.getResource());
    }

    public void setRightButton(final KSTabbarButton button) {
        _rightLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _selectedButton = ButtonType.RIGHT;
                displaySelectedButton();
                button.getListener().onClick(_rightLayout);
            }
        });
        _rightText.setText(button.getText());
        _rightImage.setImageResource(button.getResource());
    }
}
