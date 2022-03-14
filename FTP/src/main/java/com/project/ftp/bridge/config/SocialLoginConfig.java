package com.project.ftp.bridge.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SocialLoginConfig {
    private boolean loginWithGmail;
    private String googleLoginClientId;
    private String googleLoginClientSecretId;

    public SocialLoginConfig() {}

    public boolean isLoginWithGmail() {
        return loginWithGmail;
    }

    public void setLoginWithGmail(boolean loginWithGmail) {
        this.loginWithGmail = loginWithGmail;
    }

    public String getGoogleLoginClientId() {
        return googleLoginClientId;
    }

    public void setGoogleLoginClientId(String googleLoginClientId) {
        this.googleLoginClientId = googleLoginClientId;
    }

    public String getGoogleLoginClientSecretId() {
        return googleLoginClientSecretId;
    }

    public void setGoogleLoginClientSecretId(String googleLoginClientSecretId) {
        this.googleLoginClientSecretId = googleLoginClientSecretId;
    }

    @Override
    public String toString() {
        return "SocialLoginConfig{" +
                "loginWithGmail=" + loginWithGmail +
                ", googleLoginClientId='" + googleLoginClientId + '\'' +
                ", googleLoginClientSecretId='" + googleLoginClientSecretId + '\'' +
                '}';
    }
}
