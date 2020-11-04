package com.project.ftp.service;

import com.project.ftp.common.SysUtils;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.PathInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigService {
    private final static Logger logger = LoggerFactory.getLogger(ConfigService.class);
    private final AppConfig appConfig;
    private final SysUtils sysUtils = new SysUtils();
    public ConfigService(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    public String getValidPublicDir(String systemDir, String orgPublicDir, String publicPostDir) {
        systemDir = StaticService.replaceBackSlashToSlash(systemDir);
        logger.info("systemDir: {}", systemDir);
        logger.info("configPublicDir: {}", orgPublicDir);
        logger.info("configPublicPostDir: {}", publicPostDir);
        if (orgPublicDir == null) {
            orgPublicDir = "";
        }
        if (systemDir == null) {
            systemDir = "";
        }
        String[] publicDirArr = orgPublicDir.split("/");
        String[] systemDirArr;
        if (systemDir.contains("/")) {
            systemDirArr = StaticService.splitStringOnLimit(systemDir, "/",-1);
        } else {
            // Fix for windows system
            systemDirArr = systemDir.split("\\\\");
        }
        int j = systemDirArr.length-1;
        for (int i=publicDirArr.length-1; i>=0; i--) {
            if (j>=0 && publicDirArr[i].equals("..")) {
                systemDirArr[j] = "/";
                j--;
            }
        }
        String setPublicDir = "";
        for (int i=0; i<systemDirArr.length; i++) {
            if (!systemDirArr[i].equals("/")) {
                setPublicDir += systemDirArr[i] + "/";
            }
        }
        if (publicPostDir != null) {
            setPublicDir += publicPostDir;
        }
        return StaticService.getProperDirString(setPublicDir);
    }
    public void setPublicDir() {
        String systemDir = sysUtils.getProjectWorkingDir();
        String configPublicDir = appConfig.getFtpConfiguration().getPublicDir();
        String configPublicPostDir = appConfig.getFtpConfiguration().getPublicPostDir();
        String setPublicDir = this.getValidPublicDir(systemDir, configPublicDir, configPublicPostDir);
        logger.info("Calculated PublicDir: {}", setPublicDir);
        if (configPublicDir != null) {
            appConfig.setPublicDir(setPublicDir);
        } else {
            logger.info("appConfig publicDir set skip.");
        }
        String fileSaveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        PathInfo pathInfo = StaticService.getPathInfo(fileSaveDir);
        if (!AppConstant.FOLDER.equals(pathInfo.getType())) {
            logger.info("File save directory is not a folder: {}, setting as publicDir + /saved-files/", fileSaveDir);
            appConfig.getFtpConfiguration().setFileSaveDir(setPublicDir + "/saved-files/");
        } else {
            logger.info("File save directory is a folder: {}", fileSaveDir);
        }
    }
}
