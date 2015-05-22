package com.komunitystore.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import com.komunitystore.fragment.secondary.PostDealFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.ColorUtils;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;
import com.komunitystore.view.KSSearchView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class ListDealFragment extends KSFragment implements KSSearchView.OnSearchViewAnimated {

    private ListView _list;
    private DealAdapter _adapter;
    private ProgressBar _progress;
    private ImageButton _refresh;

    private KSSearchView _search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_deals_list, null);
        _list = (ListView) root.findViewById(R.id.list);

        // Header + Footer
        View space = new View(getActivity());
        space.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        space.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Singleton.convertDpToPx(getActivity(), 15)));
        _list.addHeaderView(space);
        _list.addFooterView(space);

        _progress = (ProgressBar) root.findViewById(R.id.progress);
        _refresh = (ImageButton) root.findViewById(R.id.refresh);
        _refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeals();
            }
        });
        getDeals();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        _search = ((MainActivity) getActivity()).getKSSearchView();
        _search.setOnSearchViewAnimatedListener(this);
    }

    private void getDeals() {
        _progress.setVisibility(View.VISIBLE);
        _refresh.setVisibility(View.GONE);
        _list.setVisibility(View.GONE);
        Map<String, String> params = new HashMap<>();
        params.put("limit", "30");
        NetworkManager.getInstance(getActivity()).getDeals(params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        _progress.setVisibility(View.GONE);
                        _refresh.setVisibility(View.GONE);
                        _list.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'zzzz").create();
                        ArrayList<Deal> deals = new ArrayList<Deal>();
                        for (int i = 0; i < response.length(); i++) {
                            deals.add(gson.fromJson(response.optJSONObject(i).toString(), Deal.class));
                        }
                        Singleton.getInstance()._deals = deals;
                        _adapter = new DealAdapter(getActivity(), deals);
                        _list.setAdapter(_adapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progress.setVisibility(View.GONE);
                        _refresh.setVisibility(View.VISIBLE);
                        _list.setVisibility(View.GONE);
                    }
                });
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
        return new KSActionBarButton(R.drawable.logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
