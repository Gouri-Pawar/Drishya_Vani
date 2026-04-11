package com.example.drishya_vani;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    Context context;
    ArrayList<FavouriteModel> list;
    FirebaseFirestore db;
    String userId;

    public FavouriteAdapter(Context context, ArrayList<FavouriteModel> list) {
        this.context = context;
        this.list    = list;
        this.db      = FirebaseFirestore.getInstance();
        this.userId  = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  emojiIcon;
        TextView  placeName;
        TextView  placeType;
        TextView  placeDistance;
        ImageView imgRemove;   // ❌ remove from favourites

        public ViewHolder(View itemView) {
            super(itemView);
            emojiIcon     = itemView.findViewById(R.id.txtPlaceEmoji);
            placeName     = itemView.findViewById(R.id.txtPlaceName);
            placeType     = itemView.findViewById(R.id.txtPlaceType);
            placeDistance = itemView.findViewById(R.id.txtDistance);
            imgRemove     = itemView.findViewById(R.id.imgFav);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // FIX: Was inflating R.layout.item_nearby_place — corrected to item_favourite_place
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_favourite_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavouriteModel place = list.get(position);

        // Emoji icon
        holder.emojiIcon.setText(getEmojiForType(place.getType()));

        // Name
        holder.placeName.setText(place.getName());

        // Type pill — clean underscores, capitalise
        String typeLabel = place.getType();
        if (typeLabel != null && !typeLabel.isEmpty()) {
            typeLabel = typeLabel.replace("_", " ");
            typeLabel = typeLabel.substring(0, 1).toUpperCase(Locale.ROOT)
                    + typeLabel.substring(1);
        }
        holder.placeType.setText(typeLabel);

        // Distance
        double dist = place.getDistance();
        if (dist == 0) {
            holder.placeDistance.setText("Distance unavailable");
        } else if (dist < 1000) {
            holder.placeDistance.setText(
                    String.format(Locale.ROOT, "%.0f m away", dist));
        } else {
            holder.placeDistance.setText(
                    String.format(Locale.ROOT, "%.1f km away", dist / 1000));
        }

        // ❤️ Already a favourite — show filled heart
        holder.imgRemove.setImageResource(R.drawable.ic_heart_filled);

        // Tap heart → remove from favourites
        holder.imgRemove.setOnClickListener(v -> {
            String placeKey = place.getKey();

            db.collection("favourites")
                    .document(userId)
                    .collection("places")
                    .document(placeKey)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_ID) {
                            list.remove(pos);
                            notifyItemRemoved(pos);
                            Toast.makeText(context,
                                    "Removed from favourites",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ── Emoji map ─────────────────────────────────────────────────────────────

    private String getEmojiForType(String type) {
        if (type == null) return "📍";

        switch (type.toLowerCase(Locale.ROOT).replace(" ", "_")) {

            case "hotel": case "guest_house":
            case "hostel": case "motel":        return "🏨";
            case "museum":                      return "🏛️";
            case "monument": case "memorial":   return "🗿";
            case "viewpoint":                   return "🌄";
            case "attraction":                  return "🎡";
            case "theme_park":                  return "🎢";
            case "zoo":                         return "🦁";
            case "aquarium":                    return "🐠";
            case "artwork":                     return "🎨";
            case "gallery":                     return "🖼️";
            case "camp_site":                   return "⛺";
            case "picnic_site":                 return "🧺";
            case "information":                 return "ℹ️";
            case "hospital": case "clinic":     return "🏥";
            case "pharmacy":                    return "💊";
            case "doctors":                     return "👨‍⚕️";
            case "dentist":                     return "🦷";
            case "restaurant":                  return "🍽️";
            case "fast_food":                   return "🍔";
            case "cafe":                        return "☕";
            case "bar": case "pub":             return "🍺";
            case "food_court":                  return "🥘";
            case "ice_cream":                   return "🍦";
            case "bank":                        return "🏦";
            case "atm":                         return "💳";
            case "school":                      return "🏫";
            case "college": case "university":  return "🎓";
            case "library":                     return "📚";
            case "police":                      return "👮";
            case "fire_station":                return "🚒";
            case "fuel":                        return "⛽";
            case "place":                       return "🛣️";
            case "parking":                     return "🅿️";
            case "bus_station": case "bus_stop":return "🚌";
            case "taxi":                        return "🚕";
            case "place_of_worship":            return "🛕";
            case "cinema":                      return "🎬";
            case "theatre":                     return "🎭";
            case "nightclub":                   return "🪩";
            case "post_office":                 return "📮";
            case "marketplace":                 return "🛒";
            case "gym": case "fitness_centre":  return "🏋️";
            case "swimming_pool":               return "🏊";
            case "toilets":                     return "🚻";
            case "shelter":                     return "🛖";
            case "charging_station":            return "🔋";
            case "fort": case "castle":         return "🏰";
            case "ruins":                       return "🏚️";
            case "temple":                      return "⛩️";
            case "mosque":                      return "🕌";
            case "church":                      return "⛪";
            case "archaeological_site":         return "⚱️";
            case "battlefield":                 return "⚔️";
            case "manor": case "palace":        return "🏯";
            case "tomb":                        return "🪦";
            case "wayside_shrine":              return "🙏";
            case "park":                        return "🌳";
            case "garden":                      return "🌸";
            case "nature_reserve":              return "🌿";
            case "beach":                       return "🏖️";
            case "waterfall":                   return "💧";
            case "cave_entrance":               return "🕳️";
            case "stadium":                     return "🏟️";
            case "sports_centre":               return "🏅";

            default:                            return "📍";
        }
    }
}