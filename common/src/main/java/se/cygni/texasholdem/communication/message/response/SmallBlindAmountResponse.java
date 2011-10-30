package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class SmallBlindAmountResponse extends TexasResponse {

    public long smallBlindAmount;
}
