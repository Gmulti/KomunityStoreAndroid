package com.komunitystore.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.komunitystore.BuildConfig;
import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.AccessToken;
import com.komunitystore.model.User;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.KSSharedPreferences;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class MainFragment extends KSFragment {

    private ViewPager _pager;

    private LinearLayout _toLogin, _toRegister, _splashscreen;

    private ProgressBar _progress;

    private TextView _version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_main, null);
        _pager = (ViewPager) root.findViewById(R.id.pager);
        _pager.setAdapter(new MainFragmentPagerAdapter());
        _pager.setPageMargin(20);
        _pager.setPadding(20, 0, 20, 0);
        _pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (positionOffset < 0.5f) {
                        _toRegister.setVisibility(View.VISIBLE);
                        _toRegister.setAlpha(1 - (positionOffset * 2));
                        _toLogin.setVisibility(View.GONE);
                    } else {
                        _toLogin.setVisibility(View.VISIBLE);
                        _toLogin.setAlpha((positionOffset - 0.5f) * 2);
                        _toRegister.setVisibility(View.GONE);
                    }
                } else if (position == 1) {
                    _toLogin.setAlpha(1);
                    _toLogin.setVisibility(View.VISIBLE);
                    _toRegister.setAlpha(0);
                    _toRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    _toRegister.setAlpha(1);
                    _toRegister.setVisibility(View.VISIBLE);
                    _toLogin.setAlpha(0);
                    _toLogin.setVisibility(View.GONE);
                } else if (position == 1) {
                    _toLogin.setAlpha(1);
                    _toLogin.setVisibility(View.VISIBLE);
                    _toRegister.setAlpha(0);
                    _toRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        _version = (TextView) root.findViewById(R.id.version);
        String versionName = BuildConfig.VERSION_NAME;
        _version.setText("v" + versionName);
        _toLogin = (LinearLayout) root.findViewById(R.id.to_login);
        _toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _pager.setCurrentItem(0);
            }
        });
        _toRegister = (LinearLayout) root.findViewById(R.id.to_register);
        _toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _pager.setCurrentItem(1);
            }
        });
        _progress = (ProgressBar) root.findViewById(R.id.progress);
        _splashscreen = (LinearLayout) root.findViewById(R.id.splashscreen);
        _splashscreen.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (KSSharedPreferences.getInstance(getActivity()).getAccessToken() == null) {
                    _splashscreen.setVisibility(View.GONE);
                } else {
                    _progress.setVisibility(View.VISIBLE);
                    NetworkManager.getInstance(getActivity()).verify(new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //KSSharedPreferences.getInstance(getActivity()).setAccessToken(response);
                            NetworkManager.getInstance(getActivity()).getUserInfo(new Response.Listener<User>() {
                                @Override
                                public void onResponse(User response) {
                                    Singleton.getInstance().setCurrentUser(response);
                                    EventBus.getDefault().post(new KSEvent(KSEvent.Type.LOGIN, KSEvent.Error.NO_ERROR, null));
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    _splashscreen.setVisibility(View.GONE);
                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            _splashscreen.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }, 1500);
        return root;
    }

    public void onEventMainThread(KSEvent event) {
        switch (event.getType()) {
            case REGISTER:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    _pager.setCurrentItem(0);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean shouldDisplayActionBar() {
        return false;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return null;
    }

    @Override
    public KSActionBarButton getRightButton1() {
        return null;
    }

    @Override
    public KSActionBarButton getRightButton2() {
        return null;
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        public MainFragmentPagerAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                default:
                    return new LoginFragment();
                case 1:
                    return new SignUpFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public boolean shouldDisplayTabbar() {
        return false;
    }

    @Override
    public boolean shouldDisplaySearchBar() {
        return false;
    }
}
