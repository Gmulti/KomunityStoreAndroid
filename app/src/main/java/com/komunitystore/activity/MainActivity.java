package com.komunitystore.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.fragment.main.ListDealFragment;
import com.komunitystore.fragment.main.MainFragment;
import com.komunitystore.fragment.main.DealsMapFragment;
import com.komunitystore.fragment.main.ProfileFragment;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.KSSharedPreferences;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBar;
import com.komunitystore.view.KSActionBarButton;
import com.komunitystore.view.KSSearchView;
import com.komunitystore.view.KSTabbar;
import com.komunitystore.view.KSTabbarButton;

import de.greenrobot.event.EventBus;


public class MainActivity extends FragmentActivity implements KSFragment.OnAttachListener {

    private KSActionBar _actionBar;
    private KSTabbar _tabbar;
    private KSSearchView _search;
    private View _actionBarShadow;

    private MainFragment _mainFragment;
    private ProfileFragment _profileFragment;
    private ListDealFragment _listDealFragmet;
    private DealsMapFragment _mapFragment;

    private boolean _backToQuit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _actionBar = (KSActionBar) findViewById(R.id.action_bar);
        _actionBar.setLeftButton(new KSActionBarButton(R.drawable.logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        }));
        _tabbar = (KSTabbar) findViewById(R.id.tabbar);
        _tabbar.setLeftButton(new KSTabbarButton(R.drawable.map, getResources().getString(R.string.map), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(_mapFragment);
            }
        }));
        _tabbar.setMiddleButton(new KSTabbarButton(R.drawable.list, getResources().getString(R.string.list), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(_listDealFragmet);
            }
        }));
        _tabbar.setRightButton(new KSTabbarButton(R.drawable.profile, getResources().getString(R.string.profile), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(_profileFragment);
            }
        }));
        _search = (KSSearchView) findViewById(R.id.search_bar);
        _actionBarShadow = findViewById(R.id.action_bar_shadow);
        _mainFragment = new MainFragment();
        _profileFragment = new ProfileFragment();
        _listDealFragmet = new ListDealFragment();
        _mapFragment = new DealsMapFragment();
        showFragment(_mainFragment);
    }

    private void showFragment(KSFragment fragment) {
        _search.expandWithoutAnimation(false);
        //ImageButton rightButton1 = _actionBar.getRightButton1();
        //rightButton1.setColorFilter(getResources().getColor(android.R.color.white));
        //rightButton1.setBackgroundColor(getResources().getColor(R.color.red));
        //rightButton1.setRotation(0);
        //ImageButton rightButton2 = _actionBar.getRightButton2();
        //rightButton2.setColorFilter(getResources().getColor(android.R.color.white));
        //rightButton2.setBackgroundColor(getResources().getColor(R.color.red));
        //rightButton2.setRotation(0);
        //ImageButton leftButton = _actionBar.getLeftButton();
        //leftButton.setColorFilter(getResources().getColor(android.R.color.white));
        //leftButton.setBackgroundColor(getResources().getColor(R.color.red));
        //leftButton.setRotation(0);
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        } else if (getSupportFragmentManager().findFragmentById(R.id.container).getClass() != fragment.getClass()) {
            if (fragment instanceof MainFragment) {
                _mainFragment = (MainFragment) fragment;
            } else if (fragment instanceof ListDealFragment) {
                _listDealFragmet = (ListDealFragment) fragment;
            } else if (fragment instanceof DealsMapFragment) {
                _mapFragment = (DealsMapFragment) fragment;
            } else if (fragment instanceof ProfileFragment) {
                _profileFragment = (ProfileFragment) fragment;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progress = ProgressDialog.show(MainActivity.this, getString(R.string.loading_title), getString(R.string.loading_message));
                        NetworkManager.getInstance(MainActivity.this).logout(new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                progress.dismiss();
                                KSSharedPreferences.getInstance(MainActivity.this).logout();
                                showFragment(_mainFragment);
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void onBackPressed() {
        if (_backToQuit) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.back_to_close, Toast.LENGTH_SHORT).show();
            _backToQuit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    _backToQuit = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        ((KSApp) getApplication()).setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Singleton.getInstance().destroy();
        ((KSApp) getApplication()).setCurrentActivity(null);
    }

    public void onEventMainThread(KSEvent event) {
        switch (event.getType()) {
            case LOGIN:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    showFragment(new ListDealFragment());
                }
                break;
        }
    }

    @Override
    public void onAttach(KSFragment fragment) {
        _actionBar.setTitle(fragment.getTitle());
        //_actionBar.setLeftButton(fragment.getLeftButton());
        _actionBar.setRightButton1(fragment.getRightButton1());
        _actionBar.setRightButton2(fragment.getRightButton2());
        _actionBar.setVisibility(fragment.shouldDisplayActionBar() ? View.VISIBLE : View.GONE);
        _actionBarShadow.setVisibility(fragment.shouldDisplayActionBar() ? View.VISIBLE : View.GONE);
        _tabbar.setVisibility(fragment.shouldDisplayTabbar() ? View.VISIBLE : View.GONE);
        _search.setVisibility(fragment.shouldDisplaySearchBar() ? View.VISIBLE : View.GONE);
    }

    public KSActionBar getKSActionBar() {
        return _actionBar;
    }

    public KSSearchView getKSSearchView() {
        return _search;
    }
}
