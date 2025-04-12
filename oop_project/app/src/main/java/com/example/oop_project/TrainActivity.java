package com.example.oop_project; // Replace with your actual package name

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.BountyHunterAdapter;
import com.example.oop_project.hunter.Statistic;
import com.example.oop_project.util.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TrainActivity extends AppCompatActivity {

    private Button btnTrainMelee, btnTrainRange, btnTrainXP, btnMoveHome;
    private ProgressBar trainingProgress;
    private TextView textViewTrainingProg;

    private LinearLayout hunterCardContainer;
    private View cardView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        //Intent intent = getIntent();
        //BountyHunter selectedHunter = (BountyHunter) intent.getSerializableExtra("selectedHunter");
        BountyHunter selectedHunter = (BountyHunter) getIntent().getSerializableExtra("selectedHunter");


        // Initialize buttons
        btnTrainMelee = findViewById(R.id.btnTrainMelee);
        btnTrainRange = findViewById(R.id.btnTrainRange);
        btnTrainXP = findViewById(R.id.btnTrainXP);
        btnMoveHome = findViewById(R.id.btnMoveHome);
        trainingProgress = findViewById(R.id.trainingProgress);
        textViewTrainingProg= findViewById(R.id.textViewTrainingProg);

        hunterCardContainer = findViewById(R.id.hunterCardContainer); // This is in your activity_training.xml

        cardView = getLayoutInflater().inflate(R.layout.bhuntercardview, null);





        loadValues(selectedHunter);



        /*if (selectedHunter != null) {
        ((TextView) cardView.findViewById(R.id.hunter_name)).setText(String.valueOf(selectedHunter.getName()));
            ((TextView) cardView.findViewById(R.id.MeleATKtext)).setText(String.valueOf(selectedHunter.getMeleAttack()));
            ((TextView) cardView.findViewById(R.id.MeleDEFtext)).setText(String.valueOf(selectedHunter.getMeleDefense()));
            ((TextView) cardView.findViewById(R.id.RangedATKtext)).setText(String.valueOf(selectedHunter.getRangedAttack()));
            ((TextView) cardView.findViewById(R.id.RangedDEFtext)).setText(String.valueOf(selectedHunter.getRangedDefense()));
            ((TextView) cardView.findViewById(R.id.hptext)).setText(String.valueOf(selectedHunter.getMaxHealth()));
            ((TextView) cardView.findViewById(R.id.xptext)).setText(String.valueOf(selectedHunter.getExperience()));
            ImageView img= cardView.findViewById(R.id.hunter_image);
            int resId =  img.getContext().getResources().getIdentifier(
                    selectedHunter.getImagePath(), "drawable", img.getContext().getPackageName());


            img.setImageResource(resId);

        // Add the card to the container
        hunterCardContainer.addView(cardView);*/
        // Add the card to the container
        hunterCardContainer.addView(cardView);




        String fileName = "my_bounty_hunters.json"; // Change if needed

        btnTrainMelee.setOnClickListener(v -> {
            if (selectedHunter != null) {
                selectedHunter.setExperience(selectedHunter.getExperience() + 1);
                trainingTimier(selectedHunter, () -> {
                    selectedHunter.setMeleAttack(selectedHunter.getMeleAttack() + 3);
                    selectedHunter.setMeleDefense(selectedHunter.getMeleDefense() + 3);

                    JsonHelper.saveUpdatedHunter(this, selectedHunter, fileName);
                    Toast.makeText(this, "Melee training complete!", Toast.LENGTH_SHORT).show();
                });
                //recreate();
            }
        });

        btnTrainRange.setOnClickListener(v -> {
            if (selectedHunter != null) {
                selectedHunter.setExperience(selectedHunter.getExperience() + 1);
                trainingTimier(selectedHunter, () -> {
                    selectedHunter.setRangedAttack(selectedHunter.getRangedAttack() + 3);
                    selectedHunter.setRangedDefense(selectedHunter.getRangedDefense() + 3);

                    JsonHelper.saveUpdatedHunter(this, selectedHunter, fileName);
                    Toast.makeText(this, "Ranged training complete!", Toast.LENGTH_SHORT).show();
                });
                //recreate();
            }
        });

        btnTrainXP.setOnClickListener(v -> {
            if (selectedHunter != null) {
                selectedHunter.setExperience(selectedHunter.getExperience() + 5);
                trainingTimier(selectedHunter, () -> {
                    selectedHunter.setMeleAttack(selectedHunter.getMeleAttack() + 1);
                    selectedHunter.setMeleDefense(selectedHunter.getMeleDefense() + 1);
                    selectedHunter.setRangedAttack(selectedHunter.getRangedAttack() + 1);
                    selectedHunter.setRangedDefense(selectedHunter.getRangedDefense() + 1);
                    selectedHunter.setMaxHealth(selectedHunter.getMaxHealth() + 1);

                    JsonHelper.saveUpdatedHunter(this, selectedHunter, fileName);
                    Toast.makeText(this, "XP training complete!", Toast.LENGTH_SHORT).show();
                });
                //recreate();
            }
        });

        btnMoveHome.setOnClickListener(v -> {
            // Navigate back to HomeActivity
            Intent intentback = new Intent(TrainActivity.this, HomeActivity.class);
            startActivity(intentback);
            finish();
        });
    }

    private void loadValues(BountyHunter selectedHunter) {
        if (selectedHunter != null) {
            ((TextView) cardView.findViewById(R.id.hunter_name)).setText(String.valueOf(selectedHunter.getName()));
            ((TextView) cardView.findViewById(R.id.MeleATKtext)).setText(String.valueOf(selectedHunter.getMeleAttack()));
            ((TextView) cardView.findViewById(R.id.MeleDEFtext)).setText(String.valueOf(selectedHunter.getMeleDefense()));
            ((TextView) cardView.findViewById(R.id.RangedATKtext)).setText(String.valueOf(selectedHunter.getRangedAttack()));
            ((TextView) cardView.findViewById(R.id.RangedDEFtext)).setText(String.valueOf(selectedHunter.getRangedDefense()));
            ((TextView) cardView.findViewById(R.id.hptext)).setText(String.valueOf(selectedHunter.getMaxHealth()));
            ((TextView) cardView.findViewById(R.id.xptext)).setText(String.valueOf(selectedHunter.getExperience()));
            ImageView img= cardView.findViewById(R.id.hunter_image);
            int resId =  img.getContext().getResources().getIdentifier(
                    selectedHunter.getImagePath(), "drawable", img.getContext().getPackageName());


            img.setImageResource(resId);



        } else {
            Log.e("TrainActivity", "No hunter selected!");
        }


    }

    private void trainingTimier(BountyHunter hunter, Runnable onTrainingComplete) {
        int xp = hunter.getExperience();
        int duration = xp * 500; // 0.5 second per XP

        trainingProgress.setProgress(0);
        trainingProgress.setMax(duration);
        textViewTrainingProg.setText("Training in Progress...");
        disableButtons();

        CountDownTimer timer = new CountDownTimer(duration, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = duration - (int) millisUntilFinished;
                trainingProgress.setProgress(progress);
                disableButtons();
            }

            @Override
            public void onFinish() {
                trainingProgress.setProgress(duration);
                trainingProgress.setProgress(0);

                onTrainingComplete.run();
                enableButtons();
                //recreate(); // Refresh UI
                //Toast.makeText(TrainActivity.this, "Training complete!", Toast.LENGTH_SHORT).show();

                //global stat
                int [] globalStats = JsonHelper.loadGlobalStats(TrainActivity.this, "Statistics.json");
                globalStats[3]++;
                JsonHelper.updateGlobalStats(TrainActivity.this, globalStats, "Statistics.json");

                //hunterstat
                Statistic stat = JsonHelper.loadHunterStatistic(TrainActivity.this, hunter.getName(), "Statistics.json");
                if (stat == null) {
                    stat = new Statistic();
                }
                stat.addNumberOfTrainingSessions();
                JsonHelper.updateHunterStatistic(TrainActivity.this, hunter.getName(), stat, "Statistics.json");
                textViewTrainingProg.setText("Training Complete");
                loadValues(hunter);

            }
        };

        timer.start();
    }

    private void disableButtons() {
        btnTrainMelee.setEnabled(false);
        btnTrainRange.setEnabled(false);
        btnTrainXP.setEnabled(false);
        btnMoveHome.setEnabled(false);
    }

    private void enableButtons() {
        btnTrainMelee.setEnabled(true);
        btnTrainRange.setEnabled(true);
        btnTrainXP.setEnabled(true);
        btnMoveHome.setEnabled(true);
    }

}
