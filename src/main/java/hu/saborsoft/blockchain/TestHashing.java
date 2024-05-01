package hu.saborsoft.blockchain;

import hu.saborsoft.blockchain.support.UtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHashing {

    private static final Logger LOG = LoggerFactory.getLogger(TestHashing.class);

    public static void main(String[] args) {
        String msg = "If you are a drop of tears in my eyes!";
        byte[] b = UtilityMethods.messageDigestSHA256ToBytes(msg);
        String hash = UtilityMethods.messageDigestSHA256ToString(msg);
        UtilityMethods.toBinaryString(b);

        LOG.debug(hash);
        LOG.debug(UtilityMethods.toBinaryString(b));

    }
}
