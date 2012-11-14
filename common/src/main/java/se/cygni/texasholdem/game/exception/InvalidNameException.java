package se.cygni.texasholdem.game.exception;

public class InvalidNameException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public InvalidNameException() {

        super();
    }

    public InvalidNameException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public InvalidNameException(final String message) {

        super(message);
    }

    public InvalidNameException(final Throwable cause) {

        super(cause);
    }

}
