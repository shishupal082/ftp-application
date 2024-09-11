package com.project.ftp.bridge.mysqlTable;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryBookTable {
    private final String tableName;
    private final ArrayList<String> updateColumnName = new ArrayList<>();
    private final String colTableName = "table_name";
    private final String colUniqueColumn = "unique_column";
    private final String colUniqueParameter = "unique_parameter";
    private final String colColumnName = "column_name";
    private final String colOldValue = "old_value";
    private final String colNewValue = "new_value";
    public HistoryBookTable() {
        this.tableName = "history_book";
        this.updateColumnName.add(colTableName);
        this.updateColumnName.add(colUniqueColumn);
        this.updateColumnName.add(colUniqueParameter);
        this.updateColumnName.add(colColumnName);
        this.updateColumnName.add(colOldValue);
        this.updateColumnName.add(colNewValue);
    }
    public int getMaxLength(String columnName) {
        HashMap<String, Integer> maxLength = new HashMap<>();
        maxLength.put(colTableName, 255);
        maxLength.put(colUniqueColumn, 255);
        maxLength.put(colUniqueParameter, 255);
        maxLength.put(colColumnName, 255);
        maxLength.put(colOldValue, 4000);
        maxLength.put(colNewValue, 4000);
        return maxLength.get(columnName);
    }
    public String getTableName() {
        return tableName;
    }
    public ArrayList<String> getUpdateColumnName() {
        return updateColumnName;
    }

    public String getColTableName() {
        return colTableName;
    }

    public String getColUniqueColumn() {
        return colUniqueColumn;
    }

    public String getColUniqueParameter() {
        return colUniqueParameter;
    }

    public String getColColumnName() {
        return colColumnName;
    }

    public String getColOldValue() {
        return colOldValue;
    }

    public String getColNewValue() {
        return colNewValue;
    }
}
