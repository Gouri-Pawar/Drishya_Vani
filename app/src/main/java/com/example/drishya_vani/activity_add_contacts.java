package com.example.drishya_vani;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activity_add_contacts extends AppCompatActivity {

    EditText name1, phone1, name2, phone2, name3, phone3;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        name1 = findViewById(R.id.name1);
        phone1 = findViewById(R.id.phone1);
        name2 = findViewById(R.id.name2);
        phone2 = findViewById(R.id.phone2);
        name3 = findViewById(R.id.name3);
        phone3 = findViewById(R.id.phone3);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveContacts());
    }

    private void saveContacts() {
        SharedPreferences sp = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("name1", name1.getText().toString());
        editor.putString("phone1", phone1.getText().toString());
        editor.putString("name2", name2.getText().toString());
        editor.putString("phone2", phone2.getText().toString());
        editor.putString("name3", name3.getText().toString());
        editor.putString("phone3", phone3.getText().toString());

        editor.putBoolean("isSaved", true);
        editor.apply();

        Toast.makeText(this, "Contacts Saved ❤️", Toast.LENGTH_SHORT).show();
        finish();
    }
}