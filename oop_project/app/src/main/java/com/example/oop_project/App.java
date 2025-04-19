package com.example.oop_project;

import android.app.Application;
import android.content.Context;

import com.example.oop_project.util.NetworkManager;



public class App extends Application {
    private NetworkManager networkManager;

    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
    }


    public NetworkManager getNetworkManager(Context context, NetworkManager.BattleNetworkCallback callback) {
        if (networkManager == null) {
            networkManager = new NetworkManager(context, callback);
        } else {
            // Update callback if already exists
            networkManager.updateCallback(callback);
        }
        return networkManager;
    }
}