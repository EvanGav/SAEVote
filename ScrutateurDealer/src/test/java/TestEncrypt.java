import com.clochelabs.Crypto;
import com.clochelabs.Key;
import com.clochelabs.PublicKey;
import com.clochelabs.SecretKey;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertNotEquals;
import static org.testng.AssertJUnit.*;

public class TestEncrypt {

    private boolean test_publicKeyIsCorrect(PublicKey pk){
        BigInteger pPrime = pk.getP().subtract(BigInteger.ONE).divide(BigInteger.TWO);
        if(!pPrime.isProbablePrime(100)){
            return false;
        }
        if(!pk.getG().modPow(pPrime, pk.getP()).equals(BigInteger.ONE)){
            return false;
        }
        return true;
    }

    private boolean test_privateKeyIsCorrect(PublicKey pk , SecretKey sk){
        if(!pk.getG().modPow(sk.getX(), pk.getP()).equals(pk.getH())){
            return false;
        }
        return true;
    }

    @Test
    public void testIfPublicKeyCorrectWith5(){
        Key[] keys = Crypto.KeyGen(5);
        assertTrue(test_publicKeyIsCorrect((PublicKey) keys[0]));
    }

    @Test
    public void testIfPublicKeyCorrectWith10(){
        Key[] keys = Crypto.KeyGen(10);
        assertTrue(test_publicKeyIsCorrect((PublicKey) keys[0]));
    }

    @Test
    public void testIfPublicKeyCorrectWith20(){
        Key[] keys = Crypto.KeyGen(20);
        assertTrue(test_publicKeyIsCorrect((PublicKey) keys[0]));
    }
    @Test
    public void testIfPublicKeyCorrectWith500(){
        Key[] keys = Crypto.KeyGen(500);
        assertTrue(test_publicKeyIsCorrect((PublicKey) keys[0]));
    }

    @Test
    public void testIfPublicKeyCorrectWith2048(){
        Key[] keys = Crypto.KeyGen(2048);
        assertTrue(test_publicKeyIsCorrect((PublicKey) keys[0]));
    }

    @Test
    public void testIfSecretKeyCorrectWith5(){
        Key[] keys = Crypto.KeyGen(5);
        assertTrue(test_privateKeyIsCorrect((PublicKey) keys[0], (SecretKey) keys[1]));
    }

    @Test
    public void testIfSecretKeyCorrectWith10(){
        Key[] keys = Crypto.KeyGen(10);
        assertTrue(test_privateKeyIsCorrect((PublicKey) keys[0], (SecretKey) keys[1]));
    }
    @Test
    public void testIfSecretKeyCorrectWith20(){
        Key[] keys = Crypto.KeyGen(20);
        assertTrue(test_privateKeyIsCorrect((PublicKey) keys[0], (SecretKey) keys[1]));
    }

    @Test
    public void testIfSecretKeyCorrectWith500(){
        Key[] keys = Crypto.KeyGen(500);
        assertTrue(test_privateKeyIsCorrect((PublicKey) keys[0], (SecretKey) keys[1]));
    }

    @Test
    public void testIfEncryptGivesADifferentValueOne(){
        Key[] keys = Crypto.KeyGen(5);
        BigInteger[] c = Crypto.Encrypt((PublicKey) keys[0], 1);
        assertNotEquals(c[0], BigInteger.ONE);
        assertNotEquals(c[1], BigInteger.ONE);
    }

    @Test
    public void testIfDecryptWorkWithSameKeyAndBitLength5(){
        Key[] keys = Crypto.KeyGen(5);
        PublicKey pk = (PublicKey) keys[0];
        SecretKey sk = (SecretKey) keys[1];
        BigInteger[] c = Crypto.Encrypt(pk, 3);
        int decrypt = Crypto.Decrypt(pk ,sk , c);

        assertTrue(decrypt==3);
    }

    @Test
    public void testIfDecryptWorkWithSameKeyAndBitLength10(){
        Key[] keys = Crypto.KeyGen(10);
        PublicKey pk = (PublicKey) keys[0];
        SecretKey sk = (SecretKey) keys[1];
        BigInteger[] c = Crypto.Encrypt(pk, 3);
        int decrypt = Crypto.Decrypt(pk ,sk , c);

        assertEquals(3, decrypt);
    }

    @Test
    public void testIfDecryptWorkWithSameKeyAndBitLength20(){
        Key[] keys = Crypto.KeyGen(20);
        PublicKey pk = (PublicKey) keys[0];
        SecretKey sk = (SecretKey) keys[1];
        BigInteger[] c = Crypto.Encrypt(pk, 3);
        int decrypt = Crypto.Decrypt(pk ,sk , c);

        assertEquals(3, decrypt);
    }

    @Test
    public void testIfDecryptWorkWithSameKeyAndBitLength25(){
        Key[] keys = Crypto.KeyGen(25);
        PublicKey pk = (PublicKey) keys[0];
        SecretKey sk = (SecretKey) keys[1];
        BigInteger[] c = Crypto.Encrypt(pk, 3);
        int decrypt = Crypto.Decrypt(pk ,sk , c);

        assertEquals(3, decrypt);
    }


    @Test
    public void testIfDecryptWorkWithDifferentKeyAndBitLength5(){
        Key[] keys1 = Crypto.KeyGen(5);
        PublicKey pk1 = (PublicKey) keys1[0];
        SecretKey sk1 = (SecretKey) keys1[1];

        Key[] keys2 = Crypto.KeyGen(5);
        PublicKey pk2 = (PublicKey) keys2[0];
        SecretKey sk2 = (SecretKey) keys2[1];

        BigInteger[] c = Crypto.Encrypt(pk2, 3);

        int decrypt = Crypto.Decrypt(pk1 ,sk1 , c);

        assertFalse(decrypt==3);
    }

}
