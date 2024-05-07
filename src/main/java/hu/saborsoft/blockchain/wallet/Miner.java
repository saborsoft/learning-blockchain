package hu.saborsoft.blockchain.wallet;

import hu.saborsoft.blockchain.block.Block;

public class Miner extends Wallet {

    public Miner(String minerName, String password) {
        super(minerName, password);
    }

    public boolean mineBlock(Block block) {
        return block.mineTheBlock();
    }
    
}
