package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class DatabaseParams {
    private String driverClass;
    private String user;
    private String password;
    private String url;
    public DatabaseParams() {}

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Database{" +
                "driverClass='" + driverClass + '\'' +
                ", user='" + user + '\'' +
                ", password='" + "*****" + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
