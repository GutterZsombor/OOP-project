package com.example.oop_project; // Replace with your actual package name

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.BountyHunterAdapter;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Or GridLayoutManager

        // Load bounty hunters from JSON
        loadBountyHuntersFromJson();

        adapter = new BountyHunterAdapter(this, bountyHunters);
        recyclerView.setAdapter(adapter);
    }

    private void loadBountyHuntersFromJson() {
        try {
            InputStream inputStream = getApplicationContext().getAssets().open("my_bounty_hunters.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<BountyHunter>>() {}.getType();
            List<BountyHunter> loadedHunters = gson.fromJson(reader, listType);

            bountyHunters.addAll(loadedHunters);

            // If the adapter is already initialized, update its data:
            if (adapter != null) {
                adapter.updateData(bountyHunters);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error appropriately, e.g., display an error message
        }
    }
}