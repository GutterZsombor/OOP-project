package com.example.oop_project.hunter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.oop_project.R; // Replace with your actual package name
import java.util.List;

public class BountyHunterAdapter extends RecyclerView.Adapter<BountyHunterAdapter.ViewHolder> {

    private final Context context;
    private List<BountyHunter> bountyHunters;

    public BountyHunterAdapter(Context context, List<BountyHunter> bountyHunters) {
        this.context = context;
        this.bountyHunters = bountyHunters;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        // Add other views here (e.g., for other stats)

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.hunter_image);
            nameTextView = itemView.findViewById(R.id.hunter_name);
            // Initialize other views here
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bhuntercardview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BountyHunter currentHunter = bountyHunters.get(position);
        Log.d("Adapter", "Binding data for position: " + position + ", Name: " + currentHunter.getName());
        holder.nameTextView.setText(currentHunter.getName()); // Use getter method
        // Load image using Glide:
        Glide.with(context)
                .load(currentHunter.getImagePath()) // Use getter method
                .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder
                .error(R.drawable.ic_launcher_foreground)         // Optional error image
                .into(holder.imageView);
        // Set other data on the views (e.g., holder.statTextView.setText(String.valueOf(currentHunter.getSomeStat()));)
    }

    @Override
    public int getItemCount() {
        return bountyHunters.size();
    }

    public void updateData(List<BountyHunter> newBountyHunters) {
        this.bountyHunters = newBountyHunters;
        notifyDataSetChanged();
    }
}