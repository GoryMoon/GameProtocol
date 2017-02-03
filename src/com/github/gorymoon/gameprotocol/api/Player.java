package com.github.gorymoon.gameprotocol.api;

import java.net.InetAddress;

public class Player {

    private InetAddress address;

    public Player(InetAddress inetAddress, int i) {
        address = inetAddress;
    }

    public InetAddress getAddress() {
        return address;
    }
}
