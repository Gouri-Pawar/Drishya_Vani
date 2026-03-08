package com.example.drishya_vani;

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

        englishBtn.setOnClickListener(v ->
                Toast.makeText(this,"English Selected",Toast.LENGTH_SHORT).show());

        hindiBtn.setOnClickListener(v ->
                Toast.makeText(this,"Hindi Selected",Toast.LENGTH_SHORT).show());

        marathiBtn.setOnClickListener(v ->
                Toast.makeText(this,"Marathi Selected",Toast.LENGTH_SHORT).show());
    }
}