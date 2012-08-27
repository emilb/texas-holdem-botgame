package se.cygni.texasholdem.server.session;

import com.google.common.eventbus.Subscribe;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.eventbus.PlayerQuitEvent;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;
import se.cygni.texasholdem.server.room.Tournament;

import java.util.List;

public interface SessionManager {

    public static final String SESSION_ID = "SESSION_ID";

    public abstract TexasResponse sendAndWaitForResponse(
            final BotPlayer player,
            final TexasRequest request);

    public abstract void terminateSession(final BotPlayer player);

    @Subscribe
    public abstract void notifyPlayerOfEvent(final EventWrapper eventWrapper);

    @Subscribe
    public abstract void onPlayerQuit(final PlayerQuitEvent playerQuitEvent);

    @Subscribe
    public abstract void onRegisterForPlay(
            final RegisterForPlayWrapper requestWrapper);

    public int getNoofPlayers();

    public List<BotPlayer> listPlayers();

    public Tournament getAvailableTournament();

    public List<Tournament> listFinishedOrStartedTournaments();

    public Tournament getTournament(String id);

}
