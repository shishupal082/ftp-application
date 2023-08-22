package com.project.ftp.config;

/*
* Subset of DropWizard App Configuration file
* Generated after modification of parameter of config file
*/

import com.project.ftp.FtpConfiguration;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.AppToBridge;
import com.project.ftp.obj.AppConfigObj;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.yamlObj.FtlConfig;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
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
    private UserService userService;
    public AppConfig() {
        this.configDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
        userService = null;
    }
    public void setUserService(UserService userService) {
        this.userService = userService;
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
        if (ftpConfiguration.getRateLimitThreshold() > 0) {
            rateLimitThreshold = ftpConfiguration.getRateLimitThreshold();
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
    private String getFileSaveDir(LoginUserDetails loginUserDetails) {
        String fileSaveDir = ftpConfiguration.getFileSaveDir();
        String finalSaveDir = fileSaveDir;
        if (userService != null) {
            finalSaveDir = userService.getFileSaveDirMapping(loginUserDetails, fileSaveDir);
        }
        PathInfo saveDirPathInfo = StaticService.getPathInfo(finalSaveDir);
        if (!AppConstant.FOLDER.equals(saveDirPathInfo.getType())) {
            logger.info("File save directory is not a folder: {}", finalSaveDir);
            if (fileSaveDir != null && !fileSaveDir.equals(finalSaveDir)) {
                saveDirPathInfo = StaticService.getPathInfo(fileSaveDir);
                if (!AppConstant.FOLDER.equals(saveDirPathInfo.getType())) {
                    logger.info("File save directory original is not a folder: {}", fileSaveDir);
                    finalSaveDir = null;
                } else {
                    finalSaveDir = fileSaveDir;
                }
            } else {
                finalSaveDir = null;
            }
        }
        return finalSaveDir;
    }
    public String getFileSaveDirV2(LoginUserDetails loginUserDetails) throws AppException {
        String saveDir = this.getFileSaveDir(loginUserDetails);
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (!StaticService.isDirectory(saveDir)) {
            logger.info("Invalid file save dir: {}", saveDir);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return saveDir;
    }

    public PageConfig404 getPageConfig404() {
        return pageConfig404;
    }

    public void setPageConfig404(PageConfig404 pageConfig404) {
        this.pageConfig404 = pageConfig404;
    }

    public void updateFinalFtpConfiguration(final FtpConfiguration ftpConfiguration) {
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
        this.setFtpConfiguration(ftpConfiguration);
        logger.info("FTP configuration generate complete: {}", ftpConfiguration);
    }
    public void updatePageConfig404() {
        YamlFileParser yamlFileParser = new YamlFileParser();
        pageConfig404 = yamlFileParser.getPageConfig404(this);
        logger.info("PageConfig404 update complete: {}", pageConfig404);
    }
    public AppConfigObj getAppConfigObj() {
        return new AppConfigObj(publicDir, configDate, appVersion, cmdArguments,
                logFilePath, requestCount, sessionData, ftpConfiguration, pageConfig404);
    }
    @Override
    public String toString() {
        return this.getAppConfigObj().toString();
    }
}
