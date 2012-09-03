package se.cygni.texasholdem.client;

import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.GamePlayer;
import se.cygni.texasholdem.game.PlayerShowDown;
import se.cygni.texasholdem.game.definitions.PlayState;

import java.util.*;

public class CurrentPlayState {

    // Resetable values per Table
    private List<Card> myCards = new ArrayList<Card>(2);
    private List<Card> communityCards = new ArrayList<Card>(5);
    private PlayState currentPlayState = PlayState.PRE_FLOP;
    private Set<GamePlayer> foldedPlayers = new HashSet<GamePlayer>();
    private Set<GamePlayer> allInPlayers = new HashSet<GamePlayer>();
    private Set<GamePlayer> players = new HashSet<GamePlayer>();
    private long potTotal;
    private HashMap<GamePlayer, Long> potInvestmentPerPlayer = new HashMap<GamePlayer, Long>();
    private long smallBlind;
    private long bigBlind;
    private GamePlayer dealerPlayer;
    private GamePlayer smallBlindPlayer;
    private GamePlayer bigBlindPlayer;
    
    private long myCurrentChipAmount = 0;
    private String myPlayersName;

    private CurrentPlayStatePlayer dummy = new CurrentPlayStatePlayer();

    public CurrentPlayState(String myPlayersName) {
        this.myPlayersName = myPlayersName;
    }

    protected se.cygni.texasholdem.player.Player getPlayerImpl() {
        return dummy;
    }

    public List<Card> getMyCards() {
        return new ArrayList<Card>(myCards);
    }

    public List<Card> getCommunityCards() {
        return new ArrayList<Card>(communityCards);
    }

    public PlayState getCurrentPlayState() {
        return currentPlayState;
    }

    public long getPotTotal() {
        return potTotal;
    }

    public long getSmallBlind() {
        return smallBlind;
    }

    public long getBigBlind() {
        return bigBlind;
    }

    public GamePlayer getDealerPlayer() {
        return dealerPlayer;
    }

    public GamePlayer getSmallBlindPlayer() {
        return smallBlindPlayer;
    }

    public GamePlayer getBigBlindPlayer() {
        return bigBlindPlayer;
    }

    public long getMyCurrentChipAmount() {
        return myCurrentChipAmount;
    }

    public boolean hasPlayerFolded(GamePlayer player) {
        return foldedPlayers.contains(player);
    }

    public boolean hasPlayerGoneAllIn(GamePlayer player) {
        return allInPlayers.contains(player);
    }

    public long getInvestmentInPotFor(GamePlayer player) {
        if (!potInvestmentPerPlayer.containsKey(player))
            return 0;

        return potInvestmentPerPlayer.get(player);
    }

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
            if (event.getPlayer().getChipCount() == 0)
                allInPlayers.add(event.getPlayer());
        }

        @Override
        public void onPlayerRaised(PlayerRaisedEvent event) {
            addPotInvestmentToPlayer(event.getPlayer(), event.getRaiseBet());
            if (event.getPlayer().getChipCount() == 0)
                allInPlayers.add(event.getPlayer());
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
