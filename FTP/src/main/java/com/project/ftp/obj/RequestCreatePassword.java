package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

public class RequestCreatePassword {
    @JsonProperty("username")
    private String username;
    @JsonProperty("create_password_otp")
    private String createPasswordOtp;
    @JsonProperty("new_password")
    private String newPassword;
    @JsonProperty("confirm_password")
    private String confirmPassword;
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

    public String getCreatePasswordOtp() {
        if (StaticService.isInValidString(createPasswordOtp)) {
            return null;
        }
        return createPasswordOtp.trim();
    }

    public void setCreatePasswordOtp(String createPasswordOtp) {
        this.createPasswordOtp = createPasswordOtp;
    }

    public String getNewPassword() {
        if (StaticService.isInValidString(newPassword)) {
            return null;
        }
        return newPassword.trim();
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        if (StaticService.isInValidString(confirmPassword)) {
            return null;
        }
        return confirmPassword.trim();
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "RequestCreatePassword{" +
                "username='" + username + '\'' +
                ", createPasswordOtp='" + createPasswordOtp + '\'' +
                ", newPassword='" + "*****" + '\'' +
                ", confirmPassword='" + "*****" + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
