package se.cygni.texasholdem.game.exception;

public class PlayerNameAlreadyTakenException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public PlayerNameAlreadyTakenException() {

        super();
    }

    public PlayerNameAlreadyTakenException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public PlayerNameAlreadyTakenException(final String message) {

        super(message);
    }

    public PlayerNameAlreadyTakenException(final Throwable cause) {

        super(cause);
    }

}
