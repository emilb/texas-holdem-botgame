package se.cygni.texasholdem.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.message.event.CommunityHasBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.PlayIsStartedEvent;
import se.cygni.texasholdem.communication.message.event.ShowDownEvent;
import se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.YouWonAmountEvent;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.communication.message.response.ActionResponse;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.ActionType;
import se.cygni.texasholdem.game.BestHand;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Deck;
import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.Player;
import se.cygni.texasholdem.game.PlayerShowDown;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.pot.Pot;
import se.cygni.texasholdem.game.pot.PotTransaction;
import se.cygni.texasholdem.game.util.GameUtil;
import se.cygni.texasholdem.game.util.PokerHandRankUtil;
import se.cygni.texasholdem.server.eventbus.EventBusUtil;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import com.google.common.eventbus.EventBus;

public class GameRound {

    private static Logger log = LoggerFactory
            .getLogger(GameRound.class);

    private final List<BotPlayer> players = Collections
            .synchronizedList(new ArrayList<BotPlayer>());

    private static final int NOOF_ACTION_RETRIES = 3;

    private final BotPlayer dealerPlayer;
    private final BotPlayer smallBlindPlayer;
    private final BotPlayer bigBlindPlayer;

    private final long smallBlind;
    private final long bigBlind;

    private final EventBus eventBus;
    private final SessionManager sessionManager;
    private final List<Card> communityCards = new ArrayList<Card>();
    private final Pot pot;

    private final Map<BotPlayer, Long> payoutResult = new HashMap<BotPlayer, Long>();
    private PokerHandRankUtil rankUtil;

    public GameRound(final List<BotPlayer> players,
            final BotPlayer dealerPlayer, final long smallBlind,
            final long bigBlind,
            final EventBus eventBus,
            final SessionManager sessionManager) {

        this.players.addAll(players);
        this.dealerPlayer = dealerPlayer;
        this.smallBlindPlayer = GameUtil.getNextPlayerInPlay(this.players,
                dealerPlayer);
        this.bigBlindPlayer = GameUtil.getNextPlayerInPlay(this.players,
                smallBlindPlayer);
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.eventBus = eventBus;
        this.sessionManager = sessionManager;
        pot = new Pot(this.players);
    }

    public void playGameRound() {

        // Shuffle the deck
        final Deck deck = Deck.getShuffledDeck();

        // Notify of start
        EventBusUtil.postToEventBus(eventBus, createPlayIsStartedEvent(),
                players);

        // The OPEN
        dealACardToAllParticipatingPlayers(deck);
        dealACardToAllParticipatingPlayers(deck);

        pot.bet(smallBlindPlayer, smallBlind);
        pot.bet(bigBlindPlayer, bigBlind);

        // Pre flop
        getBetsTillPotBalanced();

        // Flop
        pot.nextPlayState();
        burnAndDealCardsToCommunity(deck, 3);

        // Betting
        getBetsTillPotBalanced();

        // Turn
        pot.nextPlayState();
        burnAndDealCardsToCommunity(deck, 1);

        // Betting
        getBetsTillPotBalanced();

        // River
        pot.nextPlayState();
        burnAndDealCardsToCommunity(deck, 1);

        // Betting
        getBetsTillPotBalanced();

        // Showdown
        pot.nextPlayState();

        distributePayback();

        // Clear cards, prepare for next round
        clearAllCards();

        printTransactions();
    }

    private void printTransactions() {

        final StringBuilder sb = new StringBuilder();
        final Formatter formatter = new Formatter(sb);

        sb.append("\n\n** Transactions during game round **\n\n");
        sb.append("Small blind:        ").append(smallBlind);
        sb.append("\nBig blind:          ").append(bigBlind);
        sb.append("\nDealer:             ").append(dealerPlayer.getName());
        sb.append("\nSmall blind player: ").append(smallBlindPlayer.getName());
        sb.append("\nBig blind player:   ").append(bigBlindPlayer.getName())
                .append("\n");

        for (final PlayState state : PlayState.values()) {
            final List<PotTransaction> transactions = pot
                    .getTransactionsForState(state);

            sb.append("\nState: ").append(state).append("\n");

            if (transactions.size() > 0) {
                sb.append("Tx no  Player        Bet All in\n");

                for (final PotTransaction trans : transactions) {
                    formatter.format("%04d %10s %8d %s \n",
                            trans.getTransactionNumber(),
                            trans.getPlayer().getName(),
                            trans.getAmount(), trans.isAllIn());
                }
            } else {
                sb.append("No transactions\n");
            }
        }

        sb.append("\nGame round result:\n");
        formatter.format("%-10s %8s %-15s %-14s %-13s\n", "Player", "Won",
                "Hand",
                "Cards", "Comment");
        for (final Entry<BotPlayer, Long> entry : payoutResult.entrySet()) {

            final BotPlayer player = entry.getKey();
            final Long amount = entry.getValue();
            final BestHand bestHand = rankUtil.getBestHand(player);
            formatter.format("%-10s %8d %-15s %-14s %-6s %-6s \n",
                    player.getName(), amount,
                    bestHand.getPokerHand().getName(),
                    bestHand.cardsToShortString(),
                    (pot.hasFolded(player) ? "folded" : ""),
                    (pot.isAllIn(player) ? "all in" : ""));
        }

        sb.append("\nPlayer standing now:\n");
        formatter.format("%-10s %8s\n", "Player", "Cash");
        for (final BotPlayer player : players) {
            formatter.format("%-10s %8d\n", player.getName(),
                    player.getChipAmount());
        }

        sb.append("\n** ------------------------------ **\n");
        log.debug(sb.toString());
    }

