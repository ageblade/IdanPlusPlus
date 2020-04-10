package com.example.idan.plusplus.ui;

import android.app.UiModeManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.idan.plusplus.R;
import com.example.idan.plusplus.Utils;
import com.example.idan.plusplus.V2.App.WebapiSingleton;

import java.util.concurrent.TimeUnit;

public class MainActivity extends LeanbackActivity {
    public boolean mBackPressedOnce = false;
    public SpinnerFragment mSpinnerFragment = new SpinnerFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        Utils.setIsTv(uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedOnce) {
            super.onBackPressed();
            return;
        }

        mBackPressedOnce = true;
        Toast.makeText(this, R.string.dobule_press_back, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> mBackPressedOnce = false, TimeUnit.SECONDS.toMillis(2));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!WebapiSingleton.isInPicInPic)
            android.os.Process.killProcess(android.os.Process.myPid());
    }
}
