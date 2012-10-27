package se.cygni.texasholdem.dao.model.stats;

import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.dao.model.PlayerInGame;

import java.util.*;

public class StatsChips {

    private Set<String> players = new HashSet<String>();
    private int gameRoundCounter = 0;

    private Map<String, List<Long>> chipsPerPlayerPerRound = new HashMap<String, List<Long>>();

    public String[] getPlayers() {
        return players.toArray(new String[]{});
    }

    public Map<String, List<Long>> getChipsPerPlayerPerRound() {
        return chipsPerPlayerPerRound;
    }

    public void recordGameLogs(List<GameLog> gameLogs) {
        for (GameLog gameLog : gameLogs) {
            recordGameLog(gameLog);
        }
    }

    public void recordGameLog(GameLog gameLog) {

        for (PlayerInGame player : gameLog.getPlayers()) {
            String name = player.getName();

            players.add(name);

            if (!chipsPerPlayerPerRound.containsKey(name)) {
                chipsPerPlayerPerRound.put(name, new ArrayList<Long>());
            }

            chipsPerPlayerPerRound.get(name).add(player.getChipsAfterGame());
        }
    }
}
