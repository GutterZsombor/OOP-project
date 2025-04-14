// MultiplayerSetupActivity.java
package com.example.oop_project;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_setup);

        selectedHunter = (BountyHunter) getIntent().getSerializableExtra("selectedHunter");
        if (selectedHunter == null) {
            Toast.makeText(this, "No hunter selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        hunterImage = findViewById(R.id.hunterImage);
        hunterName= findViewById(R.id.hunterNameText);

        int resId = hunterImage.getContext().getResources().getIdentifier(
                selectedHunter.getImagePath(), "drawable", hunterImage.getContext().getPackageName());


        hunterImage.setImageResource(resId);
        hunterName.setText(selectedHunter.getName());

        Button hostBtn = findViewById(R.id.hostBtn);
        Button joinBtn = findViewById(R.id.joinBtn);

        hostBtn.setOnClickListener(v -> startBattle(true));
        joinBtn.setOnClickListener(v -> startBattle(false));
    }

    private void startBattle(boolean isHost) {
        if (selectedHunter == null) {
            Toast.makeText(this, "No hunter selected", Toast.LENGTH_SHORT).show();
            return;
        }

        final NetworkManager[] networkManagerRef = new NetworkManager[1];  // workaround to use in inner class

        NetworkManager.BattleNetworkCallback callback = new NetworkManager.BattleNetworkCallback() {
            @Override
            public void onConnected() {
                Log.d("Network", "Connected!");

                if (!isHost) {
                    // Client sends its hunter first
                    Log.d("Client", "Sending hunter JSON: " + selectedHunter.toJson());
                    networkManagerRef[0].sendHunter(selectedHunter);
                    networkManagerRef[0].sendHunter(selectedHunter);
                }
            }

            @Override
            public void onDisconnected() {
                Log.e("Network", "Disconnected");
            }

            @Override
            public void onMessageReceived(String message) {
                Log.d("Network", "Received hunter JSON: " + message);
                BountyHunter receivedHunter = BountyHunter.fromJson(message);

                Intent intent = new Intent(MultiplayerSetupActivity.this, BattleActivity.class);
                intent.putExtra("myHunter", selectedHunter);
                intent.putExtra("enemyHunter", receivedHunter);
                intent.putExtra("isMultiplayer", true);
                intent.putExtra("isHost", isHost);
                startActivity(intent);
            }
        };

        networkManagerRef[0] = new NetworkManager(this, callback);

        if (isHost) {
            networkManagerRef[0].initializeServer();
        } else {
            networkManagerRef[0].discoverAndConnect();
        }
    }

}