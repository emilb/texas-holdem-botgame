package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Card;

import java.util.List;

@IsATexasMessage
public class MyCardsResponse extends TexasResponse {

    List<Card> myCards;
}
