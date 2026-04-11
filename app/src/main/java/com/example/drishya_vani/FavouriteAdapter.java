package com.example.drishya_vani;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    Context context;
    ArrayList<FavouriteModel> list;

    public FavouriteAdapter(Context context, ArrayList<FavouriteModel> list) {
        this.context = context;
        this.list = list;
    }

    // 🔥 remove from firebase
    private void removeFromFirebase(String placeId){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("favourites")
                .child(FirebaseAuth.getInstance().getUid())
                .child(placeId);

        ref.removeValue();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, subtitle;
        ImageView heart;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtPlaceName);
            subtitle = itemView.findViewById(R.id.txtFavStatus);
            heart = itemView.findViewById(R.id.imgHeart);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_favourite_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FavouriteModel place = list.get(position);

        holder.name.setText(place.getName());
        holder.subtitle.setText("Saved to favourites");

        // ❤️ Always filled because this screen shows favourites
        holder.heart.setImageResource(R.drawable.heart_filled);

        // 💥 Remove from favourites when heart clicked
        holder.heart.setOnClickListener(v -> {

            // confirm dialog (nice UX)
            new AlertDialog.Builder(context)
                    .setTitle("Remove Favourite")
                    .setMessage("Remove this place from favourites?")
                    .setPositiveButton("Remove", (dialog, which) -> {

                        removeFromFirebase(place.getId());

                        // remove from list & update UI instantly
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}