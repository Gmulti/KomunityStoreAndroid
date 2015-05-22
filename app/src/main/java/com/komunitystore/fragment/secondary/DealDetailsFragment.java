package com.komunitystore.fragment.secondary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.view.KSActionBarButton;
import com.melnykov.fab.FloatingActionButton;

import java.text.ParseException;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class DealDetailsFragment extends KSFragment {

    private Deal _deal;

    private ImageView _likeImage;
    private NetworkImageView _profileImage, _dealImage;
    private TextView _username, _date, _title, _desc, _likeCount;
    private ImageButton _share;
    private LinearLayout _likeButton;

    private FloatingActionButton _go;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            _deal = (Deal) bundle.getSerializable(SecondaryActivity.EXTRA_DEAL);
        }
        View root = View.inflate(getActivity(), R.layout.fragment_deal_details, null);
        _profileImage = (NetworkImageView) root.findViewById(R.id.profile_image);
        _dealImage = (NetworkImageView) root.findViewById(R.id.deal_image);
        _username = (TextView) root.findViewById(R.id.profile_name);
        _date = (TextView) root.findViewById(R.id.deal_date);
        _title = (TextView) root.findViewById(R.id.deal_title);
        _desc = (TextView) root.findViewById(R.id.deal_desc);
        _share = (ImageButton) root.findViewById(R.id.share);
        _likeButton = (LinearLayout) root.findViewById(R.id.like_button);
        _likeCount = (TextView) root.findViewById(R.id.like_count);
        _likeImage = (ImageView) root.findViewById(R.id.like_image);
        _go = (FloatingActionButton) root.findViewById(R.id.fab);
        configureView();
        return root;
    }

    private void configureView() {
        _username.setText(_deal.getUser().getUsername());
        try {
            _date.setText(_deal.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        _title.setText(_deal.getTitle());
        _desc.setText(_deal.getContent());
        if (_deal.isGeoloc()) {
            _go.setVisibility(View.VISIBLE);
        } else {
            _go.setVisibility(View.GONE);
        }
        if (_deal.getMedias().size() > 0 && _deal.getMedias().get(0).getThumbnails_url() != null) {
            NetworkManager.getInstance(getActivity()).getImage(_dealImage, _deal.getMedias().get(0).getThumbnails_url().getImage_deal());
        } else {
            _dealImage.setImageResource(R.drawable.no_image);
        }
        if (_deal.isLiked()) {
            _likeButton.setBackgroundResource(R.drawable.background_button_blue);
            _likeCount.setTextColor(getResources().getColor(android.R.color.white));
            _likeImage.setColorFilter(getResources().getColor(android.R.color.white));
            _likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkManager.getInstance(getActivity()).changeLikeDeal(false, _deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            _deal = response;
                            configureView();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                }
            });
        } else {
            _likeButton.setBackgroundResource(R.drawable.background_button_border_blue);
            _likeCount.setTextColor(getResources().getColor(R.color.blue));
            _likeImage.setColorFilter(getResources().getColor(R.color.blue));
            _likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkManager.getInstance(getActivity()).changeLikeDeal(true, _deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            _deal = response;
                            configureView();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                }
            });
        }
        if (_deal.isShared()) {
            _share.setBackgroundResource(R.drawable.background_button_red);
            _share.setColorFilter(getResources().getColor(android.R.color.white));
            _share.setOnClickListener(null);
        } else {
            _share.setBackgroundResource(R.drawable.background_button_border_red);
            _share.setColorFilter(getResources().getColor(R.color.red));
            _share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkManager.getInstance(getActivity()).shareDeal(_deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            _deal = response;
                            configureView();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                }
            });
        }
        _likeCount.setText(String.valueOf(_deal.getNb_users_likes()));
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Deal";
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return new KSActionBarButton(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public KSActionBarButton getRightButton1() {
        return null;
    }

    @Override
    public KSActionBarButton getRightButton2() {
        return null;
    }

    @Override
    public boolean shouldDisplayTabbar() {
        return false;
    }

    @Override
    public boolean shouldDisplaySearchBar() {
        return false;
    }
}
