package com.project.ftp.bridge.mysqlTable;

public enum TableColumn {
    col1("col1"),
    col2("col2"),
    col3("col2"),
    col4("col2"),
    col5("col5"),
    col6("col6"),
    col7("col7"),
    col8("col8"),
    col9("col9"),
    col10("col10"),
    col11("col11"),
    col12("col12"),
    col13("col13"),
    col14("col14"),
    col15("col15"),
    col16("col16"),
    col17("col17"),
    col18("col18"),
    col19("col19"),
    col20("col20"),
    col21("col21"),
    col22("col22"),
    col23("col23"),
    col24("col24"),
    col25("col25"),
    col26("col26"),
    col27("col27"),
    col28("col28"),
    col29("col29"),
    col30("col30"),
    col31("col31");
    private final String columnName;
    TableColumn(String columnName) {
        this.columnName = columnName;
    }
    public String getColumnName() {
        return columnName;
    }
}