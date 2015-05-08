package com.komunitystore.utils;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class NetworkManager {

    private static NetworkManager _instance;

    private NetworkManager() {

    }

    public static NetworkManager getInstante() {

        return _instance;
    }
}
