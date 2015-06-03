package com.komunitystore.utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.komunitystore.R;

/**
 * Created by G3ck0z9 on 03/06/2015.
 */
public class KSRequestQueue {

    private Context _context;
    private RequestQueue _queue;
    private ImageLoader _imageLoader;

    public KSRequestQueue(Context context) {
        _context = context;
        _queue = Volley.newRequestQueue(_context);
        _imageLoader = new ImageLoader(_queue, new LruBitmapCache());
    }

    public ImageLoader getImageLoader() {
        return _imageLoader;
    }

    public void add(Request request) {
        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (request instanceof KSRequest) {
            ((KSRequest) request).setLanguage(_context.getResources().getString(R.string.language));
        }
        _queue.add(request);
    }
}
