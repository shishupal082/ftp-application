package com.project.ftp.config;

public enum FileDeleteAccess {
    SELF("self"),
    ADMIN("admin"),
    SELF_ADMIN("self_admin"); // i.e. both self & admin
    private final String deleteAccess;
    FileDeleteAccess(String deleteAccess) {
        this.deleteAccess = deleteAccess;
    }

    public String getDeleteAccess() {
        return deleteAccess;
    }
}
