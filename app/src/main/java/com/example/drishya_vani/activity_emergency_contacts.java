package com.example.drishya_vani;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class activity_emergency_contacts extends AppCompatActivity {

    CardView call1, call2, call3;

    // FIX: These are the actual TextViews that show the contact name inside each card.
    // Old code used android.R.id.text1 — a system ID that does NOT exist in our
    // custom CardView layout, causing a NullPointerException crash on setText().
    TextView txtName1, txtName2, txtName3;

    String phone1, phone2, phone3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);  // ← uses the new redesigned layout

        call1 = findViewById(R.id.callMom);
        call2 = findViewById(R.id.callDad);
        call3 = findViewById(R.id.callFriend);

        // FIX: Find the name TextViews directly by their real IDs in the layout.
        // Each card has a TextView with id txtContactName1 / 2 / 3 for the bold name line.
        txtName1 = findViewById(R.id.txtContactName1);
        txtName2 = findViewById(R.id.txtContactName2);
        txtName3 = findViewById(R.id.txtContactName3);

        // Load saved contacts from SharedPreferences
        SharedPreferences sp = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        phone1 = sp.getString("phone1", "");
        phone2 = sp.getString("phone2", "");
        phone3 = sp.getString("phone3", "");

        // Set the saved names on the cards (falls back to default if not saved)
        txtName1.setText(sp.getString("name1", "Contact 1"));
        txtName2.setText(sp.getString("name2", "Contact 2"));
        txtName3.setText(sp.getString("name3", "Contact 3"));

        call1.setOnClickListener(v -> call(phone1));
        call2.setOnClickListener(v -> call(phone2));
        call3.setOnClickListener(v -> call(phone3));
    }

    private void call(String number) {
        if (number == null || number.isEmpty()) {
            // FIX: Guard against empty number — old code would crash the dialler
            // with an empty tel: URI if a contact was never saved
            android.widget.Toast.makeText(this,
                    "No number saved for this contact", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }
}