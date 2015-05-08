package com.komunitystore.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.komunitystore.model.AccessToken;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class KSRequest extends Request {

    public enum ReturnType {
        OBJECT, ARRAY
    }

    private final Gson _gson = new Gson();
    private Response.Listener _listener;
    private Class _clazz;
    private Map<String, String> _params;
    private AccessToken _accessToken;
    private ReturnType _returnType;

    public KSRequest(int method, String url, Class clazz, ReturnType returnType, Map<String, String> params, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        _listener = listener;
        _clazz = clazz;
        _params = params;
        _returnType = returnType;
    }

    public KSRequest(int method, String url, Class clazz, ReturnType returnType, Map<String, String> params, Response.Listener listener, Response.ErrorListener errorListener, AccessToken accessToken) {
        super(method, url, errorListener);
        _listener = listener;
        _clazz = clazz;
        _params = params;
        _returnType = returnType;
        _accessToken = accessToken;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return _params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>(super.getHeaders());
        if (_accessToken != null) {
            headers.put("Accept", "application/json");
            headers.put("Authorization", _accessToken.getToken_type() + " " + _accessToken.getAccess_token());
        }
        return headers;
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        return volleyError;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            switch (_returnType) {
                case OBJECT:
                default:
                    return Response.success(
                            _gson.fromJson(json, _clazz),
                            HttpHeaderParser.parseCacheHeaders(response));
                case ARRAY:
                    return Response.success(
                            new JSONArray(json),
                            HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Object response) {
        _listener.onResponse(response);
    }

}
