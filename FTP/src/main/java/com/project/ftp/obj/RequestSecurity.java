package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestSecurity {
    @JsonProperty("text")
    private String text;
    @JsonProperty("password")
    private String password;

    public String getText() {
        if (StaticService.isInValidString(text)) {
            return null;
        }
        return text.trim();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPassword() {
        if (StaticService.isInValidString(password)) {
            return null;
        }
        return password.trim();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        String textStr = null;
        String passwordStr = null;
        if (text != null) {
            textStr = "*****";
        }
        if (password != null) {
            passwordStr = "*****";
        }
        return "RequestUserLogin{" +
                "text='" + textStr + '\'' +
                ", password='" + passwordStr + '\'' +
                '}';
    }
}
