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
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.PlayerShowDown;

public abstract class BasicPlayer implements Player {

    private static Logger log = LoggerFactory
            .getLogger(BasicPlayer.class);

    @Override
    public void serverIsShuttingDown(final ServerIsShuttingDownEvent event) {

    }

    @Override
    public void onPlayIsStarted(final PlayIsStartedEvent event) {

    }

    @Override
    public void onYouHaveBeenDealtACard(final YouHaveBeenDealtACardEvent event) {

        log.debug("I, {}, got a card: {}", getName(), event.getCard());
    }

    @Override
    public void onCommunityHasBeenDealtACard(
            final CommunityHasBeenDealtACardEvent event) {

    }

    @Override
    public void onPlayerFolded(final PlayerFoldedEvent event) {

        log.debug("{} folded", event.getPlayer().getName());
    }

    @Override
    public void onPlayerCalled(final PlayerCalledEvent event) {

        log.debug("{} called", event.getPlayer().getName());

    }

    @Override
    public void onPlayerRaised(final PlayerRaisedEvent event) {

        log.debug("{} raised", event.getPlayer().getName());

    }

    @Override
    public void onPlayerWentAllIn(final PlayerWentAllInEvent event) {

        log.debug("{} went all in", event.getPlayer().getName());

    }

    @Override
    public void onPlayerChecked(final PlayerCheckedEvent event) {

        log.debug("{} checked", event.getPlayer().getName());

    }

    @Override
    public void onYouWonAmount(final YouWonAmountEvent event) {

        log.debug("I, {}, won: {}", getName(), event.getWonAmount());

    }

    @Override
    public void onShowDown(final ShowDownEvent event) {

        final StringBuilder sb = new StringBuilder();
        sb.append("ShowDown:\n");
        for (final PlayerShowDown psd : event.getPlayersShowDown()) {
            sb.append(psd.getPlayer().getName()).append("\thand: ");
            sb.append(psd.getHand().getPokerHand().getName());
            sb.append("\t");
            sb.append(" cards: | ");
            for (final Card card : psd.getHand().getCards()) {
                sb.append(card).append(" | ");
            }
            sb.append("\n");
        }

        log.debug(sb.toString());
    }

    @Override
    public void onPlayerQuit(final PlayerQuitEvent event) {

    }

    @Override
    public void connectionToGameServerLost() {

    }

    @Override
    public void connectionToGameServerEstablished() {

    }

}
