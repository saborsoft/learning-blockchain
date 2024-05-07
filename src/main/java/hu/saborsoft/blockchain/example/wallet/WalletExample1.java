package hu.saborsoft.blockchain.example.wallet;

import hu.saborsoft.blockchain.support.EncryptionAlgorithm;
import hu.saborsoft.blockchain.wallet.Wallet;

import java.util.Scanner;

public class WalletExample1 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("To create a wallet, please give your name");
        String name = in.nextLine();
        System.out.println("please create a password");
        String password = in.nextLine();
        in.close();
        Wallet w = new Wallet(name, password, EncryptionAlgorithm.AES);
        System.out.println("wallet created for " + w.getName());

        // let's load this wallet
        Wallet w2 = new Wallet(name, password, EncryptionAlgorithm.AES);
        System.out.println("wallet loaded successfully, wallet name=" + w2.getName());
    }

}
