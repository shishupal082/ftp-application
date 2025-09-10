package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.ApiIdentifier;
import com.project.ftp.config.ApiRoleAccess;
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
    private ArrayList<ApiRoleAccess> getApiRoleAccessForApi(ApiIdentifier apiIdentifier) {
        ArrayList<ApiRoleAccess> roleAccesses = new ArrayList<>();
        if (apiIdentifier == null) {
            return null;
        }
        if (appConfig == null) {
            return null;
        }
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        if (ftpConfiguration == null) {
            return null;
        }
        HashMap<String, ArrayList<String>> apiAuthorisationConfig = ftpConfiguration.getApiAuthorisationConfig();
        ArrayList<String> roleAccess;
        ApiRoleAccess apiRoleAccess;
        if (apiAuthorisationConfig != null) {
            roleAccess = apiAuthorisationConfig.get(apiIdentifier.getApiName());
            if (roleAccess != null) {
                for (String roleName: roleAccess) {
                    apiRoleAccess = ApiRoleAccess.get(roleName);
                    if (apiRoleAccess != null) {
                        roleAccesses.add(apiRoleAccess);
                    }
                }
            }
        }
        return roleAccesses;
    }
    private Boolean checkApiAuthorisationV2(final HttpServletRequest request, ApiIdentifier apiIdentifier) {
        if (apiIdentifier == null) {
            return null;
        }
        ArrayList<ApiRoleAccess> apiRoleAccess = this.getApiRoleAccessForApi(apiIdentifier);
        if (apiRoleAccess == null || apiRoleAccess.isEmpty()) {
            return null;
        }
        boolean temp;
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        for (ApiRoleAccess apiRoleAccess1: apiRoleAccess) {
            if (apiRoleAccess1 == null) {
                continue;
            }
            if (apiRoleAccess1 == ApiRoleAccess.IS_LOGIN) {
                if (!userDetails.getLogin()) {
                    logger.info("checkApiAuthorisationV2: Login required");
                    return false;
                }
                continue;
            }
            temp = userService.isAuthorised(userDetails, apiRoleAccess1);
            if (!temp) {
                return false;
            }
        }
        return true;
    }
    public void checkApiAuthorisation(final HttpServletRequest request, ApiIdentifier apiIdentifier) {
        Boolean result = this.checkApiAuthorisationV2(request, apiIdentifier);
        if (result == null) {
            return;
        }
        if (!result) {
            logger.info("checkApiAuthorisation: result: {}", result);
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
