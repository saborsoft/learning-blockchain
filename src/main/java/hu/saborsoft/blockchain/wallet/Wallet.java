package hu.saborsoft.blockchain.wallet;

import hu.saborsoft.blockchain.support.EncryptionAlgorithm;
import hu.saborsoft.blockchain.support.UtilityMethods;

import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {

    private static final String KEY_LOCATION = "keys";

    private KeyPair keyPair;

    private String walletName;

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
