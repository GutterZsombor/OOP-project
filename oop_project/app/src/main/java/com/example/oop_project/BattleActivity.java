// BattleActivity.java
package com.example.oop_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.Statistic;
import com.example.oop_project.util.BattleAttack;
import com.example.oop_project.util.BattleResult;
import com.example.oop_project.util.JsonHelper;
import com.example.oop_project.util.NetworkManager;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;

public class BattleActivity extends AppCompatActivity implements NetworkManager.BattleNetworkCallback {
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
    //private NetworkManager networkManagerApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        /*networkManager = ((App)getApplication()).getNetworkManager(this);

        if (!networkManager.isConnected()) {
            Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show();
            finish();
        }

        networkManager.updateCallback(new NetworkManager.BattleNetworkCallback() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    Toast.makeText(BattleActivity.this,
                            "Connected to opponent!", Toast.LENGTH_SHORT).show();
            @Override
            public void onConnectionStateChanged(NetworkManager.ConnectionState state) {
                        runOnUiThread(() -> {
                            switch (state) {
                                case CONNECTED:
                                    connectionStatus.setText("Connected!");
                                    break;
                                case DISCONNECTED:
                                    connectionStatus.setText("Disconnected");
                                    break;
                                case ERROR:
                                    Toast.makeText(activity, "Connection error", Toast.LENGTH_SHORT).show();
                                    break;
                            }});


            }
        });*/

        // Initialize UI
        initViews();

        // Get intent extras
        Intent intent = getIntent();
        if (getIntent() == null || getIntent().getExtras() == null) {
            Toast.makeText(this, "Invalid battle setup", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        myHunter = (BountyHunter) intent.getSerializableExtra("myHunter");
        isMultiplayer = intent.getBooleanExtra("isMultiplayer", false);

        Log.d(TAG, "isMultiplayer: " + isMultiplayer);
        Log.d(TAG, "myHunter: " + myHunter.getName());

        if (isMultiplayer) {
            battleTitle.setText("BATTLE ONLINE");
            networkManager = ((App) getApplication()).getNetworkManager(this, new NetworkManager.BattleNetworkCallback() {
                @Override
                public void onConnected() {
                    runOnUiThread(() -> {
                        Toast.makeText(BattleActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onDisconnected() {
                    runOnUiThread(() -> {
                        Toast.makeText(BattleActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onMessageReceived(String message) {
                    // Handle battle messages
                    onMessageReceived(message);
                }
            });

            //Connection lost
           /* if (!networkManager.isConnected()) {
                Toast.makeText(this, "Connection lost line 123", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Connection lost  line 123");
                finish();
                return;
            }*/


            networkManager.updateCallback(new NetworkManager.BattleNetworkCallback() {
                @Override
                public void onConnected() {
                    runOnUiThread(() -> {
                        Toast.makeText(BattleActivity.this, "Connected to opponent!", Toast.LENGTH_SHORT).show();
                        // Initiate your game here if needed.
                    });
                }

                @Override
                public void onDisconnected() {
                    runOnUiThread(() -> {
                        Toast.makeText(BattleActivity.this, "Disconnected from opponent", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onMessageReceived(String message) {
                    // Handle incoming battle messages here
                    Log.d(TAG, "Message received from opponent: " + message);
                    // Process the message here
                }

                @Override
                public void onConnectionStateChanged(NetworkManager.ConnectionState state) {
                    if (state == NetworkManager.ConnectionState.DISCONNECTED) {
                        Toast.makeText(BattleActivity.this, "Connection lost", Toast.LENGTH_SHORT).show();
                        //finish(); // Finish the activity as the connection is lost.
                    }
                }
            });

            if (networkManager == null || !networkManager.isConnected()) {
                Log.d(TAG, "Connection check failed - Manager: " + networkManager +
                        ", Connected: " + (networkManager != null && networkManager.isConnected()));
                Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            boolean isHost = intent.getBooleanExtra("isHost", false);

            if (isHost) {
                networkManager.initializeServer();
                isMyTurn = true; // Host goes first
            } else {
                networkManager.discoverAndConnect();
                isMyTurn = false; // Client waits
            }

            // Get enemy hunter from intent
            enemyHunter = (BountyHunter) intent.getSerializableExtra("enemyHunter");
        } else {
            // Local battle
            enemyHunter = (BountyHunter) intent.getSerializableExtra("enemyHunter");
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

        // Calculate damage (same as before)
        Random rand = new Random();
        boolean prefersMelee = attacker.isPreferedAttack();
        boolean useMelee = (rand.nextFloat() < 0.6) == prefersMelee;
        int damage = useMelee ? defender.meleDefense(attacker) : defender.rangedDefense(attacker);
        if (damage < 0) damage = 0;

        // Apply tempo if damage is greater than 12
        int tempoDamage = 0;
        if (damage > 12) {
            tempoDamage = (int) Math.ceil(damage * 0.30);
            damage += tempoDamage;
        }
        else if(damage >16 ){
            tempoDamage = (int) Math.ceil(damage * 0.20);
            damage += tempoDamage;
        }

        // Update defender's health
        int newHP = Math.max(0, defender.getHealth() - damage);
        defender.setHealth(newHP);

        // Create battle log
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
                        attacker.getName(), defender.getName(), damage, newHP, true);
                networkManager.sendMessage(gson.toJson(result));

                // Handle rewards locally
                if (attacker == myHunter) {
                    rewardWinner(myHunter, enemyHunter);
                    punishLoser(this, enemyHunter, myHunter);
                } else {
                    rewardWinner(enemyHunter, myHunter);
                    punishLoser(this, myHunter, enemyHunter);
                }
            } else {
                // Local battle handling
                if (attacker == myHunter) {
                    rewardWinner(myHunter, enemyHunter);
                    punishLoser(this, enemyHunter, myHunter);
                } else {
                    rewardWinner(enemyHunter, myHunter);
                    punishLoser(this, myHunter, enemyHunter);
                }
            }
        } else {
            if (isMultiplayer) {
                // Send attack data to opponent
                BattleAttack attack = new BattleAttack(attacker.getName(), defender.getName(), damage, newHP, useMelee, tempoDamage,false);
                networkManager.sendMessage(gson.toJson(attack));

                // Switch turns
                isMyTurn = !isMyTurn;
                updateUI();
            } else {
                // Local battle - just switch turns
                isMyTurn = !isMyTurn;
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
        runOnUiThread(() -> {
            nextAttackBtn.setEnabled(isMyTurn);
            nextAttackBtn.setText(isMyTurn ? "ATTACK" : "Waiting for opponent...");
        });
    }

    // Network callbacks
    @Override
    public void onConnected() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Connected to opponent!", Toast.LENGTH_SHORT).show();
            updateUI();
        });
    }

    @Override
    public void onDisconnected() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Disconnected from opponent", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onMessageReceived(String message) {
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
        runOnUiThread(() -> {
            // Update enemy hunter's health
            enemyHunter.setHealth(attack.getNewHP());

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

            // Check if battle is over
            if (attack.isBattleOver()) {
                battleLog.append(myHunter.getName() + " is defeated!\n");
                nextAttackBtn.setEnabled(false);
                punishLoser(this, myHunter, enemyHunter);
            } else {
                // It's now my turn
                isMyTurn = true;
                updateUI();
            }
        });
    }

    private void handleBattleResult(BattleResult result) {
        runOnUiThread(() -> {
            battleLog.append(result.getWinnerName() + " wins the battle!\n");
            nextAttackBtn.setEnabled(false);

            if (result.getWinnerName().equals(myHunter.getName())) {
                rewardWinner(myHunter, enemyHunter);
            } else {
                punishLoser(this, myHunter, enemyHunter);
            }
        });
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