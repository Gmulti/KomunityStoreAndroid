package com.komunitystore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.secondary.UserFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.model.User;
import com.komunitystore.utils.NetworkManager;

import java.util.List;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class UserAdapter extends ArrayAdapter<User> {

    private List<User> _objects;

    public UserAdapter(Context context, List<User> objects) {
        super(context, R.layout.adapter_user);
        _objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = _objects.get(position);
        UserHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.adapter_user, null);
            holder = new UserHolder(convertView);
        } else {
            holder = (UserHolder) convertView.getTag();
        }
        holder.username.setText(user.getUsername());
        if (user.getMedia_profile() != null && user.getMedia_profile().getThumbnails_url() != null) {
            holder.image.setVisibility(View.VISIBLE);
            String imgUrl = user.getMedia_profile().getThumbnails_url().getUser_profile_tile_large();
            if (!imgUrl.equals(holder.url)) {
                holder.url = imgUrl;
                NetworkManager.getInstance(getContext()).getImage(holder.image, holder.url);
            }
        } else {
            holder.image.setVisibility(View.INVISIBLE);
            holder.url = null;
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return _objects.size();
    }

    @Override
    public User getItem(int position) {
        return _objects.get(position);
    }

    private class UserHolder {

        public TextView username;
        public NetworkImageView image;
        public String url;

        public UserHolder(View v) {
            username = (TextView) v.findViewById(R.id.username);
            image = (NetworkImageView) v.findViewById(R.id.profile_image);
            v.setTag(this);
        }
    }
}
