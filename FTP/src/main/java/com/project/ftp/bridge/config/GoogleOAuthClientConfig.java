package com.project.ftp.bridge.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class GoogleOAuthClientConfig {
    private String applicationName;
    private String tokenDirPath;
    private String credentialFilePath;
    private int localServerPort;
    public GoogleOAuthClientConfig() {}
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getTokenDirPath() {
        return tokenDirPath;
    }

    public void setTokenDirPath(String tokenDirPath) {
        this.tokenDirPath = tokenDirPath;
    }

    public String getCredentialFilePath() {
        return credentialFilePath;
    }

    public void setCredentialFilePath(String credentialFilePath) {
        this.credentialFilePath = credentialFilePath;
    }

    public int getLocalServerPort() {
        return localServerPort;
    }

    public void setLocalServerPort(int localServerPort) {
        this.localServerPort = localServerPort;
    }

    @Override
    public String toString() {
        return "GoogleOAuthClientConfig{" +
                "applicationName='" + applicationName + '\'' +
                ", tokenDirPath='" + tokenDirPath + '\'' +
                ", credentialFilePath='" + credentialFilePath + '\'' +
                ", localServerPort=" + localServerPort +
                '}';
    }
}
