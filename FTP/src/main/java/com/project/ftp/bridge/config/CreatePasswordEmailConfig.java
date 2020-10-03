package com.project.ftp.bridge.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CreatePasswordEmailConfig {
    private String createPasswordLink;
    private String createPasswordSubject;
    private String createPasswordMessage;

    public CreatePasswordEmailConfig() {}

    public String getCreatePasswordLink() {
        return createPasswordLink;
    }

    public void setCreatePasswordLink(String createPasswordLink) {
        this.createPasswordLink = createPasswordLink;
    }

    public String getCreatePasswordSubject() {
        return createPasswordSubject;
    }

    public void setCreatePasswordSubject(String createPasswordSubject) {
        this.createPasswordSubject = createPasswordSubject;
    }

    public String getCreatePasswordMessage() {
        return createPasswordMessage;
    }

    public void setCreatePasswordMessage(String createPasswordMessage) {
        this.createPasswordMessage = createPasswordMessage;
    }

    @Override
    public String toString() {
        return "CreatePasswordEmailConfig{" +
                "createPasswordLink='" + createPasswordLink + '\'' +
                ", createPasswordSubject='" + createPasswordSubject + '\'' +
                ", createPasswordMessage='" + createPasswordMessage + '\'' +
                '}';
    }
}
