package com.example.oop_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.Statistic;
import com.example.oop_project.util.JsonHelper;

public class ChartActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    private TextView title;
    private final List<BountyHunter> bountyHunters = new ArrayList<>();
    private final String TAG = "ChartActivity";
    private int[] globalStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //get extra from indent
        boolean win=getIntent().getBooleanExtra("win",true);
        boolean total=getIntent().getBooleanExtra("total",false);
        anyChartView = findViewById(R.id.chart);
        title = findViewById(R.id.titleView);

        showChart(win,total);
        //set title
        if (total) {
            title.setText("Total Battles Chart");
        } else if (win) {
            title.setText("Battle Wins Chart");
        } else {
            title.setText("Battle Losses Chart");
        }

    }

    private void showChart(boolean win,boolean total) {

        Pie pie = AnyChart.pie();
        pie.animation(true);

        Map<String, Integer> hunterBattleMap = new HashMap<>();
        loadBountyHunters();
        globalStats = loadGlobalStats();
        int count = 0;
        // caclculate values according to win or total
        for (BountyHunter hunter : bountyHunters) {
            int battles = 0;
            if (win) {
            battles = hunter.getStatistic().getNumberOfWins();
            }

            else {
                battles=hunter.getStatistic().getNumberOfLosts();
            }

            if (total) {
                battles = hunter.getStatistic().getNumberOfWins()+hunter.getStatistic().getNumberOfLosts();
            }
            if (battles > 0) {
                count+=battles;
                hunterBattleMap.put(hunter.getName(), battles);
            }
        }
        // add values to chart
        List<DataEntry> dataEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : hunterBattleMap.entrySet()) {
            dataEntries.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
        }
        // set chart
        pie.data(dataEntries);
        // set title of chart
        if (total) {
            pie.title("Total Battles: "+(globalStats[1]+globalStats[2]));
        } else if (win) {
            pie.title("Battle Wins: "+count);
        } else {
            pie.title("Battle Losses: "+count);
        }
        pie.labels().position("inside");


        anyChartView.setChart(pie);
    }


//same as in Statistics

    private void loadBountyHunters() {
        List<BountyHunter> loadedHunters = JsonHelper.loadBountyHunters(this, "bounty_hunters.json");

        if (loadedHunters != null && !loadedHunters.isEmpty()) {
            bountyHunters.clear();
            bountyHunters.addAll(loadedHunters);

            for (BountyHunter hunter : bountyHunters) {
                hunter.setSelected(false);
            }

            loadStats();


        } else {
            Log.e(TAG, "Failed to load bounty hunters or list is empty.");

        }
    }
    private void loadStats() {

        for (BountyHunter hunter : bountyHunters) {
            String hunterName = hunter.getName();
            Log.w(TAG, hunterName);
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

