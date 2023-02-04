package com.clochelabs;

import java.math.BigInteger;
import java.util.Random;

public class TirageX extends Thread{

    public BigInteger x;
    @Override
    public void run() {
        super.run();
        tirageX(Crypto.getP());
    }

    private void tirageX(BigInteger p) {
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

        Crypto.setX(x);
        this.interrupt();
    }
}
