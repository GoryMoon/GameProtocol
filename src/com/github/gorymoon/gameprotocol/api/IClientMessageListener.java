package com.github.gorymoon.gameprotocol.api;

public interface IClientMessageListener {

    void onConnect();
    void onDisconnect(String reason);
    void onError(MessageType type, String message);

    void onMessageReceived(MessageType type, Object message);

}
