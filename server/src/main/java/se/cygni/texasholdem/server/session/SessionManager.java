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

     static final String SESSION_ID = "SESSION_ID";

     abstract TexasResponse sendAndWaitForResponse(
            final BotPlayer player,
            final TexasRequest request);

     abstract void terminateSession(final BotPlayer player);

    @Subscribe
     abstract void notifyPlayerOfEvent(final EventWrapper eventWrapper);

    @Subscribe
     abstract void onPlayerQuit(final PlayerQuitEvent playerQuitEvent);

    @Subscribe
     abstract void onRegisterForPlay(
            final RegisterForPlayWrapper requestWrapper);

     int getNoofPlayers();

     List<BotPlayer> listPlayers();

     Tournament getAvailableTournament();

     List<Tournament> listFinishedOrStartedTournaments();

     Tournament getTournament(String id);

}
