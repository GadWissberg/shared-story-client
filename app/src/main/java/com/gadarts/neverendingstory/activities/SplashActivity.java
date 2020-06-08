package com.gadarts.neverendingstory.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;

import com.gadarts.neverendingstory.R;

import androidx.annotation.Nullable;

public class SplashActivity extends Activity {

    public static final int FADE_DURATION = 2000;
    private static final long NEXT_ACTIVITY_DELAY = 4000;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setFadeAnimation();
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        changeToNextActivityWithDelay();
    }

    private void setFadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(FADE_DURATION);
        alphaAnimation.setFillAfter(true);
        findViewById(R.id.activity_splash).startAnimation(alphaAnimation);
    }

    private void changeToNextActivityWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, NEXT_ACTIVITY_DELAY);
    }
}
