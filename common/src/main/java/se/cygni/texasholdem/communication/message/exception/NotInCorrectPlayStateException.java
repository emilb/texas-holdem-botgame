package se.cygni.texasholdem.communication.message.exception;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class NotInCorrectPlayStateException extends TexasException {

    @Override
    public void throwException()
            throws se.cygni.texasholdem.game.exception.GameException {

        throw new se.cygni.texasholdem.game.exception.NotInCorrectPlayStateException(
                message);
    }
}
