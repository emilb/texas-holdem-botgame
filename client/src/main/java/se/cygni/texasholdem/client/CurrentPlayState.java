package se.cygni.texasholdem.client;

import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.GamePlayer;
import se.cygni.texasholdem.game.PlayerShowDown;
import se.cygni.texasholdem.game.definitions.PlayState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A helper for keeping tabs on the current state of a game round
 */
public class CurrentPlayState {

    // Values are reset per GameRound
    private List<Card> myCards = Collections.synchronizedList(new ArrayList<Card>(2));
    private List<Card> communityCards = Collections.synchronizedList(new ArrayList<Card>(5));
    private PlayState currentPlayState = PlayState.PRE_FLOP;
    private Set<GamePlayer> foldedPlayers = Collections.synchronizedSet(new HashSet<GamePlayer>());
    private Set<GamePlayer> allInPlayers = Collections.synchronizedSet(new HashSet<GamePlayer>());
    private Set<GamePlayer> players = Collections.synchronizedSet(new HashSet<GamePlayer>());
    private long potTotal;
    private Map<GamePlayer, Long> potInvestmentPerPlayer = new ConcurrentHashMap<GamePlayer, Long>();
    private long smallBlind;
    private long bigBlind;
    private GamePlayer dealerPlayer;
    private GamePlayer smallBlindPlayer;
    private GamePlayer bigBlindPlayer;

    // Values that are kept between GameRounds
    private long myCurrentChipAmount = 0;
    private String myPlayersName;

    private CurrentPlayStatePlayer dummy = new CurrentPlayStatePlayer();

    public CurrentPlayState(String myPlayersName) {
        this.myPlayersName = myPlayersName;
    }

    protected se.cygni.texasholdem.player.Player getPlayerImpl() {
        return dummy;
    }

    /**
     * A list of your current cards
     *
     * @return your List<Card> of cards
     * @see Card
     */
    public List<Card> getMyCards() {
        return new ArrayList<Card>(myCards);
    }

    /**
     * The list of cards played to the community so far.
     *
     * @return List<Card> of currently played community cards
     * @see Card
     */
    public List<Card> getCommunityCards() {
        return new ArrayList<Card>(communityCards);
    }

    /**
     * The list of your two cards and the available community
     * cards.
     *
     * @return List<Card> of your cards and the currently played community cards
     * @see Card
     */
    public List<Card> getMyCardsAndCommunityCards() {
        List<Card> cards = new ArrayList<Card>(myCards);
        cards.addAll(communityCards);
        return cards;
    }

    /**
     * The current state the game play is in.
     * @return PlayState
     * @see CurrentPlayState
     */
    public PlayState getCurrentPlayState() {
        return currentPlayState;
    }

    /**
     * The sum of all bets placed during this round
     * @return The long value of the sum of all bets placed
     */
    public long getPotTotal() {
        return potTotal;
    }

    /**
     * The small blind bet this game round
     * @return The long value of the current small blind bet
     */
    public long getSmallBlind() {
        return smallBlind;
    }

    /**
     * The big blind bet this game round
     * @return The long value of the current big blind bet
     */
    public long getBigBlind() {
        return bigBlind;
    }

    /**
     * The player that acts as dealer during this game round
     * @return the Dealer player
     * @see GamePlayer
     */
    public GamePlayer getDealerPlayer() {
        return dealerPlayer;
    }

    /**
     * The player that is small blind better during this game round
     * @return the small blind player
     * @see GamePlayer
     */
    public GamePlayer getSmallBlindPlayer() {
        return smallBlindPlayer;
    }

    /**
     * The player that big blind better during this game round
     * @return the big blind player
     * @see GamePlayer
     */
    public GamePlayer getBigBlindPlayer() {
        return bigBlindPlayer;
    }

    /**
     * The amount of chips you have left
     * @return the long value of your current amount of chips
     */
    public long getMyCurrentChipAmount() {
        return myCurrentChipAmount;
    }

    /**
     *
     * @param player
     * @return True if player has folded this game round
     */
    public boolean hasPlayerFolded(GamePlayer player) {
        return foldedPlayers.contains(player);
    }

    /**
     *
     * @param player
     * @return True if player has gone all in this game round
     */
    public boolean hasPlayerGoneAllIn(GamePlayer player) {
        return allInPlayers.contains(player);
    }

    /**
     * Gives the total amount a player has invested in the pot
     * during this game round.
     * @param player
     * @return the long value of the chip amount this player has invested in the pot
     */
    public long getInvestmentInPotFor(GamePlayer player) {
        if (!potInvestmentPerPlayer.containsKey(player)) {
            return 0;
        }

        return potInvestmentPerPlayer.get(player);
    }

    /**
     *
     * @return A List<GamePlayer> participating in this game round
     */
    public List<GamePlayer> getPlayers() {
        return new ArrayList<GamePlayer>(players);
    }

    private void reset() {
        myCards.clear();
        communityCards.clear();
        currentPlayState = PlayState.PRE_FLOP;
        foldedPlayers.clear();
        allInPlayers.clear();
        players.clear();
        potTotal = 0L;
        potInvestmentPerPlayer.clear();
        smallBlind = 0L;
        bigBlind = 0L;
        dealerPlayer = null;
        smallBlindPlayer = null;
        bigBlindPlayer = null;
    }

    private void addPotInvestmentToPlayer(GamePlayer player, long amount) {
        potTotal += amount;

        if (!potInvestmentPerPlayer.containsKey(player)) {
            potInvestmentPerPlayer.put(player, new Long(0));
        }

        Long prevInv = potInvestmentPerPlayer.get(player);
        potInvestmentPerPlayer.put(player, new Long(prevInv + amount));
    }



    private class CurrentPlayStatePlayer implements se.cygni.texasholdem.player.Player {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public void serverIsShuttingDown(ServerIsShuttingDownEvent event) {
        }

        @Override
        public void onPlayIsStarted(PlayIsStartedEvent event) {
            reset();

            players.addAll(event.getPlayers());
            dealerPlayer = event.getDealer();
            smallBlindPlayer = event.getSmallBlindPlayer();
            bigBlindPlayer = event.getBigBlindPlayer();
            smallBlind = event.getSmallBlindAmount();
            bigBlind = event.getBigBlindAmount();
        }

        @Override
        public void onTableChangedStateEvent(TableChangedStateEvent event) {
            currentPlayState = event.getState();
        }

        @Override
        public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event) {
            myCards.add(event.getCard());
        }

        @Override
        public void onCommunityHasBeenDealtACard(CommunityHasBeenDealtACardEvent event) {
            communityCards.add(event.getCard());
        }

        @Override
        public void onPlayerBetBigBlind(PlayerBetBigBlindEvent event) {
            addPotInvestmentToPlayer(event.getPlayer(), event.getBigBlind());
        }

        @Override
        public void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event) {
            addPotInvestmentToPlayer(event.getPlayer(), event.getSmallBlind());
        }

        @Override
        public void onPlayerFolded(PlayerFoldedEvent event) {
            foldedPlayers.add(event.getPlayer());
        }

        @Override
        public void onPlayerCalled(PlayerCalledEvent event) {
            addPotInvestmentToPlayer(event.getPlayer(), event.getCallBet());
            if (event.getPlayer().getChipCount() == 0) {
                allInPlayers.add(event.getPlayer());
            }
        }

        @Override
        public void onPlayerRaised(PlayerRaisedEvent event) {
            addPotInvestmentToPlayer(event.getPlayer(), event.getRaiseBet());
            if (event.getPlayer().getChipCount() == 0) {
                allInPlayers.add(event.getPlayer());
            }
        }

        @Override
        public void onPlayerWentAllIn(PlayerWentAllInEvent event) {
            addPotInvestmentToPlayer(event.getPlayer(), event.getAllInAmount());
            allInPlayers.add(event.getPlayer());
        }

        @Override
        public void onPlayerChecked(PlayerCheckedEvent event) {
        }

        @Override
        public void onYouWonAmount(YouWonAmountEvent event) {
        }

        @Override
        public void onShowDown(ShowDownEvent event) {
            for (PlayerShowDown psd : event.getPlayersShowDown()) {
                players.add(psd.getPlayer());

                if (myPlayersName.equals(psd.getPlayer().getName())) {
                    myCurrentChipAmount = psd.getPlayer().getChipCount();
                }
            }
        }

        @Override
        public void onTableIsDone(TableIsDoneEvent event) {
        }

        @Override
        public void onPlayerQuit(PlayerQuitEvent event) {
        }

        @Override
        public Action actionRequired(ActionRequest request) {
            return null;
        }

        @Override
        public void connectionToGameServerLost() {
        }

        @Override
        public void connectionToGameServerEstablished() {
        }
    }
}
