package se.cygni.texasholdem.communication.message.exception;

import se.cygni.texasholdem.communication.message.response.TexasResponse;

public abstract class TexasException extends TexasResponse {

    public String message;

    public abstract void throwException()
            throws se.cygni.texasholdem.game.exception.GameException;

}
