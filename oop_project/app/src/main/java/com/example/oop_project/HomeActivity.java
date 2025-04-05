package com.example.oop_project; // Replace with your actual package name

import android.os.Bundle;
import android.util.Log;

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
    private final List<BountyHunter> bountyHunters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerViewBountyHunters);
        Log.d("HomeActivity", "RecyclerView found: " + (recyclerView != null)); // Check if recyclerView is found
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the (initially empty) list
        adapter = new BountyHunterAdapter(this, bountyHunters);
        Log.d("HomeActivity", "Adapter initialized: " + (adapter != null)); // Check if adapter is initialized
        recyclerView.setAdapter(adapter); // Set the adapter **before** loading data
        Log.d("HomeActivity", "Adapter set on RecyclerView.");

        // Load bounty hunters from JSON using JsonHelper
        loadBountyHunters();
    }

    private void loadBountyHunters() {
        List<BountyHunter> loadedHunters = JsonHelper.loadBountyHunters(this, "my_bounty_hunters.json");
        if (loadedHunters != null && !loadedHunters.isEmpty()) {
            bountyHunters.clear();
            bountyHunters.addAll(loadedHunters);
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