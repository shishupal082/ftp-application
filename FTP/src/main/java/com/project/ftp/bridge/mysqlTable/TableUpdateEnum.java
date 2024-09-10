package com.project.ftp.bridge.mysqlTable;

public enum TableUpdateEnum {
    UPDATE("update"),
    ADD("add"),
    SKIP("skip"),
    SEARCH_ERROR("searchError"),
    SKIP_WITHOUT_LOG("skipWithoutLog"),
    INVALID_UNIQUE_PARAMETER("invalidUniqueParameter"),
    SKIP_IGNORE("skipIgnore"),
    NULL(null);
    private String name;
    TableUpdateEnum (String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
