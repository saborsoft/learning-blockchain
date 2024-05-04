package hu.saborsoft.blockchain.support;

import hu.saborsoft.blockchain.exception.NoAlgorithmException;
import hu.saborsoft.blockchain.exception.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.time.Instant;
import java.util.Base64;

public class UtilityMethods {

    public static final String ALGORITHM = "SHA-256";

    public static final String WRONG_ALGORITHM = "wrong algorithm, can never happen";

    private static final Logger LOG = LoggerFactory.getLogger(UtilityMethods.class);

    private static long uniqueNumber = 0;

    public static long getUniqueNumber() {
        return uniqueNumber++;
    }

    private UtilityMethods() {
    }

    public static byte[] messageDigestSHA256ToBytes(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(message.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new NoAlgorithmException(WRONG_ALGORITHM, e);
        }
    }

    public static String messageDigestSHA256ToString(String message) {
        return Base64.getEncoder().encodeToString(messageDigestSHA256ToBytes(message));
    }

    public static long getTimeStamp() {
        return Instant.now().toEpochMilli();
    }

    public static boolean hashMeetsDifficultyLevel(String hash, int difficultyLevel) {
        if (StringUtils.isBlank(hash)) {
            return false;
        }

        char[] c = hash.toCharArray();
        for (int i = 0; i < difficultyLevel; i++) {
            if (c[i] != '0') {
                return false;

            }
        }
        return true;
    }

    public static String toBinaryString(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            // Transform a byte into an unsigned integer.
            int x = b + 128;
            StringBuilder s = new StringBuilder(Integer.toBinaryString(x));
            while (s.length() < 8) {
                s.insert(0, "0");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keySize);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new NoAlgorithmException(WRONG_ALGORITHM, e);
        }
    }

    public static byte[] generateSignature(PrivateKey privateKey, String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (Exception e) {
            throw new SignatureException("signature error", e);
        }
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] signature, String message) {
        try {
            Signature sig2 = Signature.getInstance("SHA256withRSA");
            sig2.initVerify(publicKey);
            sig2.update(message.getBytes());
            return sig2.verify(signature);
        } catch (Exception e) {
            LOG.error("Signature verification error", e.getCause());
            return false;
        }
    }

    public static String getKeyString(Key key) {
        return Base64.getEncoder().encodeToString((key.getEncoded()));
    }

}
