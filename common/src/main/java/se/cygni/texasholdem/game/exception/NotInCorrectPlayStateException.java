package se.cygni.texasholdem.game.exception;

public class NotInCorrectPlayStateException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public NotInCorrectPlayStateException() {

        super();
    }

    public NotInCorrectPlayStateException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public NotInCorrectPlayStateException(final String message) {

        super(message);
    }

    public NotInCorrectPlayStateException(final Throwable cause) {

        super(cause);
    }

}
