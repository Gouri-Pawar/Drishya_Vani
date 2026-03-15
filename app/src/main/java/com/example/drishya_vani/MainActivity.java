package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button startJourneyBtn, mapBtn, languageBtn, aboutBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startJourneyBtn = findViewById(R.id.startBtn);
        mapBtn = findViewById(R.id.mapBtn);
        languageBtn = findViewById(R.id.langBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        logoutBtn = findViewById(R.id.logout);

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

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}