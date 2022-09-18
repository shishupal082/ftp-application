package com.project.ftp.service;

import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.LoginUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class AuthService {
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    public AuthService(final UserService userService) {
        this.userService = userService;
    }
    public void isLogin(final HttpServletRequest request) throws AppException {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        if (!userDetails.getLogin()) {
            logger.info("Login required");
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
    public boolean isLoginV2(final HttpServletRequest request) {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        return userDetails.getLogin();
    }
    public boolean isAuthorised(final HttpServletRequest request, String roleAccess) throws AppException {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        if (!userDetails.getLogin()) {
            logger.info("Login required");
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        boolean isAuthorised = userService.isAuthorised(userDetails, roleAccess);
        if (!isAuthorised) {
            logger.info("Unauthorised role access: {}, {}", userDetails, roleAccess);
            ErrorCodes errorCodes = ErrorCodes.UNAUTHORIZED_ROLE_ACCESS;
            errorCodes.setErrorString("UnAuthorized Role Access: " + roleAccess);
            throw new AppException(errorCodes);
        }
        return true;
    }
    public void isLoginOtherUserEnable(HttpServletRequest request) throws AppException {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        if (!userService.isLoginOtherUserEnable(userDetails)) {
            logger.info("UnAuthorised user trying to login other user: {}", userDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
    public boolean isLoginUserAdmin(HttpServletRequest request) throws AppException {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        if (!userService.isLoginUserAdmin(userDetails)) {
            logger.info("UnAuthorised user trying to access admin data: {}", userDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        return true;
    }
    public void isControlGroupUser(HttpServletRequest request) throws AppException {
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        if (!userService.isControlGroupUser(userDetails)) {
            logger.info("UnAuthorised user trying to access control group user data: {}", userDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
    /* used for
     * get_app_config
     * get_session_config
     * get_roles_config
     * aes_encrypt
     * aes_decrypt
     * md5_encrypt
    * */
    public void isLoginUserDev(HttpServletRequest request) throws AppException {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!userService.isLoginUserDev(loginUserDetails)) {
            logger.info("UnAuthorised user: not dev user, {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
    public boolean isInfiniteTTLUser(String username) {
        return userService.isAuthorisedV3(username, AppConstant.IS_INFINITE_TTL_LOGIN_USER);
    }
}
