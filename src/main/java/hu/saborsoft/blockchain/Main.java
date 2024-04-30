package hu.saborsoft.blockchain;

import hu.saborsoft.blockchain.support.UtilityMethods;

public class Main {

    public static void main(String[] args) {
        String msg = "If you are a drop of tears in my eyes!";
        String hash = UtilityMethods.messageDigestSHA256_toString(msg);
        System.out.println(hash);
    }
}
