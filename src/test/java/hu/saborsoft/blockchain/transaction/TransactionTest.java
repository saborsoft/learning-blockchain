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

    @Test
    void prepareOutputUTXOs_When_ReceiversLengthNotEqual_FundToTransferLength_ReturnsFalse() {
        // Create a Transaction object with receivers and fundToTransfer of different lengths
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        PublicKey receiverKey = UtilityMethods.generateKeyPair(2048).getPublic();
        List<UTXO> inputs = new ArrayList<>();
        Transaction transaction = new Transaction(senderKeyPair.getPublic(), new PublicKey[]{receiverKey}, new double[]{10.0, 20.0}, inputs);

        // Call the method and verify it returns false
        assertThat(transaction.prepareOutputUTXOs()).isFalse();
    }

    @Test
    void prepareOutputUTXOs_When_AvailableFundsLessThanTotalCost_ReturnsFalse() {
        // Create a Transaction object with insufficient available funds
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        PublicKey receiverKey = UtilityMethods.generateKeyPair(2048).getPublic();
        List<UTXO> inputs = new ArrayList<>();
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKey, 5.0); // Total available funds: 5.0
        inputs.add(utxo);
        Transaction transaction = new Transaction(senderKeyPair.getPublic(), new PublicKey[]{receiverKey}, new double[]{10.0}, inputs);

        // Call the method and verify it returns false
        assertThat(transaction.prepareOutputUTXOs()).isFalse();
    }

    @Test
    void prepareOutputUTXOs_When_InputsAndOutputsCorrect_ReturnsTrue() {
        // Create a Transaction object with correct input data
        KeyPair senderKeyPair = UtilityMethods.generateKeyPair(2048);
        PublicKey receiverKey = UtilityMethods.generateKeyPair(2048).getPublic();
        List<UTXO> inputs = new ArrayList<>();
        UTXO utxo = new UTXO("parentTransactionID", senderKeyPair.getPublic(), receiverKey, 15.0); // Total available funds: 15.0
        inputs.add(utxo);
        Transaction transaction = new Transaction(senderKeyPair.getPublic(), new PublicKey[]{receiverKey}, new double[]{10.0}, inputs);

        // Call the method and verify it returns true
        assertThat(transaction.prepareOutputUTXOs()).isTrue();

        // Verify the properties of the Transaction object
        assertThat(transaction.getNumberOfOutputUTXOs()).isEqualTo(2);
        assertThat(transaction.getSender()).isEqualTo(senderKeyPair.getPublic());
        assertThat(transaction.getOutputUTXO(0).getReceiver()).isEqualTo(receiverKey);
        // the second output is the change, the receiver is also the sender in this case
        assertThat(transaction.getOutputUTXO(1).getReceiver()).isEqualTo(senderKeyPair.getPublic());
        assertThat(transaction.getOutputUTXO(0).getFundTransferred()).isEqualTo(10.0);
        // change is only 4 not 5, because we need to deduct the transaction fee
        assertThat(transaction.getOutputUTXO(1).getFundTransferred()).isEqualTo(4.0);
    }

}
