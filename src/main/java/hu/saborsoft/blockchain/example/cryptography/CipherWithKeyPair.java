package hu.saborsoft.blockchain.example.cryptography;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;

import static hu.saborsoft.blockchain.support.UtilityMethods.generateKeyPair;

/**
 * This cipher is encrypt and decrypt with a keypair (public and private key).
 */
public class CipherWithKeyPair {

    public static final String MESSAGE = "If you were a drop of tear in my eyes";

    private static final Logger LOG = LoggerFactory.getLogger(CipherWithKeyPair.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("KeyPair Cipher - it uses a private-public key pair for encrypt and decrypt");
        LOG.debug("Original message: {}", MESSAGE);

        KeyPair keyPair = generateKeyPair(4096);

        // encrypt the original message
        Cipher encrypter = initCipherEncryptMode(keyPair);
        byte[] cipherText = encrypter.doFinal(MESSAGE.getBytes());
        LOG.debug("Encrypted text: {}", new String(cipherText));

        // it is not mandatory, we can modify the type of the first cipher object but in the real world
        // this would be the case, a new cipher object for decryption
        Cipher decrypter = initCipherDecryptMode(keyPair);
        byte[] decoded = decrypter.doFinal(cipherText);
        LOG.debug("Decoded string: {}", new String(decoded));
    }

    private static Cipher initCipherEncryptMode(KeyPair keyPair) throws Exception {
        return initCipher(Cipher.ENCRYPT_MODE, keyPair.getPublic());
    }

    private static Cipher initCipherDecryptMode(KeyPair keyPair) throws Exception {
        return initCipher(Cipher.DECRYPT_MODE, keyPair.getPrivate());
    }

    private static Cipher initCipher(int cipherMode, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(cipherMode, key);
        return cipher;
    }

}
