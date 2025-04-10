package com.example.oop_project; // Replace with your actual package name

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.oop_project.util.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleActivity extends AppCompatActivity {

    private BountyHunter hunter1;
    private BountyHunter hunter2;

    // UI Elements for hunter1 (left)
    private ImageView hunterImage1;
    private TextView hunterName1, hpText1;
    private ProgressBar hpBar1;

    // UI Elements for hunter2 (right)
    private ImageView hunterImage2;
    private TextView hunterName2, hpText2;
    private ProgressBar hpBar2;

    private TextView battleLog;
    private Button nextAttackBtn, endBattleBtn;

    private int currentTurn = 0;

    final static String TAG="BattleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        // Get hunters from Intent
        hunter1 = (BountyHunter) getIntent().getSerializableExtra("hunter1");
        hunter2 = (BountyHunter) getIntent().getSerializableExtra("hunter2");

        // Left card view (your hunter)
        hunterImage1 = findViewById(R.id.hunter_imagehome);
        hunterName1 = findViewById(R.id.hunter_namehome);
        hpText1 = findViewById(R.id.hpTexthome);
        hpBar1 = findViewById(R.id.hpBarhome);

        // Right card view (enemy)
        hunterImage2 = findViewById(R.id.hunter_imageenemy);
        hunterName2 = findViewById(R.id.hunter_nameenemy);
        hpText2 = findViewById(R.id.hpTextenemy);
        hpBar2 = findViewById(R.id.hpBarenemy);

        battleLog = findViewById(R.id.battleLog);
        nextAttackBtn = findViewById(R.id.nextAttackBtn);
        endBattleBtn = findViewById(R.id.endBattleBtn);

        // Initialize UI
        setupHunterUI(hunter1, hunterImage1, hunterName1, hpText1, hpBar1);
        setupHunterUI(hunter2, hunterImage2, hunterName2, hpText2, hpBar2);

        nextAttackBtn.setOnClickListener(v -> fight());

        endBattleBtn.setOnClickListener(v -> finish());
    }

    private void setupHunterUI(BountyHunter hunter, ImageView imageView, TextView nameView, TextView hpTextView, ProgressBar hpBar) {
        nameView.setText(hunter.getName());
        hunter.setHealth(hunter.getMaxHealth());
        hpTextView.setText(hunter.getHealth() + "/" + hunter.getMaxHealth());
        hpBar.setMax(hunter.getMaxHealth());
        hpBar.setProgress(hunter.getHealth());


        //imageView.setImageResource(R.mipmap.boba);
        int resId = imageView.getContext().getResources().getIdentifier(
                hunter.getImagePath(), "drawable", imageView.getContext().getPackageName());


        imageView.setImageResource(resId);
    }

    private void fight() {
        //who attacks/defends
        BountyHunter attacker = (currentTurn % 2 == 0) ? hunter1 : hunter2;
        BountyHunter defender = (currentTurn % 2 == 0) ? hunter2 : hunter1;

        // Determine whether the attacker will use melee or ranged
        Random rand = new Random();
        boolean prefersMelee = attacker.isPreferedAttack();
        boolean useMelee = (rand.nextFloat() < 0.6) ? prefersMelee : !prefersMelee; // 60% chance for preferred attack


        int damage = 0;
        if (useMelee) {

            damage =  defender.meleDefense(attacker);
        } else {

            damage = defender.rangedDefense(attacker);
        }

        // Ensure damage is not negative
        if (damage < 0) damage = 0;


        // Apply tempo if damage is greater than 12
        int tempoDamage=0;
        if (damage > 12) {

            tempoDamage = (int) Math.ceil(damage * 0.30);
            damage += tempoDamage;
        }

        // Update defender's health
        int newHP = Math.max(0, defender.getHealth() - damage);
        defender.setHealth(newHP);

        String attackType = useMelee ? "melee" : "ranged";
        String log = attacker.getName() + " attacks " + defender.getName() + " with " + attackType + " for " + damage + " total damage.\n";
        log += defender.getName() + " has " + newHP + "/" + defender.getMaxHealth() + " HP left.\n";

        // If tempo was applied, add the tempo damage to the log
        if (tempoDamage > 0) {
            log += "Tempo bonus applied: " + tempoDamage + " additional damage.\n";
        }

        log += "\n";
        battleLog.append(log);


        updateHPBars();

        // defeated
        if (defender.getHealth() <= 0) {
            battleLog.append(defender.getName() + " is defeated!\n");
            nextAttackBtn.setEnabled(false);
            rewardWinner(attacker);
            punishLoser(this, defender);
        } else {
            currentTurn++;
        }
    }

    private void updateHPBars() {
        hpBar1.setProgress(hunter1.getHealth());
        hpText1.setText(hunter1.getHealth() + "/" + hunter1.getMaxHealth());

        hpBar2.setProgress(hunter2.getHealth());
        hpText2.setText(hunter2.getHealth() + "/" + hunter2.getMaxHealth());
    }

    public void rewardWinner(BountyHunter winner) {
        String fileName = "my_bounty_hunters.json";
        // Increase all stats except XP by 10
        winner.setMeleAttack(winner.getMeleAttack() + 10);
        winner.setRangedAttack(winner.getRangedAttack() + 10);
        winner.setMeleDefense(winner.getMeleDefense() + 10);
        winner.setRangedDefense(winner.getRangedDefense() + 10);
        winner.setMaxHealth(winner.getMaxHealth()+10); // Make sure health doesn't exceed max health

        // Increase XP by 3
        winner.setExperience(winner.getExperience() + 3);

        // Log the reward
        battleLog.append(winner.getName() + " wins! Stats increased\n\n");

        // Save the updated hunter using the current context and file name
        JsonHelper.saveUpdatedHunter(this, winner, fileName);



    }

    public static void punishLoser(Context context, BountyHunter loser) {
        // Load all hired bounty hunters from my_bounty_hunters.json
        List<BountyHunter> allHiredHunters = JsonHelper.loadBountyHunters(context, "my_bounty_hunters.json");
        List<BountyHunter> allNotHiredHunters = JsonHelper.loadBountyHunters(context, "not_hired_bounty_hunters.json");

        // Remove the loser from the hired hunters list
        for (int i = 0; i < allHiredHunters.size(); i++) {
            if (allHiredHunters.get(i).getName().equals(loser.getName())) {
                allHiredHunters.remove(i);
                break;
            }
        }

        // Load the original stats of the loser from bounty_hunter.json
        BountyHunter originalHunter = JsonHelper.loadOriginalHunter(context, loser.getName());

        // Add the loser back to the not_hired_bounty_hunters list
        allNotHiredHunters.add(originalHunter);


        boolean savedHired = JsonHelper.saveBountyHunters(context, allHiredHunters, "my_bounty_hunters.json");
        boolean savedNotHired = JsonHelper.saveBountyHunters(context, allNotHiredHunters, "not_hired_bounty_hunters.json");

        if (!savedHired) {
            Log.d(TAG, "Failed to save updated hired bounty hunters.");
        }
        if (!savedNotHired) {
            Log.d(TAG, "Failed to save updated not hired bounty hunters.");
        }
    }


}