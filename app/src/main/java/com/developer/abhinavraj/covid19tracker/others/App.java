package com.developer.abhinavraj.covid19tracker.others;

import android.app.Application;
import com.applivery.applvsdklib.Applivery;
import com.developer.abhinavraj.covid19tracker.R;
import com.google.android.gms.ads.MobileAds;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        Applivery.init(this, "zZP0lv12VeTT4nuy0-yy_KBl", false);
    }
}