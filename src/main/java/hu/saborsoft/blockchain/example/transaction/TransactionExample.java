package hu.saborsoft.blockchain.example.transaction;

import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class TransactionExample {

    public static void main(String[] args) {
        // Generate the sender
        KeyPair sender = UtilityMethods.generateKeyPair(2048);
        // Let us have two recipients.
        PublicKey[] receivers = new PublicKey[2];
        double[] fundsToTransfer = new double[receivers.length];
        for (int i = 0; i < receivers.length; i++) {
            receivers[i] = UtilityMethods.generateKeyPair(2048).getPublic();
            fundsToTransfer[i] = (i + 1) * 100;
        }
        // As we do not have a wallet class to make the transaction
        // we need to manually create the input UTXOs and output UTXO
        UTXO uin = new UTXO("0", sender.getPublic(), sender.getPublic(), 1000);
        List<UTXO> input = new ArrayList<>();
        input.add(uin);
        Transaction t = new Transaction(sender.getPublic(), receivers, fundsToTransfer, input);
        boolean b = t.prepareOutputUTXOs();
        if (!b) {
            System.out.println("Transaction failed");
        } else {
            // sign the transaction
            t.signTheTransaction(sender.getPrivate());
            // display the transaction to take a look
            UtilityMethods.displayTransaction(t, System.out, 0);
        }
    }

}
