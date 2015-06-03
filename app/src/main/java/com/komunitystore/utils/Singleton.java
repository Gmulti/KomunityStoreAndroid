package com.komunitystore.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.komunitystore.model.Deal;
import com.komunitystore.model.User;

import java.io.File;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Tanguy on 08/05/2015.
 */
public class Singleton {

    private static Singleton _instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (_instance == null) {
            _instance = new Singleton();
        }
        return _instance;
    }

    private User _currentUser;
    private List<Deal> _deals, _proximityDeals, _myDeals;

    public void destroy() {
        _currentUser = null;
        _deals = null;
        _proximityDeals = null;
        _myDeals = null;
    }

    public void setCurrentUser(User user) {
        _currentUser = user;
    }

    public User getCurrentUser() {
        return _currentUser;
    }

    public static int convertDpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public List<Deal> getDeals() {
        return _deals;
    }

    public void setDeals(List<Deal> deals) {
        if (_deals == null) {
            _deals = deals;
        } else {
            for (int i = 0; i < deals.size(); i++) {
                boolean added = false;
                for (int j = 0; j < _deals.size(); j++) {
                    if (_deals.get(j).getId() == deals.get(i).getId()) {
                        _deals.remove(j);
                        _deals.add(deals.get(i));
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    _deals.add(deals.get(i));
                }
            }
        }
        Collections.sort(_deals);
        EventBus.getDefault().post(new KSEvent(KSEvent.Type.FOLLOW_DEALS, KSEvent.Error.NO_ERROR, null));
    }

    public void replaceDeal(Deal deal) {
        //for (int i = 0; i < _deals.size(); i++) {
        //    if (_deals.get(i).getId() == deal.getId()) {
        //        _deals.remove(i);
        //        _deals.add(deal);
        //        Collections.sort(_deals);
        //        _dealsChanged = true;
        //        break;
        //    }
        //}
    }

    public List<Deal> getProximityDeals() {
        return _proximityDeals;
    }

    public void setproximityDeals(List<Deal> deals) {
        _proximityDeals = deals;
        EventBus.getDefault().post(new KSEvent(KSEvent.Type.PROXIMITY_DEALS, KSEvent.Error.NO_ERROR, null));
    }

    public List<Deal> getMyDeals() {
        return _myDeals;
    }

    public void setMyDeals(List<Deal> deals) {
        _myDeals = deals;
        EventBus.getDefault().post(new KSEvent(KSEvent.Type.MY_DEALS, KSEvent.Error.NO_ERROR, null));
    }


}
