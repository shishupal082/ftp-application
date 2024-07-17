package com.project.ftp.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.obj.yamlObj.ExcelConfig;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.FileMappingConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.helper.AppConfigHelper;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.PreRunConfig;
import com.project.ftp.obj.yamlObj.DatabaseConfig;
import com.project.ftp.obj.yamlObj.Page404Entry;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    public FtpConfiguration getFtpConfigurationFromPath(String isStaticPath, String configPath) {
        String projectWorkingDir = StaticService.getProjectWorkingDir();
        String pathname = configPath;
        if (configPath == null) {
            return null;
        }
        if (!AppConstant.TRUE.equals(isStaticPath)) {
            pathname = projectWorkingDir + "/" + pathname;
        }
        FtpConfiguration ftpConfiguration = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            ftpConfiguration = objectMapper.readValue(new File(pathname), FtpConfiguration.class);
        } catch (IOException ioe) {
            StaticService.printLog("IOE: for file: " + pathname);
        }
        return ftpConfiguration;
    }
    public DatabaseConfig getDatabaseConfig(String isStaticPath, String configPath) {
        String projectWorkingDir = StaticService.getProjectWorkingDir();
        String pathname = configPath;
        if (configPath == null) {
            return null;
        }
        if (!AppConstant.TRUE.equals(isStaticPath)) {
            pathname = projectWorkingDir + "/" + pathname;
        }
        DatabaseConfig databaseConfig = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            databaseConfig = objectMapper.readValue(new File(pathname), DatabaseConfig.class);
        } catch (IOException ioe) {
            logger.info("IOE: for file: {}", pathname);
        }
        return databaseConfig;
    }
    public FileMappingConfig getFileMappingConfigFromPath(String staticPath) {
        if (staticPath == null || staticPath.isEmpty()) {
            logger.info("Static Path for reading fileMappingConfig is invalid: {}", staticPath);
            return null;
        }
        FileMappingConfig fileMappingConfig = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            fileMappingConfig = objectMapper.readValue(new File(staticPath), FileMappingConfig.class);
        } catch (IOException ioe) {
            logger.info("IOE : for file : {}", staticPath);
        }
        return fileMappingConfig;
    }
    private HashMap<String, ExcelDataConfig> getExcelDataConfigFromPath(String staticPath) throws AppException {
        if (staticPath == null || staticPath.isEmpty()) {
            logger.info("Static Path for reading getExcelDataConfigFromPath is invalid: {}", staticPath);
            return null;
        }
        ExcelConfig excelConfig;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            excelConfig = objectMapper.readValue(new File(staticPath), ExcelConfig.class);
        } catch (IOException ioe) {
            logger.info("IOE: for file: {}", staticPath);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return excelConfig.getExcelDataConfig();
    }
    public HashMap<String, ExcelDataConfig> getExcelDataConfig(ArrayList<String> excelConfigFilePaths)
            throws AppException {
        HashMap<String, ExcelDataConfig> excelDataConfigHashMap = new HashMap<>();
        HashMap<String, ExcelDataConfig> temp;
        ArrayList<String> validForIds;
        HashMap<String, String> temp2 = new HashMap<>();
        String key, newKey;
        ExcelDataConfig value;
        if (excelConfigFilePaths == null) {
            return null;
        }
        for(String filePath: excelConfigFilePaths) {
            temp = this.getExcelDataConfigFromPath(filePath);
            if (temp != null) {
                for(Map.Entry<String, ExcelDataConfig> entry: temp.entrySet()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (value != null) {
                        validForIds = value.getValidFor();
                        if (validForIds != null) {
                            for(String str: validForIds) {
                                temp2.put(str, key);
                            }
                        }
                    }
                }
                excelDataConfigHashMap.putAll(temp);
            }
        }
        for(Map.Entry<String, String> entry: temp2.entrySet()) {
            newKey = entry.getKey();
            key = entry.getValue();
            if (key != null && !excelDataConfigHashMap.containsKey(newKey) && excelDataConfigHashMap.containsKey(key)) {
                excelDataConfigHashMap.put(newKey, excelDataConfigHashMap.get(key));
            }
        }
        return excelDataConfigHashMap;
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
    private PageConfig404 getPageConfig404ByFilepath(String filePath) {
        if (StaticService.isInValidString(filePath)) {
            return null;
        }
        PageConfig404 pageConfig404 = null;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            pageConfig404 = objectMapper.readValue(new File(filePath), PageConfig404.class);
        } catch (IOException ioe) {
            logger.info("IOE : for file : {}", filePath);
        }
        /* Remove relative path for 404 page mapping fileName */
        if (pageConfig404 != null) {
            HashMap<String, Page404Entry> pageMapping404 = pageConfig404.getPageMapping404();
            if (pageMapping404 != null) {
                String str;
                Page404Entry page404Entry;
                for (Map.Entry<String, Page404Entry> el: pageMapping404.entrySet()) {
                    page404Entry = el.getValue();
                    if (page404Entry == null) {
                        continue;
                    }
                    str = StaticService.removeRelativePath(page404Entry.getFileName());
                    page404Entry.setFileName(str);
                    el.setValue(page404Entry);
                }
            }
        }
        return pageConfig404;
    }
    public PageConfig404 getPageConfig404(AppConfig appConfig) {
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        String configDataFilePath = ftpConfiguration.getConfigDataFilePath();
        if (StaticService.isInValidString(configDataFilePath)) {
            return null;
        }
        ArrayList<String> fileNotFoundConfigFiles = AppConfigHelper.getFileNotFoundMapping(appConfig);
        PageConfig404 pageConfig404 = new PageConfig404();
        PageConfig404 tempPageConfig404;
        for(int i=0; i<fileNotFoundConfigFiles.size(); i++) {
            tempPageConfig404 = this.getPageConfig404ByFilepath(configDataFilePath + fileNotFoundConfigFiles.get(i));
            pageConfig404.update(tempPageConfig404);
        }
        return pageConfig404;
    }

}