package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

// Configuration which are only required in UI

public class FtlConfig {
    private String description;
    private String keywords;
    private String author;
    private String title;
    private String headingJson;
    private String uploadFileInstruction;
    private String gaTrackingId;
    private boolean forgotPasswordEnable;

    public FtlConfig() {}
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadingJson() {
        return headingJson;
    }

    public void setHeadingJson(String headingJson) {
        this.headingJson = headingJson;
    }

    public String getUploadFileInstruction() {
        return uploadFileInstruction;
    }

    public void setUploadFileInstruction(String uploadFileInstruction) {
        this.uploadFileInstruction = uploadFileInstruction;
    }

    public boolean isForgotPasswordEnable() {
        return forgotPasswordEnable;
    }

    public void setForgotPasswordEnable(boolean forgotPasswordEnable) {
        this.forgotPasswordEnable = forgotPasswordEnable;
    }

    public String getGaTrackingId() {
        return gaTrackingId;
    }

    public void setGaTrackingId(String gaTrackingId) {
        this.gaTrackingId = gaTrackingId;
    }

    @Override
    public String toString() {
        return "FtlConfig{" +
                "description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", headingJson='" + headingJson + '\'' +
                ", uploadFileInstruction='" + uploadFileInstruction + '\'' +
                ", gaTrackingId='" + gaTrackingId + '\'' +
                ", forgotPasswordEnable=" + forgotPasswordEnable +
                '}';
    }
}
