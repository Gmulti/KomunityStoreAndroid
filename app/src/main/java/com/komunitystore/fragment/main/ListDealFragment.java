package com.komunitystore.fragment.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.komunitystore.R;
import com.komunitystore.adapter.DealAdapter;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.model.KSErrorResponse;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class ListDealFragment extends KSFragment {

    private ListView _list;
    private DealAdapter _adapter;
    private ProgressBar _progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_deals_list, null);
        _list = (ListView) root.findViewById(R.id.list);
        _progress = (ProgressBar) root.findViewById(R.id.progress);
        getDeals();
        return root;
    }

    private void getDeals() {
        _progress.setVisibility(View.VISIBLE);
        _list.setVisibility(View.INVISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("limit", "30");
        NetworkManager.getInstance(getActivity()).getDeals(params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        _progress.setVisibility(View.GONE);
                        _list.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'zzzz").create();
                        ArrayList<Deal> deals = new ArrayList<Deal>();
                        for (int i = 0; i < response.length(); i++) {
                            deals.add(gson.fromJson(response.optJSONObject(i).toString(), Deal.class));
                        }
                        _adapter = new DealAdapter(getActivity(), deals);
                        _list.setAdapter(_adapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progress.setVisibility(View.GONE);
                        _list.setVisibility(View.VISIBLE);
                        KSErrorResponse errorResponse = new Gson().fromJson(error.getMessage(), KSErrorResponse.class);
                        if (errorResponse != null) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Erreur")
                                    .setMessage(errorResponse.getError_description())
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("An error has occured")
                                    .setMessage("Try again later")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        }
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
        return new KSActionBarButton(R.drawable.refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public KSActionBarButton getRightButton2() {
        return new KSActionBarButton(R.drawable.add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean shouldDisplayTabbar() {
        return true;
    }
}
