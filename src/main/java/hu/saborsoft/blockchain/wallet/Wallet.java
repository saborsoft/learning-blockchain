package hu.saborsoft.blockchain.wallet;

import hu.saborsoft.blockchain.support.UtilityMethods;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {

    private KeyPair keyPair;

    private String walletName;

    public Wallet(String walletName) {
        keyPair = UtilityMethods.generateKeyPair(2048);
        this.walletName = walletName;
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

}
