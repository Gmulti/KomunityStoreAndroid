package com.komunitystore.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by Tanguy on 06/05/2015.
 */
public class SignUpFragment extends Fragment {

    private EditText _id, _username, _password;
    private Button _register;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_signup, null);
        _id = (EditText) root.findViewById(R.id.id);
        _username = (EditText) root.findViewById(R.id.username);
        _password = (EditText) root.findViewById(R.id.password);
        _password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        return root;
    }

    private void register() {

    }
}
