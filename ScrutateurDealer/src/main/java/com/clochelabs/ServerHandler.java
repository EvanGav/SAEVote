package com.clochelabs;


import com.clochelabs.packet.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerHandler extends Thread{
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Socket socket;

    private int KEYLENGTH = 20;

    /*
    Builder for tests
     */
    public ServerHandler(){

    }

    public ServerHandler(Socket socket){
        System.out.println("reçu");
        try {
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            Packet request = (Packet) in.readObject();
            switch(request.getType()){
                case GETRESULT -> {
                    System.out.println("result");
                    out.writeObject(getResult((GetResultPacket) request));
                }
                case GETKEY -> {
                    out.writeObject(getPublicKey((GetKeyPacket) request));
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + request.getType());
            }
        }catch (IOException | ClassNotFoundException i){
            i.printStackTrace();
        }
    }

    public Packet getResult(GetResultPacket request) {
        if(ServiceManager.exists(request.getIdRef())){
            PublicKey pk = ServiceManager.getPk();
            SecretKey sk = ServiceManager.getSk();
            int result = Crypto.Decrypt(pk, sk, request.getChiffre());
            return new GiveResultScrutPacket(result);
        }
        return new ErrorPacket("Ref id does not correspond to a ref");
    }

    public Packet getPublicKey(GetKeyPacket request){
        System.out.println(request.getIdRef());
        if(ServiceManager.exists(request.getIdRef())){
            return new GiveKeyPacket(ServiceManager.getPk());
        }
        Key[] keys = Crypto.KeyGen(20);
        System.out.println(keys[0]);
        ServiceManager.add(request.getIdRef());
        return new GiveKeyPacket(ServiceManager.getPk());
    }
}
