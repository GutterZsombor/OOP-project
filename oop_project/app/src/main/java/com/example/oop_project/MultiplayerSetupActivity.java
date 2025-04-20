
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

import java.lang.Thread;

public class MultiplayerSetupActivity extends AppCompatActivity {
    private BountyHunter selectedHunter;
    private ImageView hunterImage;
    private TextView hunterName;
    private NetworkManager networkManager;
    private Button hostBtn, joinBtn;
    private TextView connectionStatus;
    private boolean hasSentHunter = false;
    private boolean isAlreadyConnected = false;
    boolean recived=false;
    //private NetworkManager networkManagerApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_setup);

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
                if (isAlreadyConnected) return; // Prevent action if already connected
                isAlreadyConnected = true;
                runOnUiThread(() -> {
                    connectionStatus.setText("Connected!");
                    if (!networkManager.isHost()&& !hasSentHunter) {
                        // Only client sends hunter immediately
                        Log.d("Network", "Client sent hunter: " + selectedHunter.getName());
                        networkManager.sendHunter(selectedHunter);
                        hasSentHunter = true; // Make sure to send the hunter here
                    }
                    if (networkManager.isHost()&& !hasSentHunter) {
                        // Only client sends hunter immediately
                        Log.d("Network", "Client sent hunter: " + selectedHunter.getName());
                        networkManager.sendHunter(selectedHunter);
                        hasSentHunter = true; // Make sure to send the hunter here
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

            @Override
            public void onConnectionStateChanged(NetworkManager.ConnectionState state) {
                /*if (state == NetworkManager.ConnectionState.CONNECTED && !networkManager.isHost()) {
                    // When connected and it’s not a host, send the hunter
                    networkManager.sendHunter(selectedHunter);
                }*/
            }
        };

        // create network instance
        networkManager = new NetworkManager(this, callback);
        //set it on App level
        ((App) getApplication()).setNetworkManager(networkManager);
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
            networkManager.setHunterReceivedListener(enemyHunter -> {
                // This will be called when client sends their hunter
                if (recived==false){
                    handleReceivedHunter(enemyHunter);
                    recived=true;}
            });

            // Send our hunter to client
           /* new Handler().postDelayed(() -> {
                if (networkManager.isConnected()) {
                    networkManager.sendHunter(selectedHunter);
                }
            },10);*/
            if (networkManager.isConnected()) {
                networkManager.sendHunter(selectedHunter);
            }
            System.out.println("Host End");

        });

        joinBtn.setOnClickListener(v -> {
            System.out.println("Client begin");
            connectionStatus.setText("Searching for battle...");
            hostBtn.setEnabled(false);
            joinBtn.setEnabled(false);
            startBattle(false);


            networkManager.setHunterReceivedListener(enemyHunter -> {
                if (recived==false){
                handleReceivedHunter(enemyHunter);
                recived=true;}

            });
            if (networkManager.isConnected()) {
                networkManager.sendHunter(selectedHunter);}



            System.out.println("Client End");

        });
    }

    private void startBattle(boolean isHost) {
        if (isHost) {
            networkManager.initializeServer();
        } else {
            networkManager.discoverAndConnect(false);
        }
    }

    private void handleReceivedHunter(BountyHunter enemyHunter) {

            Log.d("MultiPlayerSetup", "Received enemy hunter: " + enemyHunter.getName());

            Intent intent = new Intent(MultiplayerSetupActivity.this, BattleActivity.class);
            intent.putExtra("myHunter", selectedHunter);
            intent.putExtra("enemyHunter", enemyHunter);
            intent.putExtra("isMultiplayer", true);
            intent.putExtra("isHost", networkManager.isHost());
            startActivity(intent);
            Log.d("MultiplayerSetup", "Intent Succses " + enemyHunter.getName());

    }


}