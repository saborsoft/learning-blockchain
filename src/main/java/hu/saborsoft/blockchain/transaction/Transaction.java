package hu.saborsoft.blockchain.transaction;

import hu.saborsoft.blockchain.support.UtilityMethods;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Transaction implements Serializable {

    public static final double TRANSACTION_FEE = 1.0;

    private static final long serialVersionUID = 5818599907623118965L;

    private String hashID;

    private PublicKey sender;

    private PublicKey[] receivers;

    private double[] fundToTransfer;

    private long timestamp;

    private List<UTXO> inputs;

    private List<UTXO> outputs = new ArrayList<>(4);

    private byte[] signature;

    private boolean signed;

    private long mySequentialNumber;

    public Transaction(PublicKey sender, PublicKey receiver, double fundToTransfer,
                       List<UTXO> inputs) {
        PublicKey[] pks = new PublicKey[1];
        pks[0] = receiver;
        double[] funds = new double[1];
        funds[0] = fundToTransfer;
        setUp(sender, pks, funds, inputs);
    }

    public Transaction(PublicKey sender, PublicKey[] receivers, double[] fundToTransfer,
                       List<UTXO> inputs) {
        setUp(sender, receivers, fundToTransfer, inputs);
    }

    private void setUp(PublicKey sender, PublicKey[] receivers, double[] fundToTransfer,
                       List<UTXO> inputs) {
        mySequentialNumber = UtilityMethods.getUniqueNumber();
        this.sender = sender;
//        this.receivers = new PublicKey[1];
        this.receivers = receivers;
        this.fundToTransfer = fundToTransfer;
        this.inputs = inputs;
        timestamp = UtilityMethods.getTimeStamp();
        computeHashID();
    }

    public void signTheTransaction(PrivateKey privateKey) {
        if (signature == null && !signed) {
            signature = UtilityMethods.generateSignature(privateKey, getMessageData());
            signed = true;
        }
    }

    public boolean verifySignature() {
        String message = getMessageData();
        return UtilityMethods.verifySignature(sender, signature, message);
    }

    private String getMessageData() {
        StringBuilder sb = new StringBuilder();
        sb.append(UtilityMethods.getKeyString(sender))
                .append(Long.toHexString(timestamp))
                .append(mySequentialNumber);
        IntStream.range(0, receivers.length).mapToObj(i -> UtilityMethods.getKeyString(receivers[i])
                + Double.toHexString(fundToTransfer[i])).forEach(sb::append);
        IntStream.range(0, getNumberOfInputUTXOs()).mapToObj(this::getInputUTXO).map(UTXO::getHashID).forEach(sb::append);
        return sb.toString();
    }

    protected void computeHashID() {
        String message = getMessageData();
        hashID = UtilityMethods.messageDigestSHA256ToString(message);
    }

    public String getHashID() {
        return hashID;
    }

    public PublicKey getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getMySequentialNumber() {
        return mySequentialNumber;
    }

    public double getTotalFundToTransfer() {
        double f = 0;
        for (int i = 0; i < fundToTransfer.length; i++) {
            f += fundToTransfer[i];
        }
        return f;
    }

    protected void addOutputUTXO(UTXO utxo) {
        if (!signed) {
            outputs.add(utxo);
        }
    }

    public int getNumberOfOutputUTXOs() {
        return outputs.size();
    }

    public UTXO getOutputUTXO(int i) {
        return outputs.get(i);
    }

    public int getNumberOfInputUTXOs() {
        if (inputs == null) {
            return 0;
        }
        return inputs.size();
    }

    public UTXO getInputUTXO(int i) {
        return inputs.get(i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Transaction other = (Transaction) o;
        return getHashID().equals(other.getHashID());
    }

}
