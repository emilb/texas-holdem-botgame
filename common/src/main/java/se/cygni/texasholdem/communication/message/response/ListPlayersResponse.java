package se.cygni.texasholdem.communication.message.response;

import java.util.List;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class ListPlayersResponse extends TexasResponse {

    public List<Player> players;
}
