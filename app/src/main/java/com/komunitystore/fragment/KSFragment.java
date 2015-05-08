package com.komunitystore.fragment;

import android.support.v4.app.Fragment;

import com.komunitystore.view.KSActionBarButton;

/**
 * Created by Tanguy on 06/05/2015.
 */
public abstract class KSFragment extends Fragment {

    public abstract boolean shouldDisplayActionBar();
    public abstract String getTitle();
    public abstract KSActionBarButton getLeftButton();
    public abstract KSActionBarButton getRightButton1();
    public abstract KSActionBarButton getRightButton2();
    public abstract boolean shouldDisplayTabbar();

}
