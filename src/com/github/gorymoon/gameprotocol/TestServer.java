package com.github.gorymoon.gameprotocol;

import com.github.gorymoon.gameprotocol.api.IServerMessageListener;
import com.github.gorymoon.gameprotocol.api.MessageType;
import com.github.gorymoon.gameprotocol.api.Packet;
import com.github.gorymoon.gameprotocol.api.Player;
import com.github.gorymoon.gameprotocol.core.GameServer;

import java.util.Scanner;

public class TestServer implements IServerMessageListener {

    private Scanner scanner;

    public TestServer() {
        GameServer gameServer = new GameServer(this, 1234, 4);
        scanner = new Scanner(System.in);
        String in;
        loop: while (true) {
            in = scanner.nextLine();
            switch (in) {
                case "start":
                    gameServer.start();
                    break;
                case "send":
                    gameServer.sendToAllPlayers(new Packet(MessageType.MESSAGE, "Hi"));
                    break;
                case "stop":
                    gameServer.stop();
                    break loop;
                default:
                    System.out.println("Unknown command!");
            }
        }
    }

    public static void main(String[] args) {
        new TestServer();
    }

    @Override
    public String getServerName() {
        return "TestServer";
    }

    @Override
    public void onPlayerConnect(Player player) {
        System.out.println("Player connected to the server: " + player.getAddress());
    }

    @Override
    public void onPlayerDisconnect(Player player, boolean unexpected) {
        System.out.println("Player disconnected from the server: " + player.getAddress() + ", Expected: " + unexpected);
    }

    @Override
    public void onMessageReceived(Player player, MessageType type, String message) {
        System.out.println("Message from player (" + player.getAddress() + ") with type (" + type.toString() + "): " + message);
    }
}
