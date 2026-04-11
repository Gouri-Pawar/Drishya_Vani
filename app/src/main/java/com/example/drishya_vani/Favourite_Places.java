package com.example.drishya_vani;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Favourite_Places extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout emptyLayout;
    FavouriteAdapter adapter;

    // fullList  = all favourites loaded from Firestore (never filtered)
    // favList   = what the adapter currently shows (filtered subset)
    ArrayList<FavouriteModel> fullList;
    ArrayList<FavouriteModel> favList;

    FloatingActionButton fabExplore;
    EditText etSearch;                       // ← search bar

    FirebaseFirestore db;
    String userId;

    FusedLocationProviderClient fusedLocationClient;
    double userLat = 0, userLon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);

        recyclerView = findViewById(R.id.recyclerFavourites);
        emptyLayout  = findViewById(R.id.layoutEmptyState);
        fabExplore   = findViewById(R.id.fabExplore);
        etSearch     = findViewById(R.id.etSearchFav);   // ← wire up search bar

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fullList = new ArrayList<>();   // master copy — never touched by search
        favList  = new ArrayList<>();   // shown in RecyclerView
        adapter  = new FavouriteAdapter(this, favList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fabExplore.setOnClickListener(v -> {
            startActivity(new Intent(this, NearbyPlaces.class));
        });

        // ── Search TextWatcher ────────────────────────────────────────────────
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        getUserLocationThenFetch();
    }

    // ── Step 1: Get current location ─────────────────────────────────────────

    private void getUserLocationThenFetch() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fetchFavourites();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLon = location.getLongitude();
            }
            fetchFavourites();
        });
    }

    // ── Step 2: Fetch from Firestore → populate fullList ─────────────────────

    private void fetchFavourites() {
        db.collection("favourites")
                .document(userId)
                .collection("places")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    fullList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        String key  = doc.getString("key");
                        String name = doc.getString("name");
                        String type = doc.getString("type");

                        Double latObj = doc.getDouble("lat");
                        Double lonObj = doc.getDouble("lon");

                        if (key == null || name == null) continue;

                        double lat = latObj != null ? latObj : 0;
                        double lon = lonObj != null ? lonObj : 0;

                        double distance = 0;
                        if (userLat != 0 && userLon != 0 && lat != 0 && lon != 0) {
                            float[] result = new float[1];
                            Location.distanceBetween(userLat, userLon, lat, lon, result);
                            distance = result[0];
                        }

                        fullList.add(new FavouriteModel(key, name, type, lat, lon, distance));
                    }

                    if (userLat != 0 && userLon != 0) {
                        Collections.sort(fullList,
                                (a, b) -> Double.compare(a.getDistance(), b.getDistance()));
                    }

                    // Apply whatever is already typed in the search box
                    filterList(etSearch.getText().toString());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load favourites",
                                Toast.LENGTH_SHORT).show());
    }

    // ── Step 3: Filter fullList → update favList shown in RecyclerView ────────

    private void filterList(String query) {
        favList.clear();

        if (query == null || query.trim().isEmpty()) {
            // No query → show everything
            favList.addAll(fullList);
        } else {
            String lower = query.trim().toLowerCase();
            for (FavouriteModel item : fullList) {
                // Match against place name OR type  (e.g. typing "cafe" or "hospital")
                boolean nameMatch = item.getName() != null
                        && item.getName().toLowerCase().contains(lower);
                boolean typeMatch = item.getType() != null
                        && item.getType().toLowerCase().contains(lower);

                if (nameMatch || typeMatch) {
                    favList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    // ── Helper: show/hide empty state ────────────────────────────────────────

    private void updateEmptyState() {
        if (favList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}