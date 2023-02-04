package com.clochelabs;

import com.clochelabs.packet.*;
import org.junit.Test;

import java.math.BigInteger;

import org.junit.Assert;
public class TestServerHandler
{
    @Test
    public void testIfGetPublicKeyGiveAKeyWhenRight(){
        ServerHandler test = new ServerHandler();
        GetKeyPacket packet = new GetKeyPacket(1);

        Packet result = test.getPublicKey(packet);
        Assert.assertEquals(Packet.PacketType.GIVEKEY, result.getType());
    }

    @Test
    public void testIfGetPublicKeyGiveAnErrorWhenWrong(){
        ServerHandler test = new ServerHandler();

        GetKeyPacket packet2 = new GetKeyPacket(2);
        GetKeyPacket packet = new GetKeyPacket(2);

        Packet result2 = test.getPublicKey(packet2);
        Packet result = test.getPublicKey(packet);
        Assert.assertEquals(Packet.PacketType.ERROR, result.getType());
    }

    @Test
    public void testIfGetResultWhenRefExists(){
        int a = 1;
        int b = 2;
        ServerHandler test = new ServerHandler();
        GetKeyPacket packet = new GetKeyPacket(3);
        GiveKeyPacket result = (GiveKeyPacket) test.getPublicKey(packet);
        PublicKey pk = result.getPk();

        BigInteger[] ca = Crypto.Encrypt(pk, a);
        BigInteger[] cb = Crypto.Encrypt(pk, b);
        BigInteger[] agg = Crypto.Agrege(ca, cb, pk.getP());

        GetResultPacket request = new GetResultPacket(3,agg);

        GiveResultPacket res = (GiveResultPacket) test.getResult(request);

        Assert.assertEquals(a+b, res.getResult());
    }

    @Test
    public void testIfGetErrorWhenRefDoesntExists(){
        int a = 1;
        int b = 2;
        ServerHandler test = new ServerHandler();
        GetKeyPacket packet = new GetKeyPacket(4);
        GiveKeyPacket result = (GiveKeyPacket) test.getPublicKey(packet);
        PublicKey pk = result.getPk();

        BigInteger[] ca = Crypto.Encrypt(pk, a);
        BigInteger[] cb = Crypto.Encrypt(pk, a);
        BigInteger[] agg = Crypto.Agrege(ca, cb, pk.getP());

        GetResultPacket request = new GetResultPacket(5,agg);

        Packet res = test.getResult(request);

        Assert.assertEquals(Packet.PacketType.ERROR, res.getType());
    }

}
