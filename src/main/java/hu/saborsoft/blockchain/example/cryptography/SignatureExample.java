package hu.saborsoft.blockchain.example.cryptography;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class SignatureExample {

    public static final String MESSAGE = "If you were a drop of tear in my eyes";

    private static final Logger LOG = LoggerFactory.getLogger(SignatureExample.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("Digital signature example");

        KeyPair keyPair = createKeyPair();

        // create a signature with the private key and sign the message
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(MESSAGE.getBytes());
        byte[] digitalSignature = signature.sign();
        LOG.debug("Digital signature {}", new String(digitalSignature));

        // create another signature with the public key for verification
        Signature signature2 = Signature.getInstance("SHA256withRSA");
        signature2.initVerify(keyPair.getPublic());
        signature2.update(MESSAGE.getBytes());

        // check the validity of the signature
        boolean verified = signature2.verify(digitalSignature);
        LOG.debug("verified: {}", verified);

    }

    private static KeyPair createKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

}
