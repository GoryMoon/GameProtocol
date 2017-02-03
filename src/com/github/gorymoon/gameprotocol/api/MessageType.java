package com.github.gorymoon.gameprotocol.api;

import java.io.Serializable;

public enum MessageType implements Serializable {

    START_GAME,
    END_GAME,
    PLAYER_JOINED,
    PLAYER_LEFT,
    DISCONNECT,
    PLAYED_CARD,
    MESSAGE,
    SERVER_CLOSED,
    ERROR_FULL,


}
