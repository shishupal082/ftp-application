package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestDeleteText {
    private String deleteId;

    public String getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(String deleteId) {
        this.deleteId = deleteId;
    }

    @Override
    public String toString() {
        return "RequestDeleteText{" +
                "deleteId='" + deleteId + '\'' +
                '}';
    }
}
