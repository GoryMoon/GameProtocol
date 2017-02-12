package com.github.gorymoon.gameprotocol.api;

public interface IServerMessageListener {
    void onPlayerConnect(Player player);
    void onPlayerDisconnect(Player player, boolean unexpected);

    void onMessageReceived(Player player, MessageType type, Object data);
}
