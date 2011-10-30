package se.cygni.texasholdem.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;
import se.cygni.texasholdem.server.SessionManager;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.eventbus.NewPlayerEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Component
public class TableManager {

    private static final int MAX_PLAYERS_PER_TABLE = 10;

    private final List<Table> tables = new ArrayList<Table>();

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
    public void onNewPlayerEvent(final NewPlayerEvent event) {

        assignPlayerToFreeTable(event.getPlayer());

        final YouHaveBeenDealtACardEvent evc = new YouHaveBeenDealtACardEvent();
        evc.card = Card.valueOf(Rank.ACE, Suit.SPADES);
        final EventWrapper eventw = new EventWrapper(evc, event.getPlayer());
        eventBus.post(eventw);
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
            createNewTable().addPlayer(player);
            return;
        }

        Table freeTableWithLowestNoofPlayers = null;

        for (final Table table : tables) {
            if (table.gameHasStarted())
                continue;

            if (table.getNoofPlayers() < MAX_PLAYERS_PER_TABLE && (
                    freeTableWithLowestNoofPlayers == null ||
                    freeTableWithLowestNoofPlayers.getNoofPlayers() > table
                            .getNoofPlayers())) {
                freeTableWithLowestNoofPlayers = table;
            }
        }

        if (freeTableWithLowestNoofPlayers != null) {
            freeTableWithLowestNoofPlayers.addPlayer(player);

            if (freeTableWithLowestNoofPlayers.getNoofPlayers() > 1) {
                System.out.println("Starting game on table!");
                final Thread t = new Thread(freeTableWithLowestNoofPlayers);
                t.start();
            }
            return;
        }

        // Couldn't find a free table, start a new!
        createNewTable().addPlayer(player);
    }

    private Table createNewTable() {

        final Table table = new Table(gamePlan);
        tables.add(table);
        return table;
    }
}
