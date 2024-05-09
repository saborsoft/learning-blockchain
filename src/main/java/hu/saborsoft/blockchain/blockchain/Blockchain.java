package hu.saborsoft.blockchain.blockchain;

import hu.saborsoft.blockchain.block.Block;
import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain implements Serializable {

    public static final double MINING_REWARD = 100.0;

    private static final long serialVersionUID = -7496648992837481623L;

    private LedgerList<Block> blockchain;

    public Blockchain(Block genesisBlock) {
        blockchain = new LedgerList<>();
        blockchain.add(genesisBlock);
    }

    public Block getGenesisBlock() {
        return blockchain.getFirst();
    }

    public Block getLastBlock() {
        return blockchain.getLast();
    }

    public int size() {
        return blockchain.size();
    }

    public Block getBlock(int index) {
        return blockchain.findByIndex(index);
    }

    // Modification 1
    // rewarding UTXOs are counted in this method now
    public double findRelatedUTXOs(PublicKey key, List<UTXO> all,
                                   List<UTXO> spent, List<UTXO> unspent,
                                   List<Transaction> sentTransactions, List<UTXO> rewards) {
        double gain = 0.0;
        double spending = 0.0;
        Map<String, UTXO> map = new HashMap<>();
        int limit = size();
        for (int a = 0; a < limit; a++) {
            Block block = blockchain.findByIndex(a);
            int size = block.getTotalNumberOfTransactions();
            for (int i = 0; i < size; i++) {
                Transaction t = block.getTransaction(i);
                int n;
                if (a != 0 && t.getSender().equals(key)) {
                    n = t.getNumberOfInputUTXOs();
                    for (int x = 0; x < n; x++) {
                        UTXO ut = t.getInputUTXO(x);
                        spent.add(ut);
                        map.put(ut.getHashID(), ut);
                        spending += ut.getFundTransferred();
                    }
                    sentTransactions.add(t);
                }
                n = t.getNumberOfOutputUTXOs();
                for (int x = 0; x < n; x++) {
                    UTXO ux = t.getOutputUTXO(x);
                    if (ux.getReceiver().equals(key)) {
                        all.add(ux);
                        gain += ux.getFundTransferred();
                    }
                }
            }
            // add reward transaction. the reward should never be null in our program
            // in bitcoin, a miner might underpay himself, i.e. the reward transaction
            // can be null
            if (block.getCreator().equals(key)) {
                Transaction rt = block.getRewardTransaction();
                if (rt != null && rt.getNumberOfOutputUTXOs() > 0) {
                    UTXO ux = rt.getOutputUTXO(0);
                    // double check again, so a miner can only reward himself
                    // if he rewards others, this reward is not counted
                    if (ux.getReceiver().equals(key)) {
                        rewards.add(ux);
                        all.add(ux);
                        gain += ux.getFundTransferred();
                    }
                }
            }
        }

        for (int i = 0; i < all.size(); i++) {
            UTXO ut = all.get(i);
            if (!map.containsKey((ut.getHashID()))) {
                unspent.add(ut);
            }
        }
        return gain - spending;
    }

    public double checkBalance(PublicKey key) {
        List<UTXO> all = new ArrayList<>();
        List<UTXO> spent = new ArrayList<>();
        List<UTXO> unspent = new ArrayList<>();
        return findRelatedUTXOs(key, all, spent, unspent);
    }

    // Modification 2
    public double findRelatedUTXOs(PublicKey key, List<UTXO> all,
                                   List<UTXO> spent, List<UTXO> unspent,
                                   List<Transaction> sendingTransactions) {
        List<UTXO> rewards = new ArrayList<>();
        return findRelatedUTXOs(key, all, spent, unspent, sendingTransactions, rewards);
    }

    public double findRelatedUTXOs(PublicKey key, List<UTXO> all,
                                   List<UTXO> spent, List<UTXO> unspent) {
        List<Transaction> sendingTransactions = new ArrayList<>();
        return findRelatedUTXOs(key, all, spent, unspent, sendingTransactions);
    }

    public List<UTXO> findUnspentUTXOs(PublicKey key) {
        List<UTXO> all = new ArrayList<>();
        List<UTXO> spent = new ArrayList<>();
        List<UTXO> unspent = new ArrayList<>();
        findRelatedUTXOs(key, all, spent, unspent);
        return unspent;
    }

    public double findUnspentUTXOs(PublicKey key, List<UTXO> unspent) {
        List<UTXO> all = new ArrayList<>();
        List<UTXO> spent = new ArrayList<>();
        return findRelatedUTXOs(key, all, spent, unspent);
    }

    // Modification 3
    // this method examines if a transaction already exists inside a blockchain
    // this is necessary when collecting a transaction and when adding a block
    // into the blockchain
    public boolean isTransactionExists(Transaction t) {
        int size = blockchain.size();
        for (int i = size - 1; i > 0; i--) {
            Block b = blockchain.findByIndex(i);
            int bs = b.getTotalNumberOfTransactions();
            for (int j = 0; j < bs; j++) {
                Transaction t2 = b.getTransaction(j);
                if (t.equals(t2)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Modification 4
    // the genesis miner is the miner who starts the blockchain by mining the genesis block
    public PublicKey getGenesisMiner() {
        return getGenesisBlock().getCreator();
    }

    // Modification 5
    // this static method validates a given Blockchain. this method could be an
    // instance method. the reason why it is written as a static method is to let
    // wallets and miners use it. when a miner calls this method, it sounds more
    // like the miner is validating a blockchain. if written as an instance field,
    // it will look more like that the blockchain is validating itself.
    public static boolean validateBlockchain(Blockchain ledger) {
        int size = ledger.size();
        for (int i = size - 1; i > 0; i--) {
            Block currentBlock = ledger.getBlock(i);
            boolean b = currentBlock.verifySignature(currentBlock.getCreator());
            if (!b) {
                System.out.println("validateBlockChain(): block "
                        + (i + 1) + "  signature is invalid.");
                return false;
            }
            b = UtilityMethods.hashMeetsDifficultyLevel(currentBlock.getHashID(),
                    currentBlock.getDifficultyLevel()) &&
                    currentBlock.computeHashID().equals(currentBlock.getHashID());
            if (!b) {
                System.out.println("validateBlockChain(): block "
                        + (i + 1) + "  its hashing is bad.");
                return false;
            }
            Block previousBlock = ledger.getBlock(i - 1);
            b = currentBlock.getPreviousBlockHashID().equals(previousBlock.getHashID());
            if (!b) {
                System.out.println("validateBlockChain(): block "
                        + (i + 1) + "  invalid previous block hashID.");
                return false;
            }
        }
        Block genesisBlock = ledger.getGenesisBlock();
        // Confirm the genesis block is signed
        boolean b2 = genesisBlock.verifySignature(genesisBlock.getCreator());
        if (!b2) {
            System.out.println("validateBlockChain(): genesis block "
                    + "  is tampered, signature bad");
            return false;
        }

        b2 = UtilityMethods.hashMeetsDifficultyLevel(
                genesisBlock.getHashID(), genesisBlock.getDifficultyLevel())
                && genesisBlock.computeHashID().equals(genesisBlock.getHashID());

        if (!b2) {
            System.out.println("validateBlockChain(): genesis block's "
                    + "  hash value is bad");
            return false;
        }
        return true;
    }

    // Modification 6
    // it is a good idea to synchronize this method. in addition, this method makes sure
    // that the block is a valid successor of the last block in the chain
    public synchronized boolean addBlock(Block block) {
        if (size() == 0) {
            blockchain.add(block);
            return true;
        } else if (block.getPreviousBlockHashID().equals(getLastBlock().getHashID())) {
            blockchain.add(block);
            return true;
        } else {
            return false;
        }
    }

    // Modification 7
    // a private constructor used by this class only for copying purpose.
    private Blockchain(LedgerList<Block> chain) {
        blockchain = new LedgerList<>();
        int size = chain.size();
        for (int i=0; i<size; i++) {
            blockchain.add(chain.findByIndex(i));
        }
    }

    // Modification 8
    // This is not a deep copy, though it creates a different object of
    // blockchain. the blocks and their order are preserved
    public synchronized Blockchain copyNotDeepCopy() {
        return new Blockchain(blockchain);
    }

}
