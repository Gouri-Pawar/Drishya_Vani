package com.example.drishya_vani;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Emergency_Activity extends AppCompatActivity {

    CardView btnSOS, btnPolice, btnAmbulance, btnFire, btnShareLocation, btnContacts;
    FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        btnSOS = findViewById(R.id.btnSOS);
        btnPolice = findViewById(R.id.btnPolice);
        btnAmbulance = findViewById(R.id.btnAmbulance);
        btnFire = findViewById(R.id.btnFire);
        btnShareLocation = findViewById(R.id.btnShareLocation);
        btnContacts = findViewById(R.id.btnContacts);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnShareLocation.setOnClickListener(v -> shareLocation());
        btnContacts.setOnClickListener(v -> {

            SharedPreferences sp = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
            boolean isSaved = sp.getBoolean("isSaved", false);

            if(isSaved)
                startActivity(new Intent(this, activity_emergency_contacts.class));
            else
                startActivity(new Intent(this, activity_add_contacts.class));
        });

        btnPolice.setOnClickListener(v -> callNumber("100"));
        btnAmbulance.setOnClickListener(v -> callNumber("102"));
        btnFire.setOnClickListener(v -> callNumber("101"));
        btnSOS.setOnClickListener(v -> callNumber("112"));
    }

    private void callNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void shareLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        String mapsLink = "https://maps.google.com/?q=" + lat + "," + lon;

                        String message = "🚨 EMERGENCY!\nI need help. My live location:\n" + mapsLink;

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share via"));
                    }
                });
    }

}