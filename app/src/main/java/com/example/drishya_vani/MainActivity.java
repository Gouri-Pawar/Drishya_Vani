package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    Button startJourneyBtn, mapBtn, languageBtn, aboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startJourneyBtn = findViewById(R.id.startBtn);
        mapBtn = findViewById(R.id.mapBtn);
        languageBtn = findViewById(R.id.langBtn);
        aboutBtn = findViewById(R.id.aboutBtn);

        // Start Journey
        startJourneyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JourneyActivity.class);
            startActivity(intent);
        });

        // Open Map
        mapBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // Language Selection
        languageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LanguageSelectionActivity.class);
            startActivity(intent);
        });

        // About
        aboutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}