package com.example.drishya_vani;

import android.content.Context;
import android.provider.Settings;

import com.google.firebase.firestore.FirebaseFirestore;

public class FavouriteManager {

    FirebaseFirestore db;
    String userId;

    public FavouriteManager(Context context) {
        db = FirebaseFirestore.getInstance();
        userId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    // ⭐ Add to favourites
    public void addFavourite(String placeName) {
        db.collection("favourites")
                .document(userId)
                .collection("places")
                .document(placeName)
                .set(new PlaceFavModel(placeName));
    }

    // ❌ Remove from favourites
    public void removeFavourite(String placeName) {
        db.collection("favourites")
                .document(userId)
                .collection("places")
                .document(placeName)
                .delete();
    }

    // 🔍 Check if already favourite
    public void isFavourite(String placeName, FavouriteCallback callback) {
        db.collection("favourites")
                .document(userId)
                .collection("places")
                .document(placeName)
                .get()
                .addOnSuccessListener(doc -> {
                    callback.onResult(doc.exists());
                });
    }

    public interface FavouriteCallback {
        void onResult(boolean isFav);
    }
}