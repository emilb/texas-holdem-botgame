package se.cygni.texasholdem.server.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.profile.UnitTestProfile;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.eventbus.PlayerQuitEvent;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;
import se.cygni.texasholdem.table.GamePlan;

import com.google.common.eventbus.EventBus;

@UnitTestProfile
@Service
public class SessionManagerLocal implements SessionManager {

    private final EventBus eventBus;
    private final GamePlan gamePlan;

    @Autowired
    public SessionManagerLocal(final EventBus eventBus,
            final GamePlan gamePlan) {

        System.out.println("Created: SessionManagerLocal");

        this.eventBus = eventBus;
        this.gamePlan = gamePlan;

        eventBus.register(this);
    }

    @Override
    public TexasResponse sendAndWaitForResponse(
            final BotPlayer player,
            final TexasRequest request) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void notifyPlayerOfEvent(final EventWrapper eventWrapper) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onRegisterForPlay(final RegisterForPlayWrapper requestWrapper) {

        // TODO Auto-generated method stub

    }

}
