package se.cygni.texasholdem.server.session;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.communication.message.exception.NoRoomSpecifiedException;
import se.cygni.texasholdem.communication.message.exception.UsernameAlreadyTakenException;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.ActionResponse;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.exception.InvalidNameException;
import se.cygni.texasholdem.game.trainingplayers.TrainingPlayer;
import se.cygni.texasholdem.game.util.ValidPlayerNameVerifier;
import se.cygni.texasholdem.server.communication.MessageSender;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.eventbus.PlayerQuitEvent;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;
import se.cygni.texasholdem.server.room.FreePlay;
import se.cygni.texasholdem.server.room.Tournament;
import se.cygni.texasholdem.server.room.Training;
import se.cygni.texasholdem.table.GamePlan;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {

    private static Logger log = LoggerFactory
            .getLogger(SessionManager.class);

    public static final String SESSION_ID = "SESSION_ID";

    private final EventBus eventBus;
    private final GamePlan gamePlan;
    private final MessageSender messageSender;

    private final Map<String, BotPlayer> sessionPlayerMap = new ConcurrentHashMap<String, BotPlayer>();
    private final Map<String, ChannelHandlerContext> sessionChannelHandlerContextMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    private final List<Tournament> tournaments = Collections
            .synchronizedList(new ArrayList<Tournament>());

    private final List<FreePlay> freePlays = Collections
            .synchronizedList(new ArrayList<FreePlay>());

    @Autowired
    public SessionManager(final EventBus eventBus,
                          final MessageSender messageSender,
                          final GamePlan gamePlan) {

        this.eventBus = eventBus;
        this.messageSender = messageSender;
        this.gamePlan = gamePlan;

        eventBus.register(this);

        initConnectionStatusTimer();
    }

    public TexasResponse sendAndWaitForResponse(
            final BotPlayer player,
            final TexasRequest request) {

        if (player instanceof TrainingPlayer && request instanceof ActionRequest) {
            Action action = ((TrainingPlayer) player).actionRequired((ActionRequest) request);
            ActionResponse response = new ActionResponse();
            response.setAction(action);
            return response;
        }

        final ChannelHandlerContext context = sessionChannelHandlerContextMap.get(player
                .getSessionId());

        if (context == null || !context.getChannel().isConnected()) {
            terminateSession(player);
            return null;
        }

        return messageSender.sendAndWaitForResponse(
                context, request);
    }

    @Subscribe
    public void notifyPlayerOfEvent(final EventWrapper eventWrapper) {

        for (final BotPlayer player : eventWrapper.getReceivers()) {
            if (player instanceof TrainingPlayer) {
                TrainingPlayer trainingPlayer = (TrainingPlayer) player;
                trainingPlayer.dispatchEvent(eventWrapper.getEvent());
                continue;
            }

            final ChannelHandlerContext context = sessionChannelHandlerContextMap.get(player
                    .getSessionId());

            if (context == null || !context.getChannel().isConnected()) {
                log.info("Player {} has unexpectedly left the game",
                        player.getName());
                eventBus.post(new PlayerQuitEvent(player));
                continue;
            }

            messageSender.sendMessage(
                    sessionChannelHandlerContextMap.get(player.getSessionId()),
                    eventWrapper.getEvent());
        }
    }

    public void terminateSession(BotPlayer player) {
        final String sessionId = player.getSessionId();

        ChannelHandlerContext context = sessionChannelHandlerContextMap.remove(sessionId);
        sessionPlayerMap.remove(sessionId);

        if (context == null) {
            return;
        }

        try {
            context.getChannel().disconnect();
        } catch (Exception e) {
            log.warn("Failed to disconnect player: " + player, e);
        }
    }

    @Subscribe
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {

        log.info("Player {} has left the game",
                playerQuitEvent.getPlayer().getName());

        terminateSession(playerQuitEvent.getPlayer());
    }

    @Subscribe
    public void onRegisterForPlay(final RegisterForPlayWrapper requestWrapper) {

        final ChannelHandlerContext clientContext = requestWrapper.getChannelHandlerContext();
        final RegisterForPlayRequest request = (RegisterForPlayRequest) requestWrapper
                .getRequest();

        // TODO: Store client ip, max 5 connections per ip by using clientContext.getChannel().getRemoteAddress()

        // Check that user name is valid
        try {
            ValidPlayerNameVerifier.verifyName(request.name);
        } catch (InvalidNameException ine) {
            final se.cygni.texasholdem.communication.message.exception.InvalidNameException e
                    = new se.cygni.texasholdem.communication.message.exception.InvalidNameException();
            e.message = ine.getMessage();
            e.setRequestId(request.getRequestId());
            messageSender.sendMessage(clientContext, e);
            return;
        }

        // Check that user name is not already in use
        if (!isNameUnique(request.name)) {
            final UsernameAlreadyTakenException e = new UsernameAlreadyTakenException();
            e.message = request.name + " is already used by another player.";
            e.setRequestId(request.getRequestId());
            messageSender.sendMessage(clientContext, e);
            return;
        }

        // Check that a valid Room has been choosen
        if (request.room == null) {
            final NoRoomSpecifiedException e = new NoRoomSpecifiedException();
            e.message = "No valid room was specified in RegisterForPlayRequest.";
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
        sessionChannelHandlerContextMap.put(sessionId, clientContext);

        log.info("New client connection registered. sessionId: {} name: {}",
                sessionId, player.getName());

        // Send login response to client
        messageSender.sendMessage(clientContext, response);

        switch (request.room) {
            case TRAINING:
                se.cygni.texasholdem.server.room.Room room = new Training(eventBus, gamePlan, this);
                room.addPlayer(player);
                break;

            case TOURNAMENT:
                addPlayerToAvailableTournament(player);
                break;

            case FREEPLAY:
                addPlayerToAvailableFreePlay(player);
                break;

            default:
                log.info("Player connected with invalid room definition, terminating");
                terminateSession(player);
        }

    }

    public List<Tournament> listFinishedOrStartedTournaments() {
        List<Tournament> finishedTournaments = new ArrayList<Tournament>();

        for (Tournament tournament : tournaments) {
            if (tournament.tournamentHasStarted() || tournament.tournamentHasEnded()) {
                finishedTournaments.add(tournament);
            }
        }

        return finishedTournaments;
    }

    private synchronized void addPlayerToAvailableFreePlay(BotPlayer player) {
        for (FreePlay freePlay : freePlays) {
            if (freePlay.addPlayer(player)) {
                return;
            }
        }

        // Didn't find a free room, create new
        FreePlay freePlay = new FreePlay(eventBus, gamePlan, this);
        freePlays.add(freePlay);
        freePlay.addPlayer(player);
    }

    private synchronized void addPlayerToAvailableTournament(BotPlayer player) {
        getAvailableTournament().addPlayer(player);
    }

    /**
     * There can only be one active (i.e not started) Tournament. If none
     * exists, a new is created.
     *
     * @return The current not started Tournament
     */
    public synchronized Tournament getAvailableTournament() {
        Tournament currentTournament = null;
        for (Tournament tournament : tournaments) {
            if (!tournament.tournamentHasStarted()) {
                currentTournament = tournament;
                break;
            }
        }

        if (currentTournament == null) {
            currentTournament = new Tournament(eventBus, gamePlan, this);
            tournaments.add(currentTournament);
        }
        return currentTournament;
    }

    public Tournament getTournament(String id) {
        for (Tournament tournament : tournaments) {
            if (tournament.getTournamentId().equals(id)) {
                return tournament;
            }
        }

        return null;
    }

    public int getNoofPlayers() {
        return sessionPlayerMap.values().size();
    }

    public List<BotPlayer> listPlayers() {
        List<BotPlayer> players = new ArrayList<BotPlayer>();
        players.addAll(
                sessionPlayerMap.values());

        return players;
    }

    private String createSessionId() {

        return UUID.randomUUID().toString();
    }

    private boolean isNameUnique(final String name) {

        for (final BotPlayer player : sessionPlayerMap.values()) {
            if (StringUtils.equalsIgnoreCase(name, player.getName())) {
                if (!sessionChannelHandlerContextMap.get(player.getSessionId())
                        .getChannel().isConnected()) {
                    eventBus.post(new PlayerQuitEvent(player));
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    private void initConnectionStatusTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<BotPlayer> disconnectedPlayers = new ArrayList<BotPlayer>();
                for (BotPlayer player : listPlayers()) {
                    if (!sessionChannelHandlerContextMap.get(player.getSessionId()).getChannel().isConnected()) {
                        log.debug("Found disconnected player: {}", player);
                        disconnectedPlayers.add(player);
                    }
                }

                for (BotPlayer player : disconnectedPlayers) {
                    eventBus.post(new PlayerQuitEvent(player));
                }
            }
        }, 50, 500);
        // delay, repeat every ms
    }
}
