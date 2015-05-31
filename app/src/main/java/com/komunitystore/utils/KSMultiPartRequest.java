package com.komunitystore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.komunitystore.model.AccessToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class KSMultiPartRequest extends AsyncTask<Void, Void, String> {

    private Context _context;

    private String _url;
    private Map<String, String> _params;
    private ArrayList<Bitmap> _images;
    private Bitmap _image;
    private AccessToken _accessToken;
    private Response.Listener _listener;

    private boolean postDeal;

    public KSMultiPartRequest(Context context, String url, Map<String, String> params, ArrayList<Bitmap> images, Response.Listener listener, AccessToken accessToken) {
        postDeal = true;
        _context = context;
        _url = url;
        _params = params;
        _images = images;
        _listener = listener;
        _accessToken = accessToken;
    }

    public KSMultiPartRequest(Context context, String url, Bitmap image, Response.Listener listener, AccessToken accessToken) {
        postDeal = false;
        _context = context;
        _url = url;
        _image = image;
        _listener = listener;
        _accessToken = accessToken;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(_url);
        httppost.addHeader("Accept", "application/json");
        if (_accessToken != null) {
            httppost.addHeader("Authorization", _accessToken.getToken_type() + " " + _accessToken.getAccess_token());
        }
        try {
            MultipartEntity entity = new MultipartEntity();
            if (postDeal) {
                for (Map.Entry<String, String> entry : _params.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
                }
                for (int i = 0; i < _images.size(); i++) {
                    //entity.addPart("medias[" + i + "]", new FileBody(bitmapToFile(_images.get(i))));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    _images.get(i).compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    InputStream in = new ByteArrayInputStream(bos.toByteArray());
                    ByteArrayBody foto = new ByteArrayBody(bos.toByteArray(), _images.get(i).hashCode() + ".jpg");
                    entity.addPart("medias[" + i + "]", foto);
                }
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                _image.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                InputStream in = new ByteArrayInputStream(bos.toByteArray());
                ByteArrayBody foto = new ByteArrayBody(bos.toByteArray(), _image.hashCode() + ".jpg");
                entity.addPart("mediaProfile", foto);
            }
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        _listener.onResponse(string);
    }
}