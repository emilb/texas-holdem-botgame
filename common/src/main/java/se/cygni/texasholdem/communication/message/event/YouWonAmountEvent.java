package se.cygni.texasholdem.communication.message.event;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class YouWonAmountEvent extends TexasEvent {

    public long wonAmount;
    public long yourChipAmount;
}
