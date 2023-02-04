package com.clochelabs;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class Crypto {

    private static BigInteger p;
    private static BigInteger g;
    private static BigInteger x;

    private static BigInteger h;
    private static int nbThread;
    private static int l;

    public static int getNbThread() {
        return nbThread;
    }

    public static void setNbThread(int nbThread) {
        Crypto.nbThread = nbThread;
    }

    public static void setP(BigInteger p) {
        Crypto.p = p;
    }

    public static BigInteger getP() {
        return p;
    }

    public static void setH(BigInteger h) {
        Crypto.h = h;
    }

    public static void setG(BigInteger g) {
        Crypto.g = g;
    }

    public static void setX(BigInteger x) {
        Crypto.x = x;
    }

    public static int getL() {
        return l;
    }

    private static BigInteger tirageP(int l) {
        Crypto.l = l;
        p = BigInteger.TWO;
        do {
            Thread n = new FindP();
            n.start();
        } while (!(p.subtract(BigInteger.ONE).divide(BigInteger.TWO)).isProbablePrime(100));
        return p;
    }





    private static BigInteger tirageH(BigInteger p, BigInteger g, BigInteger x) {
        return g.modPow(x, p);
    }

    private static BigInteger tirageX(BigInteger p) {
        boolean found = false;
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        BigInteger x = new BigInteger(pPrime.bitLength(), new Random());
        while (!found) {
            if (x.compareTo(pPrime) < 0 && x.compareTo(BigInteger.ZERO) >= 0) {
                found = true;
            } else {
                x = new BigInteger(pPrime.bitLength(), new Random());
            }
        }

        return x;
    }

    private static BigInteger tirageG(BigInteger p) {
        boolean found = false;
        BigInteger g = new BigInteger(p.bitLength(), new Random());
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        while (!found) {
            BigInteger remainder = g.modPow(pPrime, p);
            if (!(g.compareTo(p) < 0 && g.compareTo(BigInteger.ONE) > 0)) {
                g = new BigInteger(p.bitLength(), new Random());
            } else {
                if (remainder.equals(BigInteger.ONE)) {
                    found = true;
                } else {
                    g = new BigInteger(p.bitLength(), new Random());
                }
            }
        }
        return g;
    }

    private static BigInteger tirageR(BigInteger p) {

        return tirageX(p);
    }

    private static BigInteger[] produireC(BigInteger g, BigInteger r, BigInteger p, BigInteger h, int m) {

        BigInteger c[] = {BigInteger.ZERO, BigInteger.ZERO};
        c[0] = g.modPow(r, p);
        BigInteger pt1 = g.modPow(BigInteger.valueOf(m), p);
        BigInteger pt2 = h.modPow(r, p);
        c[1] = pt1.multiply(pt2);

        return c;
    }

    private static BigInteger produireM(BigInteger u, BigInteger v, BigInteger x, BigInteger p) {
        BigInteger v1p = v.mod(p);
        BigInteger uxinv = (u.pow(x.intValue())).modInverse(p);
        return v1p.multiply(uxinv).mod(p);
    }

    private static int produirem(BigInteger M, BigInteger p, BigInteger g) {
        BigInteger m = BigInteger.ZERO;
        BigInteger mReel = new BigInteger(String.valueOf(8));
        while (!M.equals(g.modPow(m, p))) {
            m = m.add(BigInteger.ONE);
        }
        return m.intValue();
    }


    public static BigInteger[] Agrege(BigInteger[] c1, BigInteger[] c2, BigInteger p) {
        BigInteger c[] = {BigInteger.ZERO, BigInteger.ZERO};
        BigInteger uP = c1[0].mod(p);
        BigInteger uPrimeP = c2[0].mod(p);
        c[0] = uP.multiply(uPrimeP);

        BigInteger vP = c1[1].mod(p);
        BigInteger vPrimeP = c2[1].mod(p);
        c[1] = vP.multiply(vPrimeP);

        return c;
    }

    public static Key[] KeyGen(int l) {
        Crypto.p = Crypto.tirageP(l);
        Crypto.g = Crypto.tirageG(Crypto.p);
        Crypto.x = Crypto.tirageX(Crypto.p);

        Crypto.h = Crypto.tirageH(Crypto.p, Crypto.g, Crypto.x);

        return new Key[]{new PublicKey(Crypto.p, Crypto.g, Crypto.h), new SecretKey(Crypto.x)};
    }

    public static BigInteger[] Encrypt(PublicKey pk, int m) {
        BigInteger r = Crypto.tirageR(pk.getP());
        return Crypto.produireC(pk.getG(), r, pk.getP(), pk.getH(), m);
    }

    public static int Decrypt(PublicKey pk, SecretKey sk, BigInteger[] message) {
        BigInteger M = Crypto.produireM(message[0], message[1], sk.getX(), pk.getP());
        return Crypto.produirem(M, pk.getP(), pk.getG());
    }

    public static String sha256(String msg){
        try {


            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(msg.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 64) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}