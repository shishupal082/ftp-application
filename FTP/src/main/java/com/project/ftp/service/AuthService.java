package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.LoginUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class AuthService {
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AppConfig appConfig;
    private final UserService userService;
    public AuthService(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
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

    public void isLoginUserAdmin(HttpServletRequest request) throws AppException {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!loginUserDetails.getLoginUserAdmin(appConfig)) {
            logger.info("UnAuthorised user trying to access admin data: {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }

    public void isLoginUserDev(HttpServletRequest request) throws AppException {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!loginUserDetails.getLoginUserDev(appConfig)) {
            logger.info("UnAuthorised user: not dev user, {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
}
