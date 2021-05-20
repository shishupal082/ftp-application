package com.project.ftp.config;

/*
* Subset of DropWizard App Configuration file
* Generated after modification of parameter of config file
*/

import com.project.ftp.FtpConfiguration;
import com.project.ftp.intreface.AppToBridge;
import com.project.ftp.obj.yamlObj.BackendConfig;
import com.project.ftp.obj.yamlObj.FtlConfig;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.service.StaticService;
import com.project.ftp.session.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfig {
    private final static Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private String publicDir;
    private String configDate;
    private final String appVersion = AppConstant.AppVersion;
//    private ShutdownTask shutdownTask;
    private ArrayList<String> cmdArguments;
    private String logFilePath;
    private int requestCount = 0;
    private ArrayList<String> logFiles;
    private HashMap<String, SessionData> sessionData;
    private FtpConfiguration ftpConfiguration;
    private PageConfig404 pageConfig404;

    private AppToBridge appToBridge;
    public AppConfig() {
        this.configDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
    }

    public AppToBridge getAppToBridge() {
        return appToBridge;
    }

    public void setAppToBridge(AppToBridge appToBridge) {
        this.appToBridge = appToBridge;
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
    public int getRateLimitThreshold() {
        int rateLimitThreshold = AppConstant.DEFAULT_RATE_LIMIT_THRESHOLD;
        BackendConfig backendConfig = ftpConfiguration.getBackendConfig();
        if (backendConfig != null) {
            if (backendConfig.getRateLimitThreshold() > 0) {
                rateLimitThreshold = backendConfig.getRateLimitThreshold();
            }
        }
        return rateLimitThreshold;
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

    public ArrayList<String> getCmdArguments() {
        return cmdArguments;
    }

    public void setCmdArguments(ArrayList<String> cmdArguments) {
        this.cmdArguments = cmdArguments;
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

    public String getCookieName() {
        String cookieName = ftpConfiguration.getCookieName();
        if (StaticService.isInValidString(cookieName)) {
            cookieName = AppConstant.COOKIE_NAME;
        }
        return cookieName;
    }
    public FtlConfig getFtlConfig() {
        FtlConfig ftlConfig = ftpConfiguration.getFtlConfig();
        if (ftlConfig == null) {
            return new FtlConfig();
        }
        ftlConfig.setTempGaEnable(null);
        return ftlConfig;
    }

    public PageConfig404 getPageConfig404() {
        return pageConfig404;
    }

    public void setPageConfig404(PageConfig404 pageConfig404) {
        this.pageConfig404 = pageConfig404;
    }

    public void generateFinalFtpConfiguration(final FtpConfiguration ftpConfiguration) {
        if (StaticService.isMysqlEnable(cmdArguments)) {
            ftpConfiguration.setMysqlEnable(true);
        } else {
            ftpConfiguration.setMysqlEnable(false);
        }
        if (cmdArguments == null) {
            logger.info("FTP configuration generate complete 1: {}", ftpConfiguration);
            return;
        }
        if (cmdArguments.size() <= AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            logger.info("FTP configuration generate complete 2: {}", ftpConfiguration);
            return;
        }
        FtpConfiguration temp;
        YamlFileParser yamlFileParser = new YamlFileParser();
        for (int i = AppConstant.CMD_LINE_ARG_MIN_SIZE; i< cmdArguments.size(); i++) {
            temp = yamlFileParser.getFtpConfigurationFromPath(cmdArguments.get(i));
            ftpConfiguration.updateFtpConfig(temp);
        }
        logger.info("FTP configuration generate complete: {}", ftpConfiguration);
    }
    public void updatePageConfig404() {
        YamlFileParser yamlFileParser = new YamlFileParser();
        pageConfig404 = yamlFileParser.getPageConfig404(this);
        logger.info("PageConfig404 update complete: {}", pageConfig404);
    }
    @Override
    public String toString() {
        return "AppConfig{" +
                "publicDir='" + publicDir + '\'' +
                ", configDate='" + configDate + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", cmdArguments=" + cmdArguments +
                ", logFilePath='" + logFilePath + '\'' +
                ", requestCount=" + requestCount +
                ", logFiles='" + "*****" + '\'' +
                ", sessionData=" + sessionData +
                ", ftpConfiguration=" + ftpConfiguration +
                ", pageConfig404=" + pageConfig404 +
                ", appToBridge=" + appToBridge +
                '}';
    }
}
