package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

public class RequestForgotPassword {
    @JsonProperty("username")
    private String username;
    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("email")
    private String email;
    @JsonProperty("user_agent")
    private String userAgent;

    public String getUsername() {
        if (StaticService.isInValidString(username)) {
            return null;
        }
        return username.trim();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        if (StaticService.isInValidString(mobile)) {
            return null;
        }
        return mobile.trim();
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        if (StaticService.isInValidString(email)) {
            return null;
        }
        return email.trim();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "RequestForgotPassword{" +
                "username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
