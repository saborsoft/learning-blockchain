package hu.saborsoft.blockchain.example;

import hu.saborsoft.blockchain.block.Block;
import hu.saborsoft.blockchain.blockchain.Blockchain;
import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;
import hu.saborsoft.blockchain.wallet.Miner;
import hu.saborsoft.blockchain.wallet.Wallet;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Simulator {

    public static Blockchain ledger;
    private static final int L = 3;
    private static Miner genesisMiner;
    private static Miner[] miners = new Miner[L];
    private static Wallet[] wallets = new Wallet[L];
    private static Scanner in;
    private static int numberOfScenarios = 0;
    private static double init = 3000;
    private static int difficultLevel = 20;

    public static void main(String[] args) throws Exception
    {
        in = new Scanner(System.in);
        System.out.println("Blockchain genesis ...");
        System.out.println("genesis miner automatically got 3000 incentive.");
        System.out.println("The incentive for one block mining is " + Blockchain.MINING_REWARD
                +", the transaction fee is " + Transaction.TRANSACTION_FEE +" per transaction.");
        System.out.println("What is your intended mining difficulty level?: (15-30)");
        try{
            String s = in.nextLine();
            difficultLevel = Integer.parseInt(s);
        }catch(Exception e){
            difficultLevel = 20;
        }
        System.out.println("Blockchain platform starts ..., " + "mining difficulty level = "+difficultLevel);
        System.out.println("creating genesis miner, genesis transaction and genesis block");
        //create a genesis miner to start a blockchain
        genesisMiner = new Miner("genesis", "genesis");
        //create genesis block
        Block genesisBlock = new Block("0", difficultLevel, genesisMiner.getPublicKey());
        UTXO u1 = new UTXO("0", genesisMiner.getPublicKey(), genesisMiner.getPublicKey(), 2001.0);
        UTXO u2 = new UTXO("0", genesisMiner.getPublicKey(), genesisMiner.getPublicKey(), 1000.0);
        ArrayList<UTXO> input = new ArrayList<UTXO>();
        input.add(u1);
        input.add(u2);
        Transaction gt = new Transaction(genesisMiner.getPublicKey(), genesisMiner.getPublicKey(), 3000.0, input);
        boolean b = gt.prepareOutputUTXOs();
        if(!b){
            System.out.println("genesis transaction failed.");
            System.exit(1);
        }
        gt.signTheTransaction(genesisMiner.getPrivateKey());
        b = genesisBlock.addTransaction(gt, genesisMiner.getPublicKey());
        if(!b){
            System.out.println("failed to add the genesis transaction to the genesis block. System quit");
            System.exit(1);
        }
        //the genesis miner mines the genesis block
        System.out.println("genesis miner is mining the genesis block");
        b = genesisMiner.mineBlock(genesisBlock);
        if(b){
            System.out.println("genesis block is successfully mined. HashID:");
            System.out.println(genesisBlock.getHashID());
        }else{
            System.out.println("failed to mine genesis block. System exit");
            System.exit(1);
        }
        ledger = new Blockchain(genesisBlock);
        System.out.println("block chain genesis successful");
        //genesisMiner copies the blockchain to his local ledger
        genesisMiner.setLocalLedger(ledger);
        System.out.println("genesis miner balance: " + genesisMiner.getCurrentBalance(genesisMiner.getLocalLedger()));
        init = genesisMiner.getCurrentBalance(genesisMiner.getLocalLedger());

        System.out.println("System automatically creates 3 miners and 3 wallets");
        String[] mnames = {"John", "John", "David", "David", "Jenny", "Jenny"};
        String[] wnames = {"George", "George", "Mulan", "Mulan", "Yaojin", "Yaojin"};
        for(int i=0; i<miners.length; i++){
            System.out.println((i+1)+" miner is "+mnames[i*2]+"/"+mnames[i*2+1]);
            miners[i] = new Miner(mnames[i*2], mnames[i*2+1]);
            miners[i].setLocalLedger(ledger);
        }

        for(int i=0; i<wallets.length; i++){
            System.out.println((i+1)+" wallet is "+wnames[i*2]+"/"+wnames[i*2+1]);
            wallets[i] = new Wallet(wnames[i*2], wnames[i*2+1]);
            wallets[i].setLocalLedger(ledger);
        }
        System.out.println("miners and wallets are generated");
        System.out.println();
        displayBalance();

        System.out.println("genesis miner is sending fund to all other wallets and miners so that they can have transactions");
        PublicKey[] Rs = new PublicKey[2*L];
        double funds[] = new double[Rs.length];
        System.out.println("genesis miner is preparing the transactions, each wallet gets 400, each miner gets 200");
        for(int i=0; i<wallets.length; i++){
            Rs[i] = wallets[i].getPublicKey();
            funds[i] = 400;
        }
        for(int i=0,j=3; i<miners.length; i++,j++){
            Rs[j] = miners[i].getPublicKey();
            funds[j] = 200;
        }
        Transaction T = genesisMiner.transferFund(Rs, funds);
        Block bb = genesisMiner.createNewBlock(ledger, difficultLevel);
        if(T != null && genesisMiner.validateTransaction(T)){
            genesisMiner.addTransaction(T, bb);
        }
        genesisMiner.generateRewardTransaction(bb);
        b = genesisMiner.mineBlock(bb);
        if(b){
            System.out.println("block is mined & signed by the genesisMiner, hashID="+bb.getHashID());
            ledger.addBlock(bb);
        }
        System.out.println("after genesis miner transferred funds to others, please check the balance below");
        numberOfScenarios++;
        displayBalance();


        int X = 0;
        String vv;
        do{
            X++;
            scenario(X);
            System.out.println("Do you like to try another block? (Yes/No)");
            in.nextLine();
            vv = in.nextLine().toUpperCase();
        }while(vv.toUpperCase().startsWith("Y"));

        in.close();
        System.out.println();
        System.out.println("====Your blockchain looks like====");
        System.out.println();
        UtilityMethods.displayBlockchain(ledger, System.out, 0);
        System.out.println();
        System.out.println("=========== END ===========");
    }




    private static Wallet selectRecipient()
    {
        return selectSender();
    }

    private static Wallet selectSender()
    {
        listWallets();
        int v = -1;
        try{
            v = in.nextInt();
        }catch(Exception e){
            v = -1;
        }
        while(v < 0 || v > L+L){
            System.out.println("please enter a number between 0 and "+(2*L));
            try{
                v = in.nextInt();
            }catch(Exception e){
                v = -1;
            }
        }
        if(v == 0){
            return genesisMiner;
        }else if(v <= L){
            return wallets[v-1];
        }else{
            return miners[v-4];
        }
    }

    private static void listWallets()
    {
        int i = 0;
        System.out.println(genesisMiner.getName()+"  select " + i++);
        for(int x=0; x<wallets.length; x++, i++){
            System.out.println(wallets[x].getName() + "  select " + i);
        }
        for(int x=0; x<miners.length; x++, i++){
            System.out.println(miners[x].getName() + "  select " + i);
        }
    }

    private static void listMinersOnly()
    {
        int i = 0;
        System.out.println(genesisMiner.getName()+"  select " + i++);
        for(int x=0; x<miners.length; x++, i++){
            System.out.println(miners[x].getName() + "  select " + i);
        }
    }

    private static Miner selectMiner()
    {
        listMinersOnly();
        int v = -1;
        try{
            v = in.nextInt();
        }catch(Exception e){
            v = -1;
        }
        while(v < 0 || v > L){
            System.out.println("please enter a number between 0 and "+(L));
            try{
                v = in.nextInt();
            }catch(Exception e){
                v = -1;
            }
        }
        if(v == 0){
            return genesisMiner;
        }else{
            return miners[v-1];
        }
    }


    private static void scenario(int i)
    {
        System.out.println();
        System.out.println("------------- Start Scenario "+i+" ----------------");
        Random rand = new Random();
        int tm = rand.nextInt(4) + 1;
        if(tm>1){
            System.out.println("In this scenario "+ i+", " + tm + " transactions will be generated.");
        }else{
            System.out.println("In this scenario "+ i+", " + tm + " transaction will be generated.");
        }
        Transaction[] Ts = new Transaction[tm];
        int x = 0;
        while(x < tm){
            System.out.println("Each transaction can have only one sender. Please select a sender below for transaction "+ (x+1));
            Wallet sender = selectSender();
            System.out.println("sender is " + sender.getName());
            int um = rand.nextInt(2) + 1;
            Wallet[] receivers = new Wallet[um];
            PublicKey[] rs = new PublicKey[um];
            double[] funds = new double[um];
            System.out.println("There will be " + um + " UTXO in this transaction.");
            int z = 0;
            while(z < um){
                System.out.println("Please select a wallet as the UTXO "+ (z+1) +" recipient");
                receivers[z] = selectRecipient();
                rs[z] = receivers[z].getPublicKey();
                System.out.println("how much you want to pay to " + receivers[z].getName()+"?");
                funds[z] = in.nextDouble();
                z++;
            }

            Ts[x] = sender.transferFund(rs, funds);
            if(Ts[x] != null){
                x++;
            }
        }
        System.out.println("All transactions generated. Now, who you would like to pick to mine the block?");
        Miner miner = selectMiner();
        Block block = miner.createNewBlock(ledger, difficultLevel);
        System.out.println("block created by " + miner.getName());
        for(int z=0; z<Ts.length; z++){
            if(miner.validateTransaction(Ts[z])){
                miner.addTransaction(Ts[z], block);
            }else{
                System.out.println("Transaction " + (z+1) + " failed to be validated");
            }
        }
        miner.generateRewardTransaction(block);
        System.out.println("transactions validated and added, reward transaction added, too. Now mining the block");
        boolean b = miner.mineBlock(block);
        if(b){
            System.out.println("block mined, signed & published, hashID="+block.getHashID());
        }else{
            System.out.println("block failed to be mined. Needing to have another miner to create a block. Weird");
            System.out.println("This version does not handle this case yet, ... :)");
            return;
        }

        int count = 1;
        b = genesisMiner.verifyGuestBlock(block, ledger);
        if(b){
            System.out.println("genesis miner accepted this block");
            count++;
        }else{
            System.out.println("genesis miner REJECTED this block");
        }
        for(int z=0; z<miners.length; z++){
            if(!miners[z].equals(miner)){
                b = miners[z].verifyGuestBlock(block, ledger);
                if(b){
                    System.out.println(miners[z].getName()+" accepted this block");
                    count++;
                }else{
                    System.out.println(miners[z].getName()+" REJECTED this block");
                }
            }
        }
        System.out.println("Out of " + (L+1) + " miners, "+ count+" miners accepted the block");
        if(count > (L+1)/2){
            System.out.println("based on the majority vote, the block is finalized and accepted");
            System.out.println("Every user is updating its local blockchain... ");
            System.out.println("However, in this simulation, every user is in fact "
                    + "sharing the blockchain." );
            ledger.addBlock(block);
            System.out.println("Do you like to add the block twice to test the system? (1=Yes, 0=No) -- for testing purpose :)");
            int vv = 0;
            try{
                vv = in.nextInt();
            }catch(Exception ee){
                vv = 0;
            }
            if(vv == 1){
                ledger.addBlock(block);
            }

        }else{
            System.out.println("block is rejected.");
            System.out.println("------------- END OF Scenario " + i + " -----------------");
            return;
        }
        Simulator.numberOfScenarios++;
        displayBalance();

        System.out.println("------------- END OF Scenario " + i + " -----------------");
    }


    private static void displayBalance()
    {
        double sum = 0;
        double b = genesisMiner.getCurrentBalance(ledger);
        sum += b;
        for(int i=0; i<miners.length; i++){
            b = miners[i].getCurrentBalance(ledger);
            sum += b;
        }
        for(int i=0; i<wallets.length; i++){
            b = wallets[i].getCurrentBalance(ledger);
            sum += b;
        }
        System.out.println("Total balance="+(init+Blockchain.MINING_REWARD*Simulator.numberOfScenarios)
                +" the sum of all wallets' cash="+sum);

        System.out.println();
        displayMinerBalance(genesisMiner);
        for(int i=0; i<miners.length; i++){
            System.out.println();
            displayMinerBalance(miners[i]);
        }
        for(int i=0; i<wallets.length; i++){
            System.out.println();
            displayMinerBalance(wallets[i]);
        }

        System.out.println();
        System.out.println();
    }


    private static void displayMinerBalance(Wallet miner)
    {
        ArrayList<UTXO> all = new ArrayList<UTXO>();
        ArrayList<UTXO> spent = new ArrayList<UTXO>();
        ArrayList<UTXO> unspent = new ArrayList<UTXO>();
        ArrayList<Transaction> ts = new ArrayList<Transaction>();
        double b = miner.getLocalLedger().findRelatedUTXOs(miner.getPublicKey(), all, spent, unspent, ts);
        System.out.println("{");
        System.out.println("\t"+miner.getName()+": balance="+b);
        System.out.println("\tAll UTXOs:");
        double income = 0;
        for(int i=0; i<all.size(); i++){
            UTXO ux = all.get(i);
            if(ux.isMiningReward()){
                System.out.println("\t\t"+ux.getFundTransferred()+"|mining reward");
            }else{
                System.out.println("\t\t"+ux.getFundTransferred()+"|"+ux.getHashID()
                        +"|from="+UtilityMethods.getKeyString(ux.getSender())+"|to="+UtilityMethods.getKeyString(ux.getReceiver()));
            }
            income += ux.getFundTransferred();
        }
        System.out.println("\t---- total income = " + income+" ----------");
        System.out.println("\tSpent UTXOs:");
        income = 0;
        for(int i=0; i<spent.size(); i++){
            UTXO ux = spent.get(i);
            if(ux.isMiningReward()){
                System.out.println("\t\t"+ux.getFundTransferred()+"|mining reward");
            }else{
                System.out.println("\t\t"+ux.getFundTransferred()+"|"+ux.getHashID()
                        +"|from="+UtilityMethods.getKeyString(ux.getSender())+"|to="+UtilityMethods.getKeyString(ux.getReceiver()));
            }
            income += ux.getFundTransferred();
        }
        System.out.println("\t---- total spending = " + income+" ----------");
        double tsFee = ts.size() * Transaction.TRANSACTION_FEE;
        if(tsFee > 0){
            System.out.println("\t\tTransaction Fee "+tsFee+" is automatically deducted. Please not include it in the calculation");
        }
        System.out.println("\tUnspent UTXOs:");
        income = 0;
        for(int i=0; i<unspent.size(); i++){
            UTXO ux = unspent.get(i);
            if(ux.isMiningReward()){
                System.out.println("\t\t"+ux.getFundTransferred()+"|mining reward");
            }else{
                System.out.println("\t\t"+ux.getFundTransferred()+"|"+ux.getHashID()
                        +"|from="+UtilityMethods.getKeyString(ux.getSender())+"|to="+UtilityMethods.getKeyString(ux.getReceiver()));
            }
            income += ux.getFundTransferred();
        }
        System.out.println("\t---- total unspent = " + income+" ----------");

        System.out.println("}");
    }

}
