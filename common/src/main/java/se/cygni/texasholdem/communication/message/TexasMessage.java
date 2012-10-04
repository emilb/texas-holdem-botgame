package se.cygni.texasholdem.communication.message;


public abstract class TexasMessage {

    private final String type = this.getClass().getCanonicalName();

    // The version of the client or server that this message originated from
    private String version;

    public String getType() {

        return type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
