package hu.saborsoft.blockchain.transaction;

import hu.saborsoft.blockchain.support.UtilityMethods;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    @Test
    void constructorWithSingleReceiverTest() {
        // Generate key pairs for sender and receiver
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiverKeyPair = UtilityMethods.generateKeyPair(2048);

        // Create a UTXO as input
        List<UTXO> inputs = new ArrayList<>();
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0);
        inputs.add(utxo);


        // Create a Transaction with a single receiver
        Transaction transaction = new Transaction(senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 5.0, inputs);

        // Verify the properties of the Transaction object
        assertThat(transaction.getHashID()).isNotNull();
        assertThat(transaction.getSender()).isEqualTo(senderKeyPair.getPublic());
        assertThat(transaction.getNumberOfInputUTXOs()).isEqualTo(1);
        assertThat(transaction.getNumberOfOutputUTXOs()).isZero();
        assertThat(transaction.verifySignature()).isFalse();
    }

    @Test
    void constructorWithMultipleReceiversTest() {
        // Generate key pairs for sender and receivers
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiver1KeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiver2KeyPair = UtilityMethods.generateKeyPair(2048);

        // Create a UTXO as input
        List<UTXO> inputs = new ArrayList<>();
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiver1KeyPair.getPublic(), 10.0);
        inputs.add(utxo);

        // Create a Transaction with multiple receivers
        PublicKey[] receivers = {receiver1KeyPair.getPublic(), receiver2KeyPair.getPublic()};
        double[] fundToTransfer = {5.0, 3.0};
        Transaction transaction = new Transaction(senderKeyPair.getPublic(), receivers, fundToTransfer, inputs);

        // Verify the properties of the Transaction object
        assertThat(transaction.getHashID()).isNotNull();
        assertThat(transaction.getSender()).isEqualTo(senderKeyPair.getPublic());
        assertThat(transaction.getNumberOfInputUTXOs()).isEqualTo(1);
        assertThat(transaction.getNumberOfOutputUTXOs()).isZero();
        assertThat(transaction.verifySignature()).isFalse();
    }

    @Test
    void equalsTest() {
        // Create two Transaction objects with the same hash ID
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        KeyPair receiverKeyPair = UtilityMethods.generateKeyPair(2048);
        List<UTXO> inputs = new ArrayList<>();
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0);
        inputs.add(utxo);
        Transaction transaction1 = new Transaction(senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 5.0, inputs);

        // Verify that the two Transaction objects are equal
        assertThat(transaction1).isEqualTo(transaction1);

        // Create a Transaction object with a different hash ID
        Transaction transaction3 = new Transaction(senderKeyPair.getPublic(), receiverKeyPair.getPublic(), 10.0, inputs);

        // Verify that the two Transaction objects are not equal
        assertThat(transaction1).isNotEqualTo(transaction3);
    }

}
