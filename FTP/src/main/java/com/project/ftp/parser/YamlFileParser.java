package com.project.ftp.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.Page404Entry;
import com.project.ftp.obj.PageConfig404;
import com.project.ftp.obj.PreRunConfig;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YamlFileParser {
    final static Logger logger = LoggerFactory.getLogger(YamlFileParser.class);
    public YamlFileParser() {}
    private PreRunConfig getPreRunConfig(String configFilePath) {
        if (configFilePath == null) {
            return null;
        }
        PreRunConfig preRunConfig = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            preRunConfig = objectMapper.readValue(new File(configFilePath), PreRunConfig.class);
        } catch (IOException ioe) {
            StaticService.printLog("IOE : for file : " + configFilePath);
        }
        return preRunConfig;
    }
    public String getLogFilePath(String configFilePath) {
        if (configFilePath == null) {
            return null;
        }
        PreRunConfig preRunConfig = this.getPreRunConfig(configFilePath);
        if (preRunConfig != null) {
            return preRunConfig.getLogFilePath();
        }
        return null;
    }
    public boolean isMysqlEnable(String configFilePath) {
        if (configFilePath == null) {
            return false;
        }
        PreRunConfig preRunConfig = this.getPreRunConfig(configFilePath);
        if (preRunConfig != null) {
            return preRunConfig.isMysqlEnable();
        }
        return false;
    }

    public String getFileNotFoundMapping(AppConfig appConfig, UserService userService,
                                         String requestPath, LoginUserDetails userDetails) {
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        String filePath = ftpConfiguration.getConfigDataFilePath();
        if (StaticService.isInValidString(requestPath) && StaticService.isInValidString(filePath)) {
            return null;
        }
        filePath += AppConstant.FILE_NOT_FOUND_MAPPING;
        String newFilePath = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        PageConfig404 pageConfig404 = null;
        try {
            pageConfig404 = objectMapper.readValue(new File(filePath), PageConfig404.class);
        } catch (IOException ioe) {
            logger.info("IOE : for file : {}", filePath);
        }
        if (pageConfig404 != null) {
            HashMap<String, Page404Entry> pageMapping = pageConfig404.getPageMapping404();
            if (pageMapping != null) {
                Page404Entry page404Entry = pageMapping.get(requestPath);
                if (page404Entry != null) {
                    String newFilePath2 = page404Entry.getFileName();
                    if (newFilePath2 != null) {
                        String roleAccess = page404Entry.getRoleAccess();
                        if (roleAccess != null) {
                            if (userService.isAuthorised(userDetails, roleAccess)) {
                                newFilePath = newFilePath2;
                                logger.info("filePath changes from :{}, to :{}", requestPath, newFilePath);
                            } else {
                                newFilePath = "null";
                                logger.info("unAuthorised requestedPath: {}", requestPath);
                                logger.info("filePath changes from :{}, to :{}", requestPath, newFilePath);
                            }
                        } else {
                            newFilePath = newFilePath2;
                            logger.info("roleAccess is null for requestedPath: {}", requestPath);
                        }
                    } else {
                        newFilePath = requestPath;
                        logger.info("newFilePath2 is null for requestedPath: {}", requestPath);
                    }
                } else {
                    newFilePath = requestPath;
                    logger.info("page404Entry is null for requestedPath: {}", requestPath);
                }
            }
        }
        return newFilePath;
    }
}
