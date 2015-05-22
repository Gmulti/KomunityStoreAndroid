package com.komunitystore.fragment.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class SignUpFragment extends Fragment {

    private EditText _email, _username, _password;
    private Button _register;

    private ProgressDialog _progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_signup, null);
        _email = (EditText) root.findViewById(R.id.email);
        _username = (EditText) root.findViewById(R.id.username);
        _password = (EditText) root.findViewById(R.id.password);
        _password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(_password.getWindowToken(), 0);
                    register();
                }
                return false;
            }
        });
        _register = (Button) root.findViewById(R.id.register);
        _register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        _progress = new ProgressDialog(getActivity());
        _progress.setCancelable(false);
        _progress.setTitle(R.string.loading_title);
        _progress.setMessage(getResources().getString(R.string.loading_message));
        return root;
    }

    private void register() {
        _progress.show();
        NetworkManager.getInstance(getActivity()).register(_email.getText().toString(), _username.getText().toString(), _password.getText().toString(),
                new Response.Listener<User>() {
                    @Override
                    public void onResponse(final User response) {
                        _progress.dismiss();
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Utilisateur cree")
                                .setMessage("Appuyer sur OK pour rejoindre la page de connexion")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        EventBus.getDefault().post(new KSEvent(KSEvent.Type.REGISTER, KSEvent.Error.NO_ERROR, response));
                                    }
                                })
                                .create().show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progress.dismiss();
                    }
                });
    }
}
