package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.definitions.PlayState;

@IsATexasMessage
public class PlayStateResponse extends TexasResponse {

    public PlayState playState;
}
