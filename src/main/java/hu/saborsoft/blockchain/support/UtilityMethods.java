package hu.saborsoft.blockchain.support;

import hu.saborsoft.blockchain.exception.NoAlgorithmException;
import hu.saborsoft.blockchain.exception.SignatureException;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.security.*;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.stream.IntStream;

public class UtilityMethods {

    public static final String ALGORITHM = "SHA-256";

    public static final String WRONG_ALGORITHM = "wrong algorithm, can never happen";

    private static final Logger LOG = LoggerFactory.getLogger(UtilityMethods.class);

    private static long uniqueNumber = 0;

    public static long getUniqueNumber() {
        return uniqueNumber++;
    }

    private UtilityMethods() {
    }

    public static byte[] messageDigestSHA256ToBytes(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(message.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new NoAlgorithmException(WRONG_ALGORITHM, e);
        }
    }

    public static String messageDigestSHA256ToString(String message) {
        return Base64.getEncoder().encodeToString(messageDigestSHA256ToBytes(message));
    }

    public static long getTimeStamp() {
        return Instant.now().toEpochMilli();
    }

    public static boolean hashMeetsDifficultyLevel(String hash, int difficultyLevel) {
        if (StringUtils.isBlank(hash)) {
            return false;
        }

        char[] c = hash.toCharArray();
        for (int i = 0; i < difficultyLevel; i++) {
            if (c[i] != '0') {
                return false;

            }
        }
        return true;
    }

