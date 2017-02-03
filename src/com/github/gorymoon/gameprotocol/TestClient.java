package com.github.gorymoon.gameprotocol;

import com.github.gorymoon.gameprotocol.api.IClientMessageListener;
import com.github.gorymoon.gameprotocol.api.MessageType;
import com.github.gorymoon.gameprotocol.api.Packet;
import com.github.gorymoon.gameprotocol.core.GameClient;

import java.util.Scanner;

public class TestClient implements IClientMessageListener {

    private Scanner scanner;

    public TestClient() {
        GameClient client = new GameClient(this, "127.0.0.1", 1234);
        scanner = new Scanner(System.in);
        String in;
        loop: while (true) {
            in = scanner.nextLine();
            switch (in) {
                case "start":
                    client.connect();
                    break;
                case "send":
                    client.sendToServer(new Packet(MessageType.MESSAGE, "Hi"));
                    break;
                case "stop":
                    client.diconnect();
                    break loop;
                default:
                    System.out.println("Unknown command!");
            }
        }
    }

    public static void main(String[] args) {
        new TestClient();
    }

    @Override
    public void onConnect() {
        System.out.println("Connected to server!");
    }

    @Override
    public void onDisconnect(String reason) {
        System.out.println("Disconnected from server: " + reason);
    }

    @Override
    public void onError(MessageType type, String message) {
        System.out.println("Error from server (" + type + "): " + message);
    }

    @Override
    public void onMessageReceived(MessageType type, String message) {
        System.out.println("Message from server with type (" + type.toString() + "): " + message);
    }
}
