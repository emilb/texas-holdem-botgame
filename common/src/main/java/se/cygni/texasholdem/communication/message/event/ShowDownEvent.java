package se.cygni.texasholdem.communication.message.event;

import java.util.List;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.PlayerShowDown;

@IsATexasMessage
public class ShowDownEvent extends TexasEvent {

    public List<PlayerShowDown> playersShowDown;
}
