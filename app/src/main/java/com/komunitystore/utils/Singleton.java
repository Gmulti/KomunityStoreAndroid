package com.komunitystore.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.komunitystore.model.Deal;
import com.komunitystore.model.User;

import java.util.List;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class Singleton {

    private static Singleton _instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (_instance == null) {
            _instance = new Singleton();
        }
        return _instance;
    }

    private User _currentUser;
    public List<Deal> _deals;

    public void setCurrentUser(User user) {
        _currentUser = user;
    }

    public User getCurrentUser() {
        return _currentUser;
    }

    public static int convertDpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


}
