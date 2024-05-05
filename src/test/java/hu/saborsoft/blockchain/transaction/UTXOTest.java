package hu.saborsoft.blockchain.transaction;

import hu.saborsoft.blockchain.support.UtilityMethods;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThat;

class UTXOTest {

    @Test
    void constructorTest() {
        // GIVEN
        // Mock key pair for sender and receiver
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiverKeyPair = UtilityMethods.generateKeyPair(2048);

        // WHEN
        // Create a UTXO object
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0);

        // THEN
        // Verify the properties of the UTXO object
        assertThat(utxo.getHashID()).isNotNull();
        assertThat(utxo.getParentTransactionID()).isEqualTo("parentTransactionID");
        assertThat(utxo.getSender()).isEqualTo(senderKeyPair.getPublic());
        assertThat(utxo.getReceiver()).isEqualTo(receiverKeyPair.getPublic());
        assertThat(utxo.getFundTransferred()).isEqualTo(10.0);
        assertThat(utxo.getTimestamp()).isPositive();
        assertThat(utxo.getSequentialNumber()).isNotNegative();
    }

    @Test
    void computeHashIDTest() {
        // Mock key pair for sender and receiver
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiverKeyPair = UtilityMethods.generateKeyPair(2048);

        // Create a UTXO object
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0);

        // Compute the hash ID
        String hashID = utxo.computeHashID();

        // Verify that the hash ID is not null
        assertThat(hashID).isNotNull();
    }

    @Test
    void equalsTest() {
        // Create two UTXO objects with the same hash ID
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiverKeyPair = UtilityMethods.generateKeyPair(2048);
        UTXO utxo1 = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0);

        // Verify that the two UTXO objects are equal
        assertThat(utxo1).isEqualTo(utxo1);

        // Create a UTXO object with a different hash ID
        UTXO utxo2 = new UTXO("differentParentTransactionID", senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0);

        // Verify that the two UTXO objects are not equal
        assertThat(utxo2).isNotEqualTo(utxo1);
    }

}
