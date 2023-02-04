package com.clochelabs;

import java.math.BigInteger;
import java.util.Random;

public final class Shamir
{
    public static SecretShare[] SharedSecret(final BigInteger secret, int needed, int available, BigInteger prime, Random random)
    {

        final BigInteger[] coeff = new BigInteger[needed];
        coeff[0] = secret;
        for (int i = 1; i < needed; i++)
        {
            BigInteger r;
            while (true)
            {
                r = new BigInteger(prime.bitLength(), random);
                if (r.compareTo(BigInteger.ZERO) > 0 && r.compareTo(prime) < 0)
                {
                    break;
                }
            }
            coeff[i] = r;
        }

        SecretShare[] shares = new SecretShare[available];
        for (int x = 1; x <= available; x++)
        {
            BigInteger stack = secret;

            for (int exp = 1; exp < needed; exp++)
            {
                stack = stack.add(coeff[exp].multiply(BigInteger.valueOf(x).pow(exp).mod(prime))).mod(prime);
            }
            shares[x - 1] = new SecretShare(x, stack);
        }

        return shares;
    }

    public static BigInteger assembleSecretWithSecretShare(final SecretShare[] shares, final BigInteger prime)
    {
        BigInteger stack = BigInteger.ZERO;

        for(int i = 0; i < shares.length; i++)
        {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for(int j = 0; j < shares.length; j++)
            {
                if(i == j)
                    continue; // If not the same value

                int startPosition = shares[i].getNumber();
                int nextPosition = shares[j].getNumber();

                numerator = numerator.multiply(BigInteger.valueOf(nextPosition).negate()).mod(prime); // (numerator * -nextposition) % prime;
                denominator = denominator.multiply(BigInteger.valueOf(startPosition - nextPosition)).mod(prime); // (denominator * (startposition - nextposition)) % prime;
            }
            BigInteger value = shares[i].getShare();
            BigInteger tmp = value.multiply(numerator).multiply(InverseModulaire(denominator, prime));
            stack = prime.add(stack).add(tmp).mod(prime); //  (prime + accum + (value * numerator * modInverse(denominator))) % prime;
        }


        return stack;
    }

    private static BigInteger[] EuclideEtendu(BigInteger a, BigInteger b)
    {
        if (b.compareTo(BigInteger.ZERO) == 0)
            return new BigInteger[] {a, BigInteger.ONE, BigInteger.ZERO};
        else
        {
            BigInteger n = a.divide(b);
            BigInteger c = a.mod(b);
            BigInteger[] r = EuclideEtendu(b, c);
            return new BigInteger[] {r[0], r[2], r[1].subtract(r[2].multiply(n))};
        }
    }

    private static BigInteger InverseModulaire(BigInteger k, BigInteger prime)
    {
        k = k.mod(prime);
        BigInteger r = (k.compareTo(BigInteger.ZERO) < 0) ? (EuclideEtendu(prime, k.negate())[2]).negate() : EuclideEtendu(prime,k)[2];
        return prime.add(r).mod(prime);
    }


}