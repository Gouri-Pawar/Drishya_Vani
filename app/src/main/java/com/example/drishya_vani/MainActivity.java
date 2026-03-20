package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    View exploreBtn, mapBtn, languageBtn, aboutBtn, viewPlacesBtn, nearbyBtn;
    ImageButton logoutBtn;
    ImageView logo;
    TextView usernameText;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exploreBtn = findViewById(R.id.startBtn);
        mapBtn = findViewById(R.id.mapBtn);
        languageBtn = findViewById(R.id.langBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        viewPlacesBtn = findViewById(R.id.viewPlacesBtn);
        nearbyBtn = findViewById(R.id.nearbyBtn);


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        logoutBtn = findViewById(R.id.logout);
        logo = findViewById(R.id.logo);
        usernameText = findViewById(R.id.usernameText);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /* -------- FETCH USER NAME FROM FIRESTORE -------- */

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {

            String uid = user.getUid();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            String name = documentSnapshot.getString("name");

                            usernameText.setText("Hello, " + name);
                        }
                    });
        }

        /* -------- LOGO ANIMATION -------- */

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(logoAnimation);

        /* -------- BUTTON ACTIONS -------- */

        exploreBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, JourneyActivity.class));
        });

        mapBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });

        languageBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LanguageSelectionActivity.class));
        });

        aboutBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });

        viewPlacesBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        nearbyBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NearbyPlaces.class));
        });

        logoutBtn.setOnClickListener(v -> {

            auth.signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}