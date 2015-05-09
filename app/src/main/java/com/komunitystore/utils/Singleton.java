package com.komunitystore.utils;

import com.komunitystore.model.User;

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

    public void setCurrentUser(User user) {
        _currentUser = user;
    }

    public User getCurrentUser() {
        return _currentUser;
    }

}
