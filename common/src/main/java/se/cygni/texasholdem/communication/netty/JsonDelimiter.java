package se.cygni.texasholdem.communication.netty;

public class JsonDelimiter {

    public static byte[] delimiter() {
        return new byte[] { '_', '-', '^', 'e', 'm', 'i', 'l', '^', '-', '_' } ;
    }
}
