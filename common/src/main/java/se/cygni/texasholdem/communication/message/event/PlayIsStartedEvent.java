package se.cygni.texasholdem.communication.message.event;

import java.util.List;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayIsStartedEvent extends TexasEvent {

    public List<Player> players;
    public long yourChipAmount;
}
