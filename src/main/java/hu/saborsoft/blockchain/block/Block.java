package hu.saborsoft.blockchain.block;

import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {

    // to limit the number of transactions inside a block. note that it is not 2 any more
    public static final int TRANSACTION_UPPER_LIMIT = 100;

    // Modification 1
    // A new variable added to set a lower limit. this is to make sure that a block
    // can be started only after a certain number of valid transactions have been
    // collected. for demonstration purposes, it is set to be 1 so that there can
    // be enough blocks even when there are only a few transactions. this number should
    // always be greater than 0.
    public static final int TRANSACTION_LOWER_LIMIT = 1;

    private static final Logger LOG = LoggerFactory.getLogger(Block.class);

    @Serial
    private static final long serialVersionUID = 9204506387241993706L;
    private final int difficultyLevel;
    private final List<Transaction> transactions = new ArrayList<>();
    private final long timestamp;
    private final String previousBlockHashID;
    private int nonce = 0;
    private String hashID;
    // Modification 2
    // to record the miner of each block. doing so, we can enforce that any changes
    // to this block must be by this miner (creator)
    private PublicKey creator;
    // Modification 3
    // to mark if the block has been mined. once a block has been mined,
    // no change is allowed anymore. this is for tight secure coding.
    private boolean mined = false;
    // Modification 4
    // the miner must sign the block so that other miners can verify the signatures
    private byte[] signature = null;
    // Modification 5
    // the transaction to reward the miner
    private Transaction rewardTransaction = null;

    // Modification 6
    // the constructor is revised
    public Block(String previousBlockHashID, int difficultyLevel, PublicKey creator) {
        this.previousBlockHashID = previousBlockHashID;
        this.timestamp = UtilityMethods.getTimeStamp();
        this.difficultyLevel = difficultyLevel;
        this.creator = creator;
    }

    public String computeHashID() {
        StringBuilder sb = new StringBuilder();
        sb.append(previousBlockHashID).append(Long.toHexString(timestamp));
        // Modification 7
        // Transactions' hash values are converged into one root hash by means
        // of Merkle Tree
        sb.append(computeMerkleRoot());
        sb.append(nonce);
        byte[] b = UtilityMethods.messageDigestSHA256ToBytes(sb.toString());
        return UtilityMethods.toBinaryString(b);
    }

    // Modification 8
    // a transaction can be added only before the block is mined and signed, and only
    // the creator of this block can add transaction(s) before this block is mined or
    // signed. the number of transactions cannot exceed the allowed limit
    public boolean addTransaction(Transaction t, PublicKey key) {
        if (getTotalNumberOfTransactions() >= TRANSACTION_UPPER_LIMIT) {
            return false;
        }
        if (key.equals(creator) && !mined && !isSigned()) {
            transactions.add(t);
            return true;
        } else {
            return false;
        }
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

    // Modification 9
    // only the creator of this block can mine the block and a block
    // can be mined for only once
    public boolean mineTheBlock(PublicKey key) {
        if (!mined && key.equals(creator)) {
            hashID = computeHashID();
            while (!UtilityMethods.hashMeetsDifficultyLevel(hashID, difficultyLevel)) {
                nonce++;
                hashID = computeHashID();
            }
            LOG.debug("final nonce: {}", nonce);
            mined = true;
        }
        return mined;
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

    // ------------- Methods added in chapter 6 -----------------
    // Modification 10
    // a block has only one reward transaction. it can be added only by the block
    // creator, i.e. the miner, and it cannot be changed once it has been added.
    public boolean generateRewardTransaction(PublicKey pubKey,
                                             Transaction rewardTransaction) {
        if (this.rewardTransaction == null && pubKey.equals(creator)) {
            this.rewardTransaction = rewardTransaction;
            return true;
        } else {
            return false;
        }
    }

    // Modification 11
    public Transaction getRewardTransaction() {
        return rewardTransaction;
    }

    // Modification 12
    // the transaction fee does not include the reward transaction
    public double getTransactionFeeAmount() {
        return transactions.size() * Transaction.TRANSACTION_FEE;
    }

    // Modification 13
    // when a wallet/miner needs to add this block into its local blockchain,
    // it is necessary to verify the signature. the verification requires
    // a public key, which is usually the block creator's public key
    public boolean verifySignature(PublicKey pubKey) {
        return UtilityMethods.verifySignature(pubKey, signature, hashID);
    }

    // Modification 14
    // a block must be signed. this is how it works:
    // the miner of this block generates a signature based on the block
    // hash ID, and calls this method to set the signature. this method
    // would examine if the signature is valid before accepting it.
    // once the signature is set, no change is allowed.
    public boolean signTheBlock(PublicKey pubKey, byte[] signature) {
        if (!isSigned()) {
            if (pubKey.equals(creator)) {
                if (UtilityMethods.verifySignature(pubKey, signature, hashID)) {
                    this.signature = signature;
                    return true;
                }
            }
        }
        return false;
    }

    // Modification 15
    public PublicKey getCreator() {
        return creator;
    }

    // Modification 16
    public boolean isMined() {
        return mined;
    }

    //Modification 17
    public boolean isSigned() {
        return signature != null;
    }

    // Modification 18
    // Compute the Merkle root hash
    private String computeMerkleRoot() {
        String hashes[];
        // allowing underpay, i.e. the miner can mine a block
        // without accepting the reward, so the reward transaction
        // might be null
        if (rewardTransaction == null) {
            hashes = new String[transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                hashes[i] = transactions.get(i).getHashID();
            }
        } else {
            hashes = new String[transactions.size() + 1];
            for (int i = 0; i < transactions.size(); i++) {
                hashes[i] = transactions.get(i).getHashID();
            }
            hashes[hashes.length - 1] = rewardTransaction.getHashID();
        }
        return UtilityMethods.computeMerkleTreeRootHash(hashes);
    }

    // Modification 19
    // only the creator can delete a transaction before this block is mined and signed
    // this method is never used
    public boolean deleteTransaction(Transaction ts, PublicKey key) {
        if (!mined && !isSigned() && key.equals(creator)) {
            return transactions.remove(ts);
        } else {
            return false;
        }
    }

    // Modification 20
    // only the creator can delete a transaction before this block is mined and signed
    // this method is never used
    public boolean deleteTransaction(int index, PublicKey key) {
        if (!mined && !isSigned() && key.equals(creator)) {
            Transaction ts = transactions.remove(index);
            return (ts != null);
        } else {
            return false;
        }
    }

}
