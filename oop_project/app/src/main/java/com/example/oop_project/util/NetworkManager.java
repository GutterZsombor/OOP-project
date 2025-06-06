package com.example.oop_project.util;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
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

    private final Context context;
    private final NsdManager nsdManager;
    private NsdManager.RegistrationListener registrationListener;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int localPort;
    private boolean isHost;
    private BattleNetworkCallback callback;
    private MessageReceivedListener messageListener;
    private HunterReceivedListener hunterReceivedListener;
    private final Handler mainHandler;

    //Receives a BountyHunter
    public interface HunterReceivedListener {
        void onHunterReceived(BountyHunter hunter);
    }
    // battle related messages
    public interface MessageReceivedListener {
        void onMessageReceivedd(String message);
    }
    public void setHunterReceivedListener(HunterReceivedListener listener) {
        this.hunterReceivedListener = listener;
    }
    public void setMessageReceivedListener(MessageReceivedListener listener) {
        this.messageListener = listener;
    }


    //Handles connection state, message received
    public interface BattleNetworkCallback {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String message);
        default void onConnectionStateChanged(ConnectionState state) {

        }

    }


    public NetworkManager(Context context, BattleNetworkCallback callback) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.callback = callback;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    // Updates the callback to override stuff if needed
    public void updateCallback(BattleNetworkCallback callback) {
        this.callback = callback;
    }

    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,

    }

    private ConnectionState state = ConnectionState.CONNECTED;

    //sets Staet to connected
    private synchronized void setConnected(Socket socket, ServerSocket serverSocket) {
        this.clientSocket = socket;
        this.serverSocket = serverSocket;
        this.state = ConnectionState.CONNECTED;
        Log.d(TAG, "Connection established - Client: " + socket + ", Server: " + serverSocket);
    }

    //disconect
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
            //setDisconnected("isConnected");
            // state matches reality
            //setConnected();
            Log.i(TAG, "Connection lost");
        }
        return connected;
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

    //recive message and hunter
    private void reciveHunterandMessage() {

        new Thread(() -> {

            try {
                //receive message trough socet
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String message;
                //phrse message
                while ((message = reader.readLine()) != null) {
                    Log.d(TAG, "Received message NetworkManager: " + message);
                    //if hunter
                    try {
                        BountyHunter hunter = BountyHunter.fromJson(message);
                        if (hunterReceivedListener != null) {
                            mainHandler.post(() ->
                                    hunterReceivedListener.onHunterReceived(hunter));
                            Log.d(TAG, "Hunter received: " );
                        }
                        //continue;
                    } catch (Exception e) {
                        Log.d(TAG, "Message is not a hunter: " + e.getMessage());
                    }
                    //if message
                    try {

                        final String attackMessage=message;
                        if (messageListener != null) {
                            mainHandler.post(() ->
                                    messageListener.onMessageReceivedd(attackMessage));
                            Log.d(TAG, "Attack received: " );
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "Message is not a message: " + e.getMessage());
                    }
                    //if not hunter or message
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

        try {
            // Create a server socket let sytem choosw
            serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort(); // Get the actual port
            Log.d(TAG, "Server socket created on port: " + port);
            //register service

            setState(ConnectionState.CONNECTING);
            registerService(port);

            new Thread(() -> {
                try {
                    //get client
                    clientSocket = serverSocket.accept();
                    setConnected(clientSocket, serverSocket);
                    reciveHunterandMessage(); // Now this will work
                    if (callback != null) {
                        callback.onConnected();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Accept failed: " + e.getMessage());
                    setDisconnected("initializeServer  Inner");

                }
            }).start();
            //acceptConnections();

        } catch (IOException e) {
            Log.e(TAG, "Server init failed: " + e.getMessage());
            setDisconnected("initializeServer Outer");

        }
        //DEBUG: Print available network interfaces and IPs
        /*try {
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
        }*/



    }



    //for client
    public void discoverAndConnect(boolean lisenerexit) {
        setState(ConnectionState.CONNECTING);
        isHost = false;
        if(!lisenerexit){
            initializeDiscoveryListener();
        }
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        //acceptConnections();
    }



    public void sendMessage(String message) {

        setState(ConnectionState.CONNECTED);
        if (clientSocket == null ) {//|| !clientSocket.isConnected()
            Log.e(TAG, "Cannot send message - socket not connected");
            return;
        }


        new Thread(() -> {
            try {
                // Send message
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()), true);
                out.println(message);
                Log.d(TAG, "Message sent successfully");
            } catch (IOException e) {
                Log.e(TAG, "Error sending message: " + e.getMessage());
            }
        }).start();
    }

    //same as massege but sending hunter
    public void sendHunter(BountyHunter hunter) {
        setState(ConnectionState.CONNECTED);


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

    //regiuster service on network NSD
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

    //for client start to look for service
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
                    reciveHunterandMessage(); // Start receiving messages
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


}