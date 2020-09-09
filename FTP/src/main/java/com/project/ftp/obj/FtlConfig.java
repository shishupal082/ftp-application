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
    private boolean forgotPasswordEnable;
    public FtlConfig() {}
    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        if (keywords == null) {
            return "";
        }
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        if (author == null) {
            return "";
        }
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadingJson() {
        if (headingJson == null) {
            return "";
        }
        return headingJson;
    }

    public void setHeadingJson(String headingJson) {
        this.headingJson = headingJson;
    }

    public String getUploadFileInstruction() {
        if (uploadFileInstruction == null) {
            return "";
        }
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

    @Override
    public String toString() {
        return "FtlConfig{" +
                "description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", headingJson='" + headingJson + '\'' +
                ", uploadFileInstruction='" + uploadFileInstruction + '\'' +
                ", forgotPasswordEnable=" + forgotPasswordEnable +
                '}';
    }
}
