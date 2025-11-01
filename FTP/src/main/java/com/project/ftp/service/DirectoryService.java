package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItemsV2;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.yamlObj.DirConfigParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectoryService {
    private final static Logger logger = LoggerFactory.getLogger(DirectoryService.class);
    private final FtpConfiguration ftpConfiguration;
    private final UserService userService;
    public DirectoryService(final FtpConfiguration ftpConfiguration, final UserService userService) {
        this.ftpConfiguration = ftpConfiguration;
        this.userService = userService;
    }
    public String getDirConfigParamFromUser(FtpConfigItemsV2 ftpConfigItemsV2, String roleId, LoginUserDetails loginUserDetails) {
        if (ftpConfiguration == null || userService == null) {
            return null;
        }
        if (roleId == null || roleId.isEmpty()) {
            roleId = AppConstant.DEFAULT_ROLE_ID;
        }
        HashMap<String, DirConfigParam> dirConfigParam = ftpConfiguration.getDirConfigParam();
        if (dirConfigParam == null) {
            logger.info("dirConfigParam is null in ftpConfiguration");
            return null;
        }
        DirConfigParam dirConfigParam1 = null;
        DirConfigParam dirConfigParam2 = dirConfigParam.get(AppConstant.DEFAULT_ROLE_ID);
        if (!AppConstant.DEFAULT_ROLE_ID.equals(roleId)) {
            if (userService.isAuthorisedPermission(loginUserDetails, roleId)) {
                dirConfigParam1 = dirConfigParam.get(roleId);
            }
        }
        if (dirConfigParam1 == null && dirConfigParam2 == null) {
            logger.info("dirConfigParam is not found for roleId: {}, or {}", roleId, AppConstant.DEFAULT_ROLE_ID);
            return null;
        }
        if (dirConfigParam1 == null) {
            dirConfigParam1 = dirConfigParam2;
        }
        if (dirConfigParam2 == null) {
            dirConfigParam2 = dirConfigParam1;
        }
        String result = null, r1 = null, r2 = null;
        switch (ftpConfigItemsV2) {
            case fileSaveDir:
                r1 = dirConfigParam1.getFileSaveDir();
                r2 = dirConfigParam2.getFileSaveDir();
                break;
            case staticDataFilename:
                r1 = dirConfigParam1.getStaticDataFilename();
                r2 = dirConfigParam2.getStaticDataFilename();
                break;
            case configDataFilePath:
                r1 = dirConfigParam1.getConfigDataFilePath();
                r2 = dirConfigParam2.getConfigDataFilePath();
                break;
            case fileMappingConfigFilePath:
                r1 = dirConfigParam1.getFileMappingConfigFilePath();
                r2 = dirConfigParam2.getFileMappingConfigFilePath();
                break;
            case splitTextFileConfigPath:
                r1 = dirConfigParam1.getSplitTextFileConfigPath();
                r2 = dirConfigParam2.getSplitTextFileConfigPath();
                break;
            case scanDirConfigFilePath:
                r1 = dirConfigParam1.getScanDirConfigFilePath();
                r2 = dirConfigParam2.getScanDirConfigFilePath();
                break;
            case isRelativePath:
                r1 = dirConfigParam1.getIsRelativePath();
                r2 = dirConfigParam2.getIsRelativePath();
                break;
            case assetsDir:
                r1 = dirConfigParam1.getAssetsDir();
                r2 = dirConfigParam2.getAssetsDir();
                break;
            case publicDir:
                r1 = dirConfigParam1.getPublicDir();
                r2 = dirConfigParam2.getPublicDir();
                break;
            case publicPostDir:
                r1 = dirConfigParam1.getPublicPostDir();
                r2 = dirConfigParam2.getPublicPostDir();
                break;
        }
        if (r1 == null || r1.isEmpty()) {
            result = r2;
        } else {
            result = r1;
        }
        return result;
    }
    public ArrayList<String> getDirConfigParamFromUserV2(FtpConfigItemsV2 ftpConfigItemsV2, String roleId, LoginUserDetails loginUserDetails) {
        if (ftpConfiguration == null || userService == null) {
            return null;
        }
        if (roleId == null || roleId.isEmpty()) {
            roleId = AppConstant.DEFAULT_ROLE_ID;
        }
        HashMap<String, DirConfigParam> dirConfigParam = ftpConfiguration.getDirConfigParam();
        if (dirConfigParam == null) {
            logger.info("getDirConfigParamV2: dirConfigParam is null in ftpConfiguration");
            return null;
        }
        DirConfigParam dirConfigParam1 = null;
        DirConfigParam dirConfigParam2 = dirConfigParam.get(AppConstant.DEFAULT_ROLE_ID);
        if (!AppConstant.DEFAULT_ROLE_ID.equals(roleId)) {
            if (loginUserDetails != null && loginUserDetails.getUsername() != null && !loginUserDetails.getUsername().isEmpty()) {
                if (userService.isAuthorisedPermission(loginUserDetails, roleId)) {
                    dirConfigParam1 = dirConfigParam.get(roleId);
                }
            }
        }
        if (dirConfigParam1 == null && dirConfigParam2 == null) {
            logger.info("getDirConfigParamV2: dirConfigParam is not found for roleId: {}, or {}", roleId, AppConstant.DEFAULT_ROLE_ID);
            return null;
        }
        if (dirConfigParam1 == null) {
            dirConfigParam1 = dirConfigParam2;
        }
        if (dirConfigParam2 == null) {
            dirConfigParam2 = dirConfigParam1;
        }
        ArrayList<String> result = null, r1 = null, r2 = null;
        switch (ftpConfigItemsV2) {
            case standAloneConfigPath:
                r1 = dirConfigParam1.getStandAloneConfigPath();
                r2 = dirConfigParam2.getStandAloneConfigPath();
                break;
            case tableDbConfigFilePath:
                r1 = dirConfigParam1.getTableDbConfigFilePath();
                r2 = dirConfigParam2.getTableDbConfigFilePath();
                break;
        }
        if (r1 == null || r1.isEmpty()) {
            result = r2;
        } else {
            result = r1;
        }
        return result;
    }

    public String getDirConfigParamFromRequest(HttpServletRequest request, FtpConfigItemsV2 ftpConfigItemsV2, String roleId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        return this.getDirConfigParamFromUser(ftpConfigItemsV2, roleId, loginUserDetails);
    }
    public ArrayList<String> getDirConfigParamFromRequestV2(HttpServletRequest request, FtpConfigItemsV2 ftpConfigItemsV2, String roleId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        return this.getDirConfigParamFromUserV2(ftpConfigItemsV2, roleId, loginUserDetails);
    }
    public String getConfigPathFromRequest(HttpServletRequest request, String roleId) {
        return this.getDirConfigParamFromRequest(request, FtpConfigItemsV2.configDataFilePath, roleId);
    }
    public String getConfigPathFromUser(LoginUserDetails loginUserDetails, String roleId) {
        return this.getDirConfigParamFromUser(FtpConfigItemsV2.configDataFilePath, roleId, loginUserDetails);
    }
    public String getConfigPathDefault() {
        return this.getDirConfigParamFromRequest(null, FtpConfigItemsV2.configDataFilePath, null);
    }
}
