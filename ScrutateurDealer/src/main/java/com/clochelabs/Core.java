package com.clochelabs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Core {
    ServerSocket serverSocket;

    Socket socket;
    public Core(){
        try{
            serverSocket = new ServerSocket(5057);

        }catch(IOException e){
            e.printStackTrace();
        }
        Key[] key = Crypto.KeyGen(2048);
        run();
    }

    private void run() {

    }






}
