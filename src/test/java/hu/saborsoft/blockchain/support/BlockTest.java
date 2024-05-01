package hu.saborsoft.blockchain.support;

import hu.saborsoft.blockchain.block.Block;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {

    @Test
    void computeHashIDTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction("Transaction 1");
        block.addTransaction("Transaction 2");

        assertNotNull(block.computeHashID());
    }

    @Test
    void mineTheBlockTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction("Transaction 1");
        block.addTransaction("Transaction 2");

        assertTrue(block.mineTheBlock());
        assertNotNull(block.getHashID());
        assertTrue(UtilityMethods.hashMeetsDifficultyLevel(block.getHashID(), difficultyLevel));
    }

    @Test
    void addTransactionTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction("Transaction 1");
        block.addTransaction("Transaction 2");

        List<String> transactions = List.of("Transaction 1", "Transaction 2");
        assertEquals(transactions, block.getTransactions());
    }

    @Test
    void getHashIDTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        assertNull(block.getHashID());
        block.mineTheBlock();
        assertNotNull(block.getHashID());
    }

    @Test
    void getNonceTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 10;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        assertEquals(0, block.getNonce());
        block.mineTheBlock();
        assertTrue(block.getNonce() > 0);
    }

    @Test
    void getTimestampTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        long currentTimestamp = UtilityMethods.getTimeStamp();
        assertTrue(block.getTimestamp() >= currentTimestamp);
    }

    @Test
    void getPreviousBlockHashIDTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        assertEquals(previousBlockHashID, block.getPreviousBlockHashID());
    }

    @Test
    void getDifficultyLevelTest() {
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        Block block = new Block(previousBlockHashID, difficultyLevel);
        assertEquals(difficultyLevel, block.getDifficultyLevel());
    }

}
