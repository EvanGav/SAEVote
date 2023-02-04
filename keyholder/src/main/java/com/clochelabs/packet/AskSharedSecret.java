package com.clochelabs.packet;

import com.clochelabs.SecretShare;

public class AskSharedSecret extends Packet{

    private SecretShare ask;


    public AskSharedSecret(PacketType packetType) {
        super(packetType);
    }

    public SecretShare getAsk() {
        return ask;
    }

    public void setAsk(SecretShare ask) {
        this.ask = ask;
    }

    @Override
    public String toString() {
        return null;
    }
}
