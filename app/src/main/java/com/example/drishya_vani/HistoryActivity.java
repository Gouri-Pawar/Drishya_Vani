package com.example.drishya_vani;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView historyRecyclerView;
    HistoryAdapter adapter;
    ArrayList<PlaceModel> placeList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeList = new ArrayList<>();
        adapter = new HistoryAdapter(placeList, this);
        historyRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchPlaces();
    }

    private void fetchPlaces() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("visited_places")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    placeList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String name = doc.getString("name");
                        String description = doc.getString("description");
                        String date = doc.getString("date");

                        placeList.add(new PlaceModel(name, description, date));
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}