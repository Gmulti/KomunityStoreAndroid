package com.komunitystore.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.komunitystore.R;
import com.komunitystore.model.Deal;
import com.komunitystore.utils.NetworkManager;
import com.komunitystore.utils.Singleton;

import java.text.ParseException;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class DealView extends FrameLayout {

    private ImageView _expand, _likeImage;
    private NetworkImageView _profileImage, _dealImage;
    private TextView _username, _date, _title, _desc, _likeCount;
    private ImageButton _share;
    private LinearLayout _likeButton;

    private float _marginTop;

    private boolean _expanded = true;
    private boolean _animated = false;

    public DealView(Context context) {
        super(context);
        init();
    }

    public DealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DealView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.view_deal, null);
        _expand = (ImageView) root.findViewById(R.id.expand);
        _profileImage = (NetworkImageView) root.findViewById(R.id.profile_image);
        _dealImage = (NetworkImageView) root.findViewById(R.id.deal_image);
        _username = (TextView) root.findViewById(R.id.profile_name);
        _date = (TextView) root.findViewById(R.id.deal_date);
        _title = (TextView) root.findViewById(R.id.deal_title);
        _desc = (TextView) root.findViewById(R.id.deal_desc);
        _share = (ImageButton) root.findViewById(R.id.share);
        _likeButton = (LinearLayout) root.findViewById(R.id.like_button);
        _likeCount = (TextView) root.findViewById(R.id.like_count);
        _likeImage = (ImageView) root.findViewById(R.id.like_image);
        addView(root);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        _marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        //setY(_marginTop);
    }

    public void setDeal(Deal deal) {
        _username.setText(deal.getUser().getUsername());
        try {
            _date.setText(deal.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        _title.setText(deal.getTitle());
        _desc.setText(deal.getContent());
        if (deal.getMedias().size() > 0 && deal.getMedias().get(0).getThumbnails_url() != null) {
            NetworkManager.getInstance(getContext()).getImage(_dealImage, deal.getMedias().get(0).getThumbnails_url().getImage_deal());
        } else {
            _dealImage.setImageResource(R.drawable.no_image);
        }
        if (deal.hasUserLike(Singleton.getInstance().getCurrentUser().getUsername())) {
            _likeButton.setBackgroundResource(R.drawable.background_button_blue);
            _likeCount.setTextColor(getResources().getColor(android.R.color.white));
            _likeImage.setColorFilter(getResources().getColor(android.R.color.white));
        } else {
            _likeButton.setBackgroundResource(R.drawable.background_button_border_blue);
            _likeCount.setTextColor(getResources().getColor(R.color.blue));
            _likeImage.setColorFilter(getResources().getColor(R.color.blue));
        }
        _likeCount.setText(String.valueOf(deal.getNb_users_likes()));
    }

    public void expand() {
        if (!_animated && !_expanded) {
            TranslateAnimation animation = new TranslateAnimation(0, 0, getY() + _marginTop, -_marginTop / 2);
            animation.setDuration(700);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    _animated = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    _animated = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startAnimation(animation);
            _expanded = true;
        }
    }

    public void reduce() {
        if (!_animated && _expanded) {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, getMeasuredHeight() - (getY() + _profileImage.getMeasuredHeight()));
            animation.setDuration(700);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    _animated = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    _animated = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            startAnimation(animation);
            _expanded = false;
        }
    }
}
