package hu.saborsoft.blockchain.support;

import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilityMethodsTest {

    @Test
    void messageDigestSHA256_toBytesTest() {
        // GIVEN
        String message = "Hello, World!";
        byte[] expectedHash = {
                -33, -3, 96, 33, -69, 43, -43, -80, -81, 103, 98, -112, -128, -98, -61, -91,
                49, -111, -35, -127, -57, -9, 10, 75, 40, 104, -118, 54, 33, -126, -104, 111
        };
        // WHEN
        byte[] actualHash = UtilityMethods.messageDigestSHA256_toBytes(message);
        // THEN
        assertThat(expectedHash).isEqualTo(actualHash);
    }

    @Test
    void messageDigestSHA256_toStringTest() {
        // GIVEN
        String message = "Hello, World!";
        String expectedHash = "3/1gIbsr1bCvZ2KQgJ7DpTGR3YHH9wpLKGiKNiGCmG8=";
        // WHEN
        String actualHash = UtilityMethods.messageDigestSHA256_toString(message);
        // THEN
        assertThat(expectedHash).isEqualTo(actualHash);
    }

    @Test
    void getTimeStampTest() {
        // GIVEN
        long currentTime = Calendar.getInstance().getTimeInMillis();
        // WHEN
        long actualTime = UtilityMethods.getTimeStamp();
        // THEN
        assertThat(currentTime).isLessThanOrEqualTo(actualTime);
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithValidHashAndDifficultyLevel() {
        // GIVEN
        String hash = "00000123456789abcdef";
        int difficultyLevel = 5;
        // WHEN
        boolean result = UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel);
        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithInvalidHashAndDifficultyLevel() {
        // GIVEN
        String hash = "123456789abcdef";
        int difficultyLevel = 5;
        // WHEN
        boolean result = UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel);
        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithZeroDifficultyLevel() {
        // GIVEN
        String hash = "123456789abcdef";
        int difficultyLevel = 0;
        // WHEN
        boolean result = UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel);
        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithEmptyHashAndDifficultyLevel() {
        // GIVEN
        String hash = "";
        int difficultyLevel = 5;
        // WHEN
        boolean result = UtilityMethods.hashMeetsDifficultyLevel(hash, difficultyLevel);
        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void hashMeetsDifficultyLevelTest_WithNullHashAndDifficultyLevel() {
        // GIVEN
        int difficultyLevel = 5;
        // WHEN
        boolean result = UtilityMethods.hashMeetsDifficultyLevel(null, difficultyLevel);
        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void toBinaryStringTest_WithValidInput() {
        // GIVEN
        byte[] hash = {(byte) 0xAF, (byte) 0x0C, (byte) 0x3B};
        String expectedBinaryString = "001011111000110010111011";
        // WHEN
        String result = UtilityMethods.toBinaryString(hash);
        // THEN
        assertThat(result).isEqualTo(expectedBinaryString);
    }

    @Test
    void toBinaryStringTest_WithEmptyArray() {
        // GIVEN
        byte[] hash = {};
        String expectedBinaryString = "";
        // WHEN
        String result = UtilityMethods.toBinaryString(hash);
        // THEN
        assertThat(result).isEqualTo(expectedBinaryString);
    }

    @Test
    void toBinaryStringTest_WithNullInput() {
        assertThatThrownBy(() -> UtilityMethods.toBinaryString(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Cannot read the array length because \"<local2>\" is null");
    }

}