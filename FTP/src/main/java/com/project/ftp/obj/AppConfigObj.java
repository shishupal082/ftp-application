package com.project.ftp.obj;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.session.SessionData;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfigObj {
    private final String publicDir;
    private final String configDate;
    private final String appVersion;
    private final ArrayList<String> cmdArguments;
    private final String logFilePath;
    private final int requestCount;
    private final String logFiles;
    private final HashMap<String, SessionData> sessionData;
    private final FtpConfiguration ftpConfiguration;
    private final PageConfig404 pageConfig404;
    public AppConfigObj(String publicDir, String configDate, String appVersion,
                        ArrayList<String> cmdArguments, String logFilePath, int requestCount,
                        HashMap<String, SessionData> sessionData,
                        FtpConfiguration ftpConfiguration, PageConfig404 pageConfig404) {
        this.publicDir = publicDir;
        this.configDate = configDate;
        this.appVersion = appVersion;
        this.cmdArguments = cmdArguments;
        this.logFilePath = logFilePath;
        this.requestCount = requestCount;
        this.logFiles = "*****";
        this.sessionData = sessionData;
        this.ftpConfiguration = ftpConfiguration;
        this.pageConfig404 = pageConfig404;
    }

    public String getPublicDir() {
        return publicDir;
    }

    public String getConfigDate() {
        return configDate;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public ArrayList<String> getCmdArguments() {
        return cmdArguments;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public String getLogFiles() {
        return logFiles;
    }

    public HashMap<String, SessionData> getSessionData() {
        return sessionData;
    }

    public FtpConfiguration getFtpConfiguration() {
        return ftpConfiguration;
    }

    public PageConfig404 getPageConfig404() {
        return pageConfig404;
    }

    @Override
    public String toString() {
        return "AppConfigObj{" +
                "publicDir='" + publicDir + '\'' +
                ", configDate='" + configDate + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", cmdArguments=" + cmdArguments +
                ", logFilePath='" + logFilePath + '\'' +
                ", requestCount=" + requestCount +
                ", logFiles='" + logFiles + '\'' +
                ", sessionData=" + sessionData +
                ", ftpConfiguration=" + ftpConfiguration +
                ", pageConfig404=" + pageConfig404 +
                '}';
    }
}
