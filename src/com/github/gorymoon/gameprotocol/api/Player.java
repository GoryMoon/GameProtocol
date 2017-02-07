package com.github.gorymoon.gameprotocol.api;

import java.net.InetAddress;

public class Player {

    private InetAddress address;
    private int id;

    public Player(InetAddress inetAddress, int id) {
        address = inetAddress;
        this.id = id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getID() {
        return id;
    }
}
