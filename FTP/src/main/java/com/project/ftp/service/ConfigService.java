package com.project.ftp.service;

import com.project.ftp.common.SysUtils;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.PathInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
        ArrayList<String> cmdArgument = appConfig.getCmdArguments();
        String configPublicDir = appConfig.getFtpConfiguration().getPublicDir();
        String configPublicPostDir = appConfig.getFtpConfiguration().getPublicPostDir();
        String setPublicDir = configPublicPostDir;
        if(!AppConstant.TRUE.equals(cmdArgument.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-2))) {
            setPublicDir = this.getValidPublicDir(systemDir, configPublicDir, configPublicPostDir);
        }
        PathInfo publicDirPathInfo = StaticService.getPathInfo(setPublicDir);
        if (!AppConstant.FOLDER.equals(publicDirPathInfo.getType())) {
            logger.info("calculated publicDir is not a folder: {}", setPublicDir);
        } else {
            logger.info("final publicDir: {}", setPublicDir);
        }
        if (AppConstant.FOLDER.equals(publicDirPathInfo.getType())) {
            appConfig.setPublicDir(setPublicDir);
        } else {
            logger.info("appConfig publicDir set skip.");
        }
    }
}
