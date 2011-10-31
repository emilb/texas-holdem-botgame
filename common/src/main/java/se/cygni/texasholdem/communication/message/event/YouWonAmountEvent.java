package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class YouWonAmountEvent extends TexasEvent {

    private final long wonAmount;
    private final long yourChipAmount;

    @JsonCreator
    public YouWonAmountEvent(
            @JsonProperty("wonAmount") final long wonAmount,
            @JsonProperty("yourChipAmount") final long yourChipAmount) {

        this.wonAmount = wonAmount;
        this.yourChipAmount = yourChipAmount;
    }

    public long getWonAmount() {

        return wonAmount;
    }

    public long getYourChipAmount() {

        return yourChipAmount;
    }

    @Override
    public String toString() {

        return "YouWonAmountEvent [wonAmount=" + wonAmount
                + ", yourChipAmount=" + yourChipAmount + "]";
    }

}
