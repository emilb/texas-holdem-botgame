package se.cygni.texasholdem.game.util;

import org.apache.commons.lang.math.RandomUtils;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.table.Table;

import java.util.ArrayList;
import java.util.List;

public class TableUtil {

    public static List<List<BotPlayer>> partitionPlayers(List<BotPlayer> players) {
        List<List<BotPlayer>> partitionedPlayers = new ArrayList<List<BotPlayer>>();

        List<BotPlayer> shuffledPlayers = shufflePlayers(players);

        if (shuffledPlayers.size() <= Table.MAX_NOOF_PLAYERS) {
            partitionedPlayers.add(shuffledPlayers);
            return partitionedPlayers;
        }

        int noofTablesNeeded = (int)Math.ceil((double)players.size() / (double)Table.MAX_NOOF_PLAYERS);

        int minPlayersPerTable = players.size() / noofTablesNeeded;

        for (int i = 0; i < noofTablesNeeded; i++) {
            List<BotPlayer> playersForCurrentTable = new ArrayList<BotPlayer>();
            playersForCurrentTable.addAll(shuffledPlayers.subList(i*minPlayersPerTable, i*minPlayersPerTable + minPlayersPerTable));

            partitionedPlayers.add(playersForCurrentTable);
        }

        int startPosRest = minPlayersPerTable * noofTablesNeeded;

        int counter = 0;
        while (startPosRest < shuffledPlayers.size()) {
            partitionedPlayers.get(counter % noofTablesNeeded).add(shuffledPlayers.get(startPosRest));
            startPosRest++;
            counter++;
        }

        return partitionedPlayers;
    }

    public static List<BotPlayer> shufflePlayers(List<BotPlayer> players) {
        List<BotPlayer> playersQueue = new ArrayList<BotPlayer>(players);
        List<BotPlayer> shuffledPlayers = new ArrayList<BotPlayer>(players.size());

        while (playersQueue.size() > 0) {

            int randomPos = RandomUtils.nextInt(playersQueue.size());
            shuffledPlayers.add(playersQueue.remove(randomPos));

        }

        return shuffledPlayers;
    }
}
