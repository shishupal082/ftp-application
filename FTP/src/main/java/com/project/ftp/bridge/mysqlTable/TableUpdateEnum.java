package com.project.ftp.bridge.mysqlTable;

public enum TableUpdateEnum {
    UPDATE("update"),
    ADD("add"),
    SKIP("skip"),
    SKIP_WITHOUT_LOG("skipWithoutLog"),
    SKIP_IGNORE("skipIgnore");
    private String name;
    TableUpdateEnum (String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
