package com.komunitystore.fragment.secondary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class DealDetailsFragment extends KSFragment {

    private int _dealId = -1;
    private Deal _deal;

    private ImageView _likeImage;
    private NetworkImageView _profileImage, _dealImage;
    private TextView _username, _date, _title, _desc, _likeCount;
    private ImageButton _share;
    private LinearLayout _likeButton;
    private RelativeLayout _dealImageLayout;

    private FloatingActionButton _go;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            _dealId = bundle.getInt(SecondaryActivity.EXTRA_DEAL);
        }
        View root = View.inflate(getActivity(), R.layout.fragment_deal_details, null);
        _profileImage = (NetworkImageView) root.findViewById(R.id.profile_image);
        _dealImage = (NetworkImageView) root.findViewById(R.id.deal_image);
        _username = (TextView) root.findViewById(R.id.profile_name);
        _date = (TextView) root.findViewById(R.id.deal_date);
        _title = (TextView) root.findViewById(R.id.deal_title);
        _desc = (TextView) root.findViewById(R.id.deal_desc);
        _share = (ImageButton) root.findViewById(R.id.share);
        _dealImageLayout = (RelativeLayout) root.findViewById(R.id.deal_image_layout);
        _likeButton = (LinearLayout) root.findViewById(R.id.like_button);
        _likeCount = (TextView) root.findViewById(R.id.like_count);
        _likeImage = (ImageView) root.findViewById(R.id.like_image);
        _go = (FloatingActionButton) root.findViewById(R.id.fab);
        getDeal();
        return root;
    }

    private void getDeal() {
        final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getText(R.string.loading_title), getResources().getText(R.string.loading_message));
        NetworkManager.getInstance(getActivity()).getDeal(_dealId, new Response.Listener<Deal>() {
            @Override
            public void onResponse(Deal response) {
                progress.dismiss();
                _deal = response;
                configureView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        });
    }

    private void configureView() {
        _username.setText(_deal.getUser().getUsername());
        _date.setText(_deal.getDate());
        _title.setText(_deal.getTitle());
        _desc.setText(_deal.getContent());
        if (_deal.getUser().getMedia_profile() != null && _deal.getUser().getMedia_profile().getThumbnails_url() != null) {
            String imgUrl = _deal.getUser().getMedia_profile().getThumbnails_url().getUser_profile_tile_large();
            if (!TextUtils.isEmpty(imgUrl)) {
                NetworkManager.getInstance(getActivity()).getImage(_profileImage, imgUrl);
            } else {
                _profileImage.setImageResource(R.drawable.no_image);
            }
        }
        if (_deal.isGeoloc()) {
            _go.setVisibility(View.VISIBLE);
        } else {
            _go.setVisibility(View.GONE);
        }
        if (_deal.getMedias().size() > 0 && _deal.getMedias().get(0).getThumbnails_url() != null && !TextUtils.isEmpty(_deal.getMedias().get(0).getThumbnails_url().getImage_deal_large())) {
            _dealImageLayout.setVisibility(View.VISIBLE);
            NetworkManager.getInstance(getActivity()).getImage(_dealImage, _deal.getMedias().get(0).getThumbnails_url().getImage_deal_large());
        } else {
            _dealImageLayout.setVisibility(View.GONE);
        }
        if (_deal.hasUserLike(Singleton.getInstance().getCurrentUser().getUsername())) {
            _likeButton.setBackgroundResource(R.drawable.background_button_blue);
            _likeCount.setTextColor(getResources().getColor(android.R.color.white));
            _likeImage.setColorFilter(getResources().getColor(android.R.color.white));
            _likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
                    NetworkManager.getInstance(getActivity()).changeLikeDeal(false, _deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            progress.dismiss();
                            _deal = response;
                            getDeal();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progress.dismiss();
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
                    final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
                    NetworkManager.getInstance(getActivity()).changeLikeDeal(true, _deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            progress.dismiss();
                            _deal = response;
                            getDeal();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progress.dismiss();
                        }
                    });
                }
            });
        }
        if (_deal.isShared()) {
            _share.setBackgroundResource(R.drawable.background_button_red);
            _share.setColorFilter(getResources().getColor(android.R.color.white));
            _share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
                    NetworkManager.getInstance(getActivity()).shareDeal(false, _deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            progress.dismiss();
                            _deal = response;
                            getDeal();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progress.dismiss();
                        }
                    });
                }
            });
        } else {
            _share.setBackgroundResource(R.drawable.background_button_border_red);
            _share.setColorFilter(getResources().getColor(R.color.red));
            _share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
                    NetworkManager.getInstance(getActivity()).shareDeal(true, _deal, new Response.Listener<Deal>() {
                        @Override
                        public void onResponse(Deal response) {
                            progress.dismiss();
                            _deal = response;
                            getDeal();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progress.dismiss();
                        }
                    });
                }
            });
        }
        _likeCount.setText(String.valueOf(_deal.getUserLikesCount()));
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
