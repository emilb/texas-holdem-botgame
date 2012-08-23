package se.cygni.texasholdem.dao.model;

import se.cygni.texasholdem.game.Card;

import java.util.ArrayList;
import java.util.List;

public class GameLog {

    public List<PlayerInGame> players = new ArrayList<PlayerInGame>();
    public long bigBlind;
    public long smallBlind;
    public List<Card> communityCards = new ArrayList<Card>();
    public int logPosition;
    public long tableCounter;
    public int roundNumber;

    public List<PlayerInGame> getPlayers() {
        return players;
    }

    public long getBigBlind() {
        return bigBlind;
    }

    public long getSmallBlind() {
        return smallBlind;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public List<Card> getFlopCards() {
        return communityCards.subList(0, 3);
    }

    public List<Card> getTurnCards() {
        return communityCards.subList(3, 4);
    }

    public List<Card> getRiverCards() {
        return communityCards.subList(4, 5);
    }

    public int getLogPosition() {
        return logPosition;
    }

    public long getTableCounter() {
        return tableCounter;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
