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
    private ArrayList<DirConfigParam> getDirConfigParam(LoginUserDetails loginUserDetails, String roleId) {
        if (ftpConfiguration == null || userService == null) {
            return null;
        }
        if (roleId == null || roleId.isEmpty()) {
            roleId = AppConstant.DEFAULT_ROLE_ID;
        }
        HashMap<String, DirConfigParam> dirConfigParamHashMap = ftpConfiguration.getDirConfigParam();
        if (dirConfigParamHashMap == null) {
            logger.info("getDirConfigParam: dirConfigParamHashMap is null in ftpConfiguration");
            return null;
        }
        DirConfigParam dirConfigParam = null;
        DirConfigParam dirConfigParamDefault = dirConfigParamHashMap.get(AppConstant.DEFAULT_ROLE_ID);
        if (!AppConstant.DEFAULT_ROLE_ID.equals(roleId)) {
            dirConfigParam = dirConfigParamHashMap.get(roleId);
            if (dirConfigParam != null) {
                String checkPermission = dirConfigParam.getCheckPermission();
                if (AppConstant.TRUE.equals(checkPermission)) {
                    if (loginUserDetails != null && loginUserDetails.getUsername() != null && !loginUserDetails.getUsername().isEmpty()) {
                        if (!userService.isAuthorisedPermission(loginUserDetails, roleId)) {
                            dirConfigParam = null;
                        }
                    }
                }
            }
        }
        if (dirConfigParam == null && dirConfigParamDefault == null) {
            logger.info("getDirConfigParam: dirConfigParam is not found for roleId: {}, or {}", roleId, AppConstant.DEFAULT_ROLE_ID);
            return null;
        }
        if (dirConfigParam == null) {
            dirConfigParam = dirConfigParamDefault;
        }
        if (dirConfigParamDefault == null) {
            dirConfigParamDefault = dirConfigParam;
        }
        ArrayList<DirConfigParam> result = new ArrayList<>();
        result.add(dirConfigParam);
        result.add(dirConfigParamDefault);
        return result;
    }
    public String getDirConfigParamFromUser(FtpConfigItemsV2 ftpConfigItemsV2, String roleId, LoginUserDetails loginUserDetails) {
        ArrayList<DirConfigParam> dirConfigParams = this.getDirConfigParam(loginUserDetails, roleId);
        if (dirConfigParams == null || dirConfigParams.size() < 2) {
            return null;
        }
        DirConfigParam dirConfigParam = dirConfigParams.get(0);
        DirConfigParam dirConfigParamDefault = dirConfigParams.get(1);
        if (dirConfigParam == null || dirConfigParamDefault == null) {
            return null;
        }
        String r1 = null, r2 = null;
        switch (ftpConfigItemsV2) {
            case fileSaveDir:
                r1 = dirConfigParam.getFileSaveDir();
                r2 = dirConfigParamDefault.getFileSaveDir();
                break;
            case staticDataFilename:
                r1 = dirConfigParam.getStaticDataFilename();
                r2 = dirConfigParamDefault.getStaticDataFilename();
                break;
            case configDataFilePath:
                r1 = dirConfigParam.getConfigDataFilePath();
                r2 = dirConfigParamDefault.getConfigDataFilePath();
                break;
            case fileMappingConfigFilePath:
                r1 = dirConfigParam.getFileMappingConfigFilePath();
                r2 = dirConfigParamDefault.getFileMappingConfigFilePath();
                break;
            case splitTextFileConfigPath:
                r1 = dirConfigParam.getSplitTextFileConfigPath();
                r2 = dirConfigParamDefault.getSplitTextFileConfigPath();
                break;
            case scanDirConfigFilePath:
                r1 = dirConfigParam.getScanDirConfigFilePath();
                r2 = dirConfigParamDefault.getScanDirConfigFilePath();
                break;
            case assetsDir:
                r1 = dirConfigParam.getAssetsDir();
                r2 = dirConfigParamDefault.getAssetsDir();
                break;
            case publicPostDir:
                r1 = dirConfigParam.getPublicPostDir();
                r2 = dirConfigParamDefault.getPublicPostDir();
                break;
        }
        if (r1 == null || r1.isEmpty()) {
            return r2;
        }
        return r1;
    }
    public ArrayList<String> getDirConfigParamFromUserV2(FtpConfigItemsV2 ftpConfigItemsV2, String roleId, LoginUserDetails loginUserDetails) {
        ArrayList<DirConfigParam> dirConfigParams = this.getDirConfigParam(loginUserDetails, roleId);
        if (dirConfigParams == null || dirConfigParams.size() < 2) {
            return null;
        }
        DirConfigParam dirConfigParam = dirConfigParams.get(0);
        DirConfigParam dirConfigParamDefault = dirConfigParams.get(1);
        if (dirConfigParam == null || dirConfigParamDefault == null) {
            return null;
        }
        ArrayList<String> r1 = null, r2 = null;
        switch (ftpConfigItemsV2) {
            case standAloneConfigPath:
                r1 = dirConfigParam.getStandAloneConfigPath();
                r2 = dirConfigParamDefault.getStandAloneConfigPath();
                break;
            case tableDbConfigFilePath:
                r1 = dirConfigParam.getTableDbConfigFilePath();
                r2 = dirConfigParamDefault.getTableDbConfigFilePath();
                break;
        }
        if (r1 == null || r1.isEmpty()) {
            return r2;
        }
        return r1;
    }

    public String getDirConfigParamFromRequest(HttpServletRequest request, FtpConfigItemsV2 ftpConfigItemsV2, String roleId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        return this.getDirConfigParamFromUser(ftpConfigItemsV2, roleId, loginUserDetails);
    }
    public ArrayList<String> getDirConfigParamFromRequestV2(HttpServletRequest request, FtpConfigItemsV2 ftpConfigItemsV2, String roleId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        return this.getDirConfigParamFromUserV2(ftpConfigItemsV2, roleId, loginUserDetails);
    }
    public String getConfigPathDefault() {
        if (ftpConfiguration != null) {
            return ftpConfiguration.getCommonConfigFilePath();
        }
        return null;
    }
}
