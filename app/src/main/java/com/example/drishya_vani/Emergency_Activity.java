package com.example.drishya_vani;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Emergency_Activity extends AppCompatActivity {

    CardView btnSOS, btnPolice, btnAmbulance, btnFire, btnShareLocation, btnContacts;
    FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        btnSOS          = findViewById(R.id.btnSOS);
        btnPolice       = findViewById(R.id.btnPolice);
        btnAmbulance    = findViewById(R.id.btnAmbulance);
        btnFire         = findViewById(R.id.btnFire);
        btnShareLocation= findViewById(R.id.btnShareLocation);
        btnContacts     = findViewById(R.id.btnContacts);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnShareLocation.setOnClickListener(v -> shareLocation());

        // Check Firestore: if contacts already saved → show them, else → add screen
        btnContacts.setOnClickListener(v -> checkContactsAndNavigate());

        btnPolice   .setOnClickListener(v -> callNumber("100"));
        btnAmbulance.setOnClickListener(v -> callNumber("102"));
        btnFire     .setOnClickListener(v -> callNumber("101"));
        btnSOS      .setOnClickListener(v -> callNumber("112"));
    }

    /**
     * Checks Firestore for saved contacts.
     * → If the document exists and has at least one phone → open view screen.
     * → Otherwise → open add-contacts screen.
     */
    private void checkContactsAndNavigate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("emergency_contacts")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.getString("phone1") != null
                            && !doc.getString("phone1").isEmpty()) {
                        // Contacts found → go to view screen
                        startActivity(new Intent(this, activity_emergency_contacts.class));
                    } else {
                        // No contacts yet → go to add screen
                        startActivity(new Intent(this, activity_add_contacts.class));
                    }
                })
                .addOnFailureListener(e ->
                        startActivity(new Intent(this, activity_add_contacts.class)));
    }

    private void callNumber(String number) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }

    private void shareLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        // NEW modern way
        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener(location -> {

            if (location == null) {
                Toast.makeText(this,
                        "Turn ON GPS and try again",
                        Toast.LENGTH_LONG).show();
                return;
            }

            double lat = location.getLatitude();
            double lon = location.getLongitude();
            sendLocationShare(lat, lon);

        }).addOnFailureListener(e ->
                Toast.makeText(this,
                        "Location failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }

    private void sendLocationShare(double lat, double lon) {
        String mapsLink = "https://maps.google.com/?q=" + lat + "," + lon;
        String message  = "🚨 EMERGENCY!\nI need help. My location:\n" + mapsLink;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareLocation(); // retry after permission granted
        } else {
            Toast.makeText(this,
                    "Location permission is required to share location",
                    Toast.LENGTH_SHORT).show();
        }
    }
}