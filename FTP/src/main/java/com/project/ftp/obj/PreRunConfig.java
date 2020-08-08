package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PreRunConfig {
    private String logFilePath;
    private boolean mysqlEnable;

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public boolean isMysqlEnable() {
        return mysqlEnable;
    }

    public void setMysqlEnable(boolean mysqlEnable) {
        this.mysqlEnable = mysqlEnable;
    }

    @Override
    public String toString() {
        return "PreRunConfig{" +
                "logFilePath='" + logFilePath + '\'' +
                ", mysqlEnable=" + mysqlEnable +
                '}';
    }
}
