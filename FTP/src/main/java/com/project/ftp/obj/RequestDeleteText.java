package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestDeleteText {
    private String deleteId;
    private String role_id;

    public String getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(String deleteId) {
        this.deleteId = deleteId;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    @Override
    public String toString() {
        return "RequestDeleteText{" +
                "deleteId='" + deleteId + '\'' +
                ", role_id='" + role_id + '\'' +
                '}';
    }
}
