package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

import java.util.List;

@IsATexasMessage
public class ListPlayersResponse extends TexasResponse {

    public List<Player> players;
}
