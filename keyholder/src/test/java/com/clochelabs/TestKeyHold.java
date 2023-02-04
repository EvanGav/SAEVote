package com.clochelabs;

import com.beust.ah.A;
import com.clochelabs.packet.AskSharedSecret;
import com.clochelabs.packet.GiveSharedSecretPacket;
import com.clochelabs.packet.Packet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

public class TestKeyHold {

    @Test
    public void test_setup_KeyHold(){
        try{
            Socket s = new Socket("localHost",5057);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            Key[] key = Crypto.KeyGen(204);
            final int CERTAINTY = 256;
            final SecureRandom random = new SecureRandom();

            final SecretKey secretKey = (SecretKey) key[1];
            final BigInteger secret = secretKey.getX();

            // prime number must be longer then secret number
            final BigInteger prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);
            SecretShare[] secretShared = Shamir.SharedSecret(secret,3,4,prime,random);
            GiveSharedSecretPacket giveSharedSecretPacket = new GiveSharedSecretPacket(Packet.PacketType.GiveSharedSecretPacket,secretShared[0]);
            out.writeObject(giveSharedSecretPacket);
            s.close();
            out.close();

            Socket socket = new Socket("localHost",5057);
            out = new ObjectOutputStream(socket.getOutputStream());
            AskSharedSecret ask = new AskSharedSecret(Packet.PacketType.AskSharedSecret);


            out.writeObject(ask);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            ask = null;
            ask = (AskSharedSecret) in.readObject();

            assertEquals(ask.getAsk().getShare(),secretShared[0].getShare());
            System.out.println("fini");

        }catch(IOException e){
            throw new RuntimeException("non");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
