package com.example.oop_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.HireableHunterAdapter;
import com.example.oop_project.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class HireHunterActivity extends AppCompatActivity implements HireableHunterAdapter.OnHireClickListener {

    private static final String TAG = "HireHunterActivity";
    private RecyclerView recyclerView;
    private HireableHunterAdapter adapter;
    private List<BountyHunter> hireableHunters = new ArrayList<>();

    private Button btnHireHunter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);

        recyclerView = findViewById(R.id.recyclerViewHirehunter);
        btnHireHunter = findViewById(R.id.btnHireHunter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        JsonHelper.copyJsonIfNotExists(this, "not_hired_bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this, "my_bounty_hunters.json");

        // Load hireable hunters (replace with your actual loading logic)
        loadHireableHunters();

        // Initialize the adapter, passing the list and the listener (this activity)
        adapter = new HireableHunterAdapter(this, hireableHunters, this);
        recyclerView.setAdapter(adapter);

        btnHireHunter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hireSelectedHunter();
            }
        });
    }

    private void loadHireableHunters() {
        // Replace with your actual data loading (from JSON, database, etc.)
        hireableHunters = JsonHelper.loadBountyHunters(this, "not_hired_bounty_hunters.json");
        if (hireableHunters == null || hireableHunters.isEmpty()) {
            Log.w(TAG, "No hireable hunters found or failed to load.");
            // Handle the case where no hunters are available (e.g., display a message)
        } else {
            Log.d(TAG, "Loaded " + hireableHunters.size() + " hireable hunters.");
        }
    }

    @Override
    public void onHireClick(BountyHunter hunter) {
        // Implement the hire logic here.
        Log.d(TAG, "Hiring hunter: " + hunter.getName());
        hireSelectedHunter();

        //  Example: For now, just remove from the list and notify adapter (you'll need to adapt this to your actual data persistence)
        hireableHunters.remove(hunter);
        adapter.notifyDataSetChanged();
        // You might also want to navigate back, show a confirmation, etc.
        // finish();
    }

    private void hireSelectedHunter() {
        BountyHunter selectedHunter = adapter.getSelectedHunter();
        if (selectedHunter == null) {
            Toast.makeText(this, "Please select a hunter to hire.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting to hire: " + selectedHunter.getName());

        // 1. Load existing hunters
        List<BountyHunter> myHunters = JsonHelper.loadBountyHunters(this, "my_bounty_hunters.json");
        if (myHunters == null) {
            myHunters = new ArrayList<>(); // Initialize if loading fails
        }

        // 2. Add the selected hunter
        myHunters.add(selectedHunter);

        // 3. Save the updated list of hired hunters
        if (JsonHelper.saveBountyHunters(this, myHunters, "my_bounty_hunters.json")) {
            Log.d(TAG, "Successfully saved hired hunter to my_bounty_hunters.json");

            // 4. Remove the hired hunter from the hireable list and save
            hireableHunters.remove(selectedHunter);
            if (JsonHelper.saveBountyHunters(this, hireableHunters, "not_hired_bounty_hunters.json")) {
                Log.d(TAG, "Removed hired hunter from not_hired_bounty_hunters.json");
                Toast.makeText(this, "Hired " + selectedHunter.getName() + "!", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged(); // Update the RecyclerView
            } else {
                Log.e(TAG, "Failed to update not_hired_bounty_hunters.json");
                Toast.makeText(this, "Failed to update hireable list.", Toast.LENGTH_SHORT).show();
            }
            // Optionally navigate back or update UI
            // finish();
        } else {
            Log.e(TAG, "Failed to save hired hunter to my_bounty_hunters.json");
            Toast.makeText(this, "Failed to hire " + selectedHunter.getName() + ".", Toast.LENGTH_SHORT).show();
        }
    }
}
