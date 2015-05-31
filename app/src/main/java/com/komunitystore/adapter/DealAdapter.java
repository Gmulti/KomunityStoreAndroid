package com.komunitystore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.secondary.DealDetailsFragment;
import com.komunitystore.fragment.secondary.UserFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.NetworkManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class DealAdapter extends ArrayAdapter<Deal> {

    private List<Deal> _objects;

    private Type _type;

    public enum Type {
        FULL, REDUCE
    }

    public DealAdapter(Context context, List<Deal> objects, Type type) {
        super(context, R.layout.adapter_deal);
        _objects = objects;
        _type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Deal deal = _objects.get(position);
        if (_type == Type.FULL) {
            convertView = View.inflate(getContext(), R.layout.adapter_deal, null);
            TextView title = (TextView) convertView.findViewById(R.id.title),
                    message = (TextView) convertView.findViewById(R.id.message),
                    username = (TextView) convertView.findViewById(R.id.username),
                    priceText = (TextView) convertView.findViewById(R.id.price);
            LinearLayout userLayout = (LinearLayout) convertView.findViewById(R.id.user_layout);
            NetworkImageView profile = (NetworkImageView) convertView.findViewById(R.id.profile_image),
                    dealImage = (NetworkImageView) convertView.findViewById(R.id.deal_image);
            ImageView geoloc = (ImageView) convertView.findViewById(R.id.geoloc);
            userLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchActivity(UserFragment.class, deal.getUser());
                }
            });
            Spannable span = new SpannableString(deal.getTitle() + "  - " + deal.getDate());
            span.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.date_grey)), deal.getTitle().length(), span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(span);
            message.setText(deal.getContent());
            username.setText(deal.getUser().getUsername());
            geoloc.setVisibility(deal.isGeoloc() ? View.VISIBLE : View.GONE);
            priceText.setText(deal.getStringPrice());
            if (deal.getMedias().size() > 0 && deal.getMedias().get(0).getThumbnails_url() != null && !TextUtils.isEmpty(deal.getMedias().get(0).getThumbnails_url().getImage_deal_large())) {
                dealImage.setVisibility(View.VISIBLE);
                NetworkManager.getInstance(getContext()).getImage(dealImage, deal.getMedias().get(0).getThumbnails_url().getImage_deal_large());
            } else {
                dealImage.setVisibility(View.GONE);
            }
            if (deal.getUser().getMedia_profile() != null && deal.getUser().getMedia_profile().getThumbnails_url() != null) {
                String imgUrl = deal.getUser().getMedia_profile().getThumbnails_url().getUser_profile_tile_large();
                if (!TextUtils.isEmpty(imgUrl)) {
                    NetworkManager.getInstance(getContext()).getImage(profile, imgUrl);
                } else {
                    profile.setImageResource(R.drawable.no_image);
                }
            }
        } else {
            convertView = View.inflate(getContext(), R.layout.adapter_deal_reduce, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = ((KSApp) getContext().getApplicationContext()).getCurrentActivity();
                    Intent intent = new Intent(activity, SecondaryActivity.class);
                    intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, DealDetailsFragment.class.getName());
                    intent.putExtra(SecondaryActivity.EXTRA_DEAL, deal);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
                }
            });
            TextView title = (TextView) convertView.findViewById(R.id.title),
                    message = (TextView) convertView.findViewById(R.id.message),
                    priceText = (TextView) convertView.findViewById(R.id.price);
            NetworkImageView dealImage = (NetworkImageView) convertView.findViewById(R.id.deal_image);
            Spannable span = new SpannableString(deal.getTitle() + "  - " + deal.getDate());
            span.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.date_grey)), deal.getTitle().length(), span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(span);
            message.setText(deal.getContent());
            priceText.setText(deal.getStringPrice());
            if (deal.getMedias().size() > 0 && deal.getMedias().get(0).getThumbnails_url() != null && !TextUtils.isEmpty(deal.getMedias().get(0).getThumbnails_url().getImage_deal_large())) {
                dealImage.setVisibility(View.VISIBLE);
                NetworkManager.getInstance(getContext()).getImage(dealImage, deal.getMedias().get(0).getThumbnails_url().getImage_deal_large());
            } else {
                dealImage.setVisibility(View.GONE);
            }
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
