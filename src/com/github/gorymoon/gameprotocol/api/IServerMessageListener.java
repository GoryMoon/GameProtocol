package com.github.gorymoon.gameprotocol.api;

public interface IServerMessageListener {
    String getServerName();

    void onPlayerConnect(Player player);
    void onPlayerDisconnect(Player player, boolean unexpected);

    void onMessageReceived(Player player, MessageType type, String message);
}
