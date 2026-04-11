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
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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
                fetchNearbyPlaces(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Location not found. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchNearbyPlaces(double userLat, double userLon) {
        new Thread(() -> {
            try {
                String query =
                        "[out:json][timeout:25];" +
                                "(" +
                                "  node(around:5000," + userLat + "," + userLon + ")[\"tourism\"][\"name\"];" +
                                "  node(around:5000," + userLat + "," + userLon + ")[\"amenity\"][\"name\"];" +
                                "  node(around:5000," + userLat + "," + userLon + ")[\"historic\"][\"name\"];" +
                                ");" +
                                "out body;";

                String urlString = "https://overpass-api.de/api/interpreter?data="
                        + java.net.URLEncoder.encode(query, "UTF-8");

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(20000);

                InputStream is = conn.getInputStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                JSONObject jsonObject = new JSONObject(response);
                JSONArray elements = jsonObject.getJSONArray("elements");

                ArrayList<NearbyPlaceModel> fetchedList = new ArrayList<>();
                Set<String> seenKeys = new HashSet<>();

                for (int i = 0; i < elements.length(); i++) {
                    JSONObject obj = elements.getJSONObject(i);
                    JSONObject tags = obj.optJSONObject("tags");

                    if (tags == null) continue;

                    if (!tags.has("name")) continue;
                    String name = tags.getString("name").trim();
                    if (name.isEmpty()) continue;

                    String nodeId = obj.optString("id", "");
                    if (seenKeys.contains(nodeId)) continue;
                    seenKeys.add(nodeId);

                    String type = "place";
                    if (tags.has("tourism"))       type = tags.getString("tourism");
                    else if (tags.has("amenity"))  type = tags.getString("amenity");
                    else if (tags.has("historic")) type = tags.getString("historic");

                    double placeLat = obj.optDouble("lat", 0);
                    double placeLon = obj.optDouble("lon", 0);

                    float[] result = new float[1];
                    Location.distanceBetween(userLat, userLon, placeLat, placeLon, result);
                    double distanceMeters = result[0];

                    fetchedList.add(new NearbyPlaceModel(name, placeLat, placeLon,
                            type, distanceMeters));
                }

                Collections.sort(fetchedList,
                        (a, b) -> Double.compare(a.getDistance(), b.getDistance()));

                Log.d("NEARBY", "Total valid places: " + fetchedList.size());

                runOnUiThread(() -> {
                    placeList.clear();
                    placeList.addAll(fetchedList);
                    adapter.notifyDataSetChanged();

                    if (fetchedList.isEmpty()) {
                        Toast.makeText(this,
                                "No named places found nearby", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                fetchedList.size() + " places found nearby",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("NEARBY", "Fetch error: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error fetching places. Check internet connection.",
                                Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}