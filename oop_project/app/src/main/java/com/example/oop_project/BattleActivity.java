
package com.example.oop_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.Statistic;
import com.example.oop_project.util.BattleAttack;
import com.example.oop_project.util.BattleResult;
import com.example.oop_project.util.JsonHelper;
import com.example.oop_project.util.NetworkManager;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;



public class BattleActivity extends AppCompatActivity  {
    //HAPPYYYYYY
    private static final String TAG = "BattleActivity";

    private BountyHunter myHunter;
    private BountyHunter enemyHunter;
    private boolean isMyTurn = false;
    private boolean isMultiplayer = false;
    private NetworkManager networkManager;
    private final Gson gson = new Gson();

    // UI Elements
    private ImageView hunterImage1, hunterImage2;
    private TextView hunterName1, hunterName2, hpText1, hpText2, battleLog,battleTitle;
    private ProgressBar hpBar1, hpBar2;
    private Button nextAttackBtn, endBattleBtn;
    private CardView hunterCard1, hunterCard2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);


        // Initialize UI
        initViews();

        // Get intent extras
        Intent intent = getIntent();
        if (getIntent() == null || getIntent().getExtras() == null) {
            Toast.makeText(this, "Invalid battle setup", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //get extras
        myHunter = (BountyHunter) intent.getSerializableExtra("myHunter");

        enemyHunter = (BountyHunter) intent.getSerializableExtra("enemyHunter");
        isMultiplayer = intent.getBooleanExtra("isMultiplayer", false);

        Log.d(TAG, "isMultiplayer: " + isMultiplayer);
        Log.d(TAG, "myHunter: " + myHunter.getName());
        Log.d(TAG, "enemyHunter: " + enemyHunter.getName());
        hunterCard2.setCardBackgroundColor(Color.LTGRAY);
        hunterCard1.setCardBackgroundColor(Color.WHITE);

        if (isMultiplayer) {
            battleTitle.setText("BATTLE ONLINE");
            networkManager = ((App) getApplication()).getNetworkManager(this, new NetworkManager.BattleNetworkCallback() {
                @Override
                public void onConnected() {

                        Toast.makeText(BattleActivity.this, "Connected!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onDisconnected() {

                        Toast.makeText(BattleActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onMessageReceived(String message) {

                }
            });

            //check for host
            boolean isHost = intent.getBooleanExtra("isHost", false);

            if (isHost) {

                isMyTurn = true; // Host goes first
            } else {

                isMyTurn = false; // Client waits
            }


            if (networkManager == null || !networkManager.isConnected()) {
                Log.d(TAG, "Connection check failed - Manager: " + networkManager +
                        ", Connected: " + (networkManager != null && networkManager.isConnected()));
                Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show();

            }

            networkManager.setMessageReceivedListener(message -> {
                // This will be called when client sends their hunter
                Log.i("BattleActivity", "Message Listener" + message);
                onMessageReceivedBattleAct(message);

            });


        } else {
            // Local battle
            isMyTurn = true; // Always player's turn in local battle
        }

        setupHunterUI(myHunter, hunterImage1, hunterName1, hpText1, hpBar1);
        setupHunterUI(enemyHunter, hunterImage2, hunterName2, hpText2, hpBar2);

        updateUI();

        nextAttackBtn.setOnClickListener(v -> {
            if (isMultiplayer && !isMyTurn) {
                Toast.makeText(this, "Wait for your turn!", Toast.LENGTH_SHORT).show();
                return;
            }
            fight();
        });

        endBattleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "End button clicked");
                Intent intent = new Intent(BattleActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        hunterImage1 = findViewById(R.id.hunter_imagehome);
        hunterImage2 = findViewById(R.id.hunter_imageenemy);
        hunterName1 = findViewById(R.id.hunter_namehome);
        hunterName2 = findViewById(R.id.hunter_nameenemy);
        hpText1 = findViewById(R.id.hpTexthome);
        hpText2 = findViewById(R.id.hpTextenemy);
        hpBar1 = findViewById(R.id.hpBarhome);
        hpBar2 = findViewById(R.id.hpBarenemy);
        battleLog = findViewById(R.id.battleLog);
        nextAttackBtn = findViewById(R.id.nextAttackBtn);
        endBattleBtn = findViewById(R.id.endBattleBtn);
        battleTitle=findViewById(R.id.battleTitle);
        hunterCard1 = findViewById(R.id.hunter_card);
        hunterCard2 = findViewById(R.id.hunter_cardenemy);
    }

    private void setupHunterUI(BountyHunter hunter, ImageView imageView, TextView nameView,
                               TextView hpTextView, ProgressBar hpBar) {
        nameView.setText(hunter.getName());
        hpTextView.setText(hunter.getHealth() + "/" + hunter.getMaxHealth());
        hpBar.setMax(hunter.getMaxHealth());
        hpBar.setProgress(hunter.getHealth());

        int resId = getResources().getIdentifier(
                hunter.getImagePath(), "drawable", getPackageName());
        imageView.setImageResource(resId);
    }

    private void fight() {
        BountyHunter attacker = isMultiplayer ? myHunter : (isMyTurn ? myHunter : enemyHunter);
        BountyHunter defender = isMultiplayer ? enemyHunter : (isMyTurn ? enemyHunter : myHunter);

        // same as in OLD
        Random rand = new Random();
        boolean prefersMelee = attacker.isPreferedAttack();
        boolean useMelee = (rand.nextFloat() < 0.6) == prefersMelee;
        int damage = useMelee ? defender.meleDefense(attacker) : defender.rangedDefense(attacker);
        if (damage < 0) damage = 0;


        int tempoDamage = 0;
        if (damage > 12) {
            tempoDamage = (int) Math.ceil(damage * 0.30);
            damage += tempoDamage;
        }
        else if(damage >16 ){
            tempoDamage = (int) Math.ceil(damage * 0.20);
            damage += tempoDamage;
        }


        int newHP = Math.max(0, defender.getHealth() - damage);
        defender.setHealth(newHP);


        String attackType = useMelee ? "melee" : "ranged";
        String log = attacker.getName() + " attacks " + defender.getName() +
                " with " + attackType + " for " + damage + " total damage.\n" +
                defender.getName() + " has " + newHP + "/" + defender.getMaxHealth() + " HP left.\n";

        if (tempoDamage > 0) {
            log += "Tempo bonus applied: " + tempoDamage + " additional damage.\n";
        }

        log += "\n";
        battleLog.append(log);
        updateHPBars();

        // Check if battle is over
        if (defender.getHealth() <= 0) {
            battleLog.append(defender.getName() + " is defeated!\n");
            nextAttackBtn.setEnabled(false);

            if (isMultiplayer) {
                // Send battle result to opponent
                BattleResult result = new BattleResult(
                        defender.getName(), attacker.getName(), damage, newHP, true);
                networkManager.sendMessage(gson.toJson(result));

                // Handle rewards locally if we win or lose on our turn
                // oponent moved to handle results
                if (attacker == myHunter) {
                    rewardWinner(myHunter, enemyHunter);
                    hunterCard1.setCardBackgroundColor(Color.GREEN);
                    hunterCard2.setCardBackgroundColor(Color.RED);
                } else {

                    punishLoser(this, myHunter, enemyHunter);
                    hunterCard2.setCardBackgroundColor(Color.GREEN);
                    hunterCard1.setCardBackgroundColor(Color.RED);
                }
            } else {
                // Local battle handling
                if (attacker == myHunter) {
                    rewardWinner(myHunter, enemyHunter);
                    punishLoser(this, enemyHunter, myHunter);
                    hunterCard1.setCardBackgroundColor(Color.GREEN);
                    hunterCard2.setCardBackgroundColor(Color.RED);
                } else {
                    rewardWinner(enemyHunter, myHunter);
                    punishLoser(this, myHunter, enemyHunter);
                    hunterCard2.setCardBackgroundColor(Color.GREEN);
                    hunterCard1.setCardBackgroundColor(Color.RED);
                }
            }
        } else {
            if (isMultiplayer) {
                // Send attack data to opponent
                Log.d(TAG, "Is Conected"+networkManager.isConnected());

                Log.d(TAG, "Sending attack data to opponent");
                BattleAttack attack = new BattleAttack(attacker.getName(), defender.getName(), damage, newHP, useMelee, tempoDamage,false);
                networkManager.sendMessage(gson.toJson(attack));
                System.out.println(gson.toJson(attack));

                // Switch turns
                isMyTurn = !isMyTurn;
                updateUI();
                if(!isMyTurn){
                    hunterCard1.setCardBackgroundColor(Color.LTGRAY);
                    hunterCard2.setCardBackgroundColor(Color.WHITE);

                }
                else{
                    hunterCard1.setCardBackgroundColor(Color.WHITE);
                    hunterCard2.setCardBackgroundColor(Color.LTGRAY);
                }
            } else {
                // Local battle - just switch turns
                isMyTurn = !isMyTurn;
                if(!isMyTurn){
                    hunterCard1.setCardBackgroundColor(Color.LTGRAY);
                    hunterCard2.setCardBackgroundColor(Color.WHITE);

                }
                else{
                    hunterCard1.setCardBackgroundColor(Color.WHITE);
                    hunterCard2.setCardBackgroundColor(Color.LTGRAY);
                }
            }
        }


    }

    private void updateHPBars() {
        hpBar1.setProgress(myHunter.getHealth());
        hpText1.setText(myHunter.getHealth() + "/" + myHunter.getMaxHealth());
        hpBar2.setProgress(enemyHunter.getHealth());
        hpText2.setText(enemyHunter.getHealth() + "/" + enemyHunter.getMaxHealth());
    }

    private void updateUI() {

            nextAttackBtn.setEnabled(isMyTurn);
            nextAttackBtn.setText(isMyTurn ? "ATTACK" : "Waiting for opponent...");

    }


    public void onMessageReceivedBattleAct(String message) {
        Log.d("onMessageReceivedBattleAct", message);
        try {
            // Determine message type
            if (message.contains("\"isBattleOver\":true")) {
                BattleResult result = gson.fromJson(message, BattleResult.class);
                handleBattleResult(result);
            } else {
                BattleAttack attack = gson.fromJson(message, BattleAttack.class);
                handleOpponentAttack(attack);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing message: " + e.getMessage());
        }
    }

    private void handleOpponentAttack(BattleAttack attack) {

            // Update enemy hunter's health
            myHunter.setHealth(attack.getNewHP());

            // Add to battle log
            String attackType = attack.isMelee() ? "melee" : "ranged";
            String log = attack.getAttackerName() + " attacks " + attack.getDefenderName() +
                    " with " + attackType + " for " + attack.getDamage() + " total damage.\n" +
                    attack.getDefenderName() + " has " + attack.getNewHP() + "/" +
                    enemyHunter.getMaxHealth() + " HP left.\n";

            if (attack.getTempoDamage() > 0) {
                log += "Tempo bonus applied: " + attack.getTempoDamage() + " additional damage.\n";
            }

            log += "\n";
            battleLog.append(log);

            updateHPBars();


        isMyTurn = true;
        updateUI();

    }
    //if we win or lose on oponend turn handle here
    private void handleBattleResult(BattleResult result) {

            battleLog.append(result.getLoserName() + " Lost the battle!\n");
            battleLog.append(result.getWinnerName() + " wins the battle!\n");
            nextAttackBtn.setEnabled(false);
            myHunter.setHealth(0);
            updateHPBars();
        //if we win or lose on oponend turn handle here
            if (result.getWinnerName().equals(myHunter.getName())) {
                rewardWinner(myHunter, enemyHunter);
                hunterCard1.setCardBackgroundColor(Color.GREEN);
                hunterCard2.setCardBackgroundColor(Color.RED);
            } else {
                punishLoser(this, myHunter, enemyHunter);
                hunterCard2.setCardBackgroundColor(Color.GREEN);
                hunterCard1.setCardBackgroundColor(Color.RED);
            }

    }

    @Override
    protected void onDestroy() {

        /*if (networkManager != null) {
            networkManager.tearDown();
        }*/
        /*if (isFinishing()) { // Only cleanup if activity is really finishing
            networkManager.tearDown();
        }*/
        super.onDestroy();
        networkManager.tearDown();
    }






    public void rewardWinner(BountyHunter winner,BountyHunter loser) {
        if(winner.getName()!="DefaultEnemy"&& loser.getName()!="DefaultEnemy") {
            String fileName = "my_bounty_hunters.json";
            // Increase all stats except XP by 10
            winner.setMeleAttack(winner.getMeleAttack() + 10);
            winner.setRangedAttack(winner.getRangedAttack() + 10);
            winner.setMeleDefense(winner.getMeleDefense() + 10);
            winner.setRangedDefense(winner.getRangedDefense() + 10);
            winner.setMaxHealth(winner.getMaxHealth() + 10); // Make sure health doesn't exceed max health

            // Increase XP by 3
            winner.setExperience(winner.getExperience() + 3);

            // Log the reward
            battleLog.append(winner.getName() + " wins! Stats increased\n\n");

            // Save the updated hunter using the current context and file name
            JsonHelper.saveUpdatedHunter(this, winner, fileName);

            Statistic winnerStat = JsonHelper.loadHunterStatistic(this, winner.getName(), "Statistics.json");
            if (winnerStat != null) {
                winnerStat.addWin(loser.getName());
                JsonHelper.updateHunterStatistic(this, winner.getName(), winnerStat, "Statistics.json");
            }
        }



    }

    public static void punishLoser(Context context, BountyHunter loser,BountyHunter winner) {
        if(winner.getName()!="DefaultEnemy"&& loser.getName()!="DefaultEnemy") {
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

        Statistic loserStat = JsonHelper.loadHunterStatistic(context, loser.getName(),"Statistics.json");
        if (loserStat != null) {
            loserStat.addLost(winner.getName());
            JsonHelper.updateHunterStatistic(context, loser.getName(), loserStat, "Statistics.json");
        }

    }
}}