package com.example.oop_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.BountyHunterAdapter;
import com.example.oop_project.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnViewHome;
    private Button btnViewTraining;
    private Button btnViewBattle;
    private Button btnHireHunter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewHome = findViewById(R.id.btnViewHome);
        btnViewTraining = findViewById(R.id.btnViewTraining);
        btnViewBattle = findViewById(R.id.btnViewBattle);
        btnHireHunter = findViewById(R.id.btnHireHunter);


        // Set click listener for View Home button
        btnViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "View Home button clicked");
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnHireHunter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Hire Hunter button clicked");
                Intent intent = new Intent(MainActivity.this, HireHunterActivity.class);
                startActivity(intent);
            }
        });


        //JsonHelper.DONOTUSEcopyJson( this,"not_hired_bounty_hunters.json");
        //JsonHelper.DONOTUSEcopyJson(this ,"my_bounty_hunters.json");

        // Disable other buttons for now
        btnViewTraining.setEnabled(false);
        btnViewBattle.setEnabled(false);

        Log.d(TAG, "Other buttons disabled.");
    }
}