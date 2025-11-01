package com.project.ftp.helper;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItemsV2;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class AppConfigHelper {
    private final static Logger logger = LoggerFactory.getLogger(AppConfigHelper.class);
    public static String getStaticDataFilename(final AppConfig appConfig, HttpServletRequest request, String roleId) {
        String resultFilename = null;
        boolean isValidFilename = false;
        resultFilename = appConfig.getDirectoryService().getDirConfigParamFromRequest(request, FtpConfigItemsV2.staticDataFilename, roleId);
        isValidFilename = StaticService.isValidString(resultFilename);
        if (isValidFilename) {
            return resultFilename;
        }
        return AppConstant.APP_STATIC_DATA_FILENAME;
    }
    public static String getUserDataFilename(final AppConfig appConfig) {
        String resultFilename = null;
        boolean isValidFilename = false;
        resultFilename = appConfig.getFtpConfiguration().getUserDataFilename();
        isValidFilename = StaticService.isValidString(resultFilename);
        if (isValidFilename) {
            return resultFilename;
        }
        return AppConstant.USER_DATA_FILENAME;
    }
    public static ArrayList<String> getFileNotFoundMapping(final AppConfig appConfig) {
        ArrayList<String> result = appConfig.getFtpConfiguration().getFileNotFoundMapping();
        if (result == null) {
            result = new ArrayList<>();
            result.add(AppConstant.FILE_NOT_FOUND_MAPPING);
        }
        return result;
    }
}
