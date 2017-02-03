package com.github.gorymoon.gameprotocol.core;

import com.github.gorymoon.gameprotocol.api.IClientMessageListener;
import com.github.gorymoon.gameprotocol.api.MessageType;
import com.github.gorymoon.gameprotocol.api.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class GameClient implements Runnable {

    private String hostname;
    private int port;
    private IClientMessageListener listener;
    private Thread networkThread;
    private boolean running;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public GameClient(IClientMessageListener listener, String hostname, int port) {
        this.listener = listener;
        this.hostname = hostname;
        this.port = port;
        this.networkThread = new Thread(this, "Client network thread");
    }

    public void connect() {
        running = true;
        networkThread.start();
    }

    public void diconnect() {
        sendToServer(new Packet(MessageType.DISCONNECT, ""));
        closeConnection();
    }

    private void closeConnection() {
        running = false;
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(Packet packet) {
        try {
            if (out != null)
                out.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!setupConnection()) {
            running = false;
        }

        while (!Thread.interrupted() && running) {
            Object o = null;
            try {
                o = in.readObject();
            } catch (SocketException e) {
                closeConnection();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (o instanceof Packet) {
                Packet p = (Packet) o;
                if (p.type == MessageType.ERROR_FULL) {
                    listener.onError(p.type, p.message);
                } else if (p.type == MessageType.SERVER_CLOSED) {
                    listener.onDisconnect(p.message);
                    closeConnection();
                } else {
                    listener.onMessageReceived(p.type, p.message);
                }
            }
        }
    }

    private boolean setupConnection() {
        try {
            socket = new Socket(hostname, port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            listener.onConnect();

            System.out.println("Client network started!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
