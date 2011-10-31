package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Card;

@IsATexasMessage
public class CommunityHasBeenDealtACardEvent extends TexasEvent {

    private final Card card;

    @JsonCreator
    public CommunityHasBeenDealtACardEvent(@JsonProperty("card") final Card card) {

        this.card = card;
    }

    public Card getCard() {

        return card;
    }

    @Override
    public String toString() {

        return "CommunityHasBeenDealtACardEvent [card=" + card + "]";
    }

}
