// MultiplayerSetupActivity.java
package com.example.oop_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.util.NetworkManager;

public class MultiplayerSetupActivity extends AppCompatActivity {
    private BountyHunter selectedHunter;
    private ImageView hunterImage;
    private TextView hunterName;
    private NetworkManager networkManager;
    private Button hostBtn, joinBtn;
    private TextView connectionStatus;
    //private NetworkManager networkManagerApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_setup);
        //networkManager = ((App)getApplication()).getNetworkManager(this,null);
        // Initialize UI components
        hunterImage = findViewById(R.id.hunterImage);
        hunterName = findViewById(R.id.hunterNameText);
        hostBtn = findViewById(R.id.hostBtn);
        joinBtn = findViewById(R.id.joinBtn);
        connectionStatus = findViewById(R.id.connectionStatus);

        // Get selected hunter
        selectedHunter = (BountyHunter) getIntent().getSerializableExtra("selectedHunter");
        if (selectedHunter == null) {
            Toast.makeText(this, "No hunter selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display selected hunter
        int resId = getResources().getIdentifier(
                selectedHunter.getImagePath(), "drawable", getPackageName());
        hunterImage.setImageResource(resId);
        hunterName.setText(selectedHunter.getName());

        // Setup network callbacks
        NetworkManager.BattleNetworkCallback callback = new NetworkManager.BattleNetworkCallback() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    connectionStatus.setText("Connected!");
                    if (!networkManager.isHost()) {
                        // Only client sends hunter immediately
                        networkManager.sendHunter(selectedHunter);
                        Log.d("Network", "Client sent hunter: " + selectedHunter.getName());
                    }
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    connectionStatus.setText("Disconnected");
                    Toast.makeText(MultiplayerSetupActivity.this,
                            "Connection lost Multiplayer Setup Act", Toast.LENGTH_SHORT).show();

                    Log.d("MultiPlayerSetup", "Connection lost Multiplayer Setup Act");
                });
            }

            @Override
            public void onMessageReceived(String message) {
                // Handled by hunterReceivedListener instead
            }
        };

        //networkManager = new NetworkManager(this, callback);
        //networkManager.setHunterReceivedListener(this::handleReceivedHunter);
        networkManager = new NetworkManager(this, callback);
        networkManager.setHunterReceivedListener(hunter -> {
            Log.d("Network", "Hunter received in listener: " + hunter.getName());
            handleReceivedHunter(hunter);
        });
        // Button click listeners
        hostBtn.setOnClickListener(v -> {
            System.out.println("Host begin");
            connectionStatus.setText("Hosting battle...");
            hostBtn.setEnabled(false);
            joinBtn.setEnabled(false);
            startBattle(true);
            /*networkManager.setHunterReceivedListener(enemyHunter -> {
                // This will be called when client sends their hunter
                handleReceivedHunter(enemyHunter);
            });

            // Send our hunter to client
            new Handler().postDelayed(() -> {
                if (networkManager.isConnected()) {
                    networkManager.sendHunter(selectedHunter);
                }
            }, 1000);*/
            System.out.println("Host End");
        });

        joinBtn.setOnClickListener(v -> {
            System.out.println("Client begin");
            connectionStatus.setText("Searching for battle...");
            hostBtn.setEnabled(false);
            joinBtn.setEnabled(false);
            startBattle(false);
            System.out.println("Client End");
        });
    }

    private void startBattle(boolean isHost) {
        if (isHost) {
            networkManager.initializeServer();
        } else {
            networkManager.discoverAndConnect();
        }
    }

    private void handleReceivedHunter(BountyHunter enemyHunter) {
        runOnUiThread(() -> {
            Log.d("MultiPlayerSetup", "Received enemy hunter: " + enemyHunter.getName());
            ((App)getApplication()).setNetworkManager(networkManager);
            Intent intent = new Intent(MultiplayerSetupActivity.this, BattleActivity.class);
            intent.putExtra("myHunter", selectedHunter);
            intent.putExtra("enemyHunter", enemyHunter);
            intent.putExtra("isMultiplayer", true);
            intent.putExtra("isHost", networkManager.isHost());
            startActivity(intent);
            Log.d("Network", "Intent Succses " + enemyHunter.getName());
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Dont teardown here pass to Battle Activity
        /*
        if (networkManager != null) {
            networkManager.tearDown();
        }*/
    }
}