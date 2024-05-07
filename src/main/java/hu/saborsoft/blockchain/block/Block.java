package hu.saborsoft.blockchain.block;

import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block implements Serializable {

    public static final int TRANSACTION_UPPER_LIMIT = 2;

    private static final Logger LOG = LoggerFactory.getLogger(Block.class);

    @Serial
    private static final long serialVersionUID = 9204506387241993706L;
    private final int difficultyLevel;
    private final List<Transaction> transactions = new ArrayList<>();
    private final long timestamp;
    private final String previousBlockHashID;
    private int nonce = 0;
    private String hashID;

    public Block(String previousBlockHashID, int difficultyLevel) {
        this.previousBlockHashID = previousBlockHashID;
        this.timestamp = UtilityMethods.getTimeStamp();
        this.difficultyLevel = difficultyLevel;
    }

    public String computeHashID() {
        StringBuilder sb = new StringBuilder();
        sb.append(previousBlockHashID).append(Long.toHexString(timestamp));
        for (Transaction t : transactions) {
            sb.append(t.getHashID());
        }
        sb.append(Integer.toHexString(difficultyLevel)).append(nonce);
        byte[] b = UtilityMethods.messageDigestSHA256ToBytes(sb.toString());
        return UtilityMethods.toBinaryString(b);
    }

    public boolean mineTheBlock() {
        hashID = computeHashID();
        while (!UtilityMethods.hashMeetsDifficultyLevel(hashID, difficultyLevel)) {
            nonce++;
            hashID = computeHashID();
        }
        LOG.debug("final nonce: {}", nonce);
        return true;
    }

    public boolean addTransaction(Transaction transaction) {
        if (getTotalNumberOfTransactions() >= TRANSACTION_UPPER_LIMIT) {
            return false;
        }
        transactions.add(transaction);
        return true;
    }

    public String getHashID() {
        return hashID;
    }

    public int getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPreviousBlockHashID() {
        return previousBlockHashID;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public int getTotalNumberOfTransactions() {
        return this.transactions.size();
    }

    public Transaction getTransaction(int index) {
        return transactions.get(index);
    }
}
