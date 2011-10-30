package se.cygni.texasholdem.game.exception;

public class UsernameAlreadyTakenException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public UsernameAlreadyTakenException() {

        super();
    }

    public UsernameAlreadyTakenException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public UsernameAlreadyTakenException(final String message) {

        super(message);
    }

    public UsernameAlreadyTakenException(final Throwable cause) {

        super(cause);
    }

}
