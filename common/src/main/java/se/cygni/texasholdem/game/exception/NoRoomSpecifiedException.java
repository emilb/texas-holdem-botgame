package se.cygni.texasholdem.game.exception;

public class NoRoomSpecifiedException extends GameException {

    private static final long serialVersionUID = 5009842166122191121L;

    public NoRoomSpecifiedException() {

        super();
    }

    public NoRoomSpecifiedException(final String message, final Throwable
            cause) {

        super(message, cause);
    }

    public NoRoomSpecifiedException(final String message) {

        super(message);
    }

    public NoRoomSpecifiedException(final Throwable cause) {

        super(cause);
    }

}
