package com.komunitystore.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komunitystore.KSApp;
import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.adapter.UserAdapter;
import com.komunitystore.fragment.secondary.UserFragment;
import com.komunitystore.model.User;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by G3ck0z9 on 03/06/2015.
 */
public class UsersDialog extends Dialog {

    private JSONArray json;
    private ListView _list;
    private UserAdapter _adapter;

    public UsersDialog(Context context, JSONArray json) {
        super(context, R.style.Dialog);
        this.json = json;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        _list = new ListView(getContext());
        _list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        _list.setPadding(10, 10, 10, 10);
        _list.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));
        _list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchActivity(_adapter.getItem(position).getId());
            }
        });
        _list.setDivider(getContext().getResources().getDrawable(R.drawable.horizontal_divider));
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            users.add(gson.fromJson(json.optJSONObject(i).toString(), User.class));
        }
        _adapter = new UserAdapter(getContext(), users);
        _list.setAdapter(_adapter);
        setContentView(_list);
    }

    private void launchActivity(int id) {
        Activity activity = ((KSApp) getContext().getApplicationContext()).getCurrentActivity();
        Intent intent = new Intent(activity, SecondaryActivity.class);
        intent.putExtra(SecondaryActivity.EXTRA_FRAGMENT, UserFragment.class.getName());
        intent.putExtra(SecondaryActivity.EXTRA_USER, id);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_from_left, R.anim.activity_to_right);
    }
}
