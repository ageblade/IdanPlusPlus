package com.example.idan.plusplus.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.idan.plusplus.MyApplication;

public abstract class LeanbackActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyApplication.localeManager.setLocale(base));
    }

    @Override
    public boolean onSearchRequested() {
        return true;
    }
}
