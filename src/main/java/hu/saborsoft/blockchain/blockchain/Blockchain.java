package hu.saborsoft.blockchain.blockchain;

import hu.saborsoft.blockchain.block.Block;
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

    public synchronized void addBlock(Block block) {
        if (block.getPreviousBlockHashID().equals(getLastBlock().getHashID())) {
            blockchain.add(block);
        }
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

    public double findRelatedUTXOs(PublicKey key, List<UTXO> all,
                                   List<UTXO> spent, List<UTXO> unspent,
                                   List<Transaction> sentTransactions) {
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

}
