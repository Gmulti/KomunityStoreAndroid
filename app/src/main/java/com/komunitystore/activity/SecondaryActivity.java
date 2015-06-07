package com.komunitystore.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.fragment.secondary.DealDetailsFragment;
import com.komunitystore.fragment.secondary.PostDealFragment;
import com.komunitystore.fragment.secondary.UserFragment;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBar;
import com.komunitystore.view.KSTabbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class SecondaryActivity extends FragmentActivity implements KSFragment.OnAttachListener {

    public static final String EXTRA_FRAGMENT = "extra_fragment";
    public static final String EXTRA_USER = "extra_user";
    public static final String EXTRA_DEAL = "extra_deal";

    private KSActionBar _actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
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
    protected void onStart() {
        super.onStart();
        ((KSApp) getApplication()).setCurrentActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof DealDetailsFragment
                || getSupportFragmentManager().findFragmentById(R.id.container) instanceof UserFragment) {
            overridePendingTransition(R.anim.activity_from_right, R.anim.activity_to_left);
        } else {
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_to_bottom);
        }
    }

    private void showFragment(KSFragment fragment) {
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onAttach(KSFragment fragment) {
        if (fragment != null) {
            _actionBar.setTitle(fragment.getTitle());
            _actionBar.setLeftButton(fragment.getLeftButton());
            _actionBar.setRightButton1(fragment.getRightButton1());
            _actionBar.setRightButton2(fragment.getRightButton2());
            _actionBar.setVisibility(fragment.shouldDisplayActionBar() ? View.VISIBLE : View.GONE);
        }
    }
}
