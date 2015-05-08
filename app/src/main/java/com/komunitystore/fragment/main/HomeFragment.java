package com.komunitystore.fragment.main;

import android.view.View;

import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.view.KSActionBarButton;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class HomeFragment extends KSFragment {

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Home";
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return new KSActionBarButton(R.drawable.logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public KSActionBarButton getRightButton1() {
        return new KSActionBarButton(R.drawable.refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public KSActionBarButton getRightButton2() {
        return new KSActionBarButton(R.drawable.add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean shouldDisplayTabbar() {
        return true;
    }
}
