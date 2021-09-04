package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestDeleteText {
    private String deleteId;
    private String tableName;

    public String getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(String deleteId) {
        this.deleteId = deleteId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "RequestDeleteText{" +
                "deleteId='" + deleteId + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
