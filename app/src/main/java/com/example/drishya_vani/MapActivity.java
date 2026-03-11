package com.example.drishya_vani;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Button btnAccessLocation = findViewById(R.id.btnAccessLocation);
        ImageView imgMapPlaceholder = findViewById(R.id.imgMapPlaceholder);

        // Open Google Maps when button is clicked
        btnAccessLocation.setOnClickListener(v -> openGoogleMaps());

        // Open Google Maps when placeholder is clicked
        // imgMapPlaceholder.setOnClickListener(v -> openGoogleMaps());
    }

    private void openGoogleMaps() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.google.com"));
        startActivity(intent);
        }

    }
