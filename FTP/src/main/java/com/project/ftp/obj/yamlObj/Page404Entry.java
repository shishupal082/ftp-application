package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Page404Entry {
    private String roleAccess;
    private String fileName; // It can be publicFilePath or ftlViewMapping.id
    private String viewType; // ftl.view

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

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    @Override
    public String toString() {
        return "Page404Entry{" +
                "roleAccess='" + roleAccess + '\'' +
                ", fileName='" + fileName + '\'' +
                ", viewType='" + viewType + '\'' +
                '}';
    }
}
