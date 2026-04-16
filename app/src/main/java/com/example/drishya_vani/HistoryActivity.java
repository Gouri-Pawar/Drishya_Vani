package com.example.drishya_vani;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("visited_places")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    placeList.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        // No history yet
                        placeList.add(new PlaceModel("No places visited yet", new ArrayList<>()));
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String name = doc.getString("name");
                        if (name == null) name = "Unknown Place";

                        List<String> visits = (List<String>) doc.get("visits");
                        if (visits == null) visits = new ArrayList<>();

                        placeList.add(new PlaceModel(name, visits));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    placeList.clear();
                    placeList.add(new PlaceModel("Failed to load history", new ArrayList<>()));
                    adapter.notifyDataSetChanged();
                });
    }
}