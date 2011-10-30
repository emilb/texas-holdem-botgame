package se.cygni.texasholdem.communication.message.request;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class RegisterForPlayRequest extends TexasRequest {

    public String name;
}
