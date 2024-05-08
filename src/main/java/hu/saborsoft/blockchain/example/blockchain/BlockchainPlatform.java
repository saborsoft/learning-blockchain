package hu.saborsoft.blockchain.example.blockchain;

import hu.saborsoft.blockchain.block.Block;
import hu.saborsoft.blockchain.blockchain.Blockchain;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;
import hu.saborsoft.blockchain.wallet.Miner;
import hu.saborsoft.blockchain.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

// this class simulates a blockchain system
public class BlockchainPlatform {

    // the blockchain
    private static Blockchain blockchain;

    // use this variable to track how much transaction fee has been paid
    // currently no transaction fee has been collected by miners
     private static double transactionFee = 0.0;

    public static void main(String[] args) throws Exception {
        // set the mining difficulty level. 20 is good for practice
        // depending on your computer, the mining might take tens
        // of seconds or a few minutes
        int difficultyLevel = 20;

        System.out.println("Blockchain platform starts ...");
        System.out.println("creating genesis miner, "
                + "genesis transaction and genesis block");

        //////////////////////////////////////////////////////////////////////////////////////
        // blockchain init with a genesis block
        //////////////////////////////////////////////////////////////////////////////////////

        // create a genesis miner to start a blockchain
        Miner genesisMiner = new Miner("genesis", "genesis");

        // create the genesis block. It's 'previous block hash id' is set '0' manually
        Block genesisBlock = new Block("0", difficultyLevel);

        // manually create two UTXOs as the input of the genesis transaction
        UTXO u1 = new UTXO("0", genesisMiner.getPublicKey(), genesisMiner.getPublicKey(),
                10001.0);
        UTXO u2 = new UTXO("0", genesisMiner.getPublicKey(), genesisMiner.getPublicKey(),
                10000.0);

        // prepare the input
        List<UTXO> inputs = new ArrayList<>();
        inputs.add(u1);
        inputs.add(u2);

        // prepare the genesis transaction
        Transaction gt = new Transaction(genesisMiner.getPublicKey(), genesisMiner.getPublicKey(),
                10000.0, inputs);
        boolean b = gt.prepareOutputUTXOs();

        // check if the output preparation is successful.
        // if not, exit the system
        if (!b) {
            System.out.println("genesis transaction failed");
            System.exit(1);
        }

        // the genesis miner signs the transaction
        gt.signTheTransaction(genesisMiner.getPrivateKey());
        // add the genesis transaction into the genesis block
        genesisBlock.addTransaction(gt);
        // the genesis miner mines the genesis block
        System.out.println("genesis miner is mining the genesis block");
        b = genesisMiner.mineBlock(genesisBlock);

        // check if mining is successful
        if (b) {
            System.out.println("genesis block is successfully mined. HashID:");
            System.out.println(genesisBlock.getHashID());
        } else {
            System.out.println("failed to mine genesis block. system exit");
            System.exit(1);
        }

        // construct the blockchain
        blockchain = new Blockchain(genesisBlock);
        System.out.println("blockchain genesis successful");

        // genesis miner copies the blockchain to his local ledger
        // it is not a real copy, though
        genesisMiner.setLocalLedger(blockchain);

        // manually check the balance of the genesis miner. please verify if it is correct
        System.out.println("genesis miner balance: "
                + genesisMiner.getCurrentBalance(genesisMiner.getLocalLedger()));

        //////////////////////////////////////////////////////////////////////////////////////
        // create test miner and wallets to add those the blockchain
        //////////////////////////////////////////////////////////////////////////////////////

        // create other miner / wallets
        Miner A = new Miner("Miner A", "Miner A");
        Wallet B = new Wallet("Wallet A", "Wallet A");
        Miner C = new Miner("Miner C", "Miner C");

        // every wallet stores a local ledger. please be aware that they
        // are in fact sharing the same blockchain as it is not distributed
        A.setLocalLedger(blockchain);
        B.setLocalLedger(blockchain);
        C.setLocalLedger(blockchain);

        //////////////////////////////////////////////////////////////////////////////////////
        // create the second block
        //////////////////////////////////////////////////////////////////////////////////////

        // create the second block
        Block b2 = new Block(blockchain.getLastBlock().getHashID(), difficultyLevel);
        System.out.println("\n\nBlock b2 created");

        /////////////////
        // T1 transaction
        /////////////////

        // lets the genesis miner transfer 100 to A and 200 to B
        Transaction t1 = genesisMiner.transferFund(A.getPublicKey(), 100);
        // make sure that the transaction is not null. if null, it means that
        // the transaction construction is not successful
        if (t1 != null) {
            // assume that someone is examining the transaction
            if (t1.verifySignature() && b2.addTransaction(t1)) {
                // display the balance to show that everything works. at this
                // moment, A, B, C, should have zero balance
                System.out.println("t1 added to block b2.");
//                System.out.println("t1 added to block b2. before b2 is mined "
//                        + "and added to the chain, the balances are:");
                //displayBalanceAfterBlock(genesisBlock, genesisMiner, A, B, C);
            } else {
                System.out.println("t1 failed to add to b2");
            }
        } else {
            System.out.println("t1 failed to create");
        }

        /////////////////
        // T1 transaction
        /////////////////

        Transaction t2 = genesisMiner.transferFund(B.getPublicKey(), 200);
        if (t2 != null) {
            if (t2.verifySignature() && b2.addTransaction(t2)) {
                System.out.println("t2 added to block b2.");
//                System.out.println("t2 added to block b2. before b2 is mined "
//                        + "and added to the chain, the balances are:");
                //displayBalanceAfterBlock(genesisBlock, genesisMiner, A, B, C);
            } else {
                System.out.println("t2 failed to add to b2");
            }
        } else {
            System.out.println("t2 failed to create");
        }

        ////////////////////////
        // Mine the second block
        ////////////////////////

        // mine the block 2.
        if (A.mineBlock(b2)) {
            System.out.println("A mined b2, hashID is:");
            System.out.println(b2.getHashID());
            blockchain.addBlock(b2);
            System.out.println("After block b2 is added to the chain, the balances are:");
            displayBalanceAfterBlock(b2, genesisMiner, A, B, C);
        }

        //////////////////////////////////////////////////////////////////////////////////////
        // create another block (with some transactions)
        //////////////////////////////////////////////////////////////////////////////////////

        // another block
        Block b3 = new Block(blockchain.getLastBlock().getHashID(), difficultyLevel);
        System.out.println("\n\nBlock b3 created");

        ////////////////////////
        // t3 transaction
        ////////////////////////

        // t3 should fail as A does not have enough fund
        Transaction t3 = A.transferFund(B.getPublicKey(), 200.0);
        if (t3 != null) {
            if (t3.verifySignature() && b3.addTransaction(t3)) {
                System.out.println("t3 added to block b3");
            } else {
                System.out.println("t3 failed to add to b3");
            }
        } else {
            System.out.println("t3 failed to create");
        }

        ////////////////////////
        // t4 transaction
        ////////////////////////

        // t4 should fail as A does not have enough fund
        Transaction t4 = A.transferFund(C.getPublicKey(), 300.0);
        if (t4 != null) {
            if (t4.verifySignature() && b3.addTransaction(t4)) {
                System.out.println("t4 added to block b3");
            } else {
                System.out.println("t4 failed to add to b3");
            }
        } else {
            System.out.println("t4 failed to create");
        }

        ////////////////////////
        // t5 transaction
        ////////////////////////

        Transaction t5 = A.transferFund(C.getPublicKey(), 20.0);
        if (t5 != null) {
            if (t5.verifySignature() && b3.addTransaction(t5)) {
                System.out.println("t5 added to block b3");
            } else {
                System.out.println("t5 failed to add to b3");
            }
        } else {
            System.out.println("t5 failed to create");
        }

        ////////////////////////
        // t6 transaction
        ////////////////////////

        Transaction t6 = B.transferFund(C.getPublicKey(), 80.0);
        if (t6 != null) {
            if (t6.verifySignature() && b3.addTransaction(t6)) {
                System.out.println("t6 added to block b3");
            } else {
                System.out.println("t6 failed to add to b3");
            }
        } else {
            System.out.println("t6 failed to create");
        }

        ////////////////////////
        // Mine the third block
        ////////////////////////

        // mine the block 3.
        if (C.mineBlock(b3)) {
            System.out.println("C mined b3, hashID is:");
            System.out.println(b3.getHashID());
            blockchain.addBlock(b3);
            System.out.println("After block b3 is added to the chain, the balances are:");
            displayBalanceAfterBlock(b3, genesisMiner, A, B, C);
        }

        System.out.println("==============BlockChain platform shuts down==============");

    }

    // A method to display the balance of the wallets and miners
    private static void displayBalanceAfterBlock(Block b, Wallet wallet,
                                                 Wallet A, Wallet B, Wallet C) {
        double total = wallet.getCurrentBalance(blockchain)
                + A.getCurrentBalance(blockchain) + B.getCurrentBalance(blockchain)
                + C.getCurrentBalance(blockchain);
        transactionFee += b.getTotalNumberOfTransactions() * Transaction.TRANSACTION_FEE;
        System.out.println("genesisMiner="
                + wallet.getCurrentBalance(blockchain) + ", A="
                + A.getCurrentBalance(blockchain) + ", B="
                + B.getCurrentBalance(blockchain) + ", C="
                + C.getCurrentBalance(blockchain) + ", total cash="
                + total + ", transaction fee=" + transactionFee);
        System.out.println("====>the length of the blockchain=" + blockchain.size());
    }
}
