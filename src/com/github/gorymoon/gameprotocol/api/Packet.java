package com.github.gorymoon.gameprotocol.api;

import java.io.Serializable;

public class Packet implements Serializable {

    public MessageType type;
    public String message;

    public Packet(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }
}
