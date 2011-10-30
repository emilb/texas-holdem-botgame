package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class MyChipAmountResponse extends TexasResponse {

    public long myChipAmount;
}
