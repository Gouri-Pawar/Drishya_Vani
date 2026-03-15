package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 2;

                handler.post(() -> progressBar.setProgress(progressStatus));

                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(() -> {

                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {

                    // User already logged in
                    Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                    startActivity(intent);

                } else {

                    // New user
                    Intent intent = new Intent(Splash_Screen.this, LoginActivity.class);
                    startActivity(intent);
                }

                finish();
            });

        }).start();
    }
}