package com.example.oop_project.hunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.oop_project.R; // Replace with your actual package name
import java.util.List;

public class BountyHunterAdapter extends RecyclerView.Adapter<BountyHunterAdapter.ViewHolder> {

    private final Context context;
    private final List<BountyHunter> bountyHunters;

    public BountyHunterAdapter(Context context, List<BountyHunter> bountyHunters) {
        this.context = context;
        this.bountyHunters = bountyHunters;
        Log.d("BountyHunterAdapter", "Adapter created. Initial item count: " + getItemCount()); // Log in constructor
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
        Log.d("BountyHunterAdapter", "onBindViewHolder called for position: " + position);
        if (position >= 0 && position < bountyHunters.size()) {  // Check for valid position
            BountyHunter currentHunter = bountyHunters.get(position);
            holder.nameTextView.setText(currentHunter.getName());

            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    currentHunter.getImagePath(), "drawable", holder.itemView.getContext().getPackageName());


            holder.imageView.setImageResource(resId); // Placeholder image


            holder.meleeAtkTextView.setText(String.valueOf(currentHunter.getMeleAttack()));
            holder.meleeDefTextView.setText(String.valueOf(currentHunter.getMeleDefense()));
            holder.rangedAtkTextView.setText(String.valueOf(currentHunter.getRangedAttack()));
            holder.rangedDefTextView.setText(String.valueOf(currentHunter.getRangedDefense()));
            holder.hpTextView.setText(String.valueOf(currentHunter.getMaxHealth()));
            holder.xpTextView.setText(String.valueOf(currentHunter.getExperience()));
            holder.checkBox.setChecked(currentHunter.isSelected());

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    currentHunter.setSelected(true);
                    notifyDataSetChanged();
                } else {
                    currentHunter.setSelected(false);
                }
            });
        } else {
            Log.e("BountyHunterAdapter", "Invalid position: " + position + ". Data size: " + bountyHunters.size());
        }
    }

    @Override
    public int getItemCount() {
        int count = bountyHunters.size();
        Log.d("BountyHunterAdapter", "getItemCount() returns: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;

        public TextView meleeAtkTextView;
        public TextView meleeDefTextView;
        public TextView rangedAtkTextView;
        public TextView rangedDefTextView;
        public TextView hpTextView;
        public TextView xpTextView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.hunter_image);
            nameTextView = itemView.findViewById(R.id.hunter_name);
            meleeAtkTextView = itemView.findViewById(R.id.MeleATKtext);
            meleeDefTextView = itemView.findViewById(R.id.MeleDEFtext);
            rangedAtkTextView = itemView.findViewById(R.id.RangedATKtext);
            rangedDefTextView = itemView.findViewById(R.id.RangedDEFtext);
            hpTextView = itemView.findViewById(R.id.hptext);
            xpTextView = itemView.findViewById(R.id.xptext);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}