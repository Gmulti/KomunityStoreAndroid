package com.komunitystore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.komunitystore.model.AccessToken;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class KSSharedPreferences {

    private static KSSharedPreferences _instance;

    private Context _context;
    private SharedPreferences _prefs;

    private static final String KS_PREFS = "com.komunitystore";
    private static final String KS_ACCESS_TOKEN = "access_token";
    private static final String KS_TOKEN_TYPE = "token_type";
    private static final String KS_EXPIRES_IN = "expires_in";
    private static final String KS_REFRESH_TOKEN = "refresh_token";
    private static final String KS_SCOPE = "scope";

    private KSSharedPreferences(Context context) {
        _context = context;
        _prefs = _context.getSharedPreferences(
                KS_PREFS, Context.MODE_PRIVATE);
    }

    public static KSSharedPreferences getInstance(Context context) {
        if (_instance == null) {
            _instance = new KSSharedPreferences(context);
        }
        return _instance;
    }

    public void setAccessToken(AccessToken accessToken) {
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(KS_ACCESS_TOKEN, accessToken.getAccess_token());
        editor.putString(KS_TOKEN_TYPE, accessToken.getToken_type());
        editor.putLong(KS_EXPIRES_IN, accessToken.getExpires_in());
        editor.putString(KS_REFRESH_TOKEN, accessToken.getRefresh_token());
        editor.putString(KS_SCOPE, accessToken.getScope());
        editor.apply();
    }

    public AccessToken getAccessToken() {
        AccessToken at = new AccessToken();
        String accessToken = _prefs.getString(KS_ACCESS_TOKEN, "");
        String tokenType = _prefs.getString(KS_TOKEN_TYPE, "");
        Long expiresIn = _prefs.getLong(KS_EXPIRES_IN, 0);
        String refreshToken = _prefs.getString(KS_REFRESH_TOKEN, "");
        String scope = _prefs.getString(KS_SCOPE, "");
        at.setAccess_token(accessToken);
        at.setToken_type(tokenType);
        at.setExpires_in(expiresIn);
        at.setRefresh_token(refreshToken);
        at.setScope(scope);
        if (TextUtils.isEmpty(accessToken) ||TextUtils.isEmpty(tokenType) || expiresIn == 0 ||TextUtils.isEmpty(refreshToken) ||TextUtils.isEmpty(scope)) {
            return null;
        }
        return at;
    }
}
