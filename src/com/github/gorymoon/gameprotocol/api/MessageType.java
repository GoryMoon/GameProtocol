package com.github.gorymoon.gameprotocol.api;

import java.io.Serializable;

public enum MessageType implements Serializable {
    PLAYER_JOINED,
    PLAYER_LEFT,
    CONNECTED,
    DISCONNECT,
    MESSAGE,
    SERVER_CLOSED,
    ERROR_FULL,
    ERROR_CLOSED,
    ERROR_REACH
}
