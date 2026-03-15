package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView signupRedirect, forgotPassword;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.uname);
        password = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        signupRedirect = findViewById(R.id.account);
        forgotPassword = findViewById(R.id.forgotPassword);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(v -> loginUser());

        signupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, Signup.class));
        });

        forgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void loginUser(){

        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

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

        if(userPassword.isEmpty()){
            password.setError("Enter password");
            password.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnSuccessListener(authResult -> {


                    Toast.makeText(this,
                            "Login Successful",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(
                            LoginActivity.this,
                            MainActivity.class));

                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Invalid Email or Password",
                            Toast.LENGTH_LONG).show();
                });
    }

    private void resetPassword(){

        String userEmail = email.getText().toString().trim();

        if(userEmail.isEmpty()){
            email.setError("Enter your email first");
            email.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(userEmail)
                .addOnSuccessListener(unused ->

                        Toast.makeText(this,
                                "Password reset link sent to your email",
                                Toast.LENGTH_LONG).show())

                .addOnFailureListener(e ->

                        Toast.makeText(this,
                                "Error: "+e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}