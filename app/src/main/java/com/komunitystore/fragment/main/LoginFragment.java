package com.komunitystore.fragment.main;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.komunitystore.R;
import com.komunitystore.utils.KSEvent;

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
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        _progress.setTitle("Loading");
        _progress.setMessage("Please wait");
        return root;
    }

    private void login() {
        _progress.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new KSEvent(KSEvent.Type.LOGIN, KSEvent.Error.NO_ERROR));
            }
        }, 2000);
    }

    public void onEventMainThread(KSEvent event){
        switch (event.getType()) {
            case LOGIN:
                if (event.getError() == KSEvent.Error.NO_ERROR) {
                    _progress.dismiss();
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
}
