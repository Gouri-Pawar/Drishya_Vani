package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    EditText name,email,password,confirmpass;
    Button signup;
    TextView loginRedirect;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpass = findViewById(R.id.confirmpass);
        signup = findViewById(R.id.signup);
        loginRedirect = findViewById(R.id.loginRedirect);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signup.setOnClickListener(v -> registerUser());

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(Signup.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(){

        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String confirmPassword = confirmpass.getText().toString().trim();

        // Name validation
        if(userName.isEmpty()){
            name.setError("Enter your name");
            name.requestFocus();
            return;
        }

        // Email validation
        if(userEmail.isEmpty()){
            email.setError("Enter email");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Enter valid email");
            email.requestFocus();
            return;
        }

        // Password validation
        if(userPassword.isEmpty()){
            password.setError("Enter password");
            password.requestFocus();
            return;
        }

        if(userPassword.length() < 6){
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        if(!userPassword.equals(confirmPassword)){
            confirmpass.setError("Passwords do not match");
            confirmpass.requestFocus();
            return;
        }

        // Firebase signup
        auth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnSuccessListener(authResult -> {

                    String userId = authResult.getUser().getUid();

                    Map<String,Object> user = new HashMap<>();
                    user.put("name",userName);
                    user.put("email",userEmail);

                    db.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(this,
                                        "Account Created Successfully",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(
                                        Signup.this,
                                        LoginActivity.class));

                                finish();
                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Error: "+e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}