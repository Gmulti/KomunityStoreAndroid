package com.komunitystore.fragment.secondary;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.komunitystore.R;
import com.komunitystore.activity.SecondaryActivity;
import com.komunitystore.fragment.KSFragment;
import com.komunitystore.model.Deal;
import com.komunitystore.view.DealView;
import com.komunitystore.view.KSActionBarButton;

/**
 * Created by Tanguy on 09/05/2015.
 */
public class DealDetailsFragment extends KSFragment {

    private Deal deal;
    private DealView _dealView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            deal = (Deal) bundle.getSerializable(SecondaryActivity.EXTRA_DEAL);
        }
        View root = View.inflate(getActivity(), R.layout.fragment_deal_details, null);
        _dealView = (DealView) root.findViewById(R.id.deal_view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (deal.getLat() == 0.0 && deal.getLng() == 0.0) {
            params.setMargins(0, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics())), 0, 0);
        } else {
            params.setMargins(0, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics())), 0, 0);
        }
        _dealView.setLayoutParams(params);
        _dealView.setDeal(deal);
        return root;
    }

    @Override
    public boolean shouldDisplayActionBar() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Deal";
    }

    @Override
    public KSActionBarButton getLeftButton() {
        return new KSActionBarButton(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
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
}
