package com.komunitystore.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.secondary.DealDetailsFragment;
import com.komunitystore.fragment.secondary.UserFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.NetworkManager;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class DealAdapter extends ArrayAdapter<Deal> {

    private List<Deal> _objects;

    public DealAdapter(Context context, List<Deal> objects) {
        super(context, R.layout.adapter_deal);
        _objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //DealHolder holder = new DealHolder();
        //if (convertView == null) {
        // TODO Reactiver recyclage !!!
            convertView = View.inflate(getContext(), R.layout.adapter_deal, null);
            /*holder.goToProfile = (RelativeLayout) convertView.findViewById(R.id.go_to_profile);
            holder.goToDeal = (RelativeLayout) convertView.findViewById(R.id.go_to_deal);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.profile = (NetworkImageView) convertView.findViewById(R.id.profile_image);
            holder.dealImage = (NetworkImageView) convertView.findViewById(R.id.deal_image);
            holder.geoloc = (ImageView) convertView.findViewById(R.id.geoloc);
            convertView.setTag(holder);*/
        //}
        //else {
        //    holder = (DealHolder) convertView.getTag();
        //}
        final Deal deal = _objects.get(position);
        RelativeLayout goToProfile = (RelativeLayout) convertView.findViewById(R.id.go_to_profile);
        RelativeLayout goToDeal = (RelativeLayout) convertView.findViewById(R.id.go_to_deal);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView message = (TextView) convertView.findViewById(R.id.message);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView priceText = (TextView) convertView.findViewById(R.id.price);
        NetworkImageView profile = (NetworkImageView) convertView.findViewById(R.id.profile_image);
        NetworkImageView dealImage = (NetworkImageView) convertView.findViewById(R.id.deal_image);
        ImageView geoloc = (ImageView) convertView.findViewById(R.id.geoloc);
        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(UserFragment.class, deal.getUser());
            }
        });
        goToDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(DealDetailsFragment.class, deal);
            }
        });
        title.setText(deal.getTitle());
        message.setText(deal.getContent());
        username.setText(deal.getUser().getUsername());
        geoloc.setVisibility(deal.isGeoloc() ? View.VISIBLE : View.GONE);
        priceText.setText(deal.getStringPrice());
        try {
            date.setText(deal.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (deal.getMedias().size() > 0 && deal.getMedias().get(0).getThumbnails_url() != null) {
            NetworkManager.getInstance(getContext()).getImage(dealImage, deal.getMedias().get(0).getThumbnails_url().getImage_deal());
        } else {
            dealImage.setImageResource(R.drawable.no_image);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return _objects.size();
    }

    private void launchActivity(Class clazz, Serializable object) {
        Intent intent = new Intent(getContext(), SecondaryActivity.class);
        intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, clazz.getName());
        if (clazz == DealDetailsFragment.class) {
            intent.putExtra(SecondaryActivity.EXTRA_DEAL, object);
        } else if (clazz == UserFragment.class) {
            intent.putExtra(SecondaryActivity.EXTRA_USER, object);
        }
        FragmentActivity activity = (FragmentActivity) getContext();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
    }

    /*private class DealHolder {
        private RelativeLayout goToProfile, goToDeal;
        public TextView title, message, username, date, price;
        public NetworkImageView profile, dealImage;
        public ImageView geoloc;
    }*/
}
