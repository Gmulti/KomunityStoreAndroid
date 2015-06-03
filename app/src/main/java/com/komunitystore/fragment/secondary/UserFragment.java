package com.komunitystore.fragment.secondary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.adapter.DealAdapter;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.model.User;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class UserFragment extends KSFragment {

    private int _userId;
    private User _user;

    private ArrayList<Deal> _userDeals;

    private NetworkImageView _profileImage;
    private TextView _profileName, _followers, _subscribers, _deals;
    private Button _follow;
    private ProgressBar _progress;
    private ListView _list;
    private DealAdapter _adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            _userId = bundle.getInt(SecondaryActivity.EXTRA_USER);
        }
        View root = View.inflate(getActivity(), R.layout.fragment_user, null);
        _profileImage = (NetworkImageView) root.findViewById(R.id.profile_image);
        _profileName = (TextView) root.findViewById(R.id.profile_name);
        _followers = (TextView) root.findViewById(R.id.followers);
        _subscribers = (TextView) root.findViewById(R.id.subscribers);
        _deals = (TextView) root.findViewById(R.id.deals);
        _follow = (Button) root.findViewById(R.id.follow_button);
        _progress = (ProgressBar) root.findViewById(R.id.progress);
        _list = (ListView) root.findViewById(R.id.list);
        getUser();
        return root;
    }

    private void getUser() {
        final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getText(R.string.loading_title), getResources().getText(R.string.loading_message));
        NetworkManager.getInstance(getActivity()).getUser(_userId, new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                progress.dismiss();
                _user = response;
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
        _profileName.setText(_user.getUsername());
        _followers.setText(String.valueOf(_user.getNb_followers()));
        _subscribers.setText(String.valueOf(_user.getNb_subscribes()));
        _deals.setText(String.valueOf(_user.getNb_deals()));
        if (_user.getId() == Singleton.getInstance().getCurrentUser().getId()) {
            _follow.setVisibility(View.GONE);
        } else {
            _follow.setVisibility(View.VISIBLE);
            if (_user.isFollowed()) {
                _follow.setText(R.string.followed);
                _follow.setTextColor(getResources().getColor(android.R.color.white));
                _follow.setBackgroundResource(R.drawable.background_button_red);
                _follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
                        NetworkManager.getInstance(getActivity()).changeFollowUser(false, _user, new Response.Listener<User>() {
                            @Override
                            public void onResponse(User response) {
                                progress.dismiss();
                                _user = response;
                                getUser();
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
                _follow.setText(R.string.follow);
                _follow.setTextColor(getResources().getColor(R.color.red));
                _follow.setBackgroundResource(R.drawable.background_button_border_red);
                _follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
                        NetworkManager.getInstance(getActivity()).changeFollowUser(true, _user, new Response.Listener<User>() {
                            @Override
                            public void onResponse(User response) {
                                progress.dismiss();
                                _user = response;
                                getUser();
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
        }
        if (_user.getMedia_profile() != null && _user.getMedia_profile().getThumbnails_url() != null) {
            String imgUrl = _user.getMedia_profile().getThumbnails_url().getUser_profile_tile_large();
            if (!TextUtils.isEmpty(imgUrl)) {
                NetworkManager.getInstance(getActivity()).getImage(_profileImage, imgUrl);
            }
        }
        getUserDeals();
    }

    private void getUserDeals() {
        _progress.setVisibility(View.VISIBLE);
        _list.setVisibility(View.GONE);
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(_user.getId()));
        NetworkManager.getInstance(getActivity()).getDeals(params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                _progress.setVisibility(View.GONE);
                _list.setVisibility(View.VISIBLE);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                _userDeals = new ArrayList<Deal>();
                for (int i = 0; i < response.length(); i++) {
                    _userDeals.add(gson.fromJson(response.optJSONObject(i).toString(), Deal.class));
                }
                _adapter = new DealAdapter(getActivity(), _userDeals, DealAdapter.Type.REDUCE);
                _list.setAdapter(_adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                _progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.profile);
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
