package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Card;

import java.util.List;

@IsATexasMessage
public class CommunityCardsResponse extends TexasResponse {

    public List<Card> communityCards;
}