    protected PlayIsStartedEvent createPlayIsStartedEvent() {

        final List<Player> currentPlayers = PlayerTypeConverter.listOfBotPlayers(players);

        return new PlayIsStartedEvent(currentPlayers,
                smallBlind, bigBlind,
                PlayerTypeConverter.fromBotPlayer(dealerPlayer),
                PlayerTypeConverter.fromBotPlayer(smallBlindPlayer),
                PlayerTypeConverter.fromBotPlayer(bigBlindPlayer));
    }

    protected void burnAndDealCardsToCommunity(
            final Deck deck,
            final int noofCards) {

        deck.burn();
        for (int i = 0; i < noofCards; i++) {
            final Card card = deck.getNextCard();
            communityCards.add(card);

            EventBusUtil.postToEventBus(eventBus,
                    new CommunityHasBeenDealtACardEvent(
                            card), players);
        }
    }

    protected void dealACardToAllParticipatingPlayers(final Deck deck) {

        final Iterator<BotPlayer> iter = players.iterator();
        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            if (GameUtil.playerHasChips(player)) {
                final Card card = deck.getNextCard();
                player.receiveCard(card);

                EventBusUtil.postToEventBus(eventBus,
                        new YouHaveBeenDealtACardEvent(card), player);
            }
        }
    }

    protected void doMandatoryBettingRound() {

        final List<BotPlayer> playerOrder = GameUtil
                .getOrderedListOfPlayersInPlay(players, bigBlindPlayer);

        for (final BotPlayer player : playerOrder) {
            if (pot.hasFolded(player))
                continue;

            final Action action = prepareAndGetActionFromPlayer(player);
            if (action != null)
                act(action, player);
            else
                pot.fold(player);
        }

    }

    protected void getBetsTillPotBalanced() {

        doMandatoryBettingRound();

        BotPlayer currentPlayer = bigBlindPlayer;
        while (!pot.isCurrentPlayStateBalanced()) {

            currentPlayer = GameUtil
                    .getNextPlayerInPlay(players, currentPlayer);

            if (currentPlayer == null && pot.isCurrentPlayStateBalanced())
                return;

            if (currentPlayer == null && !pot.isCurrentPlayStateBalanced())
                throw new AssertionError(
                        "Could not find next player and pot is not balanced!");

            if (pot.hasFolded(currentPlayer))
                continue;

            final Action action = prepareAndGetActionFromPlayer(currentPlayer);
            if (action != null)
                act(action, currentPlayer);

            if (pot.isCurrentPlayStateBalanced()
                    && currentPlayer.equals(bigBlindPlayer))
                return;
        }
    }

    protected void act(final Action action, final BotPlayer player) {

        switch (action.getActionType()) {
            case ALL_IN:
                pot.bet(player, player.getChipAmount());
                break;

            case CALL:
                log.debug("{} called", player.getName());
                pot.bet(player, action.getAmount());
                break;

            case CHECK:
                log.debug("{} checked", player.getName());
                break;

            case FOLD:
                pot.fold(player);
                break;

            case RAISE:
                pot.bet(player, action.getAmount());
                break;

            default:
                break;
        }
    }

    protected Action prepareAndGetActionFromPlayer(final BotPlayer player) {
        final List<Action> possibleActions = new ArrayList<Action>();
        possibleActions.add(new Action(ActionType.FOLD, 0));

        final long callAmount = pot.getAmountNeededToCall(player);
        if (callAmount > 0 && player.getChipAmount() > 0)
            possibleActions.add(new Action(ActionType.CALL, callAmount));
        else
            possibleActions.add(new Action(ActionType.CHECK, 0));

        if (player.getChipAmount() > 0) {
            possibleActions.add(new Action(ActionType.RAISE,
                    smallBlind > player.getChipAmount() ? player.getChipAmount() : smallBlind));

            possibleActions.add(new Action(ActionType.ALL_IN, player
                    .getChipAmount()));
        }

        int counter = 0;

        Action userAction = null;
        while (!isPlayerActionValid(player, possibleActions, userAction) && counter < NOOF_ACTION_RETRIES) {
            userAction = getActionFromPlayer(possibleActions, player);
            counter++;
        }

        return userAction;
    }

    protected boolean isPlayerActionValid(final BotPlayer player, final List<Action> possibleActions, Action action) {
        if (action == null)
            return false;

        if (action.getActionType() == null)
            return false;


        switch (action.getActionType()) {
            case FOLD:
                return true;

            case CHECK:
                return containsActionType(possibleActions, ActionType.CHECK);

            case CALL:
                if (!containsActionType(possibleActions, ActionType.CALL))
                    return false;

                Action allowedCall = getActionOfType(possibleActions, ActionType.CALL);
                return (action.getAmount() == allowedCall.getAmount());

            case RAISE:
                if (!containsActionType(possibleActions, ActionType.RAISE))
                    return false;

                Action allowedRaise = getActionOfType(possibleActions, ActionType.RAISE);
                return (action.getAmount() >= allowedRaise.getAmount());

            case ALL_IN:
                if (!containsActionType(possibleActions, ActionType.ALL_IN))
                    return false;

                Action allowedAllIn = getActionOfType(possibleActions, ActionType.ALL_IN);
                return (action.getAmount() >= allowedAllIn.getAmount());
        }

        return false;
    }

    protected Action getActionFromPlayer(final List<Action> possibleActions, final BotPlayer player) {
        try {
            final ActionRequest request = new ActionRequest(possibleActions);
            final ActionResponse response = (ActionResponse) sessionManager
                    .sendAndWaitForResponse(player, request);

            return response.getAction();

        } catch (final Exception e) {
            log.info("Player failed to respond, folding for this round", e);
            pot.fold(player);
            return new Action(ActionType.FOLD, 0);
        }
    }

    protected boolean containsActionType(final List<Action> possibleActions, ActionType actionType) {
        return getActionOfType(possibleActions, actionType) != null;
    }

    protected Action getActionOfType(final List<Action> possibleActions, ActionType actionType) {

        for (Action possibleAction : possibleActions) {
            if (possibleAction.getActionType() == actionType)
                return possibleAction;
        }

        return null;
    }

    protected void clearAllCards() {

        final Iterator<BotPlayer> iter = players.iterator();

        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            player.clearCards();
        }
    }

    protected void distributePayback() {

        rankUtil = new PokerHandRankUtil(
                communityCards, players);

        // Calculate player ranking
        final List<List<BotPlayer>> playerRanking = rankUtil
                .getPlayerRankings();

        final Map<BotPlayer, Long> payout = pot
                .calculatePayout(playerRanking);

        final List<PlayerShowDown> showDowns = new ArrayList<PlayerShowDown>();

        // Distribute payout
        for (final Entry<BotPlayer, Long> entry : payout.entrySet()) {

            final BotPlayer player = entry.getKey();
            final Long amount = entry.getValue();
            final BestHand bestHand = rankUtil.getBestHand(player);

            // Transfer funds
            log.debug("{} won {}", player, amount);
            player.addChipAmount(amount);

            EventBusUtil.postToEventBus(
                    eventBus,
                    new YouWonAmountEvent(amount, player.getChipAmount()),
                    player);

            final PlayerShowDown psd = new PlayerShowDown(
                    PlayerTypeConverter.fromBotPlayer(player),
                    new Hand(bestHand.getCards(), bestHand.getPokerHand()),
                    amount);
            showDowns.add(psd);
        }

        payoutResult.putAll(payout);

        final ShowDownEvent event = new ShowDownEvent(showDowns);
        EventBusUtil.postToEventBus(eventBus, event, players);

    }

    public void removePlayerFromGame(final BotPlayer player) {

        // TODO: How to handle if removed player was a dealer or blind player?
        pot.fold(player);
    }

    protected BotPlayer getDealerPlayer() {

        return dealerPlayer;
    }

    protected BotPlayer getSmallBlindPlayer() {

        return smallBlindPlayer;
    }

    protected BotPlayer getBigBlindPlayer() {

        return bigBlindPlayer;
    }

}
