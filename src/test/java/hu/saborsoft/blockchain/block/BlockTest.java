package hu.saborsoft.blockchain.block;

import hu.saborsoft.blockchain.support.UtilityMethods;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockTest {

    @Test
    void computeHashIDTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction("Transaction 1");
        block.addTransaction("Transaction 2");
        // THEN
        assertThat(block.computeHashID()).isNotBlank();
    }

    @Test
    void mineTheBlockTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction("Transaction 1");
        block.addTransaction("Transaction 2");
        // THEN
        assertThat(block.mineTheBlock()).isTrue();
        assertThat(block.getHashID()).isNotBlank();
        assertThat(UtilityMethods.hashMeetsDifficultyLevel(block.getHashID(), difficultyLevel)).isTrue();
    }

    @Test
    void addTransactionTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction("Transaction 1");
        block.addTransaction("Transaction 2");
        // WHEN
        List<String> transactions = List.of("Transaction 1", "Transaction 2");
        assertThat(transactions).isEqualTo(block.getTransactions());
    }

    @Test
    void getHashIDTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        assertThat(block.getHashID()).isNull();
        block.mineTheBlock();
        // THEN
        assertThat(block.getHashID()).isNotNull();
    }

    @Test
    void getNonceTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 10;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        assertThat(0).isEqualTo(block.getNonce());
        block.mineTheBlock();
        // THEN
        assertThat(block.getNonce()).isGreaterThan(0);
    }

    @Test
    void getTimestampTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        long currentTimestamp = UtilityMethods.getTimeStamp();
        // THEN
        assertThat(block.getTimestamp()).isLessThanOrEqualTo(currentTimestamp);
    }

    @Test
    void getPreviousBlockHashIDTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        // THEN
        assertThat(previousBlockHashID).isEqualTo(block.getPreviousBlockHashID());
    }

    @Test
    void getDifficultyLevelTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        // THEN
        assertThat(difficultyLevel).isEqualTo(block.getDifficultyLevel());
    }

}
