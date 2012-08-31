package se.cygni.texasholdem.client;

import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Player;
import se.cygni.texasholdem.game.PlayerShowDown;
import se.cygni.texasholdem.game.definitions.PlayState;

import java.util.*;

public class CurrentPlayState {

    // Resetable values per Table
    private List<Card> myCards = new ArrayList<Card>(2);
    private List<Card> communityCards = new ArrayList<Card>(5);
    private PlayState currentPlayState = PlayState.PRE_FLOP;
    private Set<Player> foldedPlayers = new HashSet<Player>();
    private Set<Player> allInPlayers = new HashSet<Player>();
    private Set<Player> players = new HashSet<Player>();
    private long potTotal;
    private HashMap<Player, Long> potInvestmentPerPlayer = new HashMap<Player, Long>();
    private long smallBlind;
    private long bigBlind;
    private Player dealerPlayer;
    private Player smallBlindPlayer;
    private Player bigBlindPlayer;
    
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
        return myCards;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
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

    public Player getDealerPlayer() {
        return dealerPlayer;
    }

    public Player getSmallBlindPlayer() {
        return smallBlindPlayer;
    }

    public Player getBigBlindPlayer() {
        return bigBlindPlayer;
    }

    public long getMyCurrentChipAmount() {
        return myCurrentChipAmount;
    }

    public boolean hasPlayerFolded(Player player) {
        return foldedPlayers.contains(player);
    }

    public boolean hasPlayerGoneAllIn(Player player) {
        return allInPlayers.contains(player);
    }

    public long getInvestmentInPotFor(Player player) {
        if (!potInvestmentPerPlayer.containsKey(player))
            return 0;

        return potInvestmentPerPlayer.get(player);
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

    private void addPotInvestmentToPlayer(Player player, long amount) {
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
