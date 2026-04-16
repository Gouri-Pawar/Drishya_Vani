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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
import java.text.SimpleDateFormat;
import java.util.Date;
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
    String UNSPLASH_ACCESS_KEY = "HMmvQNEDOX94h3uO7YWMI0ycCZPWcv-nFqhMYtMSbZU";

    boolean isSpeaking = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        txtLocationStatus = findViewById(R.id.txtLocationStatus);
        txtLandmarkName   = findViewById(R.id.txtLandmarkName);
        txtDescription    = findViewById(R.id.txtDescription);
        imgLandmark       = findViewById(R.id.imgLandmark);
        btnPlayStory      = findViewById(R.id.btnPlayStory);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        selectedLanguage = prefs.getString("language", "en");

        // Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale;
                if (selectedLanguage.equals("hi"))      locale = new Locale("hi", "IN");
                else if (selectedLanguage.equals("mr")) locale = new Locale("mr", "IN");
                else                                    locale = Locale.ENGLISH;
                tts.setLanguage(locale);
                tts.setPitch(1.4f);
                tts.setSpeechRate(1.2f);
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

    // ─── Location ────────────────────────────────────────────────────────────────

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Location")
                    .setMessage("Location services are turned off. Please enable location to continue.")
                    .setPositiveButton("Open Settings", (dialog, which) -> {
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) ->
                            txtLocationStatus.setText("Location is required to show landmarks."))
                    .setCancelable(false)
                    .show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                txtLocationStatus.setText("Lat: " + lat + "  Lon: " + lon);

                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                    String place = "";
                    String city  = "";

                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        place = address.getSubLocality();
                        city  = address.getLocality();
                        txtLandmarkName.setText(
                                (place != null ? place : "") + ", " + (city != null ? city : ""));
                    }
                    fetchPlaceInfo(place, lat, lon, city);

                } catch (Exception e) {
                    e.printStackTrace();
                    txtLocationStatus.setText("Error reading location data.");
                }

            } else {
                txtLocationStatus.setText("Unable to get location. Try again.");
            }
        });
    }

    // ─── Fetch place info + image ─────────────────────────────────────────────

    private void fetchPlaceInfo(String place, double lat, double lon, String city) {
        new Thread(() -> {

            // ── 1. Wikipedia description ─────────────────────────────────────
            String description = "";
            try {
                String query = (place != null && !place.isEmpty()) ? place : city;
                if (query == null || query.isEmpty()) query = "India";
                query = URLEncoder.encode(query.replace(",", "").trim(), "UTF-8")
                        .replace("+", "_");

                String wikiUrl =
                        "https://en.wikipedia.org/api/rest_v1/page/summary/" + query;
                HttpURLConnection conn =
                        (HttpURLConnection) new URL(wikiUrl).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();

                    JSONObject json = new JSONObject(response);
                    description = json.optString("extract",
                            "No description available for this location.");
                } else {
                    description = "No description available for this location.";
                }

                // Limit to 15 sentences
                String[] sentences = description.split("\\. ");
                StringBuilder shortDesc = new StringBuilder();
                for (int i = 0; i < Math.min(sentences.length, 15); i++) {
                    shortDesc.append(sentences[i]).append(". ");
                }
                description = shortDesc.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
                description = "Failed to fetch location description.";
            }

            storyEnglish = description;

            // ── 2. Unsplash image ─────────────────────────────────────────────
            String imageUrl = "";
            try {

                String booster = " landmark";

                // detect common Indian area words to improve search
                String lowerPlace = place != null ? place.toLowerCase() : "";

                if (lowerPlace.contains("temple") || lowerPlace.contains("mandir"))
                    booster = " temple";
                else if (lowerPlace.contains("hospital"))
                    booster = " hospital building";
                else if (lowerPlace.contains("market") || lowerPlace.contains("bazaar"))
                    booster = " street market";
                else if (lowerPlace.contains("park") || lowerPlace.contains("garden"))
                    booster = " park";
                else if (lowerPlace.contains("fort"))
                    booster = " fort";
                else if (lowerPlace.contains("beach"))
                    booster = " beach";

                String[] searchQueries;

                // Strongest → Weakest fallback chain
                if (place != null && !place.isEmpty() && city != null && !city.isEmpty()) {
                    searchQueries = new String[]{
                            place + " " + city + booster,
                            city + " famous place",
                            city + " tourist attraction",
                            "India tourist place"
                    };
                } else if (city != null && !city.isEmpty()) {
                    searchQueries = new String[]{
                            city + " tourist attraction",
                            "India tourist place"
                    };
                } else {
                    searchQueries = new String[]{"India tourist place"};
                }

                for (String searchQuery : searchQueries) {

                    String unsplashUrl =
                            "https://api.unsplash.com/search/photos?query="
                                    + URLEncoder.encode(searchQuery.trim(), "UTF-8")
                                    + "&orientation=landscape&per_page=5&order_by=relevant&content_filter=high";

                    HttpURLConnection imgConn =
                            (HttpURLConnection) new URL(unsplashUrl).openConnection();

                    imgConn.setRequestProperty("Authorization",
                            "Client-ID " + UNSPLASH_ACCESS_KEY);

                    imgConn.setConnectTimeout(5000);
                    imgConn.setReadTimeout(5000);

                    if (imgConn.getResponseCode() != 200) continue;

                    InputStream imgStream = imgConn.getInputStream();
                    Scanner imgScanner = new Scanner(imgStream).useDelimiter("\\A");
                    String imgResponse = imgScanner.hasNext() ? imgScanner.next() : "";
                    imgScanner.close();

                    JSONObject imgJson = new JSONObject(imgResponse);
                    JSONArray results = imgJson.getJSONArray("results");

                    if (results.length() > 0) {
                        JSONObject photo = results.getJSONObject(0);
                        imageUrl = photo.getJSONObject("urls").optString("regular", "");
                        if (!imageUrl.isEmpty()) break;
                    }
                }
                // 🔴 SPECIAL CASE : Vishnupuri default image
                if (place != null && place.equalsIgnoreCase("Vishnupuri")) {

                    runOnUiThread(() -> {
                        txtDescription.setText(storyEnglish);

                        Picasso.get()
                                .load(R.drawable.vishnupuri)   // put vishnupuri.jpg in drawable
                                .placeholder(R.drawable.landmark_placeholder)
                                .error(R.drawable.landmark_placeholder)
                                .into(imgLandmark);
                    });

                    return; // stop Unsplash fetch completely
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // ── 3. Update UI on main thread ───────────────────────────────────
            String finalDescription = description;
            String finalImageUrl    = imageUrl;
            String finalCity        = city;

            runOnUiThread(() -> {
                txtDescription.setText(finalDescription);

                savePlaceToFirebase(finalCity, finalDescription);

                Picasso.get()
                        .load(finalImageUrl.isEmpty() ? null : finalImageUrl)
                        .placeholder(R.drawable.landmark_placeholder)
                        .error(R.drawable.landmark_placeholder)
                        .into(imgLandmark);

                // Kick off translations after UI is ready
                DownloadConditions conditions = new DownloadConditions.Builder().build();

                hindiTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(unused ->
                                hindiTranslator.translate(finalDescription)
                                        .addOnSuccessListener(translated ->
                                                storyHindi = translated));

                marathiTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(unused ->
                                marathiTranslator.translate(finalDescription)
                                        .addOnSuccessListener(translated ->
                                                storyMarathi = translated));
            });

        }).start();
    }

    // ─── Firebase ─────────────────────────────────────────────────────────────

    private void savePlaceToFirebase(String city, String description) {
        if (city == null || city.isEmpty()) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(new Date());

        DocumentReference docRef = db.collection("users")
                .document(userId)
                .collection("visited_places")
                .document(city.toLowerCase());

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                docRef.update("visits", FieldValue.arrayUnion(currentDate));
            } else {
                Map<String, Object> placeData = new HashMap<>();
                placeData.put("name", city);
                placeData.put("visits", Arrays.asList(currentDate));
                docRef.set(placeData);
            }
        });
    }

    // ─── TTS ──────────────────────────────────────────────────────────────────

    private void toggleSpeech() {
        if (isSpeaking) {
            tts.stop();
            isSpeaking = false;
            btnPlayStory.setText("Play Story");
        } else {
            String story;
            if (selectedLanguage.equals("hi")
                    && storyHindi != null && !storyHindi.isEmpty()) {
                story = storyHindi;
            } else if (selectedLanguage.equals("mr")
                    && storyMarathi != null && !storyMarathi.isEmpty()) {
                story = storyMarathi;
            } else {
                story = storyEnglish;
            }

            if (tts != null && story != null && !story.isEmpty()) {
                String[] parts = story.split("\\. ");
                for (String part : parts) {
                    tts.speak(part + ".", TextToSpeech.QUEUE_ADD, null, null);
                }
                isSpeaking = true;
                btnPlayStory.setText("Pause Story");
            }
        }
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}