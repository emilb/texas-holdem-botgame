package se.cygni.texasholdem.game.exception;

public class InvalidSessionException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public InvalidSessionException() {

        super();
    }

    public InvalidSessionException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public InvalidSessionException(final String message) {

        super(message);
    }

    public InvalidSessionException(final Throwable cause) {

        super(cause);
    }

}
