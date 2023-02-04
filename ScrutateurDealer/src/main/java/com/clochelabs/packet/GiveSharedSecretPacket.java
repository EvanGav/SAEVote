package com.clochelabs.packet;

import com.clochelabs.SecretShare;

public class GiveSharedSecretPacket extends Packet{

    private SecretShare bigKey;

    public GiveSharedSecretPacket(PacketType packetType, SecretShare key) {
        super(packetType);
        bigKey = key;
    }

    public SecretShare getBigKey(){
        return bigKey;
    }


    @Override
    public String toString() {
        return null;
    }
}
