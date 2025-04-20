package com.example.oop_project.hunter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oop_project.R;

import java.util.List;

public class HireableHunterAdapter extends RecyclerView.Adapter<HireableHunterAdapter.ViewHolder> {

    private final Context context;
    private final List<BountyHunter> hireableHunters;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final OnHireClickListener listener;

    // Define the listener interface
    public interface OnHireClickListener {
        void onHireClick(BountyHunter hunter);
    }


    public HireableHunterAdapter(Context context, List<BountyHunter> hireableHunters, OnHireClickListener listener) {
        this.context = context;
        this.hireableHunters = hireableHunters;
        this.listener = listener; // Initialize the listener for radio
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hirebhuntercard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {

        BountyHunter hunter = hireableHunters.get(position);
        holder.bind(hunter,position); //unlike other 2 addapters passing values happens in bind()
        //troubles with position solved by passing bind into holder
        /*holder.nameTextView.setText(hunter.getName());
        holder.meleeAtkTextView.setText(String.valueOf(hunter.getMeleAttack()));
        holder.meleeDefTextView.setText(String.valueOf(hunter.getMeleDefense()));
        holder.rangedAtkTextView.setText(String.valueOf(hunter.getRangedAttack()));
        holder.rangedDefTextView.setText(String.valueOf(hunter.getRangedDefense()));
        holder.hpTextView.setText(String.valueOf(hunter.getMaxHealth()));


        holder.selectRadioButton.setChecked(position == selectedPosition);
        holder.selectRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != selectedPosition) {
                    notifyItemChanged(selectedPosition); // Uncheck previously selected
                    selectedPosition = holder.getAdapterPosition();
                    notifyItemChanged(selectedPosition); // Check current
                    Log.d("Adapter", "Selected hunter at position: " + selectedPosition);
                }
            }
        });*/
    }

    public BountyHunter getSelectedHunter() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return hireableHunters.get(selectedPosition);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return hireableHunters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public TextView meleeAtkTextView;
        public TextView meleeDefTextView;
        public TextView rangedAtkTextView;
        public TextView rangedDefTextView;
        public TextView hpTextView;

        private final RadioButton selectRadioButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.hunter_name);
            meleeAtkTextView = itemView.findViewById(R.id.MeleATKtext);
            meleeDefTextView = itemView.findViewById(R.id.MeleDEFtext);
            rangedAtkTextView = itemView.findViewById(R.id.RangedATKtext);
            rangedDefTextView = itemView.findViewById(R.id.RangedDEFtext);
            hpTextView = itemView.findViewById(R.id.hptext);
            selectRadioButton = itemView.findViewById(R.id.radio_select_hunter);

        }

        public void bind(final BountyHunter hunter, final int position) {
            nameTextView.setText(hunter.getName());
            meleeAtkTextView.setText(String.valueOf(hunter.getMeleAttack()));
            meleeDefTextView.setText(String.valueOf(hunter.getMeleDefense()));
            rangedAtkTextView.setText(String.valueOf(hunter.getRangedAttack()));
            rangedDefTextView.setText(String.valueOf(hunter.getRangedDefense()));
            hpTextView.setText(String.valueOf(hunter.getMaxHealth()));


            selectRadioButton.setChecked(position == selectedPosition);
            selectRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != selectedPosition) {
                        notifyItemChanged(selectedPosition); // Uncheck previously selected
                        selectedPosition = getAdapterPosition();
                        notifyItemChanged(selectedPosition); // Check current
                        Log.d("Adapter", "Selected hunter at position: " + selectedPosition);
                    }
                }
            });

        }
    }
}
