package com.example.nandayemparala.myapplication.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.nandayemparala.myapplication.PrefsManager;
import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.application.App;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by nandayemparala on 9/28/16.
 */

public class IntroActivty extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome", "This app is awesome", R.drawable.common_google_signin_btn_icon_dark, R.color.primary));
        addSlide(AppIntroFragment.newInstance("Welcome 2", "This app is awesome", R.drawable.common_google_signin_btn_icon_dark, R.color.primary));

        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation(); // OR
//        setZoomAnimation(); // OR
//        setFlowAnimation(); // OR
//        setSlideOverAnimation(); // OR
//        setDepthAnimation(); // OR
//        setCustomTransformer(yourCustomTransformer);

        // Permissions -- takes a permission and slide number
        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        setSwipeLock(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        PrefsManager.setIntroAsShown();
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        App.showToast("Skip?");
    }
}
