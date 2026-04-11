package com.example.drishya_vani;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    View exploreBtn, mapBtn, languageBtn, aboutBtn, viewPlacesBtn, nearbyBtn, favPlaces, emergencyBtn;
    ImageButton logoutBtn;
    ImageView logo;
    TextView usernameText;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationSetup();

        exploreBtn = findViewById(R.id.startBtn);
        mapBtn = findViewById(R.id.mapBtn);
        languageBtn = findViewById(R.id.langBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        viewPlacesBtn = findViewById(R.id.viewPlacesBtn);
        nearbyBtn = findViewById(R.id.nearbyBtn);
        favPlaces = findViewById(R.id.favoriteBtn);
        emergencyBtn = findViewById(R.id.emergencyBtn);

        logoutBtn = findViewById(R.id.logout);
        logo = findViewById(R.id.logo);
        usernameText = findViewById(R.id.usernameText);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists())
                            usernameText.setText("Hello, " + doc.getString("name"));
                    });
        }

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(logoAnimation);

        exploreBtn.setOnClickListener(v -> startActivity(new Intent(this, JourneyActivity.class)));
        mapBtn.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        languageBtn.setOnClickListener(v -> startActivity(new Intent(this, LanguageSelectionActivity.class)));
        aboutBtn.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        viewPlacesBtn.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        nearbyBtn.setOnClickListener(v -> startActivity(new Intent(this, NearbyPlaces.class)));
        favPlaces.setOnClickListener(v -> startActivity(new Intent(this, Favourite_Places.class)));
        emergencyBtn.setOnClickListener(v -> startActivity(new Intent(this, Emergency_Activity.class)));

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    // 🔥 MAIN LOCATION CHECK
    private void checkLocationSetup() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showModernLocationDialog(true);
            return;
        }

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps && !net) showModernLocationDialog(false);
    }

    // ⭐ BEAUTIFUL POPUP
    private void showModernLocationDialog(boolean permissionMissing) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_location_permission);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.dialogTitle);
        TextView desc = dialog.findViewById(R.id.dialogDesc);
        Button allow = dialog.findViewById(R.id.btnAllow);
        TextView cancel = dialog.findViewById(R.id.btnCancel);

        if (permissionMissing) {
            title.setText("Location Permission Needed 📍");
            desc.setText("Allow location access for emergency safety features.");
            allow.setText("Allow Permission");
        } else {
            title.setText("Turn On Location 📍");
            desc.setText("Please enable GPS to use safety features.");
            allow.setText("Turn On GPS");
        }

        allow.setOnClickListener(v -> {
            dialog.dismiss();
            if (permissionMissing) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            } else {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        // Apply animation to dialog root view
        View dialogView = dialog.findViewById(android.R.id.content);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.popup_animation);
        dialogView.startAnimation(anim);
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] p, int[] res) {
        super.onRequestPermissionsResult(req, p, res);
        if (req == 101 && res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED)
            checkLocationSetup();
    }
}