package hu.saborsoft.blockchain;

import hu.saborsoft.blockchain.block.Block;

public class TestBlockMining {

    public static void main(final String[] args) {
        Block b = new Block("0", 20);
        for (int t = 0; t < 10; t++) {
            b.addTransaction("Transaction" + t);
        }
        System.out.println("Start mining the block");
        b.mineTheBlock();
        System.out.println("block is successfully mined, hash ID is:");
        System.out.println(b.getHashID());
    }
}
