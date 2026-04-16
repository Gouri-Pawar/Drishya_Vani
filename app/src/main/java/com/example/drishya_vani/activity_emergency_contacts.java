package com.example.drishya_vani;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class activity_emergency_contacts extends AppCompatActivity {

    CardView call1, call2, call3;
    TextView txtName1, txtName2, txtName3;
    TextView txtPhone1, txtPhone2, txtPhone3;
    Button btnEditContacts;

    String phone1 = "", phone2 = "", phone3 = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        call1 = findViewById(R.id.callMom);
        call2 = findViewById(R.id.callDad);
        call3 = findViewById(R.id.callFriend);

        txtName1  = findViewById(R.id.txtContactName1);
        txtName2  = findViewById(R.id.txtContactName2);
        txtName3  = findViewById(R.id.txtContactName3);

        // Optional: also show phone numbers on the card
        txtPhone1 = findViewById(R.id.txtContactPhone1);
        txtPhone2 = findViewById(R.id.txtContactPhone2);
        txtPhone3 = findViewById(R.id.txtContactPhone3);

        btnEditContacts = findViewById(R.id.btnEditContacts);

        // Allow user to update contacts by going back to add screen
        btnEditContacts.setOnClickListener(v ->
                startActivity(new Intent(this, activity_add_contacts.class)));

        call1.setOnClickListener(v -> call(phone1));
        call2.setOnClickListener(v -> call(phone2));
        call3.setOnClickListener(v -> call(phone3));

        loadContacts();
    }

    private void loadContacts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("emergency_contacts")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, activity_add_contacts.class));
                        finish();
                        return;
                    }

                    // Read values — default to empty string if field missing
                    phone1 = getString(doc, "phone1");
                    phone2 = getString(doc, "phone2");
                    phone3 = getString(doc, "phone3");

                    txtName1.setText(getString(doc, "name1", "Contact 1"));
                    txtName2.setText(getString(doc, "name2", "Contact 2"));
                    txtName3.setText(getString(doc, "name3", "Contact 3"));

                    if (txtPhone1 != null) txtPhone1.setText(phone1);
                    if (txtPhone2 != null) txtPhone2.setText(phone2);
                    if (txtPhone3 != null) txtPhone3.setText(phone3);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error loading contacts: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    /** Safe getString from Firestore document — returns "" if field absent */
    private String getString(com.google.firebase.firestore.DocumentSnapshot doc, String key) {
        String val = doc.getString(key);
        return val != null ? val : "";
    }

    /** Overload with fallback default */
    private String getString(com.google.firebase.firestore.DocumentSnapshot doc,
                             String key, String defaultVal) {
        String val = doc.getString(key);
        return (val != null && !val.isEmpty()) ? val : defaultVal;
    }

    private void call(String number) {
        if (number == null || number.isEmpty()) {
            Toast.makeText(this, "No number saved for this contact", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }
}