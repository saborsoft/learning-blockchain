package hu.saborsoft.blockchain.support;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

class UtilityMethodsTest {

    @Test
    void messageDigestSHA256_toBytesTest() {
        String message = "Hello, World!";
        byte[] expectedHash = {
                -33, -3, 96, 33, -69, 43, -43, -80, -81, 103, 98, -112, -128, -98, -61, -91,
                49, -111, -35, -127, -57, -9, 10, 75, 40, 104, -118, 54, 33, -126, -104, 111
        };
        byte[] actualHash = UtilityMethods.messageDigestSHA256_toBytes(message);
        assertArrayEquals(expectedHash, actualHash);
    }

    @Test
    void messageDigestSHA256_toStringTest() {
        String message = "Hello, World!";
        String expectedHash = "3/1gIbsr1bCvZ2KQgJ7DpTGR3YHH9wpLKGiKNiGCmG8=";
        String actualHash = UtilityMethods.messageDigestSHA256_toString(message);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    void getTimeStampTest() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long actualTime = UtilityMethods.getTimeStamp();
        assertTrue(actualTime >= currentTime);
    }
}