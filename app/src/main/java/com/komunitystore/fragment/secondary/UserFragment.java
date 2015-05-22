package com.komunitystore.fragment.secondary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.model.User;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.view.KSActionBarButton;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class UserFragment extends KSFragment {

    private User _user;

    private NetworkImageView _profileImage;
    private TextView _profileName, _followers, _subscribers, _deals;
    private Button _follow;
    private ListView _list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            _user = (User) bundle.getSerializable(SecondaryActivity.EXTRA_USER);
        }
        View root = View.inflate(getActivity(), R.layout.fragment_user, null);
        _profileImage = (NetworkImageView) root.findViewById(R.id.profile_image);
        _profileName = (TextView) root.findViewById(R.id.profile_name);
        _followers = (TextView) root.findViewById(R.id.followers);
        _subscribers = (TextView) root.findViewById(R.id.subscribers);
        _deals = (TextView) root.findViewById(R.id.deals);
        _follow = (Button) root.findViewById(R.id.follow_button);
        _list = (ListView) root.findViewById(R.id.list);
        configureView();
        return root;
    }

    private void configureView() {
        _profileName.setText(_user.getUsername());
        _followers.setText(String.valueOf(_user.getNb_followers()));
        _subscribers.setText(String.valueOf(_user.getNb_subscribes()));
        _deals.setText(String.valueOf(_user.getNb_deals()));
        if (_user.isFollowed()) {
            _follow.setText(R.string.followed);
            _follow.setBackgroundResource(R.drawable.background_button_red);
            _follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkManager.getInstance(getActivity()).changeFollowUser(false, _user, new Response.Listener<User>() {
                        @Override
                        public void onResponse(User response) {
                            _user = response;
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
            _follow.setText(R.string.follow);
            _follow.setBackgroundResource(R.drawable.background_button_border_red);
            _follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkManager.getInstance(getActivity()).changeFollowUser(true, _user, new Response.Listener<User>() {
                        @Override
                        public void onResponse(User response) {
                            _user = response;
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
