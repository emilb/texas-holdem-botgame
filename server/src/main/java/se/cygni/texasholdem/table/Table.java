package se.cygni.texasholdem.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.message.event.CommunityHasBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.PlayIsStartedEvent;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.YouWonAmountEvent;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.ActionType;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Deck;
import se.cygni.texasholdem.game.Player;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.pot.Pot;
import se.cygni.texasholdem.game.util.PokerHandRankUtil;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import com.google.common.eventbus.EventBus;

public class Table implements Runnable {

    private static Logger log = LoggerFactory
            .getLogger(Table.class);

    public static final int MAX_NOOF_PLAYERS = 11;

    private final String tableId = UUID.randomUUID().toString();

    private final List<BotPlayer> players = new ArrayList<BotPlayer>();
    private final GamePlan gamePlan;
    private final EventBus eventBus;
    private final TableManager tableManager;

    private BotPlayer dealerPlayer;
    private BotPlayer smallBlindPlayer;
    private BotPlayer bigBlindPlayer;

    private long smallBlind;
    private long bigBlind;

    private Pot pot;

    private boolean gameHasStarted = false;
    private List<Card> communityCards;

    public Table(final GamePlan gamePlan, final TableManager tableManager,
            final EventBus eventBus) {

        this.gamePlan = gamePlan;
        this.tableManager = tableManager;
        this.eventBus = eventBus;
    }

    @Override
    public void run() {

        log.info("Starting the GAME!");
        gameHasStarted = true;

        smallBlind = gamePlan.getSmalBlindStart();
        bigBlind = gamePlan.getBigBlindStart();

        while (!isThereAWinner()) {
            pot = new Pot(players);
            communityCards = new ArrayList<Card>();

            // Shuffle the deck
            final Deck deck = Deck.getShuffledDeck();

            // Assign dealer, small and big blind player
            shiftRolesForPlayers();

            // Notify of start
            postToEventBus(createPlayIsStartedEvent(), players);

            // The OPEN
            dealACardToAllParticipatingPlayers(deck);
            dealACardToAllParticipatingPlayers(deck);

            pot.bet(smallBlindPlayer, smallBlind);
            pot.bet(bigBlindPlayer, bigBlind);

            // Pre flop
            doBettingRound();

            // Flop
            pot.nextPlayState();
            burnAndDealCardsToCommunity(deck, 3);

            // Betting
            doBettingRound();

            // Turn
            pot.nextPlayState();
            burnAndDealCardsToCommunity(deck, 1);

            // Betting
            doBettingRound();

            // River
            pot.nextPlayState();
            burnAndDealCardsToCommunity(deck, 1);

            // Betting
            doBettingRound();

            // Showdown
            pot.nextPlayState();

            // Distribute payback TODO: Only include active players
            final List<List<BotPlayer>> playerRanking = PokerHandRankUtil
                    .getPlayerRanking(getCommunityCards(), getPlayers());

            final Map<BotPlayer, Long> payout = pot
                    .calculatePayout(playerRanking);

            // TODO: Notify players of showdown

            // Distribute payout
            for (final Entry<BotPlayer, Long> entry : payout.entrySet()) {

                final BotPlayer player = entry.getKey();
                final Long amount = entry.getValue();

                // Transfer funds
                log.debug("{} won {}", player, amount);
                player.addChipAmount(amount);

                postToEventBus(
                        new YouWonAmountEvent(amount, player.getChipAmount()),
                        player);
            }

            // Clear cards, prepare for next round
            for (final BotPlayer player : players) {
                player.clearCards();
            }

            communityCards.clear();

            // TODO: is it time to increase blinds?

            // try {
            // Thread.sleep(500);
            // } catch (final InterruptedException e) {
            // }
        }

        log.info("Game is finished!");
        tableManager.onTableGameDone(this);
        eventBus.unregister(this);
    }

    protected void doBettingRound() {

        BotPlayer currentPlayer = bigBlindPlayer;
        while (!pot.isCurrentPlayStateBalanced()) {
            currentPlayer = getNextPlayerInPlay(currentPlayer);
            final Action action = getActionFromPlayer(currentPlayer);
            act(action, currentPlayer);
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

    protected Action getActionFromPlayer(final BotPlayer player) {

        final List<Action> possibleActions = new ArrayList<Action>();
        possibleActions.add(new Action(ActionType.FOLD, 0));
        possibleActions.add(new Action(ActionType.ALL_IN, player
                .getChipAmount()));
        final long callAmount = pot.getAmountNeededToCall(player);
        if (callAmount > 0)
            possibleActions.add(new Action(ActionType.CALL, callAmount));
        else
            possibleActions.add(new Action(ActionType.CHECK, 0));

        if (player.getChipAmount() > 0)
            possibleActions.add(new Action(ActionType.RAISE, player
                    .getChipAmount()));

        new ActionRequest(possibleActions);

        // TODO: Send the request

        // TODO: Remove fake reply
        if (callAmount > 0)
            return new Action(ActionType.CALL, callAmount);
        else
            return new Action(ActionType.CHECK, 0);
    }

    protected void postToEventBus(
            final TexasEvent event,
            final BotPlayer... players) {

        final List<BotPlayer> recipients = new ArrayList<BotPlayer>();
        for (final BotPlayer player : players)
            recipients.add(player);

        postToEventBus(event, recipients);
    }

    protected void postToEventBus(
            final TexasEvent event,
            final List<BotPlayer> recipients) {

        if (eventBus == null) {
            log.warn("EventBus is null, cannot publish events.");
            return;
        }

        eventBus.post(new EventWrapper(event, recipients));
    }

    protected void burnAndDealCardsToCommunity(
            final Deck deck,
            final int noofCards) {

        deck.burn();
        for (int i = 0; i < noofCards; i++) {
            final Card card = deck.getNextCard();
            communityCards.add(card);

            postToEventBus(new CommunityHasBeenDealtACardEvent(
                    card), players);
        }
    }

    protected void dealACardToAllParticipatingPlayers(final Deck deck) {

        for (final BotPlayer player : players) {
            if (isPlayerInPlay(player)) {
                final Card card = deck.getNextCard();
                player.receiveCard(card);

                postToEventBus(new YouHaveBeenDealtACardEvent(card), player);
            }
        }
    }

    protected void shiftRolesForPlayers() {

        dealerPlayer = getNextPlayerInPlay(dealerPlayer);
        smallBlindPlayer = getNextPlayerInPlay(dealerPlayer);
        bigBlindPlayer = getNextPlayerInPlay(smallBlindPlayer);
    }

    private BotPlayer getNextPlayerInPlay(final BotPlayer startingFromPlayer) {

        final int ix = startingFromPlayer == null ? -1 : players
                .indexOf(startingFromPlayer);

        // Start from where startingFromPlayer is positioned
        for (int currIx = ix + 1; currIx < players.size(); currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if (isPlayerInPlay(nextPlayer))
                return nextPlayer;
        }

        // Didn't find a player, start from beginning
        for (int currIx = 0; currIx < ix + 1; currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if (isPlayerInPlay(nextPlayer))
                return nextPlayer;
        }

        return null;
    }

    private boolean isPlayerInPlay(final BotPlayer player) {

        return player.getChipAmount() > 0;
    }

    protected boolean isThereAWinner() {

        int noofPlayersWithChipsLeft = 0;
        for (final BotPlayer player : players) {
            if (player.getChipAmount() > 0)
                noofPlayersWithChipsLeft++;

            if (noofPlayersWithChipsLeft > 1)
                return false;
        }

        return true;
    }

    protected PlayIsStartedEvent createPlayIsStartedEvent() {

        final List<Player> currentPlayers = new ArrayList<Player>();
        for (final BotPlayer player : players)
            if (isPlayerInPlay(player))
                currentPlayers.add(PlayerTypeConverter.fromBotPlayer(player));

        return new PlayIsStartedEvent(currentPlayers,
                getSmallBlind(), getBigBlind(),
                PlayerTypeConverter.fromBotPlayer(getDealerPlayer()),
                PlayerTypeConverter.fromBotPlayer(getSmallBlindPlayer()),
                PlayerTypeConverter.fromBotPlayer(getBigBlindPlayer()));
    }

    public void startGame() {

        gameHasStarted = true;
    }

    public boolean gameHasStarted() {

        return gameHasStarted;
    }

    public void addPlayer(final BotPlayer player) {

        players.add(player);
    }

    public void removePlayer(final BotPlayer player) {

        players.remove(player);
        if (pot != null)
            pot.fold(player);
    }

    public List<Card> getCardsForPlayer(final BotPlayer player) {

        return player.getCards();
    }

    public long getSmallBlind() {

        return smallBlind;
    }

    public long getBigBlind() {

        return bigBlind;
    }

    public long getPotAmount() {

        return pot.getTotalPotAmount();
    }

    public PlayState getPlayState() {

        return pot.getCurrentPlayState();
    }

    public List<BotPlayer> getPlayers() {

        return new ArrayList<BotPlayer>(players);
    }

    public int getNoofPlayers() {

        return players.size();
    }

    public BotPlayer getDealerPlayer() {

        return dealerPlayer;
    }

    public BotPlayer getSmallBlindPlayer() {

        return smallBlindPlayer;
    }

    public BotPlayer getBigBlindPlayer() {

        return bigBlindPlayer;
    }

    public List<Card> getCommunityCards() {

        return new ArrayList<Card>(communityCards);
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Table other = (Table) obj;
        if (tableId == null) {
            if (other.tableId != null)
                return false;
        } else if (!tableId.equals(other.tableId))
            return false;
        return true;
    }

    @Override
    protected void finalize() throws Throwable {

        log.info("\n\n\n\nTable is being finalized, good!\n\n\n");
        super.finalize();
    }

}
