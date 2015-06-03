package com.komunitystore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.komunitystore.R;

/**
 * Created by G3ck0z9 on 01/06/2015.
 */
public class KSCheckBox extends FrameLayout {

    private TextView _text;
    private View _selector;

    private OnSelectedChangeListener _listener;

    private int _selectedColor,
            _unselectedColor;

    private boolean _selected = false;

    public KSCheckBox(Context context) {
        super(context);
        init();
    }

    public KSCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KSCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KSCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.ks_checkbox, null);
        _text = (TextView) root.findViewById(R.id.text);
        _selector = root.findViewById(R.id.selector);
        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_listener != null) {
                    _listener.onSelectedChanged(KSCheckBox.this, !_selected);
                }
            }
        });
        addView(root);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()))));
    }

    public void setColors(int selectedColor, int unselectedColor) {
        _selectedColor = selectedColor;
        _unselectedColor = unselectedColor;
        setSelected(_selected);
    }

    public void setTextColor(int colorResource) {
        _text.setTextColor(getResources().getColor(colorResource));
    }

    public void setText(String text) {
        _text.setText(text);
    }

    public void setText(int text) {
        _text.setText(getContext().getText(text));
    }

    public void setSelected(boolean selected) {
        _selected = selected;
        _selector.setBackgroundColor(_selected ? getResources().getColor(_selectedColor) : getResources().getColor(_unselectedColor));
    }

    public boolean isSelected() {
        return _selected;
    }

    public void setOnSelectedChangeListener(OnSelectedChangeListener listener) {
        _listener = listener;
    }

    public interface OnSelectedChangeListener {
        void onSelectedChanged(KSCheckBox checkBox, boolean selected);
    }
}
