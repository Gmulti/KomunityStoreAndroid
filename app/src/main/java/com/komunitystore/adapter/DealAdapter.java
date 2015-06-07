package com.komunitystore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
    private OnDealDeletedListener _listener;

    public enum Type {
        FULL, REDUCE
    }

    private boolean _myDeals = false;

    public DealAdapter(Context context, List<Deal> objects, Type type) {
        super(context, R.layout.adapter_deal);
        _objects = objects;
        _type = type;
    }

    public DealAdapter(Context context, List<Deal> objects, Type type, boolean myDeals) {
        super(context, R.layout.adapter_deal);
        _objects = objects;
        _type = type;
        _myDeals = myDeals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Deal deal = _objects.get(position);
        DealHolder holder;
        if (_type == Type.FULL) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.adapter_deal, null);
                holder = new DealHolder(convertView, _type);
            } else {
                holder = (DealHolder) convertView.getTag();
            }
            holder.userLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchActivity(UserFragment.class, deal.getUser().getId());
                }
            });
            Spannable span = new SpannableString(deal.getTitle() + "  - " + deal.getDate());
            span.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.date_grey)), deal.getTitle().length(), span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(span);
            holder.message.setText(deal.getContent());
            holder.username.setText(deal.getUser().getUsername());
            holder.geoloc.setVisibility(deal.isGeoloc() ? View.VISIBLE : View.GONE);
            if (deal.getType().equals("bon-plan")) {
                holder.dealType.setText(R.string.bon_plan);
                holder.key.setText(deal.getType_view().getInfos_view() + " " + deal.getCurrency());
            } else if (deal.getType().equals("reduction")) {
                holder.dealType.setText(R.string.reduction);
                if (deal.getType_view().getSub_type().equals("cash")) {
                    holder.key.setText(deal.getType_view().getInfos_view() + " %");
                } else {
                    holder.key.setText(deal.getType_view().getInfos_view() + " " + deal.getCurrency());
                }
            } else if (deal.getType().equals("code-promo")) {
                holder.dealType.setText(R.string.code_promo);
                holder.key.setText(deal.getType_view().getInfos_view());
            }
            holder.price.setText(deal.getStringPrice());
            if (deal.getMedias().size() > 0 && deal.getMedias().get(0).getThumbnails_url() != null && !TextUtils.isEmpty(deal.getMedias().get(0).getThumbnails_url().getImage_deal_large())) {
                holder.dealImage.setVisibility(View.VISIBLE);
                if (!deal.getMedias().get(0).getThumbnails_url().getImage_deal_large().equals(holder.dealImageUrl)) {
                    holder.dealImageUrl = deal.getMedias().get(0).getThumbnails_url().getImage_deal_large();
                    NetworkManager.getInstance(getContext()).getImage(holder.dealImage, holder.dealImageUrl);
                }
            } else {
                holder.dealImage.setVisibility(View.GONE);
                holder.dealImageUrl = null;
            }
            if (deal.getUser().getMedia_profile() != null && deal.getUser().getMedia_profile().getThumbnails_url() != null) {
                holder.userImage.setVisibility(View.VISIBLE);
                String imgUrl = deal.getUser().getMedia_profile().getThumbnails_url().getUser_profile_tile_large();
                if (!imgUrl.equals(holder.userImageUrl)) {
                    holder.userImageUrl = imgUrl;
                    NetworkManager.getInstance(getContext()).getImage(holder.userImage, holder.userImageUrl);
                }
            } else {
                holder.userImage.setVisibility(View.INVISIBLE);
                holder.userImageUrl = null;
            }
        } else {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.adapter_deal_reduce, null);
                holder = new DealHolder(convertView, Type.REDUCE);
            } else {
                holder = (DealHolder) convertView.getTag();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = ((KSApp) getContext().getApplicationContext()).getCurrentActivity();
                    Intent intent = new Intent(activity, SecondaryActivity.class);
                    intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, DealDetailsFragment.class.getName());
                    intent.putExtra(SecondaryActivity.EXTRA_DEAL, deal.getId());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
                }
            });
            if (_myDeals) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_listener != null) {
                            _listener.onDelete(deal);
                        }
                    }
                });
            } else {
                holder.delete.setVisibility(View.GONE);
            }
            Spannable span = new SpannableString(deal.getTitle() + "  - " + deal.getDate());
            span.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.date_grey)), deal.getTitle().length(), span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(span);
            holder.message.setText(deal.getContent());
            if (deal.getType().equals("bon-plan")) {
                holder.dealType.setText(R.string.bon_plan);
                holder.key.setText(deal.getType_view().getInfos_view() + " " + deal.getCurrency());
            } else if (deal.getType().equals("reduction")) {
                holder.dealType.setText(R.string.reduction);
                if (deal.getType_view().getSub_type().equals("cash")) {
                    holder.key.setText(deal.getType_view().getInfos_view() + " %");
                } else {
                    holder.key.setText(deal.getType_view().getInfos_view() + " " + deal.getCurrency());
                }
            } else if (deal.getType().equals("code-promo")) {
                holder.dealType.setText(R.string.code_promo);
                holder.key.setText(deal.getType_view().getInfos_view());
            }
            holder.price.setText(deal.getStringPrice());
            if (deal.getMedias().size() > 0 && deal.getMedias().get(0).getThumbnails_url() != null && !TextUtils.isEmpty(deal.getMedias().get(0).getThumbnails_url().getImage_deal_large())) {
                holder.dealImage.setVisibility(View.VISIBLE);
                if (!deal.getMedias().get(0).getThumbnails_url().getImage_deal_large().equals(holder.dealImageUrl)) {
                    holder.dealImageUrl = deal.getMedias().get(0).getThumbnails_url().getImage_deal_large();
                    NetworkManager.getInstance(getContext()).getImage(holder.dealImage, holder.dealImageUrl);
                }
            } else {
                holder.dealImage.setVisibility(View.GONE);
                holder.dealImageUrl = null;
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        if (_objects == null) {
            return 0;
        }
        return _objects.size();
    }

    @Override
    public Deal getItem(int position) {
        if (_objects != null && _objects.size() > 0) {
            return _objects.get(position);
        } else {
            return null;
        }
    }

    public void setOnDealDeletedListener(OnDealDeletedListener listener) {
        _listener = listener;
    }

    private void launchActivity(Class clazz, int id) {
        Intent intent = new Intent(getContext(), SecondaryActivity.class);
        intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, clazz.getName());
        if (clazz == DealDetailsFragment.class) {
            intent.putExtra(SecondaryActivity.EXTRA_DEAL, id);
        } else if (clazz == UserFragment.class) {
            intent.putExtra(SecondaryActivity.EXTRA_USER, id);
        }
        Activity activity = ((KSApp) getContext().getApplicationContext()).getCurrentActivity();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
    }

    public interface OnDealDeletedListener {
        void onDelete(Deal deal);
    }

    private class DealHolder {

        private LinearLayout userLayout;
        public TextView title, message, username, price, dealType, key;
        public NetworkImageView userImage, dealImage;
        public ImageView geoloc;
        public String dealImageUrl, userImageUrl;
        public ImageButton delete;

        public DealHolder(View v, Type type) {
            if (type == Type.FULL) {
                title = (TextView) v.findViewById(R.id.title);
                message = (TextView) v.findViewById(R.id.message);
                username = (TextView) v.findViewById(R.id.username);
                price = (TextView) v.findViewById(R.id.price);
                dealType = (TextView) v.findViewById(R.id.type);
                key = (TextView) v.findViewById(R.id.key);
                userLayout = (LinearLayout) v.findViewById(R.id.user_layout);
                dealImage = (NetworkImageView) v.findViewById(R.id.deal_image);
                userImage = (NetworkImageView) v.findViewById(R.id.profile_image);
                geoloc = (ImageView) v.findViewById(R.id.geoloc);
            } else {
                title = (TextView) v.findViewById(R.id.title);
                message = (TextView) v.findViewById(R.id.message);
                price = (TextView) v.findViewById(R.id.price);
                dealImage = (NetworkImageView) v.findViewById(R.id.deal_image);
                dealType = (TextView) v.findViewById(R.id.type);
                key = (TextView) v.findViewById(R.id.key);
                delete = (ImageButton) v.findViewById(R.id.delete);
            }
            v.setTag(this);
        }

    }
}
