package com.komunitystore.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.view.KSActionBar;
import com.komunitystore.view.KSTabbar;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class SecondaryActivity extends FragmentActivity {

    public static final String EXTRA_FRAGMENT = "extra_fragment";
    public static final String EXTRA_USER = "extra_user";
    public static final String EXTRA_DEAL = "extra_deal";

    private KSActionBar _actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        getWindow().setWindowAnimations(R.anim.activity_from_bottom);
        _actionBar = (KSActionBar) findViewById(R.id.action_bar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String fragmentClass = bundle.getString(EXTRA_FRAGMENT, null);
            if (!TextUtils.isEmpty(fragmentClass)) {
                KSFragment fragment = (KSFragment) Fragment.instantiate(this, fragmentClass);
                fragment.setArguments(bundle);
                showFragment(fragment);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.activity_to_bottom);
    }

    private void showFragment(KSFragment fragment) {
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
        _actionBar.setTitle(fragment.getTitle());
        _actionBar.setLeftButton(fragment.getLeftButton());
        _actionBar.setRightButton1(fragment.getRightButton1());
        _actionBar.setRightButton2(fragment.getRightButton2());
        _actionBar.setVisibility(fragment.shouldDisplayActionBar() ? View.VISIBLE : View.GONE);
    }
}
