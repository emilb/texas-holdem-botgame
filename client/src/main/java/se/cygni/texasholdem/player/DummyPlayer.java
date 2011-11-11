package se.cygni.texasholdem.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.message.event.CommunityHasBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.PlayIsStartedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerCalledEvent;
import se.cygni.texasholdem.communication.message.event.PlayerCheckedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerFoldedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerQuitEvent;
import se.cygni.texasholdem.communication.message.event.PlayerRaisedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerWentAllInEvent;
import se.cygni.texasholdem.communication.message.event.ServerIsShuttingDownEvent;
import se.cygni.texasholdem.communication.message.event.ShowDownEvent;
import se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.YouWonAmountEvent;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public class DummyPlayer implements PlayerInterface {

    private static Logger log = LoggerFactory
            .getLogger(DummyPlayer.class);

    private final String name = "dummmy" + (int) (100 * Math.random());

    @Override
    public String getName() {

        log.debug("Event: getName");
        return name;
    }

    @Override
    public void serverIsShuttingDown(final ServerIsShuttingDownEvent event) {

        log.debug(event.getMessage());

    }

    @Override
    public void onPlayIsStarted(final PlayIsStartedEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onYouHaveBeenDealtACard(final YouHaveBeenDealtACardEvent event) {

        log.debug("I've been dealt a card: " + event.getCard());

    }

    @Override
    public void onCommunityHasBeenDealtACard(
            final CommunityHasBeenDealtACardEvent event) {

        log.debug("Community got a card: " + event.getCard());
        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerFolded(final PlayerFoldedEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerCalled(final PlayerCalledEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerRaised(final PlayerRaisedEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerWentAllIn(final PlayerWentAllInEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerChecked(final PlayerCheckedEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onYouWonAmount(final YouWonAmountEvent event) {

        log.debug("I won: " + event.getWonAmount());

    }

    @Override
    public void onShowDown(final ShowDownEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerQuit(final PlayerQuitEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public Action onActionRequired(final ActionRequest request) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void connectionToGameServerLost() {

        log.debug("Event: connectionToGameServerLost");

    }

    @Override
    public void connectionToGameServerEstablished() {

        log.debug("Event: connectionToGameServerEstablished");

    }

}
