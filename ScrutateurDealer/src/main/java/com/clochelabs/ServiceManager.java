package com.clochelabs;

import com.clochelabs.packet.AskSharedSecret;
import com.clochelabs.packet.GiveSharedSecretPacket;
import com.clochelabs.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceManager {

    private static ArrayList<Integer> connected = new ArrayList<>();

    private static PublicKey publicKey;

    private static int SERVERPORT = 5057;

    private static ServerSocket serverSocket;

    private static Inet4Address[] addressesOfKeyHolder;

    private static BigInteger prime = null;

    public static void run() {
        Key[] key = Crypto.KeyGen(204);
        SecretKey secretKey = (SecretKey) key[1];
        PublicKey publicKey = (PublicKey) key[0] ;
        addressesOfKeyHolder = new Inet4Address[4];
        try{
            addressesOfKeyHolder[0] = (Inet4Address) Inet4Address.getByName("127.0.0.1");
            addressesOfKeyHolder[1] = (Inet4Address) Inet4Address.getByName("10.20.133.8");
            addressesOfKeyHolder[2] = (Inet4Address) Inet4Address.getByName("10.20.133.9");
            addressesOfKeyHolder[3] = (Inet4Address) Inet4Address.getByName("10.20.133.12");
        }catch(IOException e){
            e.printStackTrace();
        }

        BigInteger secret = secretKey.getX();
        final int CERTAINTY = 256;
        final SecureRandom random = new SecureRandom();
        prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);

        SecretShare[] shares = Shamir.SharedSecret(secretKey.getX(),3,4,prime,random);
        for(int i = 0 ; i < 4 ; i++){
            try{
                Socket s = new Socket(addressesOfKeyHolder[i],5057);
                GiveSharedSecretPacket giveSharedSecretPacket = new GiveSharedSecretPacket(Packet.PacketType.GiveSharedSecretPacket,shares[i]);
                ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
                outputStream.writeObject(giveSharedSecretPacket);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Socket socketForServer;
        try {
            serverSocket = new ServerSocket(SERVERPORT);
            serverSocket.setSoTimeout(1);
        }catch (IOException i){
            i.printStackTrace();
        }
        shares = null;
        secret = null;
        ServiceManager.publicKey = publicKey;
        key = null;

        while(true){
            try{
                socketForServer = serverSocket.accept();
            }catch (IOException e){
                socketForServer = null;
            }
            if(socketForServer!=null){
                runNewServerThread(socketForServer);
            }
        }
    }

    private static void runNewServerThread(Socket socketForServer) {
        Thread serverHandler = new ServerHandler(socketForServer);
        serverHandler.start();
    }

    public static void add(int id){
        connected.add(id);
    }

    public static PublicKey getPk(){
        return publicKey;
    }

    public static SecretKey getSk(){
        SecretShare[] secretShare = new SecretShare[4];
        for(int i = 0 ; i < 4 ; i++){
            try{
                Socket s = new Socket(addressesOfKeyHolder[i],5057);
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                AskSharedSecret ask = new AskSharedSecret(Packet.PacketType.AskSharedSecret);
                out.writeObject(ask);

                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                secretShare[i] = (SecretShare) in.readObject();
            }catch (IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        BigInteger t = Shamir.assembleSecretWithSecretShare(secretShare,prime);
        SecretKey secretKey = new SecretKey(t);
        return secretKey;
    }

    public static boolean exists(int id){
        return connected.contains(id);
    }
}
