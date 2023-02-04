import com.clochelabs.*;
import org.junit.Test;
import org.testng.AssertJUnit;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Clock;


public class testCrypto {
        @Test
        public void Ultimate_time(){
            long now = Clock.systemUTC().millis();
            Crypto.KeyGen(2048);
            long after = Clock.systemUTC().millis();
            System.out.println(after-now);
            AssertJUnit.assertTrue(after-now < 60000);

        }

        @Test
        public void Core_test(){

            final int CERTAINTY = 256;
            final SecureRandom random = new SecureRandom();

            Key[] key = Crypto.KeyGen(2048);
            final SecretKey secretKey = (SecretKey) key[1];
            final BigInteger secret = secretKey.getX();

            // prime number must be longer then secret number
            final BigInteger prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);

            SecretShare[] s = Shamir.SharedSecret(secret,3,4,prime,random);
            BigInteger result = Shamir.assembleSecretWithSecretShare(s,prime);
            AssertJUnit.assertEquals(secret,result);
        }
}
