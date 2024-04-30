package hu.saborsoft.blockchain.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;

public class UtilityMethods {

    public static byte[] messageDigestSHA256_toBytes(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(message.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String messageDigestSHA256_toString(String message) {
        return Base64.getEncoder().encodeToString(messageDigestSHA256_toBytes(message));
    }

    public static long getTimeStamp() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
