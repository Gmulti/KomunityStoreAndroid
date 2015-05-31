package com.komunitystore.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public abstract boolean shouldDisplaySearchBar();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnAttachListener) {
            ((OnAttachListener) activity).onAttach(this);
        }
    }

    public interface OnAttachListener {
        void onAttach(KSFragment fragment);
    }

}
