package com.example.drishya_vani;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Favourite_Places extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout emptyLayout;
    FavouriteAdapter adapter;
    ArrayList<FavouriteModel> favList;

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);

        recyclerView = findViewById(R.id.recyclerFavourites);
        emptyLayout = findViewById(R.id.layoutEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favList = new ArrayList<>();
        adapter = new FavouriteAdapter(this, favList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID);

        fetchFavourites();
    }

    private void fetchFavourites() {

        db.collection("favourites")
                .document(userId)
                .collection("places")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    favList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        favList.add(new FavouriteModel(name));
                    }

                    adapter.notifyDataSetChanged();

                    // show empty state if no favourites
                    if (favList.isEmpty()) {
                        emptyLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                });
    }
}