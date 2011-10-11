package se.cygni.texasholdem.game.exception;

public class InvalidAmountException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public InvalidAmountException() {

        super();
    }

    public InvalidAmountException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public InvalidAmountException(final String message) {

        super(message);
    }

    public InvalidAmountException(final Throwable cause) {

        super(cause);
    }

}
