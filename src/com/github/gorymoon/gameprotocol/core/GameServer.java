package com.github.gorymoon.gameprotocol.core;

import com.github.gorymoon.gameprotocol.api.IServerMessageListener;
import com.github.gorymoon.gameprotocol.api.MessageType;
import com.github.gorymoon.gameprotocol.api.Packet;
import com.github.gorymoon.gameprotocol.api.Player;
import com.github.gorymoon.gameprotocol.core.server.PlayerThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class GameServer implements Runnable {

    private IServerMessageListener listener;
    private int port;
    private int maxPlayers;

    private boolean running = false;
    private Thread mainNetworkThread;
    private HashMap<Player, PlayerThread> playerThreads;
    private ServerSocket serverSocket;
    private Socket socket = null;

    private int id = 0;

    public GameServer(IServerMessageListener listener, int port, int maxPlayers) {
        this.listener = listener;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.playerThreads = new HashMap<>();
        this.mainNetworkThread = new Thread(this, "GameProtocol Network Thread (" + listener.getServerName() + ")");
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Player, PlayerThread> entry: playerThreads.entrySet())
            entry.getValue().disconnectPlayer();
        System.out.println("Server (" + listener.getServerName() + ") stopped!");
    }

    public void start() {
        running = true;
        mainNetworkThread.start();
    }

    public void sendToAllPlayers(Packet packet) {
        for (Map.Entry<Player, PlayerThread> entry: playerThreads.entrySet())
            sendToPlayer(entry.getKey(), packet);
    }

    public void sendToPlayer(Player player, Packet packet) {
        if (playerThreads.containsKey(player))
            playerThreads.get(player).sendMessage(packet);
    }

    public void messageFromPlayer(Player player, Packet packet) {
        listener.onMessageReceived(player, packet.type, packet.message);
    }

    @Override
    public void run() {
        if (!setupConnection()) {
            running = false;
        }

        while (running) {
            try {
                if (serverSocket != null)
                    socket = serverSocket.accept();
            } catch (SocketException e) {
                running = false;
                break;
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
            if (playerThreads.size() < maxPlayers) {
                Player player = new Player(socket.getInetAddress(), id++);
                PlayerThread thread = new PlayerThread(this, socket, player);
                listener.onPlayerConnect(player);
                playerThreads.put(player, thread);
                thread.run();
            } else {
                try {
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(new Packet(MessageType.ERROR_FULL, "Game is full"));
                    out.close();
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void playerLeft(Player player) {
        playerThreads.remove(player);
        listener.onPlayerDisconnect(player);
        sendToAllPlayers(new Packet(MessageType.PLAYER_LEFT, String.valueOf(id)));
    }

    private boolean setupConnection() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server network (" + listener.getServerName() + ") started!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
