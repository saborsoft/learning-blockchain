package hu.saborsoft.blockchain.example.password;

import hu.saborsoft.blockchain.support.UtilityMethods;

public class EncryptionDecryptionWithAES {

    public static void main(String[] args) {
        String message = "At the most beautiful you."
                + "remember the most beautiful you.";
        String password = "blockchains";
        byte[] encry = UtilityMethods.encryptionByAES(message.getBytes(), password);
        // take a peak at the encrypted data.
        System.out.println(new String(encry));
        byte[] decrypted = UtilityMethods.decryptionByAES(encry, password);
        System.out.println("after proper decryption, the message is:\n");
        System.out.println(new String(decrypted));
        try {
            System.out.println("\nwith an incorrect password, "
                    + "the decrypted message looks like:");
            // let's try an incorrect password.
            decrypted = UtilityMethods.decryptionByAES(encry, "Block Chain");
            // examine the wrongly decrypted message
            System.out.println(new String(decrypted));
        } catch (Exception e) {
            System.out.println("runtime exception happened. cannot work");
        }
    }

}
