package com.project.ftp.bridge.mysqlTable;

public enum TableVariableParam {
    TABLE_NAME(-5);
    private int value;
    TableVariableParam(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
