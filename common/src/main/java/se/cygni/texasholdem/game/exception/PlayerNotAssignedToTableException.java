package se.cygni.texasholdem.game.exception;

public class PlayerNotAssignedToTableException extends GameException {

    private static final long serialVersionUID = 1296247382325404556L;

    public PlayerNotAssignedToTableException() {

        super();
    }

    public PlayerNotAssignedToTableException(final String message,
            final Throwable
            cause) {

        super(message, cause);
    }

    public PlayerNotAssignedToTableException(final String message) {

        super(message);
    }

    public PlayerNotAssignedToTableException(final Throwable cause) {

        super(cause);
    }

}
