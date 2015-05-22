package com.komunitystore.fragment.secondary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.komunitystore.R;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by G3ck0z9 on 22/05/2015.
 */
public class PostDealFragment extends KSFragment {

    private EditText _name, _content, _price;
    private RadioButton _currencyEuros, _currencyDollar, _typePromo, _typeReduction, _typeBonPlan;
    private Button _post;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_post_deal, null);
        // TODO Missing Views in XML
        _name = (EditText) root.findViewById(R.id.deal_title);
        _content = (EditText) root.findViewById(R.id.deal_content);
        _price = (EditText) root.findViewById(R.id.deal_prix);
        _currencyEuros = (RadioButton) root.findViewById(R.id.deal_currency_euros);
        _currencyDollar = (RadioButton) root.findViewById(R.id.deal_currency_dollars);
        _typePromo = (RadioButton) root.findViewById(R.id.deal_type_promo);
        _typeReduction = (RadioButton) root.findViewById(R.id.deal_type_reduction);
        _typeBonPlan = (RadioButton) root.findViewById(R.id.deal_type_bon_plan);
        _post = (Button) root.findViewById(R.id.post);
        _post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDeal();
            }
        });
        return root;
    }

    private void postDeal() {
        // TODO Random crash + 500 Internal Server Error
        String title = _name.getText().toString(),
                content = _content.getText().toString(),
                price = _price.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(price)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("")
                    .setMessage("")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else if (!_typePromo.isChecked() && !_typeReduction.isChecked() && !_typeBonPlan.isChecked()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("")
                    .setMessage("")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("title", title);
            params.put("content", content);
            params.put("price", price);
            params.put("currency", _currencyEuros.isChecked() ? "euro" : "dollar");
            String type = "";
            if (_typePromo.isChecked()) {
                type = "come-promo";
            } else if (_typeReduction.isChecked()) {
                type = "reduction";
            } else if (_typeBonPlan.isChecked()) {
                type = "bon-plan";
            }
            params.put("type", type);
            NetworkManager.getInstance(getActivity()).postDeal(params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("DEBUG", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
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
        return getResources().getString(R.string.post_a_deal);
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return new KSActionBarButton(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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