    public static String toBinaryString(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            // Transform a byte into an unsigned integer.
            int x = b + 128;
            StringBuilder s = new StringBuilder(Integer.toBinaryString(x));
            while (s.length() < 8) {
                s.insert(0, "0");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(2048);
    }

    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keySize);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new NoAlgorithmException(WRONG_ALGORITHM, e);
        }
    }

    public static byte[] generateSignature(PrivateKey privateKey, String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (Exception e) {
            throw new SignatureException("signature error", e);
        }
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] signature, String message) {
        try {
            Signature sig2 = Signature.getInstance("SHA256withRSA");
            sig2.initVerify(publicKey);
            sig2.update(message.getBytes());
            return sig2.verify(signature);
        } catch (Exception e) {
            LOG.error("Signature verification error", e.getCause());
            return false;
        }
    }

    public static String getKeyString(Key key) {
        return Base64.getEncoder().encodeToString((key.getEncoded()));
    }

    public static void displayTab(PrintStream out, int level, String s) {
        for (int i = 0; i < level; i++) {
            out.print("\t");
        }
        out.println(s);
    }

    public static void displayUTXO(UTXO ux, PrintStream out, int level) {
        displayTab(out, level, "fund: " + ux.getFundTransferred()
                + ", receiver: " + getKeyString(ux.getReceiver()));
    }

    public static void displayTransaction(Transaction t, PrintStream out, int level) {
        displayTab(out, level, "Transaction{");
        displayTab(out, level + 1, "ID: " + t.getHashID());
        displayTab(out, level + 1, "sender:" + UtilityMethods.getKeyString(t.getSender()));
        displayTab(out, level + 1, "fundToBeTransferred total: " + t.getTotalFundToTransfer());
        displayTab(out, level + 1, "Input:");
        IntStream.range(0, t.getNumberOfInputUTXOs()).mapToObj(
                t::getInputUTXO).forEach(ui -> displayUTXO(ui, out, level + 2));
        displayTab(out, level + 1, "Output:");
        IntStream.range(0, t.getNumberOfOutputUTXOs() - 1).mapToObj(
                t::getOutputUTXO).forEach(ut -> displayUTXO(ut, out, level + 2));
        UTXO change = t.getOutputUTXO(t.getNumberOfOutputUTXOs() - 1);
        displayTab(out, level + 2, "change: " + change.getFundTransferred());
        displayTab(out, level + 1, "transaction fee: " + Transaction.TRANSACTION_FEE);
        boolean b = t.verifySignature();
        displayTab(out, level + 1, "signature verification: " + b);
        displayTab(out, level, "}");
    }

    public static byte[] encryptionByXOR(byte[] key, String password) {
        int more = 100;
        byte[] p = messageDigestSHA256ToBytes(password);
        byte[] pwds = new byte[p.length * more];
        for (int i = 0, z = 0; i < more; i++) {
            for (int j = 0; j < p.length; j++, z++) {
                pwds[z] = p[j];
            }
        }
        byte[] result = new byte[key.length];
        int i;
        for (i = 0; i < key.length && i < pwds.length; i++) {
            result[i] = (byte) ((key[i] ^ pwds[i]) & 0xFF);
        }
        while (i < key.length) {
            result[i] = key[i];
            i++;
        }
        return result;
    }

    public static byte[] decryptionByXOR(byte[] key, String password) {
        return encryptionByXOR(key, password);
    }

    /**
     * used only for decrypting a key which should never be more than 4096 bytes
     *
     * @param keyIn
     * @param password
     * @return
     */
    public static byte[] decryptionByXOR(FileInputStream keyIn, String password) {
        try {
            byte[] data = new byte[4096];
            int size = keyIn.read(data);
            byte[] result = new byte[size];
            for (int i = 0; i < result.length; i++) {
                result[i] = data[i];
            }
            return decryptionByXOR(result, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] encryptionByAES(byte[] key, String password) {
        try {
            byte[] salt = new byte[8];
            SecureRandom rand = new SecureRandom();
            rand.nextBytes(salt);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1024, 128);
            SecretKey temp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(temp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] output = cipher.doFinal(key);
            //using a variable to record the length of the output
            byte[] outputSizeBytes = intToBytes(output.length);
            byte[] ivSizeBytes = intToBytes(iv.length);
            byte[] data = new byte[Integer.BYTES * 2
                    + salt.length + iv.length + output.length];
            // the order of the data is arranged as the following:
            // int-forDataSize + int-forIVsize + 8-byte-salt + iv-bytes + output-bytes
            int z = 0;
            for (int i = 0; i < outputSizeBytes.length; i++, z++) {
                data[z] = outputSizeBytes[i];
            }
            for (int i = 0; i < ivSizeBytes.length; i++, z++) {
                data[z] = ivSizeBytes[i];
            }
            for (int i = 0; i < salt.length; i++, z++) {
                data[z] = salt[i];
            }
            for (int i = 0; i < iv.length; i++, z++) {
                data[z] = iv[i];
            }
            for (int i = 0; i < output.length; i++, z++) {
                data[z] = output[i];
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptionByAES(byte[] key, String password) {
        try {
            // divide the input data key[] into proper values
            // please remember the order of the data is:
            // int_forOutputSize + int_forIVSize + 8_byte_salt + iv_bytes + output_bytes
            int z = 0;
            byte[] lengthByte = new byte[Integer.BYTES];
            for (int i = 0; i < lengthByte.length; i++, z++) {
                lengthByte[i] = key[z];
            }
            int dataSize = bytesToInt(lengthByte);

            for (int i = 0; i < lengthByte.length; i++, z++) {
                lengthByte[i] = key[z];
            }
            int ivSize = bytesToInt(lengthByte);

            byte[] salt = new byte[8];
            for (int i = 0; i < salt.length; i++, z++) {
                salt[i] = key[z];
            }
            // iv bytes
            byte[] ivBytes = new byte[ivSize];
            for (int i = 0; i < ivBytes.length; i++, z++) {
                ivBytes[i] = key[z];
            }
            // real data bytes
            byte[] dataBytes = new byte[dataSize];
            for (int i = 0; i < dataBytes.length; i++, z++) {
                dataBytes[i] = key[z];
            }
            // once data are ready, reconstruct the key and cipher
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1024, 128);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey tmp = secretKeyFactory.generateSecret(pbeKeySpec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // algorithm parameters (ivBytes) are necessary to initiate cipher
            cipher2.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
            byte[] data = cipher2.doFinal(dataBytes);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] intToBytes(int v) {
        byte[] b = new byte[Integer.BYTES];
        for (int i = b.length - 1; i >= 0; i--) {
            b[i] = (byte) (v & 0xFF);
            v = v >> Byte.SIZE;
        }
        return b;
    }

    public static int bytesToInt(byte[] b) {
        int v = 0;
        for (int i = 0; i < b.length; i++) {
            v = v << Byte.SIZE;
            v = v | (b[i] & 0xFF);
        }
        return v;
    }

    public static byte[] longToBytes(long v) {
        byte[] b = new byte[Long.BYTES];
        for (int i = b.length - 1; i >= 0; i--) {
            b[i] = (byte) (v & 0xFFFF);
            v = v >> Byte.SIZE;
        }
        return b;
    }

    /**
     * the byte array must be of size 8
     *
     * @param b
     */
    public static long bytesToLong(byte[] b) {
        long v = 0L;
        for (int i = 0; i < b.length; i++) {
            v = v << Byte.SIZE;
            v = v | (b[i] & 0xFFFF);
        }
        return v;
    }


}
