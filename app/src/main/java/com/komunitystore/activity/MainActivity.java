package com.komunitystore.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.fragment.main.ListDealFragment;
import com.komunitystore.fragment.main.MainFragment;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.view.KSActionBar;
import com.komunitystore.view.KSTabbar;
import com.komunitystore.view.KSTabbarButton;

import de.greenrobot.event.EventBus;


public class MainActivity extends FragmentActivity {

    private KSActionBar _actionBar;
    private KSTabbar _tabbar;

    private boolean _backToQuit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _actionBar = (KSActionBar) findViewById(R.id.action_bar);
        _tabbar = (KSTabbar) findViewById(R.id.tabbar);
        _tabbar.setLeftButton(new KSTabbarButton(R.drawable.map, "Map", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        _tabbar.setMiddleButton(new KSTabbarButton(R.drawable.list, "List", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        _tabbar.setRightButton(new KSTabbarButton(R.drawable.search, "Search", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        showFragment(new MainFragment());
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
        _tabbar.setVisibility(fragment.shouldDisplayTabbar() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (_backToQuit) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Appuyer une nouvelle fois pour quitter", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(KSEvent event){
        switch (event.getType()) {
            case LOGIN:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    showFragment(new ListDealFragment());
                }
                break;
        }
    }
}
