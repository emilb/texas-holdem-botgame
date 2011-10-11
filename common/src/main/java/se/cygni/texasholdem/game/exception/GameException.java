package se.cygni.texasholdem.game.exception;

public class GameException extends Exception {

    private static final long serialVersionUID = -2490106288332060830L;

    public GameException() {

        super();
    }

    public GameException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public GameException(final String message) {

        super(message);
    }

    public GameException(final Throwable cause) {

        super(cause);
    }

}
