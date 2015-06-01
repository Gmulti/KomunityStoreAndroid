package com.komunitystore.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.model.AccessToken;
import com.komunitystore.model.Deal;
import com.komunitystore.model.KSErrorResponse;
import com.komunitystore.model.User;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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

    private static final String NO_INTERNET_CONNECTION = "no_internet_connection";

    private static NetworkManager _instance;

    private Context _context;

    private RequestQueue _queue;
    private ImageLoader _imageLoader;

    private NetworkManager(Context context) {
        _context = context;
        _queue = Volley.newRequestQueue(context);
        _imageLoader = new ImageLoader(_queue, new LruBitmapCache());
    }

    public static NetworkManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new NetworkManager(context);
        }
        return _instance;
    }

    private Response.ErrorListener getErrorListener(final Response.ErrorListener errorListener) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Activity currentActivity = ((KSApp) _context.getApplicationContext()).getCurrentActivity();
                if (error.getMessage() != null) {
                    if (!error.getMessage().equals(NO_INTERNET_CONNECTION)) {
                        if (error.getMessage().contains("error_description")) {
                            KSErrorResponse errorResponse = new Gson().fromJson(error.getMessage(), KSErrorResponse.class);
                            new AlertDialog.Builder(currentActivity)
                                    .setTitle(R.string.error_title)
                                    .setMessage(errorResponse.getError_description())
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        } else {
                            new AlertDialog.Builder(currentActivity)
                                    .setTitle(R.string.error_title)
                                    .setMessage(R.string.error_message)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        }
                    }
                } else {
                    new AlertDialog.Builder(currentActivity)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.error_message)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
                errorListener.onErrorResponse(error);
            }
        };
    }

    private void addToQueue(Request request) {
        if (isOnline()) {
            _queue.add(request);
        } else {
            request.getErrorListener().onErrorResponse(new VolleyError(NO_INTERNET_CONNECTION));
            new AlertDialog.Builder(_context)
                    .setTitle(R.string.no_connection_title)
                    .setMessage(R.string.no_connection_message)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void getUserInfo(Response.Listener listener, Response.ErrorListener errorListener) {
        addToQueue(new KSRequest(Request.Method.GET, BASE_URL_API + GET_ME, User.class, KSRequest.ReturnType.OBJECT, null, listener, getErrorListener(errorListener), KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void login(String username, String password, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("grant_type", GRANT_TYPE);
        params.put("scope", SCOPE);
        addToQueue(new KSRequest(Request.Method.POST, BASE_URL + POST_TOKEN, AccessToken.class, KSRequest.ReturnType.OBJECT, params, listener, getErrorListener(errorListener)));
    }

    public void register(String email, String username, String password, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);
        addToQueue(new KSRequest(Request.Method.POST, BASE_URL_PUBLIC + POST_REGISTER, User.class, KSRequest.ReturnType.OBJECT, params, listener, getErrorListener(errorListener)));
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
        addToQueue(new KSRequest(Request.Method.GET, url, null, KSRequest.ReturnType.ARRAY, null, listener, getErrorListener(errorListener), KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void postDeal(Map<String, String> params, ArrayList<Bitmap> bitmaps, Response.Listener listener) {
        String url = BASE_URL_API + "/deals.json";
        KSMultiPartRequest request = new KSMultiPartRequest(_context, url, params, bitmaps, listener, KSSharedPreferences.getInstance(_context).getAccessToken());
        request.execute();
    }

    public void changeUserImage(Bitmap bitmap, Response.Listener listener) {
        String url = BASE_URL_API + "/users/" + Singleton.getInstance().getCurrentUser().getId() + "/images.json";
        KSMultiPartRequest request = new KSMultiPartRequest(_context, url, bitmap, listener, KSSharedPreferences.getInstance(_context).getAccessToken());
        request.execute();
    }

    public void changeLikeDeal(boolean like, Deal deal, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = BASE_URL_API + "/deals/" + deal.getId() + "/" + (like ? "likes" : "dislikes") + ".json";
        addToQueue(new KSRequest(Request.Method.POST, url, Deal.class, KSRequest.ReturnType.OBJECT, null, listener, getErrorListener(errorListener), KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void shareDeal(Deal deal, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = BASE_URL_API + "/deals/" + deal.getId() + "/shares.json";
        addToQueue(new KSRequest(Request.Method.POST, url, Deal.class, KSRequest.ReturnType.OBJECT, null, listener, getErrorListener(errorListener), KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void changeFollowUser(boolean follow, User user, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = BASE_URL_API + "/users/" + user.getId() + "/" + (follow ? "follows" : "unfollows") + ".json";
        addToQueue(new KSRequest(Request.Method.POST, url, User.class, KSRequest.ReturnType.OBJECT, null, listener, getErrorListener(errorListener), KSSharedPreferences.getInstance(_context).getAccessToken()));
    }

    public void getLocationsFromAddress(String address, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = TextUtils.htmlEncode("https://maps.googleapis.com/maps/api/geocode/json?address=" + address);
        url = url.replaceAll(" ", "%20");
        addToQueue(new KSRequest(Request.Method.GET, url, null, KSRequest.ReturnType.OBJECT, null, listener, errorListener));
    }

    public void getImage(NetworkImageView image, String url) {
        image.setImageUrl(url, _imageLoader);
    }
}
