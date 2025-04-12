// NetworkManager.java
package com.example.oop_project.util;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static final String SERVICE_TYPE = "_bountyhunter._tcp.";
    private static final String SERVICE_NAME = "BountyHunterBattle";

    private Context context;
    private NsdManager nsdManager;
    private NsdManager.RegistrationListener registrationListener;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int localPort;
    private boolean isHost;
    private BattleNetworkCallback callback;

    public interface BattleNetworkCallback {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String message);
    }

    public NetworkManager(Context context, BattleNetworkCallback callback) {
        this.context = context;
        this.callback = callback;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeServer() {
        isHost = true;
        try {
            serverSocket = new ServerSocket(0);
            localPort = serverSocket.getLocalPort();
            registerService();
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            Log.e(TAG, "Error creating server socket: " + e.getMessage());
        }
    }

    public void discoverAndConnect() {
        isHost = false;
        initializeDiscoveryListener();
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    private void acceptConnections() {
        try {
            clientSocket = serverSocket.accept();
            callback.onConnected();
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            Log.e(TAG, "Error accepting connection: " + e.getMessage());
        }
    }

    private void receiveMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytes;
            while (clientSocket != null && clientSocket.isConnected()) {
                bytes = clientSocket.getInputStream().read(buffer);
                if (bytes > 0) {
                    String message = new String(buffer, 0, bytes);
                    callback.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error receiving message: " + e.getMessage());
            callback.onDisconnected();
        }
    }

    public void sendMessage(String message) {
        if (clientSocket != null && clientSocket.isConnected()) {
            new Thread(() -> {
                try {
                    clientSocket.getOutputStream().write(message.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "Error sending message: " + e.getMessage());
                }
            }).start();
        }
    }

    private void registerService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(localPort);

        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service registered: " + serviceInfo);
            }
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Registration failed: " + errorCode);
            }
            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service unregistered: " + serviceInfo);
            }
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Unregistration failed: " + errorCode);
            }
        };

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    private void initializeDiscoveryListener() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: " + errorCode);
            }
            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Stop discovery failed: " + errorCode);
            }
            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Service discovery started");
            }
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Service discovery stopped");
            }
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    nsdManager.resolveService(serviceInfo, resolveListener);
                }
            }
            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service lost: " + serviceInfo);
            }
        };

        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                try {
                    clientSocket = new Socket(serviceInfo.getHost(), serviceInfo.getPort());
                    callback.onConnected();
                    new Thread(NetworkManager.this::receiveMessages).start();
                } catch (IOException e) {
                    Log.e(TAG, "Error creating client socket: " + e.getMessage());
                }
            }
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed: " + errorCode);
            }
        };
    }

    public void tearDown() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (nsdManager != null) {
                nsdManager.unregisterService(registrationListener);
                nsdManager.stopServiceDiscovery(discoveryListener);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error tearing down connections: " + e.getMessage());
        }
    }
}