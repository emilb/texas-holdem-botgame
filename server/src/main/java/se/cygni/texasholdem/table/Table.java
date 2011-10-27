package se.cygni.texasholdem.table;

import java.util.ArrayList;
import java.util.List;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Deck;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.pot.Pot;
import se.cygni.texasholdem.server.GameServer;

public class Table implements Runnable {

    public static final int MAX_NOOF_PLAYERS = 11;

    private final List<BotPlayer> players = new ArrayList<BotPlayer>();
    private final GamePlan gamePlan;
    private final GameServer gameServer;

    private BotPlayer dealerPlayer;
    private BotPlayer smallBlindPlayer;
    private BotPlayer bigBlindPlayer;

    private long smallBlind;
    private long bigBlind;

    private Pot pot;

    private boolean gameHasStarted = false;
    private List<Card> communityCards;

    public Table(final GamePlan gamePlan, final GameServer gameServer) {

        this.gamePlan = gamePlan;
        this.gameServer = gameServer;
    }

    @Override
    public void run() {

        System.out.println("Starting the GAME!");
        gameHasStarted = true;

        while (!isThereAWinner()) {
            pot = new Pot(players);
            communityCards = new ArrayList<Card>();
            final Deck deck = Deck.getShuffledDeck();

            // The OPEN
            dealACardToAllParticipatingPlayers(deck);
            dealACardToAllParticipatingPlayers(deck);

            pot.bet(smallBlindPlayer, smallBlind);
            pot.bet(bigBlindPlayer, bigBlind);

            // Do betting rounds

            // Flop
            pot.nextPlayState();
            burnAndDealCardsToCommunity(deck, 3);

            // Betting

            // Turn
            pot.nextPlayState();
            burnAndDealCardsToCommunity(deck, 1);

            // Betting

            // River
            pot.nextPlayState();
            burnAndDealCardsToCommunity(deck, 1);

            // Betting

            // Showdown
            pot.nextPlayState();

            // Distribute payback
        }

    }

    protected void burnAndDealCardsToCommunity(
            final Deck deck,
            final int noofCards) {

        deck.burn();
        for (int i = 0; i < noofCards; i++)
            communityCards.add(deck.getNextCard());
    }

    protected void dealACardToAllParticipatingPlayers(final Deck deck) {

        for (final BotPlayer player : players) {
            if (isPlayerInPlay(player)) {
                final Card card = deck.getNextCard();
                player.receiveCard(card);
                gameServer.onYouHaveBeenDealtACard(player, card);
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

}
