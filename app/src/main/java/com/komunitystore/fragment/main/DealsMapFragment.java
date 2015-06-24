package com.komunitystore.fragment.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.fragment.secondary.DealDetailsFragment;
import com.komunitystore.fragment.secondary.PostDealFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by G3ck0z9 on 29/05/2015.
 */
public class DealsMapFragment extends KSFragment implements LocationListener {

    private View _rootView;

    private LatLng _latLng;
    private Map<Marker, Deal> _markers;
    private MapView _mapView;
    private GoogleMap _map;
    private LocationManager _locationManager;

    private boolean located = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_map, container, false);
            _mapView = (MapView) _rootView.findViewById(R.id.map_view);
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
            _map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Deal deal = _markers.get(marker);
                    if (deal != null) {
                        Intent intent = new Intent(getActivity(), SecondaryActivity.class);
                        intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, DealDetailsFragment.class.getName());
                        intent.putExtra(SecondaryActivity.EXTRA_DEAL, deal.getId());
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
                    }
                }
            });
            _map.clear();
        }
        _map.setMyLocationEnabled(true);
        return _rootView;
    }

    private void setPosition(Location location) {
        if (location != null) {
            located = false;
            _latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        if (!located && _latLng != null) {
            located = true;
            _map.moveCamera(CameraUpdateFactory.newLatLng(_latLng));
            _map.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
        getDeals();
    }

    private void getDeals() {
        if (_latLng == null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.fail_location_title)
                    .setMessage(R.string.fail_location_message)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            _map.clear();
            Crouton.cancelAllCroutons();
            if (isAdded()) {
                Crouton.makeText(getActivity(), R.string.loading_title, Style.ALERT, _mapView).show();
                Map<String, String> params = new HashMap<>();
                params.put("limit", "30");
                params.put("lat", String.valueOf(_latLng.latitude));
                params.put("lng", String.valueOf(_latLng.longitude));
                params.put("distance", "10000");
                NetworkManager.getInstance(getActivity()).searchDeals(params, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                        ArrayList<Deal> deals = new ArrayList<Deal>();
                        for (int i = 0; i < response.length(); i++) {
                            deals.add(gson.fromJson(response.optJSONObject(i).toString(), Deal.class));
                        }
                        Singleton.getInstance().setproximityDeals(deals);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Crouton.cancelAllCroutons();
                    }
                });
            }
        }
    }

    public void onEventMainThread(KSEvent event) {
        switch (event.getType()) {
            case PROXIMITY_DEALS:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    showDeals();
                }
                break;
        }
    }

    private void showDeals() {
        Crouton.cancelAllCroutons();
        Crouton.makeText(getActivity(), R.string.success, Style.CONFIRM, _mapView).show();
        _markers = new HashMap<>();
        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        markerIcon = Bitmap.createScaledBitmap(markerIcon, markerIcon.getWidth() / 2, markerIcon.getHeight() / 2, false);
        for (Deal deal : Singleton.getInstance().getProximityDeals()) {
            Marker marker = _map.addMarker(new MarkerOptions()
                    .position(new LatLng(deal.getLat(), deal.getLng()))
                    .title(deal.getTitle())
                    .snippet(deal.getStringPrice())
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));
            _markers.put(marker, deal);
        }
    }

    private void buildAlertMessageNoGps() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.map_no_gps_title)
                .setMessage(R.string.map_no_gps_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void onLocationChanged(Location location) {
        setPosition(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        _locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 1000, this);
        _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000, 5000, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        _locationManager.removeUpdates(this);
        _locationManager = null;
        if (_map != null) {
            _map.setMyLocationEnabled(false);
        }
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.map);
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return null;
    }

    @Override
    public KSActionBarButton getRightButton1() {
        return new KSActionBarButton(R.drawable.refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(null);
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
        return false;
    }
}