package com.github.gorymoon.gameprotocol.core.server;

import com.github.gorymoon.gameprotocol.api.MessageType;
import com.github.gorymoon.gameprotocol.api.Packet;
import com.github.gorymoon.gameprotocol.api.Player;
import com.github.gorymoon.gameprotocol.core.GameServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class PlayerThread extends Thread {

    private GameServer server;
    private Socket socket;
    private Player player;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running = true;

    public PlayerThread(GameServer gameServer, Socket socket, Player player) {
        this.server = gameServer;
        this.socket = socket;
        this.player = player;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            running = false;
        }
    }

    @Override
    public void run() {
        Object o = null;
        while (!Thread.interrupted() && running) {
            try {
                o = in.readObject();
            } catch (SocketException e) {
                closeConnection();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (o instanceof Packet) {
                Packet p = (Packet) o;
                if (p.type == MessageType.DISCONNECT) {
                    server.playerLeft(player);
                    closeConnection();
                } else {
                    server.messageFromPlayer(player, (Packet) o);
                }
            }
        }
        if (socket != null)
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void disconnectPlayer() {
        sendMessage(new Packet(MessageType.SERVER_CLOSED, "Remote closed"));
        closeConnection();
    }

    private void closeConnection() {
        running = false;
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Packet packet) {
        if (socket.isConnected() && out != null) {
            try {
                out.writeObject(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
