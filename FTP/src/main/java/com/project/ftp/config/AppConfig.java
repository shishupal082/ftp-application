package com.project.ftp.config;

/*
* Subset of DropWizard App Configuration file
* Generated after modification of parameter of config file
*/

import com.project.ftp.FtpConfiguration;
import com.project.ftp.service.StaticService;
import com.project.ftp.session.SessionData;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfig {
    private String publicDir;
    private String configDate;
    private final String appVersion = AppConstant.AppVersion;
//    private ShutdownTask shutdownTask;
    private String configPath;
    private String logFilePath;
    private int requestCount = 0;
    private ArrayList<String> logFiles;
    private HashMap<String, SessionData> sessionData;
    private FtpConfiguration ftpConfiguration;
    public AppConfig() {
        this.configDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
    }
    public String getPublicDir() {
        return publicDir;
    }

    public void setPublicDir(String publicDir) {
        this.publicDir = publicDir;
    }

    public HashMap<String, SessionData> getSessionData() {
        return sessionData;
    }

    public void setSessionData(HashMap<String, SessionData> sessionData) {
        this.sessionData = sessionData;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public FtpConfiguration getFtpConfiguration() {
        return ftpConfiguration;
    }

    public void setFtpConfiguration(FtpConfiguration ftpConfiguration) {
        this.ftpConfiguration = ftpConfiguration;
    }

//    public ShutdownTask getShutdownTask() {
//        return shutdownTask;
//    }
//
//    public void setShutdownTask(ShutdownTask shutdownTask) {
//        this.shutdownTask = shutdownTask;
//    }

    public String getConfigDate() {
        return configDate;
    }

    public void setConfigDate(String configDate) {
        this.configDate = configDate;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public ArrayList<String> getLogFiles() {
        return logFiles;
    }

    public void setLogFiles(ArrayList<String> logFiles) {
        this.logFiles = logFiles;
    }
    public boolean isMySqlEnable() {
        return this.ftpConfiguration.getMysqlEnable();
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "publicDir='" + publicDir + '\'' +
                ", configDate='" + configDate + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", configPath='" + configPath + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", requestCount=" + requestCount +
                ", logFiles=" + logFiles +
                ", sessionData=" + sessionData +
                ", ftpConfiguration=" + ftpConfiguration +
                '}';
    }
}
