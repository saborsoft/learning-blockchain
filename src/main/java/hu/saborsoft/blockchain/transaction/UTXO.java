package hu.saborsoft.blockchain.transaction;

import hu.saborsoft.blockchain.support.UtilityMethods;

import java.io.Serializable;
import java.security.PublicKey;

public class UTXO implements Serializable {

    private static final long serialVersionUID = -7625313903583747317L;
    private String hashID;
    private String parentTransactionID;
    private PublicKey receiver;
    private PublicKey sender;
    private long timestamp;
    private double fundTransferred;
    private long sequentialNumber = 0;

    public UTXO(String parentTransactionID, PublicKey sender, PublicKey receiver,
                double fundToTransfer) {
        this.sequentialNumber = UtilityMethods.getUniqueNumber();
        this.parentTransactionID = parentTransactionID;
        this.receiver = receiver;
        this.sender = sender;
        this.fundTransferred = fundToTransfer;
        this.timestamp = UtilityMethods.getTimeStamp();
        this.hashID = computeHashID();
    }

    protected String computeHashID() {
        String message = parentTransactionID
                + UtilityMethods.getKeyString(sender)
                + UtilityMethods.getKeyString(receiver)
                + Double.toHexString(fundTransferred)
                + Long.toHexString(timestamp)
                + Long.toHexString(sequentialNumber);
        return UtilityMethods.messageDigestSHA256ToString(message);
    }

    public String getHashID() {
        return hashID;
    }

    public String getParentTransactionID() {
        return parentTransactionID;
    }

    public PublicKey getReceiver() {
        return receiver;
    }

    public PublicKey getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getFundTransferred() {
        return fundTransferred;
    }

    public long getSequentialNumber() {
        return sequentialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UTXO other = (UTXO)o;
        return getHashID().equals(other.getHashID());
    }

    public boolean isMiningReward() {
        return false;
    }

}
