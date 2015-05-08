package com.komunitystore.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.komunitystore.R;
import com.komunitystore.model.Deal;

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
        DealHolder holder = new DealHolder();
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.adapter_deal, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.profile = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.dealImage = (ImageView) convertView.findViewById(R.id.deal_image);
            holder.geoloc = (ImageView) convertView.findViewById(R.id.geoloc);
            convertView.setTag(holder);
        } else {
            holder = (DealHolder) convertView.getTag();
        }
        Deal deal = _objects.get(position);
        holder.title.setText(deal.getTitle());
        holder.message.setText(deal.getContent());
        holder.username.setText(deal.getUser().getUsername());
        holder.geoloc.setVisibility((deal.getLat() == 0.0 && deal.getLng() == 0.0) ? View.GONE : View.VISIBLE);
        float price = deal.getPrice();
        holder.price.setText(deal.getStringPrice());
        try {
            holder.date.setText(deal.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Deal image
        /*
        if(listDeal.get(position).getMedias().size() > 0 && listDeal.get(position).getMedias().get(0).getThumbnails_url() != null){
                new ImageDownloader(holder.pictureView,listDeal.get(position).getMedias().get(0).getThumbnails_url().getImage_deal().toString()).execute();
                holder.pictureView.setVisibility(convertView.VISIBLE);
            }
         */
        return convertView;
    }

    @Override
    public int getCount() {
        return _objects.size();
    }

    private class DealHolder {
        public TextView title, message, username, date, price;
        public ImageView profile, dealImage, geoloc;
    }
}
