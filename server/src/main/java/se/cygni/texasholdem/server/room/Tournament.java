package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.util.GameUtil;
import se.cygni.texasholdem.game.util.TableUtil;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;
import se.cygni.texasholdem.table.TournamentTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Tournament extends Room {

    private static Logger log = LoggerFactory
            .getLogger(Tournament.class);

    protected final String tournamentId = UUID.randomUUID().toString();

    private boolean tournamentHasStarted = false;
    private boolean tournamentHasEnded = false;

    private List<BotPlayer> playerRank = Collections.synchronizedList(new ArrayList<BotPlayer>());

    public Tournament(EventBus eventBus, GamePlan gamePlan, SessionManager sessionManager) {
        super(eventBus, gamePlan, sessionManager);
    }

    @Override
    public void addPlayer(BotPlayer player) {
        if (!tournamentHasStarted)
            playerPool.add(player);
    }

    public boolean tournamentHasStarted() {
        return tournamentHasStarted;
    }

    public void startTournament() {

        // Already started
        if (tournamentHasStarted)
            return;

        tournamentHasStarted = true;

        restartTables();

    }

    private boolean isThereAWinner() {
        return GameUtil.isThereAWinner(getPlayers());
    }

    private void restartTables() {
        stopAllTables();
        partitionActivePlayersOnTables();
        startAllTables();
    }

    private void partitionActivePlayersOnTables() {
        tables.clear();

        List<List<BotPlayer>> partitionedPlayers = TableUtil.partitionPlayers(GameUtil.getActivePlayersWithChipsLeft(getPlayers()));

        int playTillNoofPlayersLeft = 3;

        // Only players for one table left, play till one player remains!
        if (partitionedPlayers.size() == 1) {
            playTillNoofPlayersLeft = 0;
        }

        for (List<BotPlayer> playersInTable : partitionedPlayers) {
            Table table = new TournamentTable(gamePlan, this, eventBus, sessionManager, playTillNoofPlayersLeft);
            table.addPlayers(playersInTable);

            tables.add(table);
        }
    }

    private void shutdown() {
        log.info("Shutting down tournament");

        stopAllTables();

        // Report results
        for (BotPlayer player : playerRank) {
            log.info(player.toString());
        }

        eventBus.unregister(this);

        for (BotPlayer player : getPlayers()) {
            sessionManager.terminateSession(player);
        }
    }

    private void stopAllTables() {

        log.info("Stopping all tournament tables");

        for (Table table : tables) {
            table.stopGame();
        }

        // Wait for all tables to stop
        while (true) {
            boolean allStopped = true;
            for (Table table : tables) {
                if (!table.gameHasStopped())
                    allStopped = false;
            }
            if (allStopped)
                return;

            try { Thread.currentThread().sleep(125); } catch (Exception e) {}
        }

    }

    private void startAllTables() {
        for (Table table : tables) {
            Thread thread = new Thread(table);
            thread.setName("Tournament - table: " + table.getTableCounter());
            thread.start();
        }
    }

    @Override
    public void onTableGameDone(Table table) {

        if (!tables.contains(table))
            return;

        if (isThereAWinner()) {
            log.info("This tournament has a winner, ending!");
            tournamentHasEnded = true;
            shutdown();
            return;
        }

        restartTables();
    }

    @Override
    public synchronized void onPlayerBusted(BotPlayer player) {
        log.info("{} busted!", player);
        playerRank.add(player);
    }
}
