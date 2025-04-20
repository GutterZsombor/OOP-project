package com.example.oop_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.BountyHunterAdapter;
import com.example.oop_project.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnViewHome;
    private Button btnViewStatistics;

    private Button btnHireHunter;
    private TextView atHomeText;
    private int numofHired = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewHome = findViewById(R.id.btnViewHome);
        btnViewStatistics = findViewById(R.id.btnViewStatistics);

        btnHireHunter = findViewById(R.id.btnHireHunter);
        atHomeText = findViewById(R.id.TextHunters);



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

        //clean run
/*
        JsonHelper.DONOTUSEcopyJson( this,"not_hired_bounty_hunters.json");
        JsonHelper.DONOTUSEcopyJson(this ,"my_bounty_hunters.json");
        JsonHelper.DONOTUSEcopyJson(this ,"bounty_hunters.json");
        JsonHelper.DONOTUSEcopyJson(this ,"Statistics.json");

*/
        //load json files
        JsonHelper.copyJsonIfNotExists( this,"not_hired_bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this ,"my_bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this ,"bounty_hunters.json");
        JsonHelper.copyJsonIfNotExists(this ,"Statistics.json");

        //load myhunter list and geth its size
        numofHired = JsonHelper.loadBountyHunters(this, "my_bounty_hunters.json").size();
        //add text to textView
        atHomeText.setText("Hunters at Home: "+numofHired);


        btnViewStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Hire Hunter button clicked");
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });



    }
}