package se.cygni.texasholdem.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.eventbus.NewPlayerEvent;
import se.cygni.texasholdem.server.eventbus.PlayerQuitEvent;
import se.cygni.texasholdem.server.session.SessionManager;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Component
public class TableManager {

    private static Logger log = LoggerFactory
            .getLogger(TableManager.class);

    private final List<Table> tables = Collections
            .synchronizedList(new ArrayList<Table>());

    private final Set<BotPlayer> playerPool = Collections
            .synchronizedSet(new HashSet<BotPlayer>());

    private final EventBus eventBus;
    private final GamePlan gamePlan;
    private final SessionManager sessionManager;

    @Autowired
    public TableManager(final EventBus eventBus, final GamePlan gamePlan,
            final SessionManager sessionManager) {

        this.eventBus = eventBus;
        this.gamePlan = gamePlan;
        this.sessionManager = sessionManager;

        eventBus.register(this);
    }

    @Subscribe
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {

        final BotPlayer player = playerQuitEvent.getPlayer();
        final Table table = getTableForPlayer(player);
        if (table != null)
            table.removePlayer(player);

    }

    @Subscribe
    public void onNewPlayerEvent(final NewPlayerEvent event) {

        assignPlayerToFreeTable(event.getPlayer());
    }

    public void onTableGameDone(final Table table) {

        log.info("A table game is finished, removing table and returning players to pool");
        tables.remove(table);
        playerPool.addAll(table.getPlayers());
    }

    public Table getTableForPlayer(final BotPlayer player) {

        return getTableForSessionId(player.getSessionId());
    }

    public Table getTableForSessionId(final String sessionId) {

        for (final Table table : tables) {
            for (final BotPlayer player : table.getPlayers()) {
                if (player.getSessionId().equals(sessionId))
                    return table;
            }
        }
        return null;
    }

    public void assignPlayerToFreeTable(final BotPlayer player) {

        if (tables.size() == 0) {
            //createNewTable().addPlayer(player);
            return;
        }

        Table freeTableWithLowestNoofPlayers = null;

        for (final Table table : tables) {
            if (table.gameHasStarted())
                continue;

            if (table.getNoofPlayers() < Table.MAX_NOOF_PLAYERS && (
                    freeTableWithLowestNoofPlayers == null ||
                    freeTableWithLowestNoofPlayers.getNoofPlayers() > table
                            .getNoofPlayers())) {
                freeTableWithLowestNoofPlayers = table;
            }
        }

        if (freeTableWithLowestNoofPlayers != null) {
            freeTableWithLowestNoofPlayers.addPlayer(player);

            if (freeTableWithLowestNoofPlayers.getNoofPlayers() > 3) {
                log.info("Starting game on table!");
                final Thread t = new Thread(freeTableWithLowestNoofPlayers);
                t.start();
            }
            return;
        }

        // Couldn't find a free table, start a new!
//        createNewTable().addPlayer(player);
    }

//    private Table createNewTable() {
//
//        final Table table = new Table(gamePlan, this, eventBus, sessionManager);
//        tables.add(table);
//        return table;
//    }
}
