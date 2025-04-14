// NetworkManager.java
package com.example.oop_project.util;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.oop_project.hunter.BountyHunter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;



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
    private OnHunterReceivedListener hunterListener;

    public interface BattleNetworkCallback {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String message);
    }

    public interface OnHunterReceivedListener {
        void onHunterReceived(BountyHunter hunter);
    }

    public NetworkManager(Context context, BattleNetworkCallback callback) {
        this.context = context;
        this.callback = callback;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }
    public void initializeServer() {
        isHost = true;

        //DEBUG: Print available network interfaces and IPs
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        Log.d(TAG, "Available IP: " + addr.getHostAddress());
                    }
                }
            } else {
                Log.e(TAG, "No network interfaces found (getNetworkInterfaces() returned null)");
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error listing network interfaces", e);
        }


        try {
            serverSocket = new ServerSocket(0); // Let system choose port and IP
            serverSocket.setReuseAddress(true);
            localPort = serverSocket.getLocalPort();
            Log.d(TAG, "Server started on port: " + localPort);
            registerService();
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            Log.e(TAG, "Server socket error: ", e);
        }
    }

    /*public void initializeServer() {
        isHost = true;
        try {
            // Try with SO_REUSEADDR and explicit timeout
            //serverSocket = new ServerSocket(0, 50, InetAddress.getByName("0.0.0.0"));
            serverSocket = new ServerSocket(0);
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(10000); // 10 second timeout
            localPort = serverSocket.getLocalPort();
            Log.d(TAG, "Server started on port: " + localPort);
            registerService();
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            Log.e(TAG, "Server socket error: ", e); // Full stack trace
            // Fallback to hardcoded port if needed
            try {
                //if emulator needs it
                serverSocket = new ServerSocket(8080, 50, InetAddress.getByName("10.0.2.15"));
                localPort = 8888;
                registerService();
                new Thread(this::acceptConnections).start();
            } catch (IOException ex) {
                Log.e(TAG, "Fallback port failed: ", ex);
            }
        }
    }*/
/*
    public void initializeServer() {
        isHost = true;
        try {
            // Solution 1: Try with explicit local IP
            InetAddress localIp = InetAddress.getByName("192.168.1.x"); // Replace with your local IP
            serverSocket = new ServerSocket(0, 50, localIp);
            serverSocket.setReuseAddress(true);
            localPort = serverSocket.getLocalPort();
            Log.d(TAG, "Server started on port: " + localPort);
            registerService();
            new Thread(this::acceptConnections).start();
        } catch (Exception e) {
            Log.e(TAG, "Primary method failed, trying fallback", e);

            // Solution 2: Try on UI thread (for emulator workaround)
            runOnUiThread(() -> {
                try {
                    serverSocket = new ServerSocket(8888);
                    localPort = 8888;
                    registerService();
                    new Thread(this::acceptConnections).start();
                } catch (IOException ex) {
                    Log.e(TAG, "UI thread fallback failed", ex);

                    // Solution 3: Last resort - use AsyncTask
                    new AsyncTask<Void, Void, Void>() {
                        protected Void doInBackground(Void... params) {
                            try {
                                serverSocket = new ServerSocket(8888);
                                localPort = 8888;
                                registerService();
                                new Thread(NetworkManager.this::acceptConnections).start();
                            } catch (IOException e) {
                                Log.e(TAG, "AsyncTask fallback failed", e);
                            }
                            return null;
                        }
                    }.execute();
                }
            });
        }
    }
*/


    public void setHunterReceivedListener(OnHunterReceivedListener listener) {
        this.hunterListener = listener;
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
            StringBuilder fullMessage = new StringBuilder();

            while (clientSocket != null && clientSocket.isConnected()) {
                bytes = clientSocket.getInputStream().read(buffer);
                if (bytes > 0) {
                    String message = new String(buffer, 0, bytes);
                    fullMessage.append(message);

                    // Optional: Check for end-of-message marker like newline or special character
                    if (message.contains("}")) { // crude way to detect end of JSON
                        String json = fullMessage.toString().trim();

                        // Try parsing as BountyHunter
                        try {
                            BountyHunter received = BountyHunter.fromJson(json);
                            if (hunterListener != null) {
                                hunterListener.onHunterReceived(received);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse BountyHunter: " + e.getMessage());
                        }

                        fullMessage.setLength(0); // clear buffer after processing
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error receiving message: " + e.getMessage());
            callback.onDisconnected();
        }
    }

    public void sendHunter(BountyHunter hunter) {
        Log.d(TAG, "Sending hunter JSON: " + hunter.toJson());

        String json = hunter.toJson();
        sendMessage(json);
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