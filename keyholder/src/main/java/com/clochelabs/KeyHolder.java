package com.clochelabs;

import com.clochelabs.packet.AskSharedSecret;
import com.clochelabs.packet.GiveSharedSecretPacket;
import com.clochelabs.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class KeyHolder {

    private Inet4Address serverAdress;

    private SecretShare keyHold;
    private ServerSocket socket;



    public KeyHolder() throws IOException {
        serverAdress = (Inet4Address) Inet4Address.getByName("127.0.0.1");
        socket = new ServerSocket(5057);
        boolean keyReceived = false;
        while(!keyReceived){
            Socket s = socket.accept();

            ObjectInputStream o = new ObjectInputStream(s.getInputStream());
            System.out.println(serverAdress + "--> IP SERVER  CURRENT IP :" + s.getInetAddress());

            if(!s.getInetAddress().equals(serverAdress)){
                System.out.println("not a good packet error of address");
            }else {
                try {
                    Packet p = (Packet) o.readObject();
                    if (p.getType().equals(Packet.PacketType.GiveSharedSecretPacket)) {
                        GiveSharedSecretPacket g = (GiveSharedSecretPacket) p;
                        keyHold = g.getBigKey();
                        keyReceived = true;

                    } else {
                        System.out.println("not a good packet Shared exception");
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("not a good packet class not found");
                }
            }
            s.close();
            o.close();
        }

    }

    public void run(){


        System.out.println("I run");
        while(true){
            try{
                System.out.println("test");
                Socket s = socket.accept();

                System.out.println("test1" + s.getInputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());

                System.out.println("test2");
                if(s.getInetAddress().equals(serverAdress)){
                    System.out.println("test3");
                    System.out.println("test4");
                    Packet p = (Packet) in.readObject();
                    System.out.println("ici");
                    if(p.getType().equals(Packet.PacketType.AskSharedSecret)){
                        AskSharedSecret ask = (AskSharedSecret) p;
                        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                        ask.setAsk(keyHold);
                        out.writeObject(ask);
                    }
                }
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
