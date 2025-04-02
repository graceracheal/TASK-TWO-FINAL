package com.example.tasktwo2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 3000;
    private Handler handler;
    private Runnable networkCheckRunnable;
    private ImageView splashLogo;
    private TextView appNameText;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashLogo = findViewById(R.id.splash_logo);
        appNameText = findViewById(R.id.splash_logo);

        handler = new Handler(Looper.getMainLooper());
        networkCheckRunnable = this::checkNetworkAndProceed;

        startSplashAnimation();

        handler.postDelayed(networkCheckRunnable, SPLASH_DELAY);
    }

    private void startSplashAnimation() {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);

        splashLogo.startAnimation(fadeIn);
        AlphaAnimation textFadeIn = new AlphaAnimation(0.0f, 1.0f);
        textFadeIn.setDuration(1500);
        textFadeIn.setStartOffset(500);
        appNameText.startAnimation(textFadeIn);
    }

    private void checkNetworkAndProceed() {
        if (isNetworkAvailable()) {
            try {
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                splashLogo.startAnimation(fadeOut);
                appNameText.startAnimation(fadeOut);

            } catch (Exception e) {
                showError("Error launching app. Please try again.");
            }
        } else {
            showNoInternetSnackbar();
        }
    }

    private void showNoInternetSnackbar() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "No internet connection! Please check your network.",
                Snackbar.LENGTH_INDEFINITE
        );

        snackbar.setAction("Retry", v -> {
            handler.removeCallbacks(networkCheckRunnable);
            checkNetworkAndProceed();
        });

        snackbar.show();
    }

    private void showError(String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(networkCheckRunnable);
        }
    }
}