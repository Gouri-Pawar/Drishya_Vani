package com.example.drishya_vani;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NearbyPlaces extends AppCompatActivity {

    RecyclerView recyclerView;
    NearbyAdapter adapter;
    ArrayList<NearbyPlaceModel> placeList;

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        recyclerView = findViewById(R.id.recyclerNearby);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeList = new ArrayList<>();
        adapter = new NearbyAdapter(placeList, this);
        recyclerView.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                fetchNearbyPlaces(location); // ✅ FIXED CALL
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ FIXED METHOD
    private void fetchNearbyPlaces(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        fetchNearbyPlaces(lat, lon);
    }

    private void fetchNearbyPlaces(double lat, double lon) {

        new Thread(() -> {
            try {

                String urlString =
                        "https://overpass-api.de/api/interpreter?data=[out:json];(" +
                                "node(around:5000," + lat + "," + lon + ")[\"tourism\"];"+
                                "node(around:5000," + lat + "," + lon + ")[\"amenity\"];"+
                                "node(around:5000," + lat + "," + lon + ")[\"historic\"];"+
                                ");out;";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream is = conn.getInputStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                JSONObject jsonObject = new JSONObject(response);
                JSONArray elements = jsonObject.getJSONArray("elements");

                placeList.clear();

                for (int i = 0; i < elements.length(); i++) {

                    JSONObject obj = elements.getJSONObject(i);
                    JSONObject tags = obj.optJSONObject("tags");

                    String name = "Unknown Place";

                    if (tags != null) {
                        if (tags.has("name")) {
                            name = tags.getString("name");
                        } else if (tags.has("tourism")) {
                            name = tags.getString("tourism"); // fallback
                        }
                    }

                    placeList.add(new NearbyPlaceModel(name));
                }

                Log.d("COUNT", "Total places: " + placeList.size());

                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Total places: " + placeList.size(), Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error fetching places", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}