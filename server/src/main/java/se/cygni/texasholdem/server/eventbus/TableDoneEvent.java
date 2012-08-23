package se.cygni.texasholdem.server.eventbus;

import se.cygni.texasholdem.table.Table;

public class TableDoneEvent {

    private Table table;

    public TableDoneEvent(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }
}
