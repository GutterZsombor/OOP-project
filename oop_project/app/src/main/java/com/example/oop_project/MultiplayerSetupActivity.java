// MultiplayerSetupActivity.java
package com.example.oop_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.oop_project.hunter.BountyHunter;

public class MultiplayerSetupActivity extends AppCompatActivity {
    private BountyHunter selectedHunter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_setup);

        selectedHunter = (BountyHunter) getIntent().getSerializableExtra("selectedHunter");

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

        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("myHunter", selectedHunter);
        intent.putExtra("isMultiplayer", true);
        intent.putExtra("isHost", isHost);

        // In a real app, you'd need a way to select the opponent's hunter
        // For simplicity, we'll just use a default hunter here
        BountyHunter defaultEnemy = new BountyHunter("DefaultEnemy", "default", false, 50, 50, 50, 50, 100,0);
        intent.putExtra("enemyHunter", defaultEnemy);

        startActivity(intent);
    }
}