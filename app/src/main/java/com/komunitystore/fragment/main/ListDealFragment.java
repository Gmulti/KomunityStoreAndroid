package com.komunitystore.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.R;
import com.komunitystore.activity.MainActivity;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.adapter.DealAdapter;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.fragment.secondary.DealDetailsFragment;
import com.komunitystore.fragment.secondary.PostDealFragment;
import com.komunitystore.fragment.secondary.UserFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.ColorUtils;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;
import com.komunitystore.view.KSSearchView;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class ListDealFragment extends KSFragment implements KSSearchView.OnSearchViewAnimated {

    private View _rootView;

    private SwipeRefreshLayout _swipe;
    private ListView _list;
    private DealAdapter _adapter;
    private KSSearchView _search;
    private TextView _noDeal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_rootView == null) {
            _rootView = View.inflate(getActivity(), R.layout.fragment_deals_list, null);
            _list = (ListView) _rootView.findViewById(R.id.list);

            // Header + Footer
            View space = new View(getActivity());
            space.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            space.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Singleton.convertDpToPx(getActivity(), 10)));
            _list.addHeaderView(space);
            _list.addFooterView(space);
            _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    launchActivity(DealDetailsFragment.class, Singleton.getInstance().getDeals().get(position - 1).getId());
                }
            });
            _swipe = (SwipeRefreshLayout) _rootView.findViewById(R.id.swipe);
            _swipe.setColorSchemeColors(getResources().getColor(R.color.blue));
            _swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getDeals();
                }
            });
            _noDeal = (TextView) _rootView.findViewById(R.id.no_deal);
        }
        if (Singleton.getInstance().getDeals() != null) {
            if (_adapter != null || _adapter.getCount() > 0) {
                showDeals();
            }
        } else {
            getDeals();
        }
        return _rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        _search = ((MainActivity) getActivity()).getKSSearchView();
        _search.setOnSearchViewAnimatedListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(KSEvent event) {
        switch (event.getType()) {
            case FOLLOW_DEALS:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    showDeals();
                }
                break;
        }
    }

    private void showDeals() {

        _swipe.setRefreshing(false);
        _adapter = new DealAdapter(getActivity(), Singleton.getInstance().getDeals(), DealAdapter.Type.FULL);
        _list.setAdapter(_adapter);
        if (_adapter.getCount() == 0) {
            _noDeal.setVisibility(View.VISIBLE);
        }
    }

    private void getDeals() {
        _noDeal.setVisibility(View.GONE);
        _swipe.post(new Runnable() {
            @Override
            public void run() {
                _swipe.setRefreshing(true);
            }
        });
        Map<String, String> params = new HashMap<>();
        params.put("limit", "30");
        if (_adapter != null && _adapter.getItem(0) != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            String dateOffset = sdf.format(_adapter.getItem(0).getCreated());
            if (!TextUtils.isEmpty(dateOffset)) {
                dateOffset = dateOffset.replaceAll(" ", "%20");
                params.put("date_offset", dateOffset);
            }
        }
        NetworkManager.getInstance(getActivity()).getDeals(params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                        ArrayList<Deal> deals = new ArrayList<Deal>();
                        for (int i = 0; i < response.length(); i++) {
                            deals.add(gson.fromJson(response.optJSONObject(i).toString(), Deal.class));
                        }
                        Singleton.getInstance().setDeals(deals);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _swipe.setRefreshing(false);
                        if (isAdded()) {
                            showDeals();
                        }
                    }
                });
    }

    private void launchActivity(Class clazz, int id) {
        Intent intent = new Intent(getActivity(), SecondaryActivity.class);
        intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, clazz.getName());
        if (clazz == DealDetailsFragment.class) {
            intent.putExtra(SecondaryActivity.EXTRA_DEAL, id);
        } else if (clazz == UserFragment.class) {
            intent.putExtra(SecondaryActivity.EXTRA_USER, id);
        }
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Deals Line";
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return null;
    }

    @Override
    public KSActionBarButton getRightButton1() {
        return new KSActionBarButton(R.drawable.search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _search.expand(!_search.isExpanded());
            }
        });
    }

    @Override
    public KSActionBarButton getRightButton2() {
        return new KSActionBarButton(R.drawable.add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SecondaryActivity.class);
                intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, PostDealFragment.class.getName());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_from_bottom, R.anim.activity_fade_out);
            }
        });
    }

    @Override
    public boolean shouldDisplayTabbar() {
        return true;
    }

    @Override
    public boolean shouldDisplaySearchBar() {
        return true;
    }

    @Override
    public void onAnimated(float animationValue) {
        int white = getResources().getColor(android.R.color.white),
                red = getResources().getColor(R.color.red);
        ImageButton rightButton1 = ((MainActivity) getActivity()).getKSActionBar().getRightButton1();
        if (animationValue >= 0 && animationValue <= 1) {
            if (_search.isExpanded()) {
                rightButton1.setColorFilter(ColorUtils.getColor(red, white, animationValue));
                rightButton1.setBackgroundColor(ColorUtils.getColor(white, red, animationValue));
            } else {
                rightButton1.setColorFilter(ColorUtils.getColor(white, red, animationValue));
                rightButton1.setBackgroundColor(ColorUtils.getColor(red, white, animationValue));
            }
        }
        if (_search.isExpanded()) {
            animationValue = animationValue * -1;
        }
        rightButton1.setRotation(animationValue * 360);
    }
}
