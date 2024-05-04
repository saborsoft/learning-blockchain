package hu.saborsoft.blockchain.example.cryptography;

import hu.saborsoft.blockchain.support.UtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;

import static hu.saborsoft.blockchain.support.UtilityMethods.generateKeyPair;

public class SignatureExample {

    public static final String MESSAGE = "If you were a drop of tear in my eyes";

    private static final Logger LOG = LoggerFactory.getLogger(SignatureExample.class);

    public static void main(String[] args) {
        LOG.debug("Digital signature example");

        KeyPair keyPair = generateKeyPair(2048);

        // create a signature with the private key and sign the message
        byte[] digitalSignature = UtilityMethods.generateSignature(keyPair.getPrivate(), MESSAGE);
        LOG.debug("Digital signature {}", new String(digitalSignature));

        // create another signature with the public key for verification
        // check the validity of the signature
        boolean verified = UtilityMethods.verifySignature(keyPair.getPublic(), digitalSignature, MESSAGE);
        LOG.debug("verified: {}", verified);

    }

}
