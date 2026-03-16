package com.example.drishya_vani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<PlaceModel> placeList;

    public HistoryAdapter(ArrayList<PlaceModel> placeList, Context context) {
        this.placeList = placeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_place_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PlaceModel place = placeList.get(position);

        holder.placeName.setText(place.getName());
        holder.placeLocation.setText(place.getDescription());
        holder.placeDate.setText(place.getDate());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView placeName;
        TextView placeLocation;
        TextView placeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            placeLocation = itemView.findViewById(R.id.placeLocation);
            placeDate = itemView.findViewById(R.id.placeDate);
        }
    }
}
