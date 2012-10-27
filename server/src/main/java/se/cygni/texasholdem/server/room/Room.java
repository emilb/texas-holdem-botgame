package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.trainingplayers.*;
import se.cygni.texasholdem.server.eventbus.PlayerQuitEvent;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;

import java.util.*;

public abstract class Room {

    private static Logger log = LoggerFactory
            .getLogger(Room.class);

    protected final List<Table> tables = Collections
            .synchronizedList(new ArrayList<Table>());

    protected final Set<BotPlayer> playerPool = Collections
            .synchronizedSet(new HashSet<BotPlayer>());

    protected final EventBus eventBus;
    protected GamePlan gamePlan;
    protected final SessionManager sessionManager;

    private static long playerTrainerCounter = 0;

    public Room(final EventBus eventBus, final GamePlan gamePlan,
                final SessionManager sessionManager) {

        this.eventBus = eventBus;
        this.gamePlan = gamePlan;
        this.sessionManager = sessionManager;

        eventBus.register(this);
    }

    @Subscribe
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        final BotPlayer player = playerQuitEvent.getPlayer();

        playerPool.remove(player);

        log.debug("Player {} has quit, ordering Table to remove her", player);
        final Table table = getTableForPlayer(player);
        if (table != null) {
            table.removePlayer(player);
        }
        else {
            log.debug("Couldn't find table for user!?");
        }

    }

    public Table getTableForPlayer(final BotPlayer player) {

        return getTableForSessionId(player.getSessionId());
    }

    public Table getTableForSessionId(final String sessionId) {

        for (final Table table : tables) {
            for (final BotPlayer player : table.getPlayers()) {
                if (player.getSessionId().equals(sessionId)) {
                    return table;
                }
            }
        }
        return null;
    }

    protected List<BotPlayer> getPlayers() {
        List<BotPlayer> players = new ArrayList<BotPlayer>();
        players.addAll(playerPool);
        return players;
    }


    protected BotPlayer getWeightedPlayer() {
        return new WeightedPlayer("Weighted", UUID.randomUUID().toString(), gamePlan.getStartingChipsAmount());
    }

    protected BotPlayer getRaiserPlayer() {
        return new RaiserPlayer("Raiser", UUID.randomUUID().toString(), gamePlan.getStartingChipsAmount());
    }

    protected BotPlayer getSensiblePlayer() {
        return new RaiserPlayer("Sensible", UUID.randomUUID().toString(), gamePlan.getStartingChipsAmount());
    }

    protected TrainingPlayer getPhilHellmuthPlayer() {
        return new PhilHellmuthPlayer("Hellmuth", UUID.randomUUID().toString(), gamePlan.getStartingChipsAmount());
    }

    protected TrainingPlayer getCautiousPlayer() {
        return new CautiousPlayer("Cautious", UUID.randomUUID().toString(), gamePlan.getStartingChipsAmount());
    }

    public abstract boolean addPlayer(BotPlayer player);

    public abstract void onTableGameDone(Table table);

    public abstract void onPlayerBusted(BotPlayer player);
}


