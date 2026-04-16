package com.example.drishya_vani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class activity_add_contacts extends AppCompatActivity {

    EditText etName1, etPhone1, etName2, etPhone2, etName3, etPhone3;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        etName1  = findViewById(R.id.name1);
        etPhone1 = findViewById(R.id.phone1);
        etName2  = findViewById(R.id.name2);
        etPhone2 = findViewById(R.id.phone2);
        etName3  = findViewById(R.id.name3);
        etPhone3 = findViewById(R.id.phone3);
        btnSave  = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveContacts());
    }

    private void saveContacts() {
        String name1  = etName1.getText().toString().trim();
        String phone1 = etPhone1.getText().toString().trim();
        String name2  = etName2.getText().toString().trim();
        String phone2 = etPhone2.getText().toString().trim();
        String name3  = etName3.getText().toString().trim();
        String phone3 = etPhone3.getText().toString().trim();

        // Require at least one contact
        if (name1.isEmpty() || phone1.isEmpty()) {
            Toast.makeText(this, "Please fill at least Contact 1", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name1",  name1);
        data.put("phone1", phone1);
        data.put("name2",  name2);
        data.put("phone2", phone2);
        data.put("name3",  name3);
        data.put("phone3", phone3);

        FirebaseFirestore.getInstance()
                .collection("emergency_contacts")
                .document(user.getUid())
                .set(data)   // set() creates OR overwrites the document
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Contacts saved!", Toast.LENGTH_SHORT).show();
                    // Navigate to the view screen so user sees what was saved
                    startActivity(new Intent(this, activity_emergency_contacts.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to save: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}