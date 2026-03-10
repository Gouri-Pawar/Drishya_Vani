package com.example.drishya_vani;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class JourneyActivity extends AppCompatActivity {

    TextView txtLocationStatus, txtLandmarkName, txtDescription;
    ImageView imgLandmark;
    Button btnPlayStory;

    TextToSpeech tts;
    String selectedLanguage;

    Translator hindiTranslator;
    Translator marathiTranslator;

    FusedLocationProviderClient fusedLocationClient;

    String storyEnglish = "";
    String storyHindi = "";
    String storyMarathi = "";

    // Unsplash Access Key
    String UNSPLASH_ACCESS_KEY = "YOUR_UNSPLASH_ACCESS_KEY";

    boolean isSpeaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        txtLocationStatus = findViewById(R.id.txtLocationStatus);
        txtLandmarkName = findViewById(R.id.txtLandmarkName);
        txtDescription = findViewById(R.id.txtDescription);
        imgLandmark = findViewById(R.id.imgLandmark);
        btnPlayStory = findViewById(R.id.btnPlayStory);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        selectedLanguage = prefs.getString("language", "en");

        // Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale;
                if (selectedLanguage.equals("hi")) locale = new Locale("hi", "IN");
                else if (selectedLanguage.equals("mr")) locale = new Locale("mr", "IN");
                else locale = Locale.ENGLISH;
                tts.setLanguage(locale);
            }
        });

        // Translators
        TranslatorOptions hindiOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build();
        hindiTranslator = com.google.mlkit.nl.translate.Translation.getClient(hindiOptions);

        TranslatorOptions marathiOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.MARATHI)
                .build();
        marathiTranslator = com.google.mlkit.nl.translate.Translation.getClient(marathiOptions);

        DownloadConditions conditions = new DownloadConditions.Builder().build();
        hindiTranslator.downloadModelIfNeeded(conditions);
        marathiTranslator.downloadModelIfNeeded(conditions);

        btnPlayStory.setOnClickListener(v -> toggleSpeech());

        getUserLocation();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Location")
                    .setMessage("Location services are turned off. Please enable location to continue.")
                    .setPositiveButton("Open Settings", (dialog, which) -> {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> txtLocationStatus.setText("Location is required to show landmarks."))
                    .setCancelable(false)
                    .show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                txtLocationStatus.setText("Lat: " + lat + " Lon: " + lon);

                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                    String place = "", city = "";
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        place = address.getSubLocality();
                        city = address.getLocality();
                        txtLandmarkName.setText((place != null ? place : "") + ", " + (city != null ? city : ""));
                    }
                    fetchPlaceInfo(place, lat, lon, city);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                txtLocationStatus.setText("Unable to get location. Try again.");
            }
        });
    }

    private void fetchPlaceInfo(String place, double lat, double lon, String city) {
        new Thread(() -> {
            try {
                String description = "";
                String query = (place != null && !place.isEmpty()) ? place : city;
                if (query == null || query.isEmpty()) query = "India";
                query = URLEncoder.encode(query.replace(",", "").trim(), "UTF-8").replace("+", "_");

                // Wikipedia API
                String wikiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + query;
                HttpURLConnection conn = (HttpURLConnection) new URL(wikiUrl).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    JSONObject json = new JSONObject(response);
                    description = json.optString("extract", "No description available for this location.");
                } else {
                    description = "No description available for this location.";
                }

                // Limit to 10 sentences
                String[] sentences = description.split("\\. ");
                StringBuilder shortDescription = new StringBuilder();
                for (int i = 0; i < Math.min(sentences.length, 10); i++) {
                    shortDescription.append(sentences[i]).append(". ");
                }
                description = shortDescription.toString().trim();
                storyEnglish = description;

                // Unsplash image
                String imageUrl = "";
                try {
                    String unsplashQuery = query.replace("_", "+");
                    String unsplashUrl = "https://api.unsplash.com/search/photos?query=" + unsplashQuery
                            + "&client_id=" + UNSPLASH_ACCESS_KEY + "&per_page=1";
                    HttpURLConnection imgConn = (HttpURLConnection) new URL(unsplashUrl).openConnection();
                    imgConn.setConnectTimeout(5000);
                    imgConn.setReadTimeout(5000);
                    InputStream imgStream = imgConn.getInputStream();
                    Scanner imgScanner = new Scanner(imgStream).useDelimiter("\\A");
                    String imgResponse = imgScanner.hasNext() ? imgScanner.next() : "";
                    JSONObject imgJson = new JSONObject(imgResponse);
                    JSONArray results = imgJson.optJSONArray("results");
                    if (results != null && results.length() > 0) {
                        imageUrl = results.getJSONObject(0).getJSONObject("urls").getString("regular");
                    }
                } catch (Exception e) {
                    imageUrl = "https://via.placeholder.com/600x300.png?text=No+Image";
                }

                String finalDescription = description;
                String finalImageUrl = imageUrl;

                runOnUiThread(() -> {
                    txtDescription.setText(finalDescription);

                    // Picasso with placeholder and error drawable
                    Picasso.get()
                            .load(finalImageUrl.isEmpty() ? null : finalImageUrl) // If empty, triggers error()
                            .placeholder(R.drawable.landmark_placeholder) // while loading
                            .error(R.drawable.landmark_placeholder) // if load fails
                            .into(imgLandmark);

                    DownloadConditions conditions = new DownloadConditions.Builder().build();
                    hindiTranslator.downloadModelIfNeeded(conditions)
                            .addOnSuccessListener(unused ->
                                    hindiTranslator.translate(finalDescription)
                                            .addOnSuccessListener(translated -> storyHindi = translated));
                    marathiTranslator.downloadModelIfNeeded(conditions)
                            .addOnSuccessListener(unused ->
                                    marathiTranslator.translate(finalDescription)
                                            .addOnSuccessListener(translated -> storyMarathi = translated));
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    txtDescription.setText("Failed to fetch landmark information.");
                    Picasso.get().load("https://via.placeholder.com/600x300.png?text=No+Image").into(imgLandmark);
                });
            }
        }).start();
    }

    private void toggleSpeech() {
        if (isSpeaking) {
            tts.stop();
            isSpeaking = false;
            btnPlayStory.setText("Play Story");
        } else {
            String story;
            if (selectedLanguage.equals("hi") && storyHindi != null && !storyHindi.isEmpty())
                story = storyHindi;
            else if (selectedLanguage.equals("mr") && storyMarathi != null && !storyMarathi.isEmpty())
                story = storyMarathi;
            else
                story = storyEnglish;

            if (tts != null && story != null) {
                // Split long text to speak in chunks for long narration
                String[] parts = story.split("\\. ");
                for (String part : parts) {
                    tts.speak(part + ".", TextToSpeech.QUEUE_ADD, null, null);
                }
                isSpeaking = true;
                btnPlayStory.setText("Pause Story");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}