package hu.saborsoft.blockchain.example.hashing;

import hu.saborsoft.blockchain.block.Block;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockMiningExample {

    private static final Logger LOG = LoggerFactory.getLogger(BlockMiningExample.class);

    public static void main(final String[] args) {
        Block b = new Block("0", 20);
        for (int t = 0; t < 10; t++) {
            b.addTransaction("Transaction" + t);
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
