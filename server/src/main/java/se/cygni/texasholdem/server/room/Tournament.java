package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;

public class Tournament extends Room {

    @Autowired
    public Tournament(EventBus eventBus, GamePlan gamePlan, SessionManager sessionManager) {
        super(eventBus, gamePlan, sessionManager);
    }

    @Override
    public void addPlayer(BotPlayer player) {
    }

    @Override
    public void onTableGameDone(Table table) {
    }
}
