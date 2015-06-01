package com.komunitystore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.komunitystore.R;
import com.komunitystore.utils.NetworkManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by G3ck0z9 on 01/06/2015.
 */
public class FindAddressDialog extends Dialog {

    private EditText _address;
    private ListView _list;
    private ProgressBar _progress;
    private ImageButton _search;

    private OnLocationChoosedListener _listener;

    private AddressAdapter _adapter;

    public FindAddressDialog(Context context) {
        super(context, R.style.Dialog);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        View view = View.inflate(getContext(), R.layout.dialog_find_address, null);
        _address = (EditText) view.findViewById(R.id.address);
        _search = (ImageButton) view.findViewById(R.id.search);
        _search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddresses();
            }
        });
        _progress = (ProgressBar) view.findViewById(R.id.progress);
        _list = (ListView) view.findViewById(R.id.address_list);
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (_listener != null) {
                    JSONObject json = _adapter.getItem(position);
                    JSONObject location = json.optJSONObject("geometry").optJSONObject("location");
                    if (json != null) {
                        _listener.onLocationChoosed(new LatLng(location.optDouble("lat"), location.optDouble("lng")), json.optString("formatted_address", ""));
                    } else {
                        _listener.onError();
                    }
                    dismiss();
                }
            }
        });
        setContentView(view);
    }

    private void searchAddresses() {
        _address.setError(null);
        if (!TextUtils.isEmpty(_address.getText().toString())) {
            _progress.setVisibility(View.VISIBLE);
            NetworkManager.getInstance(getContext()).getLocationsFromAddress(_address.getText().toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    _progress.setVisibility(View.GONE);
                    _adapter = new AddressAdapter(getContext(), response.optJSONArray("results"));
                    _list.setAdapter(_adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    _progress.setVisibility(View.GONE);
                }
            });
        } else {
            _address.setError(getContext().getText(R.string.empty));
        }
    }

    public void setOnLocationChoosedListener(OnLocationChoosedListener listener) {
        _listener = listener;
    }

    public interface OnLocationChoosedListener {
        void onLocationChoosed(LatLng latlng, String display);

        void onError();
    }

    private class AddressAdapter extends ArrayAdapter<JSONObject> {

        private JSONArray _array;

        public AddressAdapter(Context context, JSONArray array) {
            super(context, android.R.layout.simple_list_item_1);
            _array = array;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject json = _array.optJSONObject(position);
            if (convertView == null) {
                convertView = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
            }
            TextView textview = (TextView) convertView.findViewById(android.R.id.text1);
            textview.setTextColor(getContext().getResources().getColor(android.R.color.black));
            textview.setText(json.optString("formatted_address", ""));
            return convertView;
        }

        @Override
        public int getCount() {
            if (_array == null) {
                return 0;
            }
            return _array.length();
        }

        @Override
        public JSONObject getItem(int position) {
            return _array.optJSONObject(position);
        }
    }
}
