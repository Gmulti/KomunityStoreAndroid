package com.komunitystore.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.view.KSActionBarButton;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class MainFragment extends KSFragment {

    private ViewPager _pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_main, null);
        _pager = (ViewPager) root.findViewById(R.id.pager);
        _pager.setAdapter(new MainFragmentPagerAdapter());
        return root;
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
}
