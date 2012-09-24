package se.cygni.texasholdem.dao.model;

import java.util.List;

public class TablePartition {
    private final List<Long> tableIds;
    private final int index;

    public TablePartition(List<Long> tableIds, int index) {
        this.tableIds = tableIds;
        this.index = index;
    }

    public List<Long> getTableIds() {
        return tableIds;
    }

    public int getIndex() {
        return index;
    }
}
