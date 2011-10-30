package se.cygni.texasholdem.communication.message.event;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Card;

@IsATexasMessage
public class YouHaveBeenDealtACardEvent extends TexasEvent {

    public Card card;
}
