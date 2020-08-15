package com.project.ftp.event;

public enum EventName {
    LOGIN("login"),
    LOGOUT("logout"),
    REGISTER("register"),
    FORGOT_PASSWORD("forgot_password"),
    CHANGE_PASSWORD("change_password"),
    UPLOAD_FILE("upload_file"),
    UPLOAD_FILE_V1("upload_file_v1"),
    UPLOAD_FILE_V2("upload_file_v2"),
    VIEW_FILE("view_file"),
    DOWNLOAD_FILE("download_file"),
    DELETE_FILE("delete_file");

    final String name;
    EventName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
