package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.ApiIdentifier;
import com.project.ftp.config.ApiRoleAccess;
import com.project.ftp.config.ApiRoleMappingData;
import com.project.ftp.config.AppConfig;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.LoginUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class AuthService {
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AppConfig appConfig;
    private final UserService userService;
    public AuthService(final UserService userService, final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.userService = userService;
    }
    public void isLogin(final HttpServletRequest request) throws AppException {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        if (!userDetails.getLogin()) {
            logger.info("isLogin: Login required");
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
    private ArrayList<String> getApiRoleAccessForApi(ApiIdentifier apiIdentifier) {
        ArrayList<String> result = new ArrayList<>();
        if (apiIdentifier == null) {
            return null;
        }
        if (appConfig == null) {
            return null;
        }
        HashMap<String, ArrayList<ApiRoleMappingData>> apiAuthorisationConfig = appConfig.getApiRoleMappingList();
        ArrayList<ApiRoleMappingData> roleAccess;
        if (apiAuthorisationConfig != null) {
            roleAccess = apiAuthorisationConfig.get(apiIdentifier.getApiName());
            if (roleAccess != null) {
                for (ApiRoleMappingData apiRoleMappingData: roleAccess) {
                    if (apiRoleMappingData != null) {
                        if (apiRoleMappingData.getRole() != null) {
                            result.add(apiRoleMappingData.getRole());
                        }
                    }
                }
            }
        }
        return result;
    }
    private Boolean checkApiAuthorisationV2(final HttpServletRequest request, ApiIdentifier apiIdentifier) {
        if (apiIdentifier == null) {
            return null;
        }
        ArrayList<String> apiRoleAccess = this.getApiRoleAccessForApi(apiIdentifier);
        if (apiRoleAccess == null || apiRoleAccess.isEmpty()) {
            return null;
        }
        boolean temp;
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        for (String apiRoleAccess1: apiRoleAccess) {
            if (apiRoleAccess1 == null) {
                continue;
            }
            if (apiRoleAccess1.equals(ApiRoleAccess.IS_LOGIN.getRoleAccessName())) {
                if (!userDetails.getLogin()) {
                    logger.info("checkApiAuthorisationV2: Login required");
                    return false;
                }
                continue;
            }
            temp = userService.isAuthorisedPermission(userDetails, apiRoleAccess1);
            if (!temp) {
                return false;
            }
        }
        return true;
    }
    public void checkApiAuthorisation(final HttpServletRequest request, ApiIdentifier apiIdentifier) {
        Boolean result = this.checkApiAuthorisationV2(request, apiIdentifier);
        if (result == null) {
            logger.info("checkApiAuthorisation, result: null");
            return;
        }
        if (!result) {
            logger.info("checkApiAuthorisation, result: {}", false);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
    public boolean isLoginV2(final HttpServletRequest request) {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        return userDetails.getLogin();
    }
    public boolean isInfiniteTTLUser(String username) {
        return userService.isAuthorisedV3(username, ApiRoleAccess.IS_INFINITE_TTL_LOGIN_USER.getRoleAccessName());
    }
}
