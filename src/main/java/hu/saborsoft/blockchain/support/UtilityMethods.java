package hu.saborsoft.blockchain.support;

import hu.saborsoft.blockchain.exception.NoAlgorithmException;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

public class UtilityMethods {

    public static final String ALGORITHM = "SHA-256";

    private UtilityMethods() {
    }

    public static byte[] messageDigestSHA256ToBytes(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(message.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new NoAlgorithmException("wrong algorithm, can never happen", e);
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

}
