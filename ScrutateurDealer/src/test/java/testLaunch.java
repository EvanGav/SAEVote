import com.clochelabs.*;
import com.clochelabs.packet.AskSharedSecret;
import com.clochelabs.packet.GiveSharedSecretPacket;
import com.clochelabs.packet.Packet;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;



public class testLaunch {

    @Test
    public void testSeparateKeyThrowAndReAssemble(){
        Key[] key = Crypto.KeyGen(204);
        SecretKey secretKey = (SecretKey) key[1];
        PublicKey publicKey = (PublicKey) key[0] ;
        Inet4Address[] addressesOfKeyHolder = new Inet4Address[4];
        try{
            addressesOfKeyHolder[0] = (Inet4Address) Inet4Address.getByName("127.0.0.1");
            addressesOfKeyHolder[1] = (Inet4Address) Inet4Address.getByName("10.20.133.11");
            addressesOfKeyHolder[2] = (Inet4Address) Inet4Address.getByName("10.20.133.10");
            addressesOfKeyHolder[3] = (Inet4Address) Inet4Address.getByName("10.20.133.12");
        }catch(IOException e){
            e.printStackTrace();
        }

        BigInteger secret = secretKey.getX();
        final int CERTAINTY = 256;
        final SecureRandom random = new SecureRandom();
        BigInteger prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);

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
        SecretKey secretKeyEnd = new SecretKey(t);
        Assert.assertEquals(secretKeyEnd,secretKey);
    }
}
