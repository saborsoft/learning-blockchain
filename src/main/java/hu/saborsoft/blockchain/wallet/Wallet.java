package hu.saborsoft.blockchain.wallet;

import hu.saborsoft.blockchain.block.Block;
import hu.saborsoft.blockchain.blockchain.Blockchain;
import hu.saborsoft.blockchain.support.EncryptionAlgorithm;
import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;

import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Wallet {

    private static final String KEY_LOCATION = "keys";

    private KeyPair keyPair;

    private String walletName;

    private Blockchain localLedger;

    public Wallet(String walletName, String password) {
        this(walletName, password, EncryptionAlgorithm.AES);
    }

    public Wallet(String walletName, String password, EncryptionAlgorithm algo) {
        keyPair = UtilityMethods.generateKeyPair();
        this.walletName = walletName;
        try {
            populateExistingWallet(password, algo);
            System.out.println("A wallet exists with the same name "
                    + "and password. Loaded the existing wallet");
        } catch (Exception ee) {
            try {
                prepareWallet(password, algo);
                System.out.println("Created a new wallet based on "
                        + "the name and password");
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    public String getName() {
        return walletName;
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public synchronized Blockchain getLocalLedger() {
        return localLedger;
    }

    public double getCurrentBalance(Blockchain ledger) {
        return ledger.checkBalance(getPublicKey());
    }

    public Transaction transferFund(PublicKey[] receivers, double[] fundToTransfer) {
        List<UTXO> unspent = new ArrayList<>();
        double available = getLocalLedger().findUnspentUTXOs(getPublicKey(), unspent);
        double totalNeeded = Transaction.TRANSACTION_FEE;
        for (int i = 0; i < fundToTransfer.length; i++) {
            totalNeeded += fundToTransfer[i];
        }
        if (available < totalNeeded) {
            System.out.println(walletName + " balance=" + available
                    + ", not enough to make the transfer of " + totalNeeded);
            return null;
        }

        // create input for the transaction
        List<UTXO> inputs = new ArrayList<>();
        available = 0;
        for (int i = 0; i < unspent.size() && available < totalNeeded; i++) {
            UTXO uxo = unspent.get(i);
            available += uxo.getFundTransferred();
            inputs.add(uxo);
        }

        // create the transaction
        Transaction t = new Transaction(getPublicKey(), receivers, fundToTransfer, inputs);

        // prepare output UTXO
        boolean b = t.prepareOutputUTXOs();
        if (b) {
            t.signTheTransaction(getPrivateKey());
            return t;
        } else {
            return null;
        }
    }

    public Transaction transferFund(PublicKey receiver, double fundToTransfer) {
        PublicKey[] receivers = new PublicKey[1];
        double[] funds = new double[1];
        receivers[0] = receiver;
        funds[0] = fundToTransfer;
        return transferFund(receivers, funds);
    }

    // Modification 1
    // when setting the local blockchain (ledger), if the wallet does not have a local
    // ledger, the wallet simply accepts the incoming ledger. if the wallet already
    // has a local ledger, then it is necessary to compare the existing ledger with
    // the incoming one. the wallet only accepts the incoming ledger if it 1) is validated;
    // 2) is longer than the existing one; 3) both the incoming one and local one
    // have the same genesis block
    public synchronized boolean setLocalLedger(Blockchain ledger) {
        // make sure that the incoming blockchain is valid first
        boolean b = Blockchain.validateBlockchain(ledger);
        if (!b) {
            System.out.println("[" + getName()
                    + "] Warning: the incoming blockchain failed validation");
            return false;
        }
        // if there is no current blockchain locally, accepts the incoming one
        if (localLedger == null) {
            localLedger = ledger;
            return true;
        } else {
            // the incoming blockchain must be longer
            // also make sure that both the incoming blockchain and the local
            // one have the same genesis miner
            if (ledger.size() > localLedger.size() &&
                    ledger.getGenesisMiner().equals(localLedger.getGenesisMiner())) {
                localLedger = ledger;
                return true;
            } else if (ledger.size() <= localLedger.size()) {
                System.out.println("[" + getName() + "] Warning: the incoming "
                        + "blockchain is no longer than current local one"
                        + ", local size=" + localLedger.size()
                        + ", incoming size=" + ledger.size());
                return false;
            } else {
                System.out.println("[" + getName() + "] Warning: the incoming blockchain "
                        + "has a different genesis miner than current local one");
                return false;
            }
        }
    }

    // Modification 2: a new method added in a chapter 6
    // this method is never used, but we should be prepared for the scenario: when
    // there are a number of incoming blockchain, how to select one to update the
    // local copy. there are two cases.
    // case 1, if the wallet has a local copy;
    // case 2, if the wallet currently does not have a local copy
    public synchronized boolean updateLocalLedger(List<Blockchain> chains) {
        // if the array list is empty, no action is needed
        if (chains.size() == 0) {
            return false;
        }
        // when there is already a local blockchain, let's find the longest
        // validated blockchain in the incoming blockchains
        if (localLedger != null) {
            Blockchain max = localLedger;
            for (int i=0; i<chains.size(); i++) {
                Blockchain bc = chains.get(i);
                boolean b = bc.getGenesisMiner().equals(localLedger.getGenesisMiner());
                if (b && bc.size() > max.size() && Blockchain.validateBlockchain(bc)) {
                    max = bc;
                }
            }
            // it is possible that nothing changed, i.e. the max is the local one
            this.localLedger = max;
            return true;
        } else {
            // when there is no local one, then simply picks the longest one that
            // is validated. no need to check on the genesis miner
            Blockchain max = null;
            int currentLength = 0;
            for (int i=0; i< chains.size(); i++) {
                Blockchain bc = chains.get(i);
                boolean b = Blockchain.validateBlockchain(bc);
                if (b && bc.size() > currentLength) {
                    max = bc;
                    currentLength = max.size();
                }
            }
            if (max != null) {
                localLedger = max;
                return true;
            } else {
                return false;
            }
        }
    }

    // Modification 3: a new method added in chapter 6
    // when a new block comes, before accepting it and adding it to the local
    // blockchain, we must verify the block
    public synchronized boolean updateLocalLedger(Block block) {
        if (verifyGuestBlock(block)) {
            return localLedger.addBlock(block);
        }
        return false;
    }

    // Modification 4
    // this is a new method added in chapter 6
    // verify an incoming block against a blockchain. some codes in the method
    // show repetition of codes in method Blockchain.validateBlockchain().
    // please be aware of the difference between verifying a block and validating
    // a blockchain. Validating a blockchain in this implementation does not
    // validate each transaction. however, verifying a block must make sure that
    // each transaction in the block is validated against the local blockchain.
    public boolean verifyGuestBlock(Block block, Blockchain ledger) {
        // verify the signature
        if (!block.verifySignature(block.getCreator())) {
            System.out.println("\tWarning:   block(" + block.getHashID() + ")   signature tampered");
            return false;
        }
        // verify the proof-of-work including recomputing block hash
        if (!UtilityMethods.hashMeetsDifficultyLevel(block.getHashID(), block.getDifficultyLevel())
            || !block.computeHashID().equals(block.getHashID())) {
            System.out.println("\tWarning:   block(" + block.getHashID() + ")   mining is not successful");
            return false;
        }
        // make sure that this block is built upon the last block
        if (!ledger.getLastBlock().getHashID().equals(block.getPreviousBlockHashID())) {
            System.out.println("\tWarning:   block(" + block.getHashID() + ")   is not linked to the last block");
            return false;
        }
        // examine if all the transactions are valid
        int size = block.getTotalNumberOfTransactions();
        for (int i=0; i<size; i++) {
            Transaction t = block.getTransaction(i);
            if (!validateTransaction(t)) {
                System.out.println("\tWarning:   block(" + block.getHashID() +
                        ")   transaction " + i + " is invalid either because of signature "
                + " being tampered or already existing in the blockchain.");
                return false;
            }
        }
        // here, we do not examine if the transaction balance is in good standing
        // however, we do scrutinize the rewarding transaction
        Transaction tr = block.getRewardTransaction();
        if (tr.getTotalFundToTransfer() > Blockchain.MINING_REWARD
        + block.getTransactionFeeAmount()) {
            System.out.println("\tWarning:   block(" + block.getHashID() + ")   over rewarded");
            return false;
        }
        return true;
    }

    // Modification 5
    public boolean verifyGuestBlock(Block block) {
        return verifyGuestBlock(block, localLedger);
    }

    // modification 6 - a new method added in chapter 6
    // a transaction must be validated before it is collected into a block
    public boolean validateTransaction(Transaction ts) {
        // in case of a null
        if (ts == null) {
            return false;
        }
        if (!ts.verifySignature()) {
            System.out.println("\tWarning:   transaction ID=" + ts.getHashID() +
                    " from  " + UtilityMethods.getKeyString(ts.getSender())
                    + " is invalid. it has been tampered.");
            return false;
        }

        // make sure that this transaction does not exist in the existing ledger.
        // this type of implementation is a time consuming process
        boolean exists;
        if (localLedger == null) {
            exists = false;
        } else {
            exists = localLedger.isTransactionExists(ts);
        }

        return !exists;
    }

    private void prepareWallet(String password, EncryptionAlgorithm algo) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bo);
        out.writeObject(this.keyPair);
        byte[] keyBytes = UtilityMethods.encrypt(bo.toByteArray(), password, algo);
        File f = new File(KEY_LOCATION);
        if (!f.exists()) {
            f.mkdir();
        }
        FileOutputStream fout = new FileOutputStream(getFilePath());
        fout.write(keyBytes);
        fout.close();
        bo.close();
    }

    private void populateExistingWallet(String password, EncryptionAlgorithm algo) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(getFilePath());
        byte[] bb = new byte[4096];
        int size = fin.read(bb);
        fin.close();
        byte[] data = new byte[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = bb[i];
        }
        byte[] keyBytes = UtilityMethods.decrypt(data, password, algo);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(keyBytes));
        keyPair = (KeyPair) in.readObject();
    }

    private String getFilePath() {
        return KEY_LOCATION + "/" + getName().replace(" ", "_") + "_keys";
    }

}
