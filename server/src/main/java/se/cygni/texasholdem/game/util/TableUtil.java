package se.cygni.texasholdem.game.util;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.table.Table;

import java.util.ArrayList;
import java.util.List;

public final class TableUtil {

    private static RandomAdaptor random = new RandomAdaptor(new SynchronizedRandomGenerator(new JDKRandomGenerator()));

    private TableUtil() {
    }

    public static List<List<BotPlayer>> partitionPlayers(List<BotPlayer> players) {
        List<List<BotPlayer>> partitionedPlayers = new ArrayList<List<BotPlayer>>();

        List<BotPlayer> shuffledPlayers = shufflePlayers(players);

        if (shuffledPlayers.size() <= Table.MAX_NOOF_PLAYERS) {
            partitionedPlayers.add(shuffledPlayers);
            return partitionedPlayers;
        }

        int noofTablesNeeded = (int) Math.ceil((double) players.size() / (double) Table.MAX_NOOF_PLAYERS);

        int minPlayersPerTable = players.size() / noofTablesNeeded;

        for (int i = 0; i < noofTablesNeeded; i++) {
            List<BotPlayer> playersForCurrentTable = new ArrayList<BotPlayer>();
            playersForCurrentTable.addAll(shuffledPlayers.subList(i * minPlayersPerTable, i * minPlayersPerTable + minPlayersPerTable));

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
        if (players.size() <= 1) {
            return players;
        }

        List<BotPlayer> playersQueue = new ArrayList<BotPlayer>(players);
        List<BotPlayer> shuffledPlayers = new ArrayList<BotPlayer>(players.size());

        while (playersQueue.size() > 0) {

            int randomPos = random.nextInt(playersQueue.size());
            shuffledPlayers.add(playersQueue.remove(randomPos));

        }

        if (!isListShuffled(players, shuffledPlayers)) {
            return shufflePlayers(players);
        }

        return shuffledPlayers;
    }

    private static boolean isListShuffled(List<BotPlayer> originalList, List<BotPlayer> shuffledList) {
        if (originalList.size() != shuffledList.size()) {
            return false;
        }

        for (int i = 0; i < originalList.size(); i++) {
            if (!originalList.get(i).equals(shuffledList.get(i))) {
                return true;
            }
        }

        return false;
    }
}
