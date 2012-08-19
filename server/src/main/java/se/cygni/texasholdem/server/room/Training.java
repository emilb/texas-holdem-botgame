package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;

/**
 * Rooms manage players in tables and starts games when appropriate.
 * A player may be moved to another table when the Room deems necessary.
 */
public class Training extends Room {

    private static Logger log = LoggerFactory
            .getLogger(Training.class);

    BotPlayer player;
    Table table;

    Thread thread;

    public Training(EventBus eventBus, GamePlan gamePlan, SessionManager sessionManager) {
        super(eventBus, gamePlan, sessionManager);
    }

    @Override
    public void addPlayer(BotPlayer player) {

        this.player = player;

        table = new Table(gamePlan, this, eventBus, sessionManager);
        table.addPlayer(getTrainingPlayer());
        table.addPlayer(getTrainingPlayer());
        table.addPlayer(getTrainingPlayer());
        table.addPlayer(getTrainingPlayer());
        table.addPlayer(getPhilHellmuthPlayer());
        table.addPlayer(player);

        tables.add(table);

        thread = new Thread(table);
        thread.setName("Training - " + player.getName());
        thread.start();
    }

    @Override
    public void onTableGameDone(Table table) {
        log.debug("Training for player: " + player.getName() + " is done");
        eventBus.unregister(this);
        sessionManager.terminateSession(player);
    }

    @Override
    public void onPlayerBusted(BotPlayer player) {
    }
}