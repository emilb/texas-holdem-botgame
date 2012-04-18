package se.cygni.texasholdem.server.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.codemonkey.swiftsocketserver.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.message.exception.UsernameAlreadyTakenException;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.communication.MessageSender;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.eventbus.NewPlayerEvent;
import se.cygni.texasholdem.server.eventbus.PlayerQuitEvent;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;
import se.cygni.texasholdem.table.GamePlan;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Service
public class SessionManagerRemote implements SessionManager {

    private static Logger log = LoggerFactory
            .getLogger(SessionManagerRemote.class);

    private final EventBus eventBus;
    private final GamePlan gamePlan;
    private final MessageSender messageSender;

    private final Map<String, BotPlayer> sessionPlayerMap = new ConcurrentHashMap<String, BotPlayer>();
    private final Map<String, ClientContext> sessionClientContextMap = new ConcurrentHashMap<String, ClientContext>();

    @Autowired
    public SessionManagerRemote(final EventBus eventBus,
            final MessageSender messageSender,
            final GamePlan gamePlan) {

        this.eventBus = eventBus;
        this.messageSender = messageSender;
        this.gamePlan = gamePlan;

        eventBus.register(this);
    }

    @Override
    public TexasResponse sendAndWaitForResponse(
            final BotPlayer player,
            final TexasRequest request) {

        final ClientContext context = sessionClientContextMap.get(player
                .getSessionId());

        return messageSender.sendAndWaitForResponse(
                context, request);
    }

    @Subscribe
    @Override
    public void notifyPlayerOfEvent(final EventWrapper eventWrapper) {

        // log.debug("Notifying players {} of event: {}",
        // eventWrapper.getReceivers(), eventWrapper.getEvent());

        for (final BotPlayer player : eventWrapper.getReceivers()) {

            final ClientContext context = sessionClientContextMap.get(player
                    .getSessionId());

            if (context == null || !context.isActive()) {
                log.info("Player {} has unexpectedly left the game",
                        player.getName());
                eventBus.post(new PlayerQuitEvent(player));
                continue;
            }

            messageSender.sendMessage(
                    sessionClientContextMap.get(player.getSessionId()),
                    eventWrapper.getEvent());
        }
    }

    @Subscribe
    @Override
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {

        log.info("Player {} has left the game",
                playerQuitEvent.getPlayer().getName());

        final String sessionId = playerQuitEvent.getPlayer().getSessionId();

        sessionPlayerMap.remove(sessionId);
        sessionClientContextMap.remove(sessionId);

    }

    @Subscribe
    @Override
    public void onRegisterForPlay(final RegisterForPlayWrapper requestWrapper) {

        final ClientContext clientContext = requestWrapper.getClientContext();
        final RegisterForPlayRequest request = (RegisterForPlayRequest) requestWrapper
                .getRequest();

        // Check that user name is not already in use
        if (!isNameUnique(request.name)) {
            final UsernameAlreadyTakenException e = new UsernameAlreadyTakenException();
            e.message = request.name + " is already used by another player.";
            e.setRequestId(request.getRequestId());
            messageSender.sendMessage(clientContext, e);
            return;
        }

        final String sessionId = createSessionId();

        // Create response
        final RegisterForPlayResponse response = new RegisterForPlayResponse();
        response.setRequestId(request.getRequestId());
        response.sessionId = sessionId;

        // Create the bot player
        final BotPlayer player = new BotPlayer(request.name, sessionId,
                gamePlan.getStartingChipsAmount());

        sessionPlayerMap.put(sessionId, player);
        sessionClientContextMap.put(sessionId, clientContext);

        log.debug("New client connection registered. sessionId: {} name: {}",
                sessionId, player.getName());

        // Store the sessionId in the context
        clientContext.getSessionData().put(SESSION_ID, sessionId);

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
            if (StringUtils.equalsIgnoreCase(name, player.getName())) {
                if (!sessionClientContextMap.get(player.getSessionId())
                        .isActive()) {
                    eventBus.post(new PlayerQuitEvent(player));
                    return true;
                }
                return false;
            }
        }
        return true;
    }
}
