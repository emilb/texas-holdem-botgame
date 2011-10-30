package se.cygni.texasholdem.communication.message;


public abstract class TexasMessage {

    String type = this.getClass().getCanonicalName();

    public String getType() {

        return type;
    }

    public void setType(final String type) {

        this.type = type;
    }

}
