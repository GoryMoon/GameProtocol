package com.github.gorymoon.gameprotocol;

import com.github.gorymoon.gameprotocol.api.IClientMessageListener;
import com.github.gorymoon.gameprotocol.api.MessageType;
import com.github.gorymoon.gameprotocol.api.Packet;
import com.github.gorymoon.gameprotocol.client.GameClient;

import java.util.Scanner;

public class TestClient implements IClientMessageListener {

    private Scanner scanner;

    public TestClient(String host, String port) {
        GameClient client = new GameClient(this, host, Integer.parseInt(port));
        scanner = new Scanner(System.in);
        String in;
        loop: while (true) {
            in = scanner.nextLine();
            switch (in.substring(0, in.contains(" ") ? in.indexOf(' '): in.length())) {
                case "start":
                    client.connect();
                    break;
                case "send":
                    if (in.length() > 4) {
                        client.sendToServer(new Packet(MessageType.MESSAGE, in.substring(5)));
                        System.out.println("Message sent!");
                    }
                    break;
                case "stop":
                    client.disconnect();
                    break loop;
                default:
                    System.out.println("Unknown command!");
            }
        }
    }

    public static void main(String[] args) {
        new TestClient(args[0], args[1]);
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
    public void onMessageReceived(MessageType type, Object message) {
        System.out.println("Message from server with type (" + type.toString() + "): " + message);
    }
}
