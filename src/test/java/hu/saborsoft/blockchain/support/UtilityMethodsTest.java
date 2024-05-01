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

    @Test
    void hashMeetsDifficultyLevelTest_WithValidHashAndDifficultyLevel() {
        String hash = "00000123456789abcdef";
        int difficultyLevel = 5;
        assertTrue(UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel));
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithInvalidHashAndDifficultyLevel() {
        String hash = "123456789abcdef";
        int difficultyLevel = 5;
        assertFalse(UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel));
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithZeroDifficultyLevel() {
        String hash = "123456789abcdef";
        int difficultyLevel = 0;
        assertTrue(UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel));
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithEmptyHashAndDifficultyLevel() {
        String hash = "";
        int difficultyLevel = 5;
        assertFalse(UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel));
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithNullHashAndDifficultyLevel() {
        String hash = null;
        int difficultyLevel = 5;
        assertFalse(UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel));
    }

    @Test
    void toBinaryStringTest_WithValidInput() {
        byte[] hash = {(byte) 0xAF, (byte) 0x0C, (byte) 0x3B};
        String expectedBinaryString = "001011111000110010111011";
        assertEquals(expectedBinaryString, UtilityMethods.toBinaryString(hash));
    }

    @Test
    void toBinaryStringTest_WithEmptyArray() {
        byte[] hash = {};
        String expectedBinaryString = "";
        assertEquals(expectedBinaryString, UtilityMethods.toBinaryString(hash));
    }

    @Test
    void toBinaryStringTest_WithNullInput() {
        byte[] hash = null;
        assertThrows(NullPointerException.class, () -> {
            UtilityMethods.toBinaryString(hash);
        });
    }

}