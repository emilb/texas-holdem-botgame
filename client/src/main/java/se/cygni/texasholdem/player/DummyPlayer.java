package se.cygni.texasholdem.player;

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

    @Override
    public String getName() {

        System.out.println("Event: getName");
        return "dummmy";
    }

    @Override
    public void serverIsShuttingDown(final ServerIsShuttingDownEvent event) {

        System.out.println(event.message);

    }

    @Override
    public void onPlayIsStarted(final PlayIsStartedEvent event) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onYouHaveBeenDealtACard(final YouHaveBeenDealtACardEvent event) {

        System.out.println("I've been dealt a card: " + event.card);

    }

    @Override
    public void onCommunityHasBeenDealtACard(
            final CommunityHasBeenDealtACardEvent event) {

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

        // TODO Auto-generated method stub

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

        System.out.println("Event: connectionToGameServerLost");

    }

    @Override
    public void connectionToGameServerEstablished() {

        System.out.println("Event: connectionToGameServerEstablished");

    }

}
