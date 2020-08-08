package com.project.ftp.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.project.ftp.obj.PreRunConfig;
import com.project.ftp.service.StaticService;
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

}
