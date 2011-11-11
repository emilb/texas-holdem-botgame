package se.cygni.texasholdem.game;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.game.definitions.PokerHand;

public class Hand {

    private final List<Card> cards;
    private final PokerHand pokerHand;

    @JsonCreator
    public Hand(@JsonProperty("cards") final List<Card> cards,
            @JsonProperty("pokerHand") final PokerHand pokerHand) {

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
