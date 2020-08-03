package com.project.ftp.config;

public enum FileViewer {
    SELF("self"),
    ALL("all");
    final String viewer;
    FileViewer(String viewer) {
        this.viewer = viewer;
    }

    public String getViewer() {
        return viewer;
    }
}
