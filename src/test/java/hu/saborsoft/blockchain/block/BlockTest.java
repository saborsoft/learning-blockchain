package hu.saborsoft.blockchain.block;

import hu.saborsoft.blockchain.support.UtilityMethods;
import hu.saborsoft.blockchain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BlockTest {

    @Mock
    Transaction transaction1;

    @Mock
    Transaction transaction2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void computeHashIDTest() {
        // GIVEN
        String previousBlockHashID = "0000000000000000";
        int difficultyLevel = 4;
        // WHEN
        Block block = new Block(previousBlockHashID, difficultyLevel);
        block.addTransaction(transaction1);
        block.addTransaction(transaction2);
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
        block.addTransaction(transaction1);
        block.addTransaction(transaction2);
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
        block.addTransaction(transaction1);
        block.addTransaction(transaction2);
        // WHEN
        List<Transaction> transactions = List.of(transaction1, transaction2);
        assertThat(transaction1).isEqualTo(block.getTransaction(0));
        assertThat(transaction2).isEqualTo(block.getTransaction(1));
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
        assertThat(block.getNonce()).isZero();
        block.mineTheBlock();
        // THEN
        assertThat(block.getNonce()).isPositive();
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
