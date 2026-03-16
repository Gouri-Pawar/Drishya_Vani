package com.example.drishya_vani;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    Button btnAccessLocation;
    ImageView imgMapPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnAccessLocation = findViewById(R.id.btnAccessLocation);
        imgMapPlaceholder = findViewById(R.id.imgMapPlaceholder);

        // Open Google Maps when button is clicked
        btnAccessLocation.setOnClickListener(v -> openGoogleMaps());

        // Optional: open maps when image clicked
        imgMapPlaceholder.setOnClickListener(v -> openGoogleMaps());
    }

    private void openGoogleMaps() {

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=nearby places");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        startActivity(mapIntent);
    }
}