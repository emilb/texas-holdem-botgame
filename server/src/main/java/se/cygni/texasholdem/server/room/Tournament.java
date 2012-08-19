package se.cygni.texasholdem.server.room;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.table.GamePlan;
import se.cygni.texasholdem.table.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tournament extends Room {

    private final List<BotPlayer> players = Collections
            .synchronizedList(new ArrayList<BotPlayer>());

    private boolean tournamentHasStarted = false;

    public Tournament(EventBus eventBus, GamePlan gamePlan, SessionManager sessionManager) {
        super(eventBus, gamePlan, sessionManager);
    }

    @Override
    public void addPlayer(BotPlayer player) {
        if (!tournamentHasStarted)
            playerPool.add(player);
    }

    public void startTournament() {

    }

    @Override
    public void onTableGameDone(Table table) {
    }

    @Override
    public void onPlayerBusted(BotPlayer player) {
    }
}
