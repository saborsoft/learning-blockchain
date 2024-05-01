package hu.saborsoft.blockchain.support;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;

public class UtilityMethods {

    private static final Logger LOG = LoggerFactory.getLogger(UtilityMethods.class);

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
            int x = ((int) b) + 128;
            StringBuilder s = new StringBuilder(Integer.toBinaryString(x));
            while (s.length() < 8) {
                s.insert(0, "0");
            }
            sb.append(s);
        }
        return sb.toString();
    }

}
