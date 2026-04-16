package com.example.drishya_vani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    ArrayList<NearbyPlaceModel> list;
    Context context;
    FavouriteManager favouriteManager;

    public NearbyAdapter(ArrayList<NearbyPlaceModel> list, Context context) {
        this.list = list;
        this.context = context;
        favouriteManager = new FavouriteManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // FIX BUG 3: Added txtEmoji — was completely missing, so no emoji ever showed
        TextView  txtEmoji;
        TextView  placeName;
        TextView  placeType;
        TextView  placeDistance;
        ImageView heart;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEmoji      = itemView.findViewById(R.id.txtPlaceEmoji); // FIX BUG 3
            placeName     = itemView.findViewById(R.id.txtPlaceName);
            placeType     = itemView.findViewById(R.id.txtPlaceType);
            placeDistance = itemView.findViewById(R.id.txtDistance);
            heart         = itemView.findViewById(R.id.imgFav);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_nearby_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NearbyPlaceModel place = list.get(position);

        // FIX BUG 3: Set emoji icon — was never set before
        holder.txtEmoji.setText(getEmojiForType(place.getType()));

        // Place name — only the real name, never the key
        holder.placeName.setText(place.getName());

        // Type pill — capitalize, replace underscores with spaces
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
            holder.placeDistance.setText(String.format(Locale.ROOT, "%.0f m away", dist));
        } else {
            holder.placeDistance.setText(String.format(Locale.ROOT, "%.1f km away", dist / 1000));
        }

        // Unique key for Firestore document
        String placeKey = place.getKey();

        // Restore heart state when item is bound
        favouriteManager.isFavourite(placeKey, isFav ->
                holder.heart.setImageResource(
                        isFav ? R.drawable.ic_heart_filled
                                : R.drawable.ic_heart_outline)
        );

        holder.heart.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            // STEP 1 — Toggle UI instantly (no waiting 🔥)
            boolean currentlyFav =
                    holder.heart.getDrawable().getConstantState() ==
                            context.getDrawable(R.drawable.ic_heart_filled).getConstantState();

            if (currentlyFav) {
                holder.heart.setImageResource(R.drawable.ic_heart_outline);
                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                favouriteManager.removeFavourite(placeKey);
            } else {
                holder.heart.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                favouriteManager.addFavourite(
                        place.getName(),
                        place.getType(),
                        place.getType(),
                        place.getLat(),
                        place.getLon()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ── Emoji map ─────────────────────────────────────────────────────────────
    // FIX BUG 3: Entire method was missing from NearbyAdapter.
    // Without it, emoji TextViews were always blank in Nearby Places, AND because
    // the "type" field was never saved correctly, FavouriteAdapter also showed
    // wrong/default emojis in Favourite Places.
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
            case "blood_bank": case "blood bank": return "🩸";
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