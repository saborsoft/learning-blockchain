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
        // make sure that the sender has enough fund
        double available = 0.0;
        for (int i = 0; i < input.size(); i++) {
            available += input.get(i).getFundTransferred();
        }
        // compute the total cost and add the transaction fee
        double totalCost = t.getTotalFundToTransfer() + Transaction.TRANSACTION_FEE;
        // if fund is not enough, abort
        if (available < totalCost) {
            System.out.println("fund available=" + available
                    + ", not enough for total cost of " + totalCost);
            return;
        }
        // generate the output
        for (int i = 0; i < receivers.length; i++) {
            UTXO ut = new UTXO(t.getHashID(), sender.getPublic(), receivers[i], fundsToTransfer[i]);
            t.addOutputUTXO(ut);
        }
        // generate the change as an UTXO to the sender
        UTXO change = new UTXO(t.getHashID(), sender.getPublic(), sender.getPublic(), available - totalCost);
        t.addOutputUTXO(change);
        // sign the transaction
        t.signTheTransaction(sender.getPrivate());
        // display the transaction to take a look
        displayTransaction(t);
    }

    // A method written to display the transaction properly.
    private static void displayTransaction(Transaction t) {
        System.out.println("Transaction{");
        System.out.println("\tID: " + t.getHashID());
        System.out.println("\tsender:" + UtilityMethods.getKeyString(t.getSender()));
        System.out.println("\tfundToBeTransferred total: " + t.getTotalFundToTransfer());
        System.out.println("\tReceivers:");
        for (int i = 0; i < t.getNumberOfOutputUTXOs(); i++) {
            UTXO utxo = t.getOutputUTXO(i);
            System.out.println("\t\tfund=" + utxo.getFundTransferred()
                    + ",receiver=" + UtilityMethods.getKeyString(utxo.getReceiver()));
        }
        UTXO change = t.getOutputUTXO(t.getNumberOfOutputUTXOs() - 1);
        System.out.println("\ttransaction fee: " + Transaction.TRANSACTION_FEE);
        System.out.println("\tchange: " + change.getFundTransferred());
        boolean b = t.verifySignature();
        System.out.println("\tsignature verification: " + b);
        System.out.println("}");
    }
}
