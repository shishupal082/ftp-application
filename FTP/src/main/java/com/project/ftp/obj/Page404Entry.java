package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Page404Entry {
    private String roleAccess;
    private String fileName;

    public String getRoleAccess() {
        return roleAccess;
    }

    public void setRoleAccess(String roleAccess) {
        this.roleAccess = roleAccess;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Page404Entry{" +
                "roleAccess='" + roleAccess + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
