package com.project.ftp.helper;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.yamlObj.BackendConfig;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AppConfigHelper {
    private final static Logger logger = LoggerFactory.getLogger(AppConfigHelper.class);
    public static String getStaticDataFilename(final AppConfig appConfig) {
        BackendConfig backendConfig = appConfig.getFtpConfiguration().getBackendConfig();
        String resultFilename = null;
        boolean isValidFilename = false;
        if (backendConfig != null) {
            resultFilename = backendConfig.getStaticDataFilename();
            isValidFilename = StaticService.isValidString(resultFilename);
        }
        if (isValidFilename) {
            return resultFilename;
        }
        return AppConstant.APP_STATIC_DATA_FILENAME;
    }
    public static String getUserDataFilename(final AppConfig appConfig) {
        BackendConfig backendConfig = appConfig.getFtpConfiguration().getBackendConfig();
        String resultFilename = null;
        boolean isValidFilename = false;
        if (backendConfig != null) {
            resultFilename = backendConfig.getUserDataFilename();
            isValidFilename = StaticService.isValidString(resultFilename);
        }
        if (isValidFilename) {
            return resultFilename;
        }
        return AppConstant.USER_DATA_FILENAME;
    }
    public static String getFileDataFilename(final AppConfig appConfig) {
        BackendConfig backendConfig = appConfig.getFtpConfiguration().getBackendConfig();
        String resultFilename = null;
        boolean isValidFilename = false;
        if (backendConfig != null) {
            resultFilename = backendConfig.getFileDataFilename();
            isValidFilename = StaticService.isValidString(resultFilename);
        }
        if (isValidFilename) {
            return resultFilename;
        }
        return AppConstant.FILE_DATA_FILENAME;
    }
    public static ArrayList<String> getFileNotFoundMapping(final AppConfig appConfig) {
        BackendConfig backendConfig = appConfig.getFtpConfiguration().getBackendConfig();
        ArrayList<String> result = null;
        if (backendConfig != null) {
            result = backendConfig.getFileNotFoundMapping();
        }
        if (result == null) {
            result = new ArrayList<>();
            result.add(AppConstant.FILE_NOT_FOUND_MAPPING);
        }
        return result;
    }
}
