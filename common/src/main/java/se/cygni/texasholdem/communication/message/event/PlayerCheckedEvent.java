package se.cygni.texasholdem.communication.message.event;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerCheckedEvent extends TexasEvent {

    public Player player;
}
