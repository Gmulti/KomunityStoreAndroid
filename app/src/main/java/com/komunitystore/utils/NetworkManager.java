package com.komunitystore.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.komunitystore.R;
import com.komunitystore.model.AccessToken;
import com.komunitystore.model.Deal;
import com.komunitystore.model.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class NetworkManager {

    public static final String BASE_URL = "http://104.236.195.92";
    public static final String POST_TOKEN = "/token";

    public static final String CLIENT_ID = "317b47172";
    public static final String GRANT_TYPE = "password";
    public static final String SCOPE = "public";
    public static final String CLIENT_SECRET = "jsz8bll8p6o0ocww8ssg4ccwcoowcw8";

    public static final String BASE_URL_PUBLIC = "http://104.236.195.92/api/public";
    public static final String POST_REGISTER = "/users/register.json";
    public static final String BASE_URL_API = "http://104.236.195.92/app_dev.php/api/v1";
    public static final String GET_ME = "/me.json";
    public static final String GET_DEALS = "/deals.json";

    private static NetworkManager _instance;

    private Context _context;

    private RequestQueue _queue;
    private ImageLoader _imageLoader;

    private NetworkManager(Context context) {
        _context = context;
        _queue = Volley.newRequestQueue(context);
        _imageLoader = new ImageLoader(_queue, new LruBitmapCache(LruBitmapCache.getCacheSize(_context)));
    }

    public static NetworkManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new NetworkManager(context);
        }
        return _instance;
    }

    public void getUserInfo(Response.Listener listener, Response.ErrorListener errorListener) {
        _queue.add(new KSRequest(Request.Method.GET, BASE_URL_API + GET_ME, User.class, KSRequest.ReturnType.OBJECT, null, listener, errorListener, KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void login(String username, String password, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("grant_type", GRANT_TYPE);
        params.put("scope", SCOPE);
        _queue.add(new KSRequest(Request.Method.POST, BASE_URL + POST_TOKEN, AccessToken.class, KSRequest.ReturnType.OBJECT, params, listener, errorListener));
    }

    public void register(String email, String username, String password, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);
        _queue.add(new KSRequest(Request.Method.POST, BASE_URL_PUBLIC + POST_REGISTER, User.class, KSRequest.ReturnType.OBJECT, params, listener, errorListener));
    }

    public void getDeals(Map<String, String> params, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = BASE_URL_API + GET_DEALS;
        if (params != null) {
            int count = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (count == 0) {
                    url = url + "?" + key + "=" + value;
                } else {
                    url = url + "&" + key + "=" + value;
                }
                count++;
            }
        }
        _queue.add(new KSRequest(Request.Method.GET, url, Deal.class, KSRequest.ReturnType.ARRAY, null, listener, errorListener, KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void getImage(NetworkImageView image, String url) {
        image.setErrorImageResId(R.drawable.no_image);
        image.setImageUrl(url, _imageLoader);
    }
}
