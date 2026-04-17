package com.example.drishya_vani;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    placeList.clear();

                    for (QueryDocumentSnapshot doc : value) {

                        String placeName = doc.getString("placeName");

                        List<String> visits =
                                (List<String>) doc.get("visits");

                        if (placeName == null) placeName = "Unknown Place";
                        if (visits == null) visits = new ArrayList<>();

                        placeList.add(new PlaceModel(placeName, visits));
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}