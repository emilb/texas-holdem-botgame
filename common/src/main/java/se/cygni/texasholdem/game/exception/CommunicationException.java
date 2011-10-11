package se.cygni.texasholdem.game.exception;

public class CommunicationException extends GameException {

    private static final long serialVersionUID = 5009842166127191132L;

    public CommunicationException() {

        super();
    }

    public CommunicationException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public CommunicationException(final String message) {

        super(message);
    }

    public CommunicationException(final Throwable cause) {

        super(cause);
    }

}
