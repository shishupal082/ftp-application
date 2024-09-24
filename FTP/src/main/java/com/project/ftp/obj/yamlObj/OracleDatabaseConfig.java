package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class OracleDatabaseConfig {
    private String driver;
    private String url;
    private String username;
    private String password;
    private int connectionResetCount;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionResetCount() {
        if (connectionResetCount > 0) {
            return connectionResetCount;
        }
        return 30;
    }

    public void setConnectionResetCount(int connectionResetCount) {
        this.connectionResetCount = connectionResetCount;
    }
}