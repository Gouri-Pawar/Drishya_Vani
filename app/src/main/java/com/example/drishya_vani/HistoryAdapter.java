package com.example.drishya_vani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
        holder.placeName.setText(place.getPlaceName());

        List<String> visits = place.getVisits();

        if (visits != null && !visits.isEmpty()) {

            // Sort dates ascending
            List<String> sorted = new ArrayList<>(visits);
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
            sorted.sort((a, b) -> {
                try {
                    return sdf.parse(a).compareTo(sdf.parse(b));
                } catch (Exception e) {
                    return 0;
                }
            });

            StringBuilder sb = new StringBuilder();
            for (String date : sorted) {
                sb.append("• ").append(date).append("\n");
            }
            holder.placeVisits.setText(sb.toString().trim());

        } else {
            holder.placeVisits.setText("No visits yet");
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView placeName, placeVisits;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            placeVisits = itemView.findViewById(R.id.placeVisits);
        }
    }
}