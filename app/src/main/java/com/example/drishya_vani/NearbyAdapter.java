package com.example.drishya_vani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        TextView placeName;
        ImageView heart;

        public ViewHolder(View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.txtPlaceName);
            heart = itemView.findViewById(R.id.imgFav);
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
        holder.placeName.setText(place.getName());

        // 🔥 Check if already favourite when item loads
        favouriteManager.isFavourite(place.getName(), isFav -> {
            if (isFav)
                holder.heart.setImageResource(R.drawable.heart_filled);
            else
                holder.heart.setImageResource(R.drawable.ic_heart_outline);
        });

        // ❤️ Toggle favourite on click
        holder.heart.setOnClickListener(v -> {

            favouriteManager.isFavourite(place.getName(), isFav -> {

                if (isFav) {
                    // Remove from favourites
                    favouriteManager.removeFavourite(place.getName());
                    holder.heart.setImageResource(R.drawable.ic_heart_outline);

                } else {
                    // Add to favourites
                    favouriteManager.addFavourite(place.getName());
                    holder.heart.setImageResource(R.drawable.heart_filled);
                }

            });

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}