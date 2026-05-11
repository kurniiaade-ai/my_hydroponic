package com.delan.myhydroponic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static final int DELAY_MILLIS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        // ambil logo
        ImageView logo = findViewById(R.id.logo_image);

        // animasi logo (fade + zoom)
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        logo.startAnimation(anim);

        // delay pindah activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);

            // animasi transisi antar screen
            overridePendingTransition(R.anim.slide_in, R.anim.fade_out);

            finish();
        }, DELAY_MILLIS);
    }
}