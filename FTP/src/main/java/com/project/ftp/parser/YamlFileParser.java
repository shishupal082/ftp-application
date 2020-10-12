package com.project.ftp.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.PageConfig404;
import com.project.ftp.obj.PreRunConfig;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

    public String getFileNotFoundMapping(AppConfig appConfig, String requestPath) {
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
            Map pageMapping = pageConfig404.getPageMapping404();
            if (pageMapping.get(requestPath) != null) {
                newFilePath = (String) pageMapping.get(requestPath);
                logger.info("filePath changes from :{}, to :{}", requestPath, newFilePath);
            }
        }
        return newFilePath;
    }
}
