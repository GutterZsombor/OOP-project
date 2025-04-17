package com.example.oop_project.hunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.oop_project.R; // Replace with your actual package name
import com.example.oop_project.util.JsonHelper;

import java.util.List;


public class BountyHunterStatisticsAdapter extends RecyclerView.Adapter<BountyHunterStatisticsAdapter.ViewHolder> {
    private final Context context;
    private final List<BountyHunter> bountyHunters;

    //almost 1 to 1 normal bounty hunter adapter except fro statistic values

    private static final String TAG = "BountyHunterStatisticsAdapter";
    public BountyHunterStatisticsAdapter(Context context, List<BountyHunter> bountyHunters) {
        this.context = context;
        this.bountyHunters = bountyHunters;

        Log.d(TAG, "Adapter created. Initial item count: " + getItemCount()); // Log in constructor
    }



    @NonNull
    @Override
    public BountyHunterStatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "created");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bhunterstatisticscardview, parent, false);
        return new BountyHunterStatisticsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BountyHunterStatisticsAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);
        if (position >= 0 && position < bountyHunters.size()) {
            BountyHunter currentHunter = bountyHunters.get(position);
            holder.nameTextView.setText(currentHunter.getName());

            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    currentHunter.getImagePath(), "drawable", holder.itemView.getContext().getPackageName());


            holder.imageView.setImageResource(resId); // Placeholder image

            holder.textBattles.setText(String.valueOf(currentHunter.getStatistic().getNumberOfWins()+currentHunter.getStatistic().getNumberOfLosts()));
            holder.textwiinlo.setText(String.valueOf(currentHunter.getStatistic().getWinLossRatio()));
            holder.textTraining.setText(String.valueOf(currentHunter.getStatistic().getNumberOfTrainingSessions()));

            holder.textWin.setText(String.valueOf(currentHunter.getStatistic().getNumberOfWins()));
            holder.textLoss.setText(String.valueOf(currentHunter.getStatistic().getNumberOfLosts()));

            holder.listWin.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, currentHunter.getStatistic().getWins()));
            holder.listloose.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, currentHunter.getStatistic().getLost()));


        } else {
            Log.e(TAG, "Invalid position: " + position + ". Data size: " + bountyHunters.size());
        }
    }

    @Override
    public int getItemCount() {
        int count = bountyHunters.size();
        Log.d(TAG, "getItemCount() returns: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView ;
        public TextView textBattles ;
        public TextView textwiinlo ;

        public TextView textTraining ;
        public TextView textWin ;
        public TextView textLoss ;
        public ListView listWin ;
        public ListView listloose ;




        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.hunter_image);
            nameTextView = itemView.findViewById(R.id.hunter_name);
            textBattles = itemView.findViewById(R.id.textBattles);
            textwiinlo = itemView.findViewById(R.id.textwiinloss);
            textTraining = itemView.findViewById(R.id.textTraining);
            textWin = itemView.findViewById(R.id.textWin);
            textLoss = itemView.findViewById(R.id.textLoss);
            listWin = itemView.findViewById(R.id.listWin);
            listloose = itemView.findViewById(R.id.listloose);

        }
    }
}
