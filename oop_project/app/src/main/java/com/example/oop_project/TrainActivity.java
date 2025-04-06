package com.example.oop_project; // Replace with your actual package name

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class TrainActivity extends AppCompatActivity {

    private Button btnTrainMelee, btnTrainRange, btnTrainXP, btnMoveHome;

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




        LinearLayout hunterCardContainer = findViewById(R.id.hunterCardContainer); // This is in your activity_training.xml

        View cardView = getLayoutInflater().inflate(R.layout.bhuntercardview, null);

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

        // Add the card to the container
        hunterCardContainer.addView(cardView);

        } else {
            Log.e("TrainActivity", "No hunter selected!");
        }


        String fileName = "my_bounty_hunters.json"; // Change if needed

        btnTrainMelee.setOnClickListener(v -> {
            if (selectedHunter != null) {
                selectedHunter.setMeleAttack(selectedHunter.getMeleAttack() + 3);
                selectedHunter.setMeleDefense(selectedHunter.getMeleDefense() + 3);
                selectedHunter.setExperience(selectedHunter.getExperience() + 1);
                JsonHelper.saveUpdatedHunter(this,selectedHunter, fileName);
                Toast.makeText(this, "Melee training complete!", Toast.LENGTH_SHORT).show();
                recreate(); // Refresh UI
            }
        });

        btnTrainRange.setOnClickListener(v -> {
            if (selectedHunter != null) {
                selectedHunter.setRangedAttack(selectedHunter.getRangedAttack() + 3);
                selectedHunter.setRangedDefense(selectedHunter.getRangedDefense() + 3);
                selectedHunter.setExperience(selectedHunter.getExperience() + 1);
                JsonHelper.saveUpdatedHunter(this,selectedHunter, fileName);
                Toast.makeText(this, "Ranged training complete!", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        btnTrainXP.setOnClickListener(v -> {
            if (selectedHunter != null) {
                selectedHunter.setMeleAttack(selectedHunter.getMeleAttack() + 1);
                selectedHunter.setMeleDefense(selectedHunter.getMeleDefense() + 1);
                selectedHunter.setRangedAttack(selectedHunter.getRangedAttack() + 1);
                selectedHunter.setRangedDefense(selectedHunter.getRangedDefense() + 1);
                selectedHunter.setMaxHealth(selectedHunter.getMaxHealth() + 1);
                selectedHunter.setExperience(selectedHunter.getExperience() + 5);
                JsonHelper.saveUpdatedHunter(this,selectedHunter, fileName);
                Toast.makeText(this, "XP training complete!", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        btnMoveHome.setOnClickListener(v -> {
            // Navigate back to HomeActivity
            Intent intentback = new Intent(TrainActivity.this, HomeActivity.class);
            startActivity(intentback);
            finish();
        });
    }
}
