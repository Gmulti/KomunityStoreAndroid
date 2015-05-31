package com.komunitystore.fragment.secondary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.KSMultiPartRequest;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by G3ck0z9 on 22/05/2015.
 */
public class PostDealFragment extends KSFragment implements CompoundButton.OnCheckedChangeListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public String _imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_image.jpg";

    private EditText _name, _content, _price, _promoCode, _reduc;
    private RadioButton _currencyEuros, _currencyDollar, _typePromo, _typeReduction, _typeBonPlan, _reducEuros, _reducDollar, _reducPercent;
    private ImageView _dealPhoto;
    private Button _post, _takePhoto;
    private LinearLayout _typeBonPlanLayout, _typePromoLayout, _typeReductionLayout;

    private Bitmap _dealImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_post_deal, null);
        // TODO Missing Views in XML
        _name = (EditText) root.findViewById(R.id.deal_title);
        _content = (EditText) root.findViewById(R.id.deal_content);
        _price = (EditText) root.findViewById(R.id.deal_prix);
        _promoCode = (EditText) root.findViewById(R.id.code_promo_value);
        _reduc = (EditText) root.findViewById(R.id.reduc_value);
        _typeBonPlanLayout = (LinearLayout) root.findViewById(R.id.bon_plan_layout);
        _typePromoLayout = (LinearLayout) root.findViewById(R.id.code_promo_layout);
        _typeReductionLayout = (LinearLayout) root.findViewById(R.id.reduc_layout);
        _currencyEuros = (RadioButton) root.findViewById(R.id.deal_currency_euros);
        _currencyEuros.setOnCheckedChangeListener(this);
        _currencyDollar = (RadioButton) root.findViewById(R.id.deal_currency_dollars);
        _typePromo = (RadioButton) root.findViewById(R.id.deal_type_promo);
        _typePromo.setOnCheckedChangeListener(this);
        _typeReduction = (RadioButton) root.findViewById(R.id.deal_type_reduction);
        _typeReduction.setOnCheckedChangeListener(this);
        _typeBonPlan = (RadioButton) root.findViewById(R.id.deal_type_bon_plan);
        _typeBonPlan.setOnCheckedChangeListener(this);
        _reducEuros = (RadioButton) root.findViewById(R.id.deal_reduc_euros);
        _reducDollar = (RadioButton) root.findViewById(R.id.deal_reduc_dollar);
        _reducPercent = (RadioButton) root.findViewById(R.id.deal_reduc_percent);
        _dealPhoto = (ImageView) root.findViewById(R.id.deal_photo);
        _post = (Button) root.findViewById(R.id.post);
        _post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDeal();
            }
        });
        _takePhoto = (Button) root.findViewById(R.id.take_photo);
        _takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        _typeBonPlan.setChecked(true);
        _currencyEuros.setChecked(true);
        _reducEuros.setChecked(true);
        return root;
    }

    private void postDeal() {
        String title = _name.getText().toString(),
                content = _content.getText().toString(),
                price = _price.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            showFillInfoDialog();
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("title", title);
            params.put("content", content);
            String type = "";
            if (_typePromo.isChecked()) {
                type = "code-promo";
                if (TextUtils.isEmpty(_promoCode.getText())) {
                    showFillInfoDialog();
                    return;
                } else {
                    params.put("promoCode", _promoCode.getText().toString());
                }
            } else if (_typeReduction.isChecked()) {
                type = "reduction";
                if (TextUtils.isEmpty(_reduc.getText())) {
                    showFillInfoDialog();
                    return;
                } else {
                    params.put("reduction", _reduc.getText().toString());
                    params.put("reductionType", _reducPercent.isChecked() ? "pourcent" : "cash");
                }
            } else if (_typeBonPlan.isChecked()) {
                type = "bon-plan";
                if (TextUtils.isEmpty(_price.getText())) {
                    showFillInfoDialog();
                    return;
                } else {
                    params.put("price", _price.getText().toString());
                    params.put("currency", _currencyEuros.isChecked() ? "euro" : "dollar");
                }
            }
            params.put("type", type);
            ArrayList<Bitmap> images = new ArrayList<>();
            images.add(_dealImage);
            final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
            NetworkManager.getInstance(getActivity()).postDeal(params, images, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progress.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.deal_send_success_title)
                                .setMessage(null)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error_title)
                                .setMessage(R.string.error_message)
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
    }

    private void takePicture() {
        File img = new File(_imagePath);
        Uri uri = Uri.fromFile(img);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public void showPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            _dealImage = bitmap;
            _dealPhoto.setImageBitmap(reduceBitmapSize(bitmap));
            _dealPhoto.setVisibility(View.VISIBLE);
            _takePhoto.setVisibility(View.GONE);
        }
    }

    private Bitmap reduceBitmapSize(Bitmap bitmap) {
        if (bitmap.getHeight() >= 2048 || bitmap.getWidth() >= 2048) {
            int width = new Double(bitmap.getWidth() / 1.5).intValue();
            int height = new Double(bitmap.getHeight() / 1.5).intValue();
            bitmap = reduceBitmapSize(Bitmap.createScaledBitmap(bitmap, width, height, true));
        }
        return bitmap;
    }

    private void showFillInfoDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.fill_info_title)
                .setMessage(R.string.fill_info_message)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bmp = BitmapFactory.decodeFile(_imagePath);
                showPhoto(bmp);
            }
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
        return new KSActionBarButton(R.drawable.close, new View.OnClickListener() {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.equals(_typeBonPlan)) {
                _typeBonPlanLayout.setVisibility(View.VISIBLE);
                _typeReductionLayout.setVisibility(View.GONE);
                _typePromoLayout.setVisibility(View.GONE);
            } else if (buttonView.equals(_typePromo)) {
                _typeBonPlanLayout.setVisibility(View.GONE);
                _typeReductionLayout.setVisibility(View.GONE);
                _typePromoLayout.setVisibility(View.VISIBLE);
            } else if (buttonView.equals(_typeReduction)) {
                _typeBonPlanLayout.setVisibility(View.GONE);
                _typeReductionLayout.setVisibility(View.VISIBLE);
                _typePromoLayout.setVisibility(View.GONE);
            }
        }
    }
}
