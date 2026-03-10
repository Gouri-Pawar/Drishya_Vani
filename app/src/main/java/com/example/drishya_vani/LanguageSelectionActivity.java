package com.example.drishya_vani;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageSelectionActivity extends AppCompatActivity {

    Button englishBtn, hindiBtn, marathiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        englishBtn = findViewById(R.id.englishBtn);
        hindiBtn = findViewById(R.id.hindiBtn);
        marathiBtn = findViewById(R.id.marathiBtn);

        englishBtn.setOnClickListener(v -> selectLanguage("en"));

        hindiBtn.setOnClickListener(v -> selectLanguage("hi"));

        marathiBtn.setOnClickListener(v -> selectLanguage("mr"));
    }

    private void selectLanguage(String langCode) {

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", langCode);
        editor.apply();

        Toast.makeText(this,"Language Selected",Toast.LENGTH_SHORT).show();

        // Move to next activity
        Intent intent = new Intent(LanguageSelectionActivity.this, JourneyActivity.class);
        startActivity(intent);
        finish();
    }
}