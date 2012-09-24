package se.cygni.texasholdem.dao.model;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.PokerHand;

import java.util.ArrayList;
import java.util.List;

public class PlayerInGame {

    public String name;
    public boolean dealer;
    public boolean bigBlind;
    public boolean smallBlind;

    public List<Card> cards = new ArrayList<Card>();
    public long winnings;
    public long chipsAfterGame;

    public List<Card> cardsBestHand = new ArrayList<Card>();
    public PokerHand pokerHand;

    public boolean preflopFolded;
    public boolean flopFolded;
    public boolean turnFolded;
    public boolean riverFolded;

    public boolean preflopAllIn;
    public boolean flopAllIn;
    public boolean turnAllIn;
    public boolean riverAllIn;

    public long preflopBet;
    public long flopBet;
    public long turnBet;
    public long riverBet;

    public boolean allIn;

    public int noofRaises;
    public int noofCalls;

    public String getName() {
        return name;
    }

    public boolean getDealer() {
        return dealer;
    }

    public boolean getBigBlind() {
        return bigBlind;
    }

    public boolean getSmallBlind() {
        return smallBlind;
    }

    public List<Card> getCards() {
        return cards;
    }

    public long getWinnings() {
        return winnings;
    }

    public long getChipsAfterGame() {
        return chipsAfterGame;
    }

    public List<Card> getCardsBestHand() {
        return cardsBestHand;
    }

    public String getPokerHand() {
        return pokerHand.getName();
    }

    public boolean isFolded() {
        return isPreflopFolded() || isFlopFolded() || isTurnFolded() || isRiverFolded();
    }

    public boolean isPreflopFolded() {
        return preflopFolded;
    }

    public boolean isFlopFolded() {
        return flopFolded;
    }

    public boolean isTurnFolded() {
        return turnFolded;
    }

    public boolean isRiverFolded() {
        return riverFolded;
    }

    public long getPreflopBet() {
        return preflopBet;
    }

    public long getFlopBet() {
        return flopBet;
    }

    public long getTurnBet() {
        return turnBet;
    }

    public long getRiverBet() {
        return riverBet;
    }

    public boolean getAllIn() {
        return allIn;
    }

    public boolean getPreflopAllIn() {
        return preflopAllIn;
    }

    public boolean getFlopAllIn() {
        return flopAllIn;
    }

    public boolean getTurnAllIn() {
        return turnAllIn;
    }

    public boolean getRiverAllIn() {
        return riverAllIn;
    }
}
