package com.komunitystore.view;

import android.view.View;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class KSTabbarButton {

    private int resource;
    private String text;
    private View.OnClickListener listener;

    public KSTabbarButton(int resource, String text, View.OnClickListener listener) {
        this.resource = resource;
        this.text = text;
        this.listener = listener;
    }

    public int getResource() {
        return resource;
    }

    public String getText() {
        return text;
    }

    public View.OnClickListener getListener() {
        return listener;
    }
}
