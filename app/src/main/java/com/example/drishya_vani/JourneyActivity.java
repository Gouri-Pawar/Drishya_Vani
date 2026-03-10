package com.example.drishya_vani;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class JourneyActivity extends AppCompatActivity {

    TextView txtLocationStatus, txtLandmarkName, txtDescription;
    ImageView imgLandmark;
    Button btnPlayStory;

    TextToSpeech tts;
    String selectedLanguage;

    String storyEnglish = "Welcome to Lonavala. It is a famous hill station in the Western Ghats known for its scenic beauty and waterfalls.";
    String storyHindi = "लोणावला में आपका स्वागत है। यह पश्चिमी घाट का एक प्रसिद्ध हिल स्टेशन है जो अपनी सुंदरता और झरनों के लिए जाना जाता है।";
    String storyMarathi = "लोणावळ्यात आपले स्वागत आहे. हे पश्चिम घाटातील एक प्रसिद्ध हिल स्टेशन आहे जे आपल्या निसर्गरम्य सौंदर्यासाठी ओळखले जाते.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        txtLocationStatus = findViewById(R.id.txtLocationStatus);
        txtLandmarkName = findViewById(R.id.txtLandmarkName);
        txtDescription = findViewById(R.id.txtDescription);
        imgLandmark = findViewById(R.id.imgLandmark);
        btnPlayStory = findViewById(R.id.btnPlayStory);

        txtLocationStatus.setText("Location detected near Lonavala");
        txtLandmarkName.setText("Lonavala Hill Station");
        txtDescription.setText("Beautiful hill station in the Western Ghats famous for waterfalls and valleys.");

        // Get selected language
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        selectedLanguage = prefs.getString("language", "en");

        // Initialize Google TTS engine
        tts = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {

                Locale locale;

                if (selectedLanguage.equals("en")) {
                    locale = Locale.ENGLISH;
                } else if (selectedLanguage.equals("hi")) {
                    locale = new Locale("hi", "IN");
                } else {
                    locale = new Locale("mr", "IN");
                }

                int result = tts.setLanguage(locale);

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {

                    Toast.makeText(this, "Voice data missing. Install language pack.", Toast.LENGTH_LONG).show();

                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
            }

        }, "com.google.android.tts"); // Force Google TTS engine


        btnPlayStory.setOnClickListener(v -> speakStory());
    }

    private void speakStory() {

        String story;

        if (selectedLanguage.equals("en")) {
            story = storyEnglish;
        } else if (selectedLanguage.equals("hi")) {
            story = storyHindi;
        } else {
            story = storyMarathi;
        }

        if (tts != null) {
            tts.speak(story, TextToSpeech.QUEUE_FLUSH, null, null);
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