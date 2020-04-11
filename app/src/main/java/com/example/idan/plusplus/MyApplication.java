package com.example.idan.plusplus;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.example.idan.plusplus.Classes.LocaleManager;
import com.example.idan.plusplus.V2.Services.Retrofit2Services.DaggerIRetrofitServices;
import com.example.idan.plusplus.V2.Services.Retrofit2Services.IRetrofitServices;

public class MyApplication extends Application {
    public static LocaleManager localeManager;
    private static IRetrofitServices sRetrofitServices;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.setAppContext(getApplicationContext());
        sRetrofitServices = DaggerIRetrofitServices.create();
    }

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        localeManager.setNewLocale(base,"iw");
        super.attachBaseContext(localeManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
    }

    public static IRetrofitServices getsRetrofitServices() {
        return sRetrofitServices;
    }

}
