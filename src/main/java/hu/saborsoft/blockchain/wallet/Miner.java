package hu.saborsoft.blockchain.wallet;

import hu.saborsoft.blockchain.block.Block;
import hu.saborsoft.blockchain.blockchain.Blockchain;
import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;
import hu.saborsoft.blockchain.transaction.UTXOsMiningReward;

public class Miner extends Wallet {

    public Miner(String minerName, String password) {
        super(minerName, password);
    }

    // modification 1
    // after a miner mines a block, the miner signs the block
    public boolean mineBlock(Block block) {
        if (block.mineTheBlock(getPublicKey())) {
            // the miner needs to sign the block
            byte[] signature = UtilityMethods.generateSignature(getPrivateKey(), block.getHashID());
            return block.signTheBlock(getPublicKey(), signature);
        } else {
            return false;
        }
    }

    // modification 2
    // a transaction must be validated before being added into a block
    public boolean addTransaction(Transaction ts, Block block) {
        if (validateTransaction(ts)) {
            return block.addTransaction(ts, getPublicKey());
        } else {
            return false;
        }
    }

    // modification 3
    // only the block creator can delete a transaction before the block
    // is signed and mined
    public boolean deleteTransaction(Transaction ts, Block block) {
        return block.deleteTransaction(ts, getPublicKey());
    }

    // modification 4
    public boolean generateRewardTransaction(Block block) {
        double amount = Blockchain.MINING_REWARD + block.getTransactionFeeAmount();
        Transaction t = new Transaction(getPublicKey(), getPublicKey(), amount, null);
        UTXO ut = new UTXOsMiningReward(t.getHashID(), t.getSender(), getPublicKey(), amount);
        t.addOutputUTXO(ut);
        t.signTheTransaction(getPrivateKey());
        return block.generateRewardTransaction(getPublicKey(), t);
    }

    // modification 5
    // a block is supposed to be created by a miner
    public Block createNewBlock(Blockchain ledger, int difficultLevel) {
        Block b = new Block(ledger.getLastBlock().getHashID(), difficultLevel, getPublicKey());
        return b;
    }
    
}
