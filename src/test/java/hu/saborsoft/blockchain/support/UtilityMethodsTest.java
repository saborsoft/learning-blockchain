package hu.saborsoft.blockchain.support;

import org.junit.jupiter.api.Test;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
        byte[] actualHash = UtilityMethods.messageDigestSHA256ToBytes(message);
        // THEN
        assertThat(expectedHash).isEqualTo(actualHash);
    }

    @Test
    void messageDigestSHA256_toStringTest() {
        // GIVEN
        String message = "Hello, World!";
        String expectedHash = "3/1gIbsr1bCvZ2KQgJ7DpTGR3YHH9wpLKGiKNiGCmG8=";
        // WHEN
        String actualHash = UtilityMethods.messageDigestSHA256ToString(message);
        // THEN
        assertThat(expectedHash).isEqualTo(actualHash);
    }

    @Test
    void getTimeStampTest() {
        // GIVEN
        long currentTime = Instant.now().toEpochMilli();
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
                .hasMessageContaining("Cannot read the array length because");
    }

    @Test
    void generateKeyPairTest() {
        // GIVEN
        // Define the key size
        int keySize = 2048; // For example, you can choose any key size

        // WHEN
        // Invoke the method
        KeyPair keyPair = UtilityMethods.generateKeyPair(keySize);

        // THEN
        // Assert that the keyPair is not null
        assertThat(keyPair).isNotNull();
        // Optionally, you can also assert more specific properties of the keyPair, such as
        // checking that the public and private keys are not null.
        // For example:
        assertThat(keyPair.getPublic()).isNotNull();
        assertThat(keyPair.getPrivate()).isNotNull();
    }

    @Test
    void getUniqueNumberTest() {
        // Invoke the method twice and capture the returned values
        long firstNumber = UtilityMethods.getUniqueNumber();
        long secondNumber = UtilityMethods.getUniqueNumber();

        // Assert that the first number is less than the second number,
        // ensuring that each call returns a unique number
        assertThat(firstNumber).isLessThan(secondNumber);
    }

    @Test
    void getKeyStringTest() {
        // Create a mock Key object
        Key mockKey = new MockKey();

        // Generate the expected Base64-encoded string from the mock key
        String expectedEncodedString = Base64.getEncoder().encodeToString(mockKey.getEncoded());

        // Invoke the method with the mock key
        String result = UtilityMethods.getKeyString(mockKey);

        // Assert that the result matches the expected encoded string
        assertThat(result).isEqualTo(expectedEncodedString);
    }

    // Define a mock Key implementation for testing purposes
    static class MockKey implements Key {
        @Override
        public String getAlgorithm() {
            return null;
        }

        @Override
        public String getFormat() {
            return null;
        }

        @Override
        public byte[] getEncoded() {
            return "MockKeyEncodedBytes".getBytes(); // Provide some mock encoded bytes
        }
    }

}