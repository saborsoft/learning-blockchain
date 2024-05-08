package hu.saborsoft.blockchain.example.hashing;

import hu.saborsoft.blockchain.block.Block;
import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import hu.saborsoft.blockchain.transaction.UTXO;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class BlockMiningExample {

    private static final Logger LOG = LoggerFactory.getLogger(BlockMiningExample.class);

    public static void main(final String[] args) {
        Block b = new Block("0", 20);
        for (int t = 0; t < 2; t++) {
            KeyPair keyPair = UtilityMethods.generateKeyPair();
            UTXO uin = new UTXO("0", keyPair.getPublic(), keyPair.getPublic(), 1000);
            List<UTXO> input = new ArrayList<>();
            input.add(uin);
            Transaction tran = new Transaction(keyPair.getPublic(), keyPair.getPublic(), 100.0, input);
            b.addTransaction(tran);
        }
        LOG.debug("Start mining the block");
        StopWatch watch = new StopWatch();
        watch.start();
        b.mineTheBlock();
        watch.stop();
        LOG.debug("block is successfully mined, hash ID is:");
        LOG.debug(b.getHashID());
        LOG.debug("Elapsed time: {}", watch.getTime());
    }
}
