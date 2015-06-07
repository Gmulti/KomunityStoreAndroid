package com.komunitystore.fragment.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.R;
import com.komunitystore.activity.MainActivity;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.adapter.DealAdapter;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.fragment.secondary.PostDealFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.model.User;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;
import com.komunitystore.view.KSActionBarButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class ProfileFragment extends KSFragment implements DealAdapter.OnDealDeletedListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 2;

    public String _imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_profile_image.jpg";

    private View _rootView;

    private User _user;

    private Bitmap _newImageProfile;

    private NetworkImageView _profileImage;
    private TextView _profileName, _followers, _subscribers, _deals;
    private ProgressBar _progress;
    private RelativeLayout _changeImage;
    private ListView _list;
    private DealAdapter _adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_rootView == null) {
            _user = Singleton.getInstance().getCurrentUser();
            _rootView = View.inflate(getActivity(), R.layout.fragment_profile, null);
            _profileImage = (NetworkImageView) _rootView.findViewById(R.id.profile_image);
            _profileName = (TextView) _rootView.findViewById(R.id.profile_name);
            _followers = (TextView) _rootView.findViewById(R.id.followers);
            _subscribers = (TextView) _rootView.findViewById(R.id.subscribers);
            _deals = (TextView) _rootView.findViewById(R.id.deals);
            _progress = (ProgressBar) _rootView.findViewById(R.id.progress);
            _changeImage = (RelativeLayout) _rootView.findViewById(R.id.profile_image_layout);
            _changeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            });
            _list = (ListView) _rootView.findViewById(R.id.list);
            configureView();
        }
        if (Singleton.getInstance().getMyDeals() != null) {
            if (_adapter == null || _adapter.getCount() == 0) {
                showDeals();
            }
        } else {
            getUserDeals();
        }
        return _rootView;
    }

    private void configureView() {
        _profileName.setText(_user.getUsername());
        _followers.setText(String.valueOf(_user.getNb_followers()));
        _subscribers.setText(String.valueOf(_user.getNb_subscribes()));
        _profileImage.setImageBitmap(null);
        _deals.setText(String.valueOf(_user.getNb_deals()));
        if (_user.getMedia_profile() != null && _user.getMedia_profile().getThumbnails_url() != null) {
            String imgUrl = _user.getMedia_profile().getThumbnails_url().getUser_profile_tile_large();
            if (!TextUtils.isEmpty(imgUrl)) {
                NetworkManager.getInstance(getActivity()).getImage(_profileImage, imgUrl);
            } else {
                _profileImage.setImageResource(R.drawable.no_image);
            }
        }
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

    public void uploadNewImage(Bitmap bitmap) {
        if (bitmap != null) {
            _newImageProfile = bitmap;
            final ProgressDialog progress = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
            NetworkManager.getInstance(getActivity()).changeUserImage(_newImageProfile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progress.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                        _user = gson.fromJson(json.toString(), User.class);
                        configureView();
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

    private Bitmap reduceBitmapSize(Bitmap bitmap) {
        if (bitmap.getHeight() >= 2048 || bitmap.getWidth() >= 2048) {
            int width = new Double(bitmap.getWidth() / 1.5).intValue();
            int height = new Double(bitmap.getHeight() / 1.5).intValue();
            bitmap = reduceBitmapSize(Bitmap.createScaledBitmap(bitmap, width, height, true));
        }
        return bitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bmp = BitmapFactory.decodeFile(_imagePath);
                uploadNewImage(bmp);
            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    uploadNewImage(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(KSEvent event) {
        switch (event.getType()) {
            case MY_DEALS:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    showDeals();
                }
                break;
        }
    }

    private void showDeals() {
        _adapter = new DealAdapter(getActivity(), Singleton.getInstance().getMyDeals(), DealAdapter.Type.REDUCE);
        _adapter.setOnDealDeletedListener(this);
        _list.setAdapter(_adapter);
    }

    @Override
    public void onDelete(Deal deal) {
        // TODO DELETE DEAL HEAR
    }


    private void getUserDeals() {
        _progress.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(_user.getId()));
        NetworkManager.getInstance(getActivity()).getDeals(params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                _progress.setVisibility(View.GONE);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'+0200'").create();
                ArrayList<Deal> userDeals = new ArrayList<Deal>();
                for (int i = 0; i < response.length(); i++) {
                    userDeals.add(gson.fromJson(response.optJSONObject(i).toString(), Deal.class));
                }
                Singleton.getInstance().setMyDeals(userDeals);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.my_profile);
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return null;
    }

    @Override
    public KSActionBarButton getRightButton1() {

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
    public KSActionBarButton getRightButton2() {
        return null;
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
