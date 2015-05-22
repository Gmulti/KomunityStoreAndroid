package com.komunitystore.fragment.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.komunitystore.R;
import com.komunitystore.model.AccessToken;
import com.komunitystore.model.BaseResponse;
import com.komunitystore.model.KSErrorResponse;
import com.komunitystore.model.User;
import com.komunitystore.utils.KSEvent;
import com.komunitystore.utils.KSSharedPreferences;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class LoginFragment extends Fragment {

    private EditText _username, _password;
    private Button _login;

    private ProgressDialog _progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_login, null);
        _username = (EditText) root.findViewById(R.id.username);
        _password = (EditText) root.findViewById(R.id.password);
        _password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(_password.getWindowToken(), 0);
                    login();
                }
                return false;
            }
        });
        _login = (Button) root.findViewById(R.id.login);
        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        _progress = new ProgressDialog(getActivity());
        _progress.setCancelable(false);
        _progress.setTitle(R.string.loading_title);
        _progress.setMessage(getResources().getString(R.string.loading_message));
        return root;
    }

    public void onEventMainThread(KSEvent event) {
        switch (event.getType()) {
            case REGISTER:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    _username.setText(((User) event.getObject()).getUsername());
                    _password.setText("");
                }
                break;
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

    private void login() {
        _progress.show();
        NetworkManager.getInstance(getActivity()).login(_username.getText().toString(), _password.getText().toString(),
                new Response.Listener<AccessToken>() {
                    @Override
                    public void onResponse(AccessToken response) {
                        KSSharedPreferences.getInstance(getActivity()).setAccessToken(response);
                        NetworkManager.getInstance(getActivity()).getUserInfo(new Response.Listener<User>() {
                            @Override
                            public void onResponse(User response) {
                                _progress.dismiss();
                                Singleton.getInstance().setCurrentUser(response);
                                EventBus.getDefault().post(new KSEvent(KSEvent.Type.LOGIN, KSEvent.Error.NO_ERROR, null));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                _progress.dismiss();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progress.dismiss();
                    }
                });
    }
}
