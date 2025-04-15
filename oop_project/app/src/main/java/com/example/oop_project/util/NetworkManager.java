// NetworkManager.java
package com.example.oop_project.util;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.oop_project.hunter.BountyHunter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
    private HunterReceivedListener hunterReceivedListener;
    private Handler mainHandler;
    public interface HunterReceivedListener {
        void onHunterReceived(BountyHunter hunter);
    }
    public void setHunterReceivedListener(HunterReceivedListener listener) {
        this.hunterReceivedListener = listener;
    }
   /* public void setHunterReceivedListener(OnHunterReceivedListener listener) {
        this.hunterListener = listener;
    }*/


    public interface BattleNetworkCallback {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String message);
        default void onConnectionStateChanged(ConnectionState state) {
            // Default implementation does nothing
        }

    }

    public interface OnHunterReceivedListener {
        void onHunterReceived(BountyHunter hunter);
    }

    public NetworkManager(Context context, BattleNetworkCallback callback) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.callback = callback;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void updateCallback(BattleNetworkCallback callback) {
        this.callback = callback;
    }

    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }

    private ConnectionState state = ConnectionState.CONNECTED;

    // Add these methods
    private synchronized void setConnected(Socket socket, ServerSocket serverSocket) {
        this.clientSocket = socket;
        this.serverSocket = serverSocket;
        this.state = ConnectionState.CONNECTED;
        Log.d(TAG, "Connection established - Client: " + socket + ", Server: " + serverSocket);
    }

    private synchronized void setDisconnected(String where) {
        this.state = ConnectionState.DISCONNECTED;
        this.clientSocket = null;
        this.serverSocket = null;
        Log.d(TAG, "Connection reset"+where);
    }

    // Update isConnected()
    public synchronized boolean isConnected() {
        boolean connected = (state == ConnectionState.CONNECTED) &&
                ((isHost && serverSocket != null) ||
                        (!isHost && clientSocket != null && clientSocket.isConnected()));

        Log.d(TAG, "isConnected() - State: " + state +
                ", Host: " + isHost +
                ", ServerSocket: " + (serverSocket != null) +
                ", ClientSocket: " + (clientSocket != null && clientSocket.isConnected()));

        if (!connected) {
            setDisconnected("isConnected"); // state matches reality
        }
        return connected;
    }

    public ConnectionState getConnectionState() {
        return state;
    }

    private void setState(ConnectionState newState) {
        this.state = newState;
        if (callback != null) {
            // Post to main thread if needed
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onConnectionStateChanged(newState);
            });
        }
        Log.d(TAG, "Connection state changed to: " + newState);
    }

    /*private void startReceiveThread() {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String message;

                while ((message = reader.readLine()) != null) {
                    Log.d(TAG, "Received message NetworkManager: " + message);
                    if (callback != null) {
                        callback.onMessageReceived(message);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Receive error: " + e.getMessage());
                setDisconnected();
                if (callback != null) {
                    callback.onDisconnected();
                }
            }
        }).start();
    }*/
    private void startReceiveThread() {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String message;

                while ((message = reader.readLine()) != null) {
                    Log.d(TAG, "Received message NetworkManager: " + message);

                    try {
                        BountyHunter hunter = BountyHunter.fromJson(message);
                        if (hunterReceivedListener != null) {
                            mainHandler.post(() ->
                                    hunterReceivedListener.onHunterReceived(hunter));
                        }
                        continue;
                    } catch (Exception e) {
                        Log.d(TAG, "Message is not a hunter: " + e.getMessage());
                    }
                    final String messageFinal = message;
                    if (callback != null) {
                        mainHandler.post(() -> callback.onMessageReceived(messageFinal));
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Receive error: " + e.getMessage());
                setDisconnected("startReceiveThread");
                if (callback != null) {
                    mainHandler.post(callback::onDisconnected);
                }
            }
        }).start();
    }

    public void initializeServer() {
        isHost = true;
        //setState(ConnectionState.CONNECTING);
        //setDisconnected();
        /*try {
            serverSocket = new ServerSocket(0);
            setState(ConnectionState.CONNECTED);
        } catch (IOException e) {
            setState(ConnectionState.ERROR);
        }*/
        /*
        try {
            serverSocket = new ServerSocket(0); // Let system choose port and IP
            serverSocket.setReuseAddress(true);
            localPort = serverSocket.getLocalPort();
            Log.d(TAG, "Server started on port: " + localPort);
            registerService();
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            Log.e(TAG, "Server socket error: ", e);
        }*/
        try {
            serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort(); // Get the actual port
            Log.d(TAG, "Server socket created on port: " + port);

            setState(ConnectionState.CONNECTING);
            registerService(port);

            new Thread(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    setConnected(clientSocket, serverSocket);
                    startReceiveThread(); // Now this will work
                    if (callback != null) {
                        callback.onConnected();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Accept failed: " + e.getMessage());
                    setDisconnected("initializeServer  Inner");

                }
            }).start();

        } catch (IOException e) {
            Log.e(TAG, "Server init failed: " + e.getMessage());
            setDisconnected("initializeServer Outer");

        }
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



    public void discoverAndConnect() {
        setState(ConnectionState.CONNECTING);
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

   /*private void receiveMessages() {
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
    }*/
   private void receiveMessages() {
       try {
           setState(ConnectionState.CONNECTED);
           BufferedReader reader = new BufferedReader(
                   new InputStreamReader(clientSocket.getInputStream()));
           String line;

           while ((line = reader.readLine()) != null) {
               Log.d(TAG, "Received raw message: " + line);

               try {
                   BountyHunter hunter = BountyHunter.fromJson(line);
                   if (hunterListener != null) {
                       hunterListener.onHunterReceived(hunter);
                   }
               } catch (Exception e) {
                   Log.e(TAG, "Error parsing hunter: " + e.getMessage());
               }
           }
       } catch (IOException e) {
           Log.e(TAG, "Error receiving messages: " + e.getMessage());
           callback.onDisconnected();
       }
   }
    /*public void sendHunter(BountyHunter hunter) {
        Log.d(TAG, "Sending hunter JSON: " + hunter.toJson());

        String json = hunter.toJson();
        sendMessage(json);
    }*/
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

    public void sendHunter(BountyHunter hunter) {
        setState(ConnectionState.CONNECTED);
        if (clientSocket == null || !clientSocket.isConnected()) {
            Log.e(TAG, "Cannot send hunter - socket not connected");
            return;
        }

        new Thread(() -> {
            try {
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()), true);
                out.println(hunter.toJson());
                Log.d(TAG, "Hunter sent successfully");
            } catch (IOException e) {
                Log.e(TAG, "Error sending hunter: " + e.getMessage());
            }
        }).start();
    }
    private void registerService(int port) {
        this.localPort = port; // Store the port if needed elsewhere
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port); // Use the passed port parameter

        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service registered on port: " + serviceInfo.getPort());
            }
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Registration failed for port " + port + ": " + errorCode);
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(TAG, "UnRegistration failed for port " + port + ": " + nsdServiceInfo+ ": " + i);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Log.e(TAG, "Service Unregistered port " + port + ": " + nsdServiceInfo);
            }
        };

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    private void initializeDiscoveryListener() {
        setState(ConnectionState.CONNECTED);
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
                    setConnected(clientSocket, null);
                    startReceiveThread(); // Start receiving messages
                    callback.onConnected();
                } catch (IOException e) {
                    Log.e(TAG, "Error resolving service: initializeDiscoveryListener " + e.getMessage());
                    setDisconnected("initializeDiscoveryListener  onServiceResolved");
                    callback.onDisconnected();
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
            //setState(ConnectionState.DISCONNECTED);
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }

            // Only unregister if we registered a service
            if (isHost && registrationListener != null) {
                nsdManager.unregisterService(registrationListener);
            }

            // Only stop discovery if we started it
            if (!isHost && discoveryListener != null) {
                nsdManager.stopServiceDiscovery(discoveryListener);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error tearing down connections: " + e.getMessage());
        }
        Log.d(TAG, "Tear down complete");
        setDisconnected("tearDown");
    }

    public synchronized boolean isHost() {
        return this.isHost;
    }

    public int getLocalPort() {
        return this.localPort;
    }
}