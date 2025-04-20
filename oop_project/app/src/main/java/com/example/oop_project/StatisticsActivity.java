package com.example.oop_project;

import static com.example.oop_project.util.JsonHelper.loadBountyHunters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.BountyHunterAdapter;
import com.example.oop_project.hunter.BountyHunterStatisticsAdapter;
import com.example.oop_project.hunter.HireableHunterAdapter;
import com.example.oop_project.hunter.Statistic;
import com.example.oop_project.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "StatisticsActivity";
    private RecyclerView recyclerView;
    private BountyHunterStatisticsAdapter adapter;
    private final List<BountyHunter> bountyHunters = new ArrayList<>();

    private Button moveToMain,movetoWin,movetoLose,movetoBattle;

    private TextView hunterHired;
    private TextView localBattles;
    private TextView onlineBattles;
    private TextView trainingSessions;

    @Override
    protected void onResume() {
        super.onResume();
        loadBountyHunters();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        JsonHelper.copyJsonIfNotExists(this, "not_hired_bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this, "my_bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this, "bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this, "Statistics.json");


        recyclerView = findViewById(R.id.recyclerViewBountyHunters);
        moveToMain = findViewById(R.id.movetoMain);
        movetoWin = findViewById(R.id.viewWin);
        movetoLose = findViewById(R.id.viewLose);
        movetoBattle = findViewById(R.id.viewBattle);
        hunterHired = findViewById(R.id.textHunterHired);
        localBattles = findViewById(R.id.textLocalBat);
        onlineBattles = findViewById(R.id.textOnlineBattle);
        trainingSessions = findViewById(R.id.textTrainingSes);

        Log.d(TAG, "RecyclerView found: " + (recyclerView != null)); // Check if recyclerView is found
        //set up horizontal recycle view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter with the  list
        adapter = new BountyHunterStatisticsAdapter(this, bountyHunters);
        Log.d(TAG, "Adapter initialized: " + (adapter != null));
        recyclerView.setAdapter(adapter); // Set the adapter before*loading data
        Log.d(TAG, "Adapter set on RecyclerView.");

        loadBountyHunters();

        int [] globalStats = loadGlobalStats();


        hunterHired.setText(String.valueOf(globalStats[0]));
        localBattles.setText(String.valueOf(globalStats[1]));
        onlineBattles.setText(String.valueOf(globalStats[2]));
        trainingSessions.setText(String.valueOf(globalStats[3]));





        moveToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        movetoWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(StatisticsActivity.this, ChartActivity.class);
                intent.putExtra("win", true);
                startActivity(intent);
            }
        });
        movetoLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(StatisticsActivity.this, ChartActivity.class);
                intent.putExtra("win", false);
                startActivity(intent);
            }
        });
        movetoBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(StatisticsActivity.this, ChartActivity.class);
                intent.putExtra("total", true);
                startActivity(intent);
            }
        });

    }



    private void loadBountyHunters() {
        List<BountyHunter> loadedHunters = JsonHelper.loadBountyHunters(this, "my_bounty_hunters.json");

        if (loadedHunters != null && !loadedHunters.isEmpty()) {
            bountyHunters.clear();
            bountyHunters.addAll(loadedHunters);

            for (BountyHunter hunter : bountyHunters) {
                hunter.setSelected(false);
            }

            loadStats();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged(); // Notify the adapter on the UI thread
                    Log.d(TAG, "Data loaded and adapter notified. Item count: " + adapter.getItemCount());
                }
            });
        } else {
            Log.e(TAG, "Failed to load bounty hunters or list is empty.");

        }
    }
    private void loadStats() {

        for (BountyHunter hunter : bountyHunters) {
            String hunterName = hunter.getName();
            //Log.w(TAG, hunterName);
            Statistic hunterStat = JsonHelper.loadHunterStatistic(this, hunterName, "Statistics.json");

            if (hunterStat != null) {
                hunter.setStatistic(hunterStat);
            }
        }
    }

    private int[] loadGlobalStats() {

        int[] globalStats = JsonHelper.loadGlobalStats(this,"Statistics.json");

        return globalStats;

    }

}


