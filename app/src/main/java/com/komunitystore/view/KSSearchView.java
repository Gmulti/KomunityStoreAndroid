package com.komunitystore.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edmodo.rangebar.RangeBar;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.dialog.DealsDialog;
import com.komunitystore.dialog.UsersDialog;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by G3ck0z9 on 19/05/2015.
 */
public class KSSearchView extends FrameLayout implements KSCheckBox.OnSelectedChangeListener {

    private static final int TICK_COUNT = 11;

    private LinearLayout _userLayout, _dealLayout, _priceLayout;
    private EditText _userName, _dealContent;
    private KSCheckBox _searchUser, _searchDeal, _typeBonPlan, _typePromo, _typeReduc;
    private CheckBox _enablePrice;
    private TextView _priceDisplay;
    private RangeBar _priceValue;

    private Button _search;

    private String _minPrice = "0", _maxPrice = "1000";

    private OnSearchViewAnimated _listener;
    private boolean isExpanded = false, isAnimated = false, measured = false;

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
        _userLayout = (LinearLayout) root.findViewById(R.id.search_user_layout);
        _dealLayout = (LinearLayout) root.findViewById(R.id.search_deal_layout);
        _searchDeal = (KSCheckBox) root.findViewById(R.id.search_deal);
        _searchDeal.setText(R.string.search_deal);
        _searchDeal.setColors(R.color.red, R.color.dark_grey);
        _searchDeal.setSelected(true);
        _searchDeal.setTextColor(android.R.color.black);
        _searchDeal.setOnSelectedChangeListener(this);
        _searchUser = (KSCheckBox) root.findViewById(R.id.search_user);
        _searchUser.setText(R.string.search_user);
        _searchUser.setColors(R.color.red, R.color.dark_grey);
        _searchUser.setTextColor(android.R.color.black);
        _searchUser.setOnSelectedChangeListener(this);
        _userName = (EditText) root.findViewById(R.id.user_name);
        _dealContent = (EditText) root.findViewById(R.id.deal_content);
        _typePromo = (KSCheckBox) root.findViewById(R.id.deal_type_promo);
        _typePromo.setText(R.string.code_promo);
        _typePromo.setColors(R.color.red, R.color.dark_grey);
        _typePromo.setTextColor(android.R.color.black);
        _typePromo.setOnSelectedChangeListener(this);
        _typeReduc = (KSCheckBox) root.findViewById(R.id.deal_type_reduction);
        _typeReduc.setText(R.string.reduction);
        _typeReduc.setColors(R.color.red, R.color.dark_grey);
        _typeReduc.setTextColor(android.R.color.black);
        _typeReduc.setOnSelectedChangeListener(this);
        _typeBonPlan = (KSCheckBox) root.findViewById(R.id.deal_type_bon_plan);
        _typeBonPlan.setText(R.string.bon_plan);
        _typeBonPlan.setColors(R.color.red, R.color.dark_grey);
        _typeBonPlan.setTextColor(android.R.color.black);
        _typeBonPlan.setOnSelectedChangeListener(this);
        _priceLayout = (LinearLayout) root.findViewById(R.id.price_layout);
        _priceLayout.setVisibility(GONE);
        _priceDisplay = (TextView) root.findViewById(R.id.price_display);
        _priceDisplay.setText(_minPrice + " - " + _maxPrice);
        _priceValue = (RangeBar) root.findViewById(R.id.price);
        _priceValue.setTickCount(TICK_COUNT);
        _priceValue.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {
                for (int tick = 0; tick < TICK_COUNT; tick++) {
                    if (i == tick) {
                        _minPrice = String.valueOf(i * 100);
                    }
                    if (i1 == tick) {
                        _maxPrice = String.valueOf(i1 * 100);
                    }
                }
                _priceDisplay.setText(_minPrice + " - " + _maxPrice);
            }
        });
        _enablePrice = (CheckBox) root.findViewById(R.id.enable_price);
        _enablePrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _priceLayout.setVisibility(isChecked ? VISIBLE : GONE);
            }
        });
        _enablePrice.setChecked(false);
        _dealLayout.setVisibility(VISIBLE);
        _userLayout.setVisibility(GONE);
        _search = (Button) root.findViewById(R.id.search);
        _search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(root);
    }

    private void search() {
        final Activity activity = ((KSApp) getContext().getApplicationContext()).getCurrentActivity();
        Map<String, String> params = new HashMap<>();
        if (_searchUser.isSelected()) {
            if (!TextUtils.isEmpty(_userName.getText().toString())) {
                params.put("username", _userName.getText().toString());
                final ProgressDialog progress = ProgressDialog.show(getContext(), getContext().getText(R.string.loading_title), getContext().getText(R.string.loading_message));
                NetworkManager.getInstance(getContext()).getUsers(params, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progress.dismiss();
                        UsersDialog usersDialog = new UsersDialog(activity, response);
                        usersDialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                    }
                });
            } else {
                _userName.setError(getContext().getText(R.string.empty));
            }
        } else {
            boolean noParams = true;
            if (!TextUtils.isEmpty(_dealContent.getText().toString())) {
                noParams = false;
                params.put("content", _dealContent.getText().toString());
            }
            String type = null;
            if (_typeBonPlan.isSelected()) {
                noParams = false;
                type = "bon-plan";
            } else if (_typePromo.isSelected()) {
                noParams = false;
                type = "code-promo";
            } else if (_typeReduc.isSelected()) {
                noParams = false;
                type = "reduction";
            }
            if (!TextUtils.isEmpty(type)) {
                noParams = false;
                params.put("type", type);
            }
            if (_enablePrice.isChecked()) {
                noParams = false;
                params.put("start_price", _minPrice);
                params.put("end_price", _maxPrice);
            }
            if (noParams) {
                new AlertDialog.Builder(activity)
                        .setTitle(getContext().getText(R.string.search_no_params))
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                final ProgressDialog progress = ProgressDialog.show(getContext(), getContext().getText(R.string.loading_title), getContext().getText(R.string.loading_message));
                NetworkManager.getInstance(getContext()).getDeals(params, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progress.dismiss();
                        DealsDialog dealsDialog = new DealsDialog(activity, response);
                        dealsDialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                    }
                });
            }
        }
    }

    @Override
    public void onSelectedChanged(KSCheckBox checkBox, boolean selected) {
        if (checkBox.equals(_searchDeal)) {
            if (selected) {
                _searchDeal.setSelected(selected);
                _searchUser.setSelected(false);
                _dealLayout.setVisibility(VISIBLE);
                _userLayout.setVisibility(GONE);
            }
        } else if (checkBox.equals(_searchUser)) {
            if (selected) {
                _searchUser.setSelected(selected);
                _searchDeal.setSelected(false);
                _dealLayout.setVisibility(GONE);
                _userLayout.setVisibility(VISIBLE);
            }
        } else if (checkBox.equals(_typeBonPlan)) {
            _typeBonPlan.setSelected(selected);
            if (selected) {
                _typePromo.setSelected(false);
                _typeReduc.setSelected(false);
            }
        } else if (checkBox.equals(_typePromo)) {
            _typePromo.setSelected(selected);
            if (selected) {
                _typeBonPlan.setSelected(false);
                _typeReduc.setSelected(false);
            }
        } else if (checkBox.equals(_typeReduc)) {
            _typeReduc.setSelected(selected);
            if (selected) {
                _typeBonPlan.setSelected(false);
                _typePromo.setSelected(false);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isExpanded) {
            setY(getMeasuredHeight());
        }
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

    public void expandWithoutAnimation(final boolean expand) {
        if (!isAnimated) {
            isExpanded = expand;
            if (expand) {
                setY(0);
            } else {
                setY(getMeasuredHeight());
            }
        }
    }

    public void setOnSearchViewAnimatedListener(OnSearchViewAnimated listener) {
        _listener = listener;
    }

    public interface OnSearchViewAnimated {
        void onAnimated(float animationValue);
    }
}
