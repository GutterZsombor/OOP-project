package com.example.oop_project; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.BountyHunterAdapter;
import com.example.oop_project.util.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BountyHunterAdapter adapter;
    private  List<BountyHunter> bountyHunters = new ArrayList<>();

    private static final String TAG = "HomeActivity";

    private Button moveToTraining, moveToBattle,movetoOnlineBattle,movetoMain;
    private CheckBox selectedCheckBox;
    @Override
    protected void onResume() {
        super.onResume();
        loadBountyHunters(); // Reload or refresh your hunter list here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerViewBountyHunters);
        moveToTraining = findViewById(R.id.movetoTraining);
        moveToBattle = findViewById(R.id.MovetoBattle);
        movetoOnlineBattle = findViewById(R.id.MovetoOnlineBattle);
        movetoMain = findViewById(R.id.moveMain);

        Log.d(TAG, "RecyclerView found: " + (recyclerView != null)); // Check if recyclerView is found
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the (initially empty) list
        adapter = new BountyHunterAdapter(this, bountyHunters);
        Log.d("HomeActivity", "Adapter initialized: " + (adapter != null)); // Check if adapter is initialized
        recyclerView.setAdapter(adapter); // Set the adapter **before** loading data
        Log.d("HomeActivity", "Adapter set on RecyclerView.");


        loadBountyHunters();


        moveToTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BountyHunter> selectedHunters = new ArrayList<>();
                for (BountyHunter hunter : bountyHunters) {
                    if (hunter.isSelected()) {
                        selectedHunters.add(hunter);
                    }
                }

                if (selectedHunters.size() == 1) {
                    // If only one hunter is selected, pass it to the next activity
                    Intent intent = new Intent(HomeActivity.this, TrainActivity.class);
                    intent.putExtra("selectedHunter", selectedHunters.get(0)); // Send selected hunter
                    startActivity(intent);
                } else {
                    // Show a Toast if no or multiple hunters are selected
                    Toast.makeText(HomeActivity.this, "Please select exactly one bounty hunter", Toast.LENGTH_SHORT).show();
                }
            }
        });
        moveToBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BountyHunter> selectedHunters = new ArrayList<>();
                for (BountyHunter hunter : bountyHunters) {
                    if (hunter.isSelected()) {
                        selectedHunters.add(hunter);
                    }
                }

                if (selectedHunters.size() == 2) {
                    int [] globalStats = JsonHelper.loadGlobalStats(HomeActivity.this, "Statistics.json");
                    globalStats[1]++;
                    JsonHelper.updateGlobalStats(HomeActivity.this, globalStats, "Statistics.json");


                    BountyHunter hunter1 = selectedHunters.get(0);
                    BountyHunter hunter2 = selectedHunters.get(1);

                    if (hunter1 == null || hunter2 == null) {
                        Log.e(TAG, "Selected hunters are null. Cannot start battle.");
                        return;
                    }

                    Intent intent = new Intent(HomeActivity.this, BattleActivity.class);
                    intent.putExtra("myHunter", hunter1); // First hunter
                    intent.putExtra("enemyHunter", hunter2); // Second hunter
                    startActivity(intent);
                } else {

                    Toast.makeText(HomeActivity.this, "Please select exactly two bounty hunter", Toast.LENGTH_SHORT).show();
                }
            }
        });

        movetoOnlineBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BountyHunter> selectedHunters = new ArrayList<>();
                for (BountyHunter hunter : bountyHunters) {
                    if (hunter.isSelected()) {
                        selectedHunters.add(hunter);
                    }
                }

                if (selectedHunters.size() == 1) {
                    int [] globalStats = JsonHelper.loadGlobalStats(HomeActivity.this, "Statistics.json");
                    globalStats[2]++;
                    JsonHelper.updateGlobalStats(HomeActivity.this, globalStats, "Statistics.json");


                    BountyHunter hunter1 = selectedHunters.get(0);


                    if (hunter1 == null ) {
                        Log.e(TAG, "Selected hunter is null. Cannot start online battle.");
                        return;
                    }

                    Intent intent = new Intent(HomeActivity.this, MultiplayerSetupActivity.class);
                    intent.putExtra("selectedHunter", hunter1); // First hunter
                    //intent.putExtra("enemyHunter", hunter2); // Second hunter
                    startActivity(intent);
                } else {

                    Toast.makeText(HomeActivity.this, "Please select exactly One bounty hunter", Toast.LENGTH_SHORT).show();
                }
            }
        });

        movetoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged(); // Notify the adapter on the UI thread
                    Log.d("HomeActivity", "Data loaded and adapter notified. Item count: " + adapter.getItemCount());
                }
            });
        } else {
            Log.e("HomeActivity", "Failed to load bounty hunters or list is empty.");
            // Optionally display a message to the user indicating the data loading failed.
        }
    }
}