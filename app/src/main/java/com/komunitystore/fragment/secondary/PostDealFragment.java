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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.dialog.FindAddressDialog;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.KSMultiPartRequest;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;
import com.komunitystore.view.KSCheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by G3ck0z9 on 22/05/2015.
 */
public class PostDealFragment extends KSFragment implements CompoundButton.OnCheckedChangeListener, KSCheckBox.OnSelectedChangeListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 2;

    public String _imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_image.jpg";

    private EditText _name, _content, _price, _promoCode, _reduc;
    private TextView _address;
    private KSCheckBox _typePromo, _typeReduction, _typeBonPlan;
    private RadioButton _currencyEuros, _currencyDollar, _reducEuros, _reducDollar, _reducPercent;
    private ImageView _dealPhoto;
    private Button _post;
    private ImageButton _takePhoto, _findAddress;
    private LinearLayout _typeBonPlanLayout, _typePromoLayout, _typeReductionLayout;

    private Bitmap _dealImage;
    private LatLng _dealPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_post_deal, null);
        // TODO Missing Views in XML
        _name = (EditText) root.findViewById(R.id.deal_title);
        _content = (EditText) root.findViewById(R.id.deal_content);
        _price = (EditText) root.findViewById(R.id.deal_prix);
        _promoCode = (EditText) root.findViewById(R.id.code_promo_value);
        _reduc = (EditText) root.findViewById(R.id.reduc_value);
        _address = (TextView) root.findViewById(R.id.address);
        _typeBonPlanLayout = (LinearLayout) root.findViewById(R.id.bon_plan_layout);
        _typePromoLayout = (LinearLayout) root.findViewById(R.id.code_promo_layout);
        _typeReductionLayout = (LinearLayout) root.findViewById(R.id.reduc_layout);
        _typePromo = (KSCheckBox) root.findViewById(R.id.deal_type_promo);
        _typePromo.setText(R.string.code_promo);
        _typePromo.setColors(R.color.red, R.color.dark_grey);
        _typePromo.setTextColor(android.R.color.black);
        _typePromo.setOnSelectedChangeListener(this);
        _typeReduction = (KSCheckBox) root.findViewById(R.id.deal_type_reduction);
        _typeReduction.setText(R.string.reduction);
        _typeReduction.setColors(R.color.red, R.color.dark_grey);
        _typeReduction.setTextColor(android.R.color.black);
        _typeReduction.setOnSelectedChangeListener(this);
        _typeBonPlan = (KSCheckBox) root.findViewById(R.id.deal_type_bon_plan);
        _typeBonPlan.setText(R.string.bon_plan);
        _typeBonPlan.setColors(R.color.red, R.color.dark_grey);
        _typeBonPlan.setTextColor(android.R.color.black);
        _typeBonPlan.setOnSelectedChangeListener(this);
        _currencyEuros = (RadioButton) root.findViewById(R.id.deal_currency_euros);
        _currencyEuros.setOnCheckedChangeListener(this);
        _currencyDollar = (RadioButton) root.findViewById(R.id.deal_currency_dollars);
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
        _takePhoto = (ImageButton) root.findViewById(R.id.take_photo);
        _takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        _findAddress = (ImageButton) root.findViewById(R.id.find_address);
        _findAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAddress();
            }
        });
        _typeBonPlan.setSelected(true);
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
            if (_typePromo.isSelected()) {
                type = "code-promo";
                if (TextUtils.isEmpty(_promoCode.getText())) {
                    showFillInfoDialog();
                    return;
                } else {
                    params.put("promoCode", _promoCode.getText().toString());
                }
            } else if (_typeReduction.isSelected()) {
                type = "reduction";
                if (TextUtils.isEmpty(_reduc.getText())) {
                    showFillInfoDialog();
                    return;
                } else {
                    params.put("reduction", _reduc.getText().toString());
                    params.put("reductionType", _reducPercent.isChecked() ? "pourcent" : "cash");
                }
            } else if (_typeBonPlan.isSelected()) {
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
            if (_dealPosition != null) {
                params.put("lat", String.valueOf(_dealPosition.latitude));
                params.put("lng", String.valueOf(_dealPosition.longitude));
            }
            ArrayList<Bitmap> images = new ArrayList<>();
            if (_dealImage != null) {
                images.add(_dealImage);
            }
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

    private void findAddress() {
        /*new AlertDialog.Builder(getActivity())
                .setPositiveButton("Here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            _dealPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        } else {
                            final ProgressDialog progress = ProgressDialog.show(getActivity(), null, getResources().getText(R.string.loading_message));
                            progress.setCancelable(false);
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    _dealPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                    locationManager.removeUpdates(this);
                                    progress.dismiss();

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
                            });
                        }
                    }
                })
                .setNegativeButton("Search an address", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create().show();*/
        FindAddressDialog findAddressDialog = new FindAddressDialog(getActivity());
        findAddressDialog.setOnLocationChoosedListener(new FindAddressDialog.OnLocationChoosedListener() {
            @Override
            public void onLocationChoosed(LatLng latlng, String display) {
                _address.setText(display);
                _dealPosition = latlng;
            }

            @Override
            public void onError() {
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
        });
        findAddressDialog.show();
    }

    private void takePicture() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.take_photo)
                .setPositiveButton(R.string.from_galery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
                    }
                })
                .setNegativeButton(R.string.from_photo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File img = new File(_imagePath);
                        Uri uri = Uri.fromFile(img);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                })
                .create().show();
    }

    public void showPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            _dealImage = bitmap;
            _dealPhoto.setImageBitmap(reduceBitmapSize(bitmap));
            _dealPhoto.setVisibility(View.VISIBLE);
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
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    showPhoto(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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
        }
    }

    @Override
    public void onSelectedChanged(KSCheckBox checkBox, boolean selected) {
        if (selected && checkBox.isSelected() != selected) {
            if (checkBox.equals(_typeBonPlan)) {
                _typeBonPlan.setSelected(selected);
                _typeBonPlanLayout.setVisibility(View.VISIBLE);
                _typeReductionLayout.setVisibility(View.GONE);
                _typePromoLayout.setVisibility(View.GONE);
                _typePromo.setSelected(false);
                _typeReduction.setSelected(false);
            } else if (checkBox.equals(_typePromo)) {
                _typePromo.setSelected(selected);
                _typeBonPlanLayout.setVisibility(View.GONE);
                _typeReductionLayout.setVisibility(View.GONE);
                _typePromoLayout.setVisibility(View.VISIBLE);
                _typeBonPlan.setSelected(false);
                _typeReduction.setSelected(false);
            } else if (checkBox.equals(_typeReduction)) {
                _typeReduction.setSelected(selected);
                _typeBonPlanLayout.setVisibility(View.GONE);
                _typeReductionLayout.setVisibility(View.VISIBLE);
                _typePromoLayout.setVisibility(View.GONE);
                _typeBonPlan.setSelected(false);
                _typePromo.setSelected(false);

            }
        }
    }
}
