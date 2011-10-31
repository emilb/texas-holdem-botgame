package se.cygni.texasholdem.communication.message;


public abstract class TexasMessage {

    private final String type = this.getClass().getCanonicalName();

    public String getType() {

        return type;
    }

}
