package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.GamePlayer;
import se.cygni.texasholdem.game.util.GameUtil;
import se.cygni.texasholdem.game.util.TableUtil;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.statistics.AtomicCounter;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;
import se.cygni.texasholdem.table.TournamentTable;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import java.util.*;

public class Tournament extends Room {

    private static Logger log = LoggerFactory
            .getLogger(Tournament.class);

    public static final String COUNTER_ID = "tournament";
    protected final String tournamentId = UUID.randomUUID().toString();
    protected final long tournamentCounter;

    private boolean tournamentHasStarted = false;
    private boolean tournamentHasEnded = false;
    private Date created;

    private List<BotPlayer> playerRank = Collections.synchronizedList(new ArrayList<BotPlayer>());

    private List<List<Long>> tablePartitions = new ArrayList<List<Long>>();
    private List<Long> tablesPlayedIds = new ArrayList<Long>();

    public Tournament(EventBus eventBus, GamePlan gamePlan, SessionManager sessionManager) {
        super(eventBus, gamePlan, sessionManager);

        this.tournamentCounter = AtomicCounter.increment(COUNTER_ID);
        this.created = new Date();
    }

    @Override
    public boolean addPlayer(BotPlayer player) {
        if (!tournamentHasStarted) {
            playerPool.add(player);
            return true;
        }

        return false;
    }

    public GamePlan getGamePlan() {
        return gamePlan.createCopy();
    }

    public void setGamePlan(GamePlan gamePlan) {
        this.gamePlan = gamePlan;
    }

    public boolean tournamentHasStarted() {
        return tournamentHasStarted;
    }

    public boolean tournamentHasEnded() {
        return tournamentHasEnded;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public long getTournamentCounter() {
        return tournamentCounter;
    }

    public Date getCreated() {
        return created;
    }

    public List<GamePlayer> getPlayerRanking() {
        if (tournamentHasEnded()) {
            return PlayerTypeConverter.listOfBotPlayers(playerRank);
        }

        List<GamePlayer> currentPlayerRank = new ArrayList<GamePlayer>();

        // Add all current active players
        for (BotPlayer player : playerPool) {
            if (GameUtil.playerHasChips(player)) {
                currentPlayerRank.add(PlayerTypeConverter.fromBotPlayer(player));
            }
        }

        // Sort descending
        Collections.sort(currentPlayerRank, new Comparator<GamePlayer>() {
            @Override
            public int compare(GamePlayer player, GamePlayer player1) {
                if (player.getChipCount() == player1.getChipCount()) {
                    return 0;
                }

                if (player.getChipCount() > player1.getChipCount()) {
                    return -1;
                }

                return 1;
            }
        });

        currentPlayerRank.addAll(PlayerTypeConverter.listOfBotPlayers(playerRank));
        return currentPlayerRank;
    }

    public void startTournament() {

        // Already started
        if (tournamentHasStarted) {
            return;
        }

        tournamentHasStarted = true;

        restartTables();

    }

    private boolean isThereAWinner() {
        return GameUtil.isThereAWinner(getPlayers());
    }

    private BotPlayer getWinner() {

        if (!isThereAWinner()) {
            return null;
        }

        for (BotPlayer player : playerPool) {
            if (GameUtil.playerHasChips(player)) {
                return player;
            }
        }

        return null;
    }

    private void restartTables() {
        stopAllTables();
        partitionActivePlayersOnTables();
        startAllTables();
    }

    private void partitionActivePlayersOnTables() {
        tables.clear();
        tablesPlayedIds.clear();

        List<List<BotPlayer>> partitionedPlayers = TableUtil.partitionPlayers(GameUtil.getActivePlayersWithChipsLeft(getPlayers()));

        int playTillNoofPlayersLeft = 3;

        // Only players for one table left, play till one player remains!
        if (partitionedPlayers.size() == 1) {
            playTillNoofPlayersLeft = 0;
        }

        for (List<BotPlayer> playersInTable : partitionedPlayers) {
            Table table = new TournamentTable(gamePlan, this, eventBus, sessionManager, playTillNoofPlayersLeft);
            table.addPlayers(playersInTable);
            tablesPlayedIds.add(table.getTableCounter());
            tables.add(table);
        }

        tablePartitions.add(new ArrayList<Long>(tablesPlayedIds));
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
                if (!table.gameHasStopped()) {
                    allStopped = false;
                }
            }
            if (allStopped) {
                return;
            }

            try {
                Thread.currentThread().sleep(125);
            } catch (Exception e) {
            }
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

        if (!tables.contains(table)) {
            return;
        }

        if (isThereAWinner()) {
            log.info("This tournament has a winner, ending!");
            tournamentHasEnded = true;
            shutdown();

            playerRank.add(0, getWinner());

            return;
        }

        restartTables();
    }

    @Override
    public synchronized void onPlayerBusted(BotPlayer player) {
        log.info("{} busted!", player);
        playerRank.add(0, player);
    }

    public List<Long> getTablesPlayedIds() {
        List<Long> tableIds = new ArrayList<Long>();
        for (List<Long> pTableIds : tablePartitions) {
            tableIds.addAll(pTableIds);
        }

        return tableIds;
    }

    public List<List<Long>> getTablePartitions() {
        return tablePartitions;
    }
}
