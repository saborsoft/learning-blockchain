package hu.saborsoft.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class TestChipher1 {

    public static final String MESSAGE = "If you were a drop of tear in my eyes";

    private static final Logger LOG = LoggerFactory.getLogger(TestChipher1.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("Original message: {}", MESSAGE);
        SecretKey key = createSecretKey();

        // encrypt the original message
        Cipher encrypter = initCipher(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = encrypter.doFinal(MESSAGE.getBytes());
        LOG.debug("Encrypted text: {}", new String(cipherText));

        // it is not mandatory, we can modify the type of the first cipher object but in the real world
        // this would be the case, a new cipher object for decryption
        Cipher decrypter = initCipher(Cipher.DECRYPT_MODE, key);
        byte[] decoded = decrypter.doFinal(cipherText);
        LOG.debug("Decoded string: {}", new String(decoded));
    }

    private static SecretKey createSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        keyGenerator.init(sr);
        return keyGenerator.generateKey();
    }

    private static Cipher initCipher(int cipherMode, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherMode, key);
        return cipher;
    }

}
