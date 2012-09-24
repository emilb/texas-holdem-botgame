package se.cygni.texasholdem.dao.model.stats;

import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.dao.model.PlayerInGame;

import java.util.*;

public class StatsActions {

    private Set<String> players = new HashSet<String>();
    private Map<String, Integer> playerFoldedStat = new HashMap<String, Integer>();
    private Map<String, Integer> playerRaisedStat = new HashMap<String, Integer>();;
    private Map<String, Integer> playerCalledStat = new HashMap<String, Integer>();;
    private Map<String, Integer> playerAllInStat = new HashMap<String, Integer>();;

    public String[] getPlayers() {
        return players.toArray(new String[] {});
    }

    public int[] getFoldedStat() {
        return getIntArray(playerFoldedStat);
    }

    public int[] getAllInStat() {
        return getIntArray(playerAllInStat);
    }

    public int[] getRaisedStat() {
        return getIntArray(playerRaisedStat);
    }

    public int[] getCalledStat() {
        return getIntArray(playerCalledStat);
    }

    public void recordsGameLogs(List<GameLog> gameLogs) {
        for (GameLog gameLog : gameLogs) {
            recordGameLog(gameLog);
        }
    }

    public void recordGameLog(GameLog gameLog) {

        for (PlayerInGame player : gameLog.getPlayers()) {
            String name = player.getName();

            players.add(name);

            if (player.isFolded()) {
                increment(playerFoldedStat, name);
            }

            if (player.allIn) {
                increment(playerAllInStat, name);
            }

            increment(playerRaisedStat, name, player.noofRaises);
            increment(playerCalledStat, name, player.noofCalls);
        }
    }

    private int[] getIntArray(Map<String, Integer> map) {
        int[] intArray = new int[players.size()];
        int counter = 0;
        for (String player : players) {
            try {
                intArray[counter++] = map.get(player).intValue();
            } catch (Exception e) {
                intArray[counter - 1] = 0;
            }
        }

        return intArray;
    }

    private void increment(Map<String, Integer> map, String key) {
        if (!map.containsKey(key)) {
            map.put(key, Integer.valueOf(1));
        } else {
            map.put(key, Integer.valueOf(map.get(key)+1));
        }
    }

    private void increment(Map<String, Integer> map, String key, int incrementWith) {
        if (!map.containsKey(key)) {
            map.put(key, Integer.valueOf(incrementWith));
        } else {
            map.put(key, Integer.valueOf(map.get(key)+incrementWith));
        }
    }
}
