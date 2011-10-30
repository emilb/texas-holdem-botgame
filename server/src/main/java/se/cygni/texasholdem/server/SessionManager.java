package se.cygni.texasholdem.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.codemonkey.swiftsocketserver.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.message.exception.UsernameAlreadyTakenException;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.eventbus.NewPlayerEvent;
import se.cygni.texasholdem.server.eventbus.RequestContextWrapper;
import se.cygni.texasholdem.table.GamePlan;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Service
public class SessionManager {

    private static Logger log = LoggerFactory
            .getLogger(SessionManager.class);

    private final EventBus eventBus;

    private final GamePlan gamePlan;

    private final MessageSender messageSender;

    private final Map<String, BotPlayer> sessionPlayerMap = new HashMap<String, BotPlayer>();
    private final Map<String, ClientContext> sessionClientContextMap = new HashMap<String, ClientContext>();

    @Autowired
    public SessionManager(final EventBus eventBus,
            final MessageSender messageSender,
            final GamePlan gamePlan) {

        this.eventBus = eventBus;
        this.messageSender = messageSender;
        this.gamePlan = gamePlan;

        eventBus.register(this);
    }

    @Subscribe
    public void notifyPlayerOfEvent(final EventWrapper eventWrapper) {

        log.debug("Notifying player of event");

        for (final BotPlayer player : eventWrapper.getReceivers()) {
            messageSender.sendMessage(
                    sessionClientContextMap.get(player.getSessionId()),
                    eventWrapper.getEvent());
        }
    }

    @Subscribe
    public void onRegisterForPlay(final RequestContextWrapper requestWrapper) {

        final ClientContext clientContext = requestWrapper.getClientContext();
        final RegisterForPlayRequest request = (RegisterForPlayRequest) requestWrapper
                .getRequest();

        // Check that user name is not already in use
        if (!isNameUnique(request.name)) {
            final UsernameAlreadyTakenException e = new UsernameAlreadyTakenException();
            e.message = request.name + " is already used by another player.";
            e.requestId = request.requestId;
            messageSender.sendMessage(clientContext, e);
            return;
        }

        final String sessionId = createSessionId();

        // Create response
        final RegisterForPlayResponse response = new RegisterForPlayResponse();
        response.requestId = request.requestId;
        response.sessionId = sessionId;

        // Create the bot player
        final BotPlayer player = new BotPlayer(request.name, sessionId,
                gamePlan.getStartingChipsAmount());

        sessionPlayerMap.put(sessionId, player);
        sessionClientContextMap.put(sessionId, clientContext);

        log.debug("New client connection registered. sessionId: {} name: {}",
                sessionId, player.getName());

        // Send login response to client
        messageSender.sendMessage(clientContext, response);

        // Notify of new player
        eventBus.post(new NewPlayerEvent(player));
    }

    private String createSessionId() {

        return UUID.randomUUID().toString();
    }

    private boolean isNameUnique(final String name) {

        for (final BotPlayer player : sessionPlayerMap.values()) {
            if (StringUtils.equalsIgnoreCase(name, player.getName()))
                return false;
        }
        return true;
    }
}
