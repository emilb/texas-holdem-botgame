package se.cygni.texasholdem.table;

import java.util.ArrayList;
import java.util.List;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.pot.Pot;
import se.cygni.texasholdem.server.GameServer;

public class Table {

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

    private List<Card> communityCards;

    public Table(final GamePlan gamePlan, final GameServer gameServer) {

        this.gamePlan = gamePlan;
        this.gameServer = gameServer;
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
