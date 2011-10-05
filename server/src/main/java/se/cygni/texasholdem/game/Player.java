package se.cygni.texasholdem.game;

public class Player {

    private final String name;
    private final String sessionId;

    public Player(final String name, final String sessionId) {

        this.name = name;
        this.sessionId = sessionId;
    }

    public String getName() {

        return name;
    }

    public String getSessionId() {

        return sessionId;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Player other = (Player) obj;
        if (sessionId == null) {
            if (other.sessionId != null)
                return false;
        } else if (!sessionId.equals(other.sessionId))
            return false;
        return true;
    }
}
