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
    private String get404Filename(UserService userService, LoginUserDetails userDetails, Page404Entry page404Entry) {
        String filename = null;
        if (page404Entry != null) {
            filename = page404Entry.getFileName();
            if (filename != null) {
                String roleAccess = page404Entry.getRoleAccess();
                if (roleAccess != null) {
                    if (!userService.isAuthorised(userDetails, roleAccess)) {
                        filename = "null";
                        logger.info("unAuthorised page404Entry: {}", page404Entry);
                    }
                }
            }
        }
        return filename;
    }
    public PageConfig404 getPageConfig404(AppConfig appConfig) {
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        String filePath = ftpConfiguration.getConfigDataFilePath();
        if (StaticService.isInValidString(filePath)) {
            return null;
        }
        filePath += AppConstant.FILE_NOT_FOUND_MAPPING;
        PageConfig404 pageConfig404 = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            pageConfig404 = objectMapper.readValue(new File(filePath), PageConfig404.class);
        } catch (IOException ioe) {
            logger.info("IOE : for file : {}", filePath);
        }
        return pageConfig404;
    }

}
