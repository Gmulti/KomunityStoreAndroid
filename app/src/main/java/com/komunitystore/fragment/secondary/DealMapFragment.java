package com.komunitystore.fragment.secondary;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.view.KSActionBarButton;

/**
 * Created by G3ck0z9 on 24/06/2015.
 */
public class DealMapFragment extends KSFragment {

    private MapView _mapView;
    private GoogleMap _map;

    private Deal _deal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_map, null);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(SecondaryActivity.EXTRA_DEAL)) {
            _deal = (Deal) bundle.getSerializable(SecondaryActivity.EXTRA_DEAL);
            _mapView = (MapView) root.findViewById(R.id.map_view);
            _mapView.onCreate(savedInstanceState);
            _mapView.onResume();
            MapsInitializer.initialize(getActivity().getApplicationContext());
            _map = _mapView.getMap();
            _map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });
            _map.clear();
            _map.setMyLocationEnabled(true);
            Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            markerIcon = Bitmap.createScaledBitmap(markerIcon, markerIcon.getWidth() / 2, markerIcon.getHeight() / 2, false);
            Marker marker = _map.addMarker(new MarkerOptions()
                    .position(new LatLng(_deal.getLat(), _deal.getLng()))
                    .title(_deal.getTitle())
                    .snippet(_deal.getStringPrice())
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_deal.getLat(), _deal.getLng()), 15));
            _map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error_title)
                    .setMessage(R.string.error_message)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .create().show();
        }
        return root;
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Deal Map (Change title ! ! !)";
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
