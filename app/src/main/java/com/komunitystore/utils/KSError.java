package com.komunitystore.utils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.komunitystore.model.BaseResponse;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class KSError extends VolleyError {

    private BaseResponse _responseError;

    public KSError(VolleyError error, BaseResponse responseError) {
        super(error);
        _responseError = responseError;
    }

    public BaseResponse getResponseError() {
        return _responseError;
    }
}
