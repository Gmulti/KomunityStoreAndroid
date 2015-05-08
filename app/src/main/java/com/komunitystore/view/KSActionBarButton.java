package com.komunitystore.view;

import android.view.View;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class KSActionBarButton {

    private int resource;
    private View.OnClickListener listener;

    public KSActionBarButton(int resource, View.OnClickListener listener) {
        this.resource = resource;
        this.listener = listener;
    }

    public int getResource() {
        return resource;
    }

    public View.OnClickListener getListener() {
        return listener;
    }
}
