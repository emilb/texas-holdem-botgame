package se.cygni.texasholdem.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.communication.message.event.*;
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
    public void onTableChangedStateEvent(TableChangedStateEvent event) {

        log.debug("Table changed state: {}", event.getState());
    }

    @Override
    public void onYouHaveBeenDealtACard(final YouHaveBeenDealtACardEvent event) {

        log.debug("I, {}, got a card: {}", getName(), event.getCard());
    }

    @Override
    public void onCommunityHasBeenDealtACard(
            final CommunityHasBeenDealtACardEvent event) {

        log.debug("Community got a card: {}", event.getCard());

    }

    @Override
    public void onPlayerBetBigBlind(PlayerBetBigBlindEvent event) {

        log.debug("{} placed big blind with amount {}", event.getPlayer().getName(), event.getBigBlind());
    }

    @Override
    public void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event) {

        log.debug("{} placed small blind with amount {}", event.getPlayer().getName(), event.getSmallBlind());
    }

    @Override
    public void onPlayerFolded(final PlayerFoldedEvent event) {

        log.debug("{} folded after putting {} in the pot", event.getPlayer().getName(), event.getInvestmentInPot());
    }

    @Override
    public void onPlayerCalled(final PlayerCalledEvent event) {

        log.debug("{} called with amount {}", event.getPlayer().getName(), event.getCallBet());

    }

    @Override
    public void onPlayerRaised(final PlayerRaisedEvent event) {

        log.debug("{} raised with bet {}", event.getPlayer().getName(), event.getRaiseBet());

    }

    @Override
    public void onTableIsDone(TableIsDoneEvent event) {

        log.debug("Table is done!");

    }

    @Override
    public void onPlayerWentAllIn(final PlayerWentAllInEvent event) {

        log.debug("{} went all in with amount {}", event.getPlayer().getName(), event.getAllInAmount());

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
