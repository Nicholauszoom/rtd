package com.example.transactionsms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the splash layout
        setContentView(R.layout.activity_splash);

        // Find the logo ImageView
        ImageView logoImage = findViewById(R.id.logoImage);

        // Load the animation
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_fade_scale);

        // First animation
        handler.postDelayed(() -> {
            logoImage.startAnimation(logoAnimation);
        }, 0); // Start immediately

        // Second animation after 2 seconds
        handler.postDelayed(() -> {
            logoImage.startAnimation(logoAnimation);
        }, 2000); // Start after 2 seconds

        // Transition to the main activity after 4 seconds
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 4000); // End splash after 4 seconds
    }
}