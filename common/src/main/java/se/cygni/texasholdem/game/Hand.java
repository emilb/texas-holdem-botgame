package se.cygni.texasholdem.game;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.game.definitions.PokerHand;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private final List<Card> cards;
    private final PokerHand pokerHand;
    private final boolean folded;

    @JsonCreator
    public Hand(@JsonProperty("cards") final List<Card> cards,
                @JsonProperty("pokerHand") final PokerHand pokerHand,
                @JsonProperty("folded") final boolean folded) {


        this.folded = folded;

        this.cards = folded ? new ArrayList<Card>() : cards;
        this.pokerHand = folded ? PokerHand.NOTHING : pokerHand;
    }

    public List<Card> getCards() {

        return cards;
    }

    public PokerHand getPokerHand() {

        return pokerHand;
    }

    public boolean isFolded() {
        return folded;
    }
}
