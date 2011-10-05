package se.cygni.texasholdem.game;

import java.util.List;

import se.cygni.texasholdem.game.definitions.PokerHand;

public class BestHand {

    private final List<Card> cards;
    private final PokerHand pokerHand;

    public BestHand(final List<Card> cards, final PokerHand pokerHand) {

        this.cards = cards;
        this.pokerHand = pokerHand;
    }

    public List<Card> getCards() {

        return cards;
    }

    public PokerHand getPokerHand() {

        return pokerHand;
    }
}
