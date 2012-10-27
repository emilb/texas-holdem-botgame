package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.GamePlayer;
import se.cygni.texasholdem.game.util.GameUtil;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.statistics.AtomicCounter;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import java.util.*;

/**
 * Room for real players, only one table.
 * <p/>
 * Starts when at least MIN_NOOF_PLAYERS have connected.
 */
public class FreePlay extends Room {

    private static Logger log = LoggerFactory
            .getLogger(FreePlay.class);

    public static final String COUNTER_ID = "freeplay";
    public static final int MIN_NOOF_PLAYERS = 3;
    public static final int TIME_TILL_START_AFTER_NOOF_PLAYERS_CONNECTED_IN_MS = 1 * 60 * 1000; // 1 minute

    protected final String freePlayId = UUID.randomUUID().toString();
    protected final long freePlayCounter;

    private boolean freePlayHasStarted = false;
    private boolean freePlayHasEnded = false;
    private Date created;

    private final Table table;
    private Timer timer;

    private List<BotPlayer> playerRank = Collections.synchronizedList(new ArrayList<BotPlayer>());

    public FreePlay(EventBus eventBus, GamePlan gamePlan, SessionManager sessionManager) {
        super(eventBus, gamePlan, sessionManager);

        table = new Table(gamePlan, this, eventBus, sessionManager);
        tables.add(table);
        this.freePlayCounter = AtomicCounter.increment(COUNTER_ID);
        this.created = new Date();
    }

    @Override
    public boolean addPlayer(BotPlayer player) {

        if (playerPool.size() >= Table.MAX_NOOF_PLAYERS || freePlayHasStarted || freePlayHasEnded) {
            return false;
        }

        log.info("FreePlay got a player: {}", player);
        playerPool.add(player);

        // Start the game after a while (so other players may join)
        if (playerPool.size() >= MIN_NOOF_PLAYERS && timer == null) {
            log.info("Creating start timer for FreePlay room");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.info("Timer is starting play on FreePlay room");
                    startFreePlay();
                }
            }, TIME_TILL_START_AFTER_NOOF_PLAYERS_CONNECTED_IN_MS);
        }

        return true;
    }

    public boolean freePlayHasStarted() {
        return freePlayHasStarted;
    }

    public boolean freePlayHasEnded() {
        return freePlayHasEnded;
    }

    public String getFreePlayId() {
        return freePlayId;
    }

    public long getFreePlayCounter() {
        return freePlayCounter;
    }

    public Date getCreated() {
        return created;
    }

    public List<GamePlayer> getPlayerRanking() {
        if (freePlayHasEnded()) {
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

    public void startFreePlay() {

        // Already started
        if (freePlayHasStarted) {
            return;
        }

        // Check that min number of players are connected
        if (playerPool.size() < MIN_NOOF_PLAYERS) {
            log.info("I must have lost players, now only {} are connected. Need {} to start.", playerPool.size(), MIN_NOOF_PLAYERS);
            timer = null;
            return;
        }

        log.info("FreePlay game is starting");
        freePlayHasStarted = true;

        for (BotPlayer player : playerPool) {
            table.addPlayer(player);
        }

        Thread thread = new Thread(table);
        thread.setName("FreePlay - table: " + table.getTableCounter());
        thread.start();

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

    private void shutdown() {
        log.info("Shutting down freeplay");

        stopTable();

        // Report results
        for (BotPlayer player : playerRank) {
            log.info(player.toString());
        }

        eventBus.unregister(this);

        for (BotPlayer player : getPlayers()) {
            sessionManager.terminateSession(player);
        }
    }

    private void stopTable() {

        log.info("Stopping free play table");
        table.stopGame();
    }

    @Override
    public void onTableGameDone(Table table) {

        if (isThereAWinner()) {
            log.info("This free play has a winner, ending!");
            freePlayHasEnded = true;
            shutdown();

            playerRank.add(0, getWinner());

            return;
        }
    }

    @Override
    public synchronized void onPlayerBusted(BotPlayer player) {
        log.info("{} busted!", player);
        playerRank.add(0, player);
    }
}
