package se.cygni.texasholdem.game.util;

import junit.framework.Assert;
import org.junit.Test;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.table.Table;

import java.util.List;

import static se.cygni.texasholdem.test.util.PlayerTestUtil.createRandomListOfPlayers;

public class TableUtilTest {

    @Test
    public void testPartitionPlayersFourTablesEven() {
        List<BotPlayer> players = createRandomListOfPlayers(36);
        List<List<BotPlayer>> partitions = TableUtil.partitionPlayers(players);

        Assert.assertEquals(4, partitions.size());

        int sum = 0;
        for (List<BotPlayer> table : partitions) {
            sum += table.size();

            for (BotPlayer player : table) {
                players.remove(player);
            }
        }

        Assert.assertEquals("Total player count wrong", 36, sum);
        Assert.assertEquals("Not all players were included in the partition", 0, players.size());
    }

    @Test
    public void testPartitionPlayersFourTablesUneven() {
        List<BotPlayer> players = createRandomListOfPlayers(42);
        List<List<BotPlayer>> partitions = TableUtil.partitionPlayers(players);

        Assert.assertEquals(4, partitions.size());

        int sum = 0;
        for (List<BotPlayer> table : partitions) {
            sum += table.size();

            for (BotPlayer player : table) {
                players.remove(player);
            }
        }

        Assert.assertEquals("Total player count wrong", 42, sum);
        Assert.assertEquals("Not all players were included in the partition", 0, players.size());
    }

    @Test
    public void testPartitionPlayersOneTableFull() {
        List<BotPlayer> players = createRandomListOfPlayers(Table.MAX_NOOF_PLAYERS);
        List<List<BotPlayer>> partitions = TableUtil.partitionPlayers(players);

        Assert.assertEquals(1, partitions.size());
        assertListShuffled(players, partitions.get(0));
    }

    @Test
    public void testPartitionPlayersOneTable() {
        List<BotPlayer> players = createRandomListOfPlayers(5);
        List<List<BotPlayer>> partitions = TableUtil.partitionPlayers(players);

        Assert.assertEquals(1, partitions.size());
        assertListShuffled(players, partitions.get(0));
    }

    @Test
    public void testShufflePlayers() {
        List<BotPlayer> players = createRandomListOfPlayers(15);

        List<BotPlayer> shuffledPlayers = TableUtil.shufflePlayers(players);

        assertListShuffled(players, shuffledPlayers);
    }

    @Test
    public void testCreateRandomListOfPlayers() {
        List<BotPlayer> players = createRandomListOfPlayers(5);

        Assert.assertNotNull(players);
        Assert.assertEquals(5, players.size());
        for (BotPlayer player : players) {
            Assert.assertNotNull(player);
        }

    }


    private void assertListShuffled(List<BotPlayer> originalList, List<BotPlayer> shuffledList) {
        Assert.assertEquals(originalList.size(), shuffledList.size());

        boolean everyPositionIsSame = true;

        for (int i = 0; i < originalList.size(); i++) {
            if (!originalList.get(i).equals(shuffledList.get(i))) {
                everyPositionIsSame = false;
            }
        }

        Assert.assertFalse(everyPositionIsSame);
    }
}
