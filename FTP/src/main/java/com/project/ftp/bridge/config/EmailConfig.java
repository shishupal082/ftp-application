package com.project.ftp.bridge.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class EmailConfig {
    private boolean enable;
    private String senderEmail;
    private String senderPassword;
    public EmailConfig() {}

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    @Override
    public String toString() {
        return "EmailConfig{" +
                "enable=" + enable +
                ", senderEmail='" + "*****" + '\'' +
                ", senderPassword='" + "*****" + '\'' +
                '}';
    }
}
