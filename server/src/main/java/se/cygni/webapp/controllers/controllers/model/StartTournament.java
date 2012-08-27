package se.cygni.webapp.controllers.controllers.model;

public class StartTournament {

    private String id;

    public StartTournament() {
    }

    public StartTournament(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
