package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    private static int SPLASH_TIME = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
            startActivity(intent);
            finish();

        }, SPLASH_TIME);
    }
}