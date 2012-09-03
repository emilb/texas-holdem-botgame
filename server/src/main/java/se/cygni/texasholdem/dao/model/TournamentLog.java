package se.cygni.texasholdem.dao.model;

import se.cygni.texasholdem.game.GamePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TournamentLog {

    private final String id;
    private final long tournamentCounter;

    private final boolean tournamentHasStarted;
    private final boolean tournamentHasEnded;

    private final List<GamePlayer> playerRanking;
    private final List<Long> tableIds;

    private final Date created;
    private final SimpleDateFormat sdf;

    public TournamentLog(String id, long tournamentCounter, Date created, boolean tournamentHasStarted, boolean tournamentHasEnded, List<GamePlayer> playerRanking, List<Long> tableIds) {
        this.id = id;
        this.tournamentCounter = tournamentCounter;
        this.created = created;
        this.tournamentHasStarted = tournamentHasStarted;
        this.tournamentHasEnded = tournamentHasEnded;
        this.playerRanking = playerRanking;
        this.tableIds = tableIds;

        sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    }

    public String getId() {
        return id;
    }

    public long getTournamentCounter() {
        return tournamentCounter;
    }

    public boolean getTournamentHasStarted() {
        return tournamentHasStarted;
    }

    public boolean getTournamentHasEnded() {
        return tournamentHasEnded;
    }

    public List<GamePlayer> getPlayerRanking() {
        return playerRanking;
    }

    public List<Long> getTableIds() {
        return tableIds;
    }

    public String getCreatedDate() {
        return sdf.format(created);
    }

    public boolean getCanStart() {
        return (!tournamentHasEnded && !tournamentHasStarted && playerRanking.size() > 1);
    }

    public String getStatus() {
        if (!tournamentHasStarted)
            return "Accepting players";
        if (tournamentHasStarted && !tournamentHasEnded)
            return "Running";
        return "Finished";
    }
}
