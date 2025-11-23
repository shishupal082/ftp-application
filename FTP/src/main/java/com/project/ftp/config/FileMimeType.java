package com.project.ftp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FileMimeType {
    pdf("application/pdf"),
    json("application/json"),
    aae("application/octet-stream"),
    webp("image/webp"),
    png("image/png"),
    jpg("image/jpg"),
    jpeg("image/jpeg"),
    gif("image/gif"),
    ico("image/x-icon"),
    css("text/css"),
    js("text/javascript"),
    html("text/html"),
    yaml("text/plain"),
    yml("text/plain"),
    csv("text/plain"),
    txt("text/plain"),
    bat("text/plain"),
    log("text/plain"),
    ini("text/plain"),
    sh("text/plain"),
    mov("video/quicktime"),
    mp4("video/mp4"),
    avi("video/x-msvideo"),
    mp3("audio/mpeg");

    /*Not supported by browser
        - doc, docx, xls, xlsx, ppt, pptx
        - zar, exe, mp3, mp4, mov, avi
    */

    private final static Logger logger = LoggerFactory.getLogger(FileMimeType.class);

    private final String fileMimeType;
    FileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public String getFileMimeType() {
        return fileMimeType;
    }

//    public void setFileMimeType(String fileMimeType) {
//        this.fileMimeType = fileMimeType;
//    }
}
