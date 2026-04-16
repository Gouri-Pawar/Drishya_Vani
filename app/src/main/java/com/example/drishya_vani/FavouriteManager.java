package com.example.drishya_vani;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FavouriteManager {

    FirebaseFirestore db;
    String userId;

    public FavouriteManager(Context context) {
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    // ⭐ ADD FAVOURITE
    public void addFavourite(String name, String type, String placeType, double lat, double lon) {

        if (userId == null) return;

        String key = FavouriteModel.generateKey(name, lat, lon);

        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("name", name);
        data.put("type", type);
        data.put("placeType", placeType);
        data.put("lat", lat);
        data.put("lon", lon);

        db.collection("users")
                .document(userId)
                .collection("favourite_places")
                .document(key)
                .set(data);
    }

    // ⭐ REMOVE FAVOURITE
    public void removeFavourite(String key) {

        if (userId == null) return;

        db.collection("users")
                .document(userId)
                .collection("favourite_places")
                .document(key)
                .delete();
    }

    // ⭐ CHECK FAVOURITE
    public void isFavourite(String key, FavouriteCallback callback) {

        if (userId == null) {
            callback.onResult(false);
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("favourite_places")
                .document(key)
                .get()
                .addOnSuccessListener(doc -> callback.onResult(doc.exists()));
    }

    public interface FavouriteCallback {
        void onResult(boolean isFav);
    }
}