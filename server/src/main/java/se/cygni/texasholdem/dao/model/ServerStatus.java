package se.cygni.texasholdem.dao.model;

public class ServerStatus {

    public final String uptime;
    public final int noofPlayers;
    public final long totalNoofConnections;

    public ServerStatus(String uptime, int noofPlayers, long totalNoofConnections) {
        this.uptime = uptime;
        this.noofPlayers = noofPlayers;
        this.totalNoofConnections = totalNoofConnections;
    }
}
