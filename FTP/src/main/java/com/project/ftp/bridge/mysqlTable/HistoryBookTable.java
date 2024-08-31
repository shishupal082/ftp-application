package com.project.ftp.bridge.mysqlTable;

import java.util.ArrayList;

public class HistoryBookTable {
    private final String tableName;
    private final ArrayList<String> updateColumnName = new ArrayList<>();
    public HistoryBookTable() {
        this.tableName = "history_book";
        this.updateColumnName.add("table_name");
        this.updateColumnName.add("unique_column");
        this.updateColumnName.add("unique_parameter");
        this.updateColumnName.add("column_name");
        this.updateColumnName.add("old_value");
        this.updateColumnName.add("new_value");
    }
    public String getTableName() {
        return tableName;
    }
    public ArrayList<String> getUpdateColumnName() {
        return updateColumnName;
    }
}
