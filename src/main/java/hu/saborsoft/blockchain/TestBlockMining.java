package hu.saborsoft.blockchain;

import hu.saborsoft.blockchain.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBlockMining {

    private static final Logger LOG = LoggerFactory.getLogger(TestBlockMining.class);

    public static void main(final String[] args) {
        Block b = new Block("0", 20);
        for (int t = 0; t < 10; t++) {
            b.addTransaction("Transaction" + t);
        }
        LOG.debug("Start mining the block");
        b.mineTheBlock();
        LOG.debug("block is successfully mined, hash ID is:");
        LOG.debug(b.getHashID());
    }
}
