package se.cygni.texasholdem.communication.message.event;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerRaisedEvent extends TexasEvent {

    public Player player;
    public long callBet;
    public long raiseBet;
}
