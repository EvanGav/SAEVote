package com.clochelabs;

import java.math.BigInteger;
import java.util.Random;

public class TirageG extends Thread{
    @Override
    public void run() {
        super.run();
        tirageG(Crypto.getP());
    }

    private void tirageG(BigInteger p) {
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
        Crypto.setG(g);
        this.interrupt();
    }
}
