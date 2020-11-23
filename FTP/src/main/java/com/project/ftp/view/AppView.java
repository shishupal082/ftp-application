package com.project.ftp.view;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.FtlConfig;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.UiBackendConfig;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shishupalkumar on 11/02/17.
 */
public class AppView extends View {
    private final static Logger logger = LoggerFactory.getLogger(AppView.class);
    private final String appVersion;
    private final String pageName;
    private final String isLogin;
    private final String userName;
    private final String userDisplayName;
    private final String isLoginUserAdmin;
    private final String uploadFileApiVersion;
    private final String isGuestEnable;
    private final String isForgotPasswordEnable;
    private final FtlConfig ftlConfig;
    public AppView(HttpServletRequest request, String ftl, String pageName,
                   UserService userService, AppConfig appConfig) {
        super(ftl);
        ftlConfig = appConfig.getFtlConfig();
        UiBackendConfig uiBackendConfig = appConfig.getFtpConfiguration().getUiBackendConfig();
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        this.pageName = pageName;
        this.userName = loginUserDetails.getUsername();
        this.isLogin = Boolean.toString(loginUserDetails.getLogin());
        this.isLoginUserAdmin = Boolean.toString(userService.isLoginUserAdmin(loginUserDetails));
        this.isGuestEnable = Boolean.toString(appConfig.getFtpConfiguration().isGuestEnable());
        this.isForgotPasswordEnable = Boolean.toString(uiBackendConfig.isForgotPasswordEnable());
        this.appVersion = AppConstant.AppVersion;
        this.userDisplayName = "";
        this.uploadFileApiVersion = StaticService.getUploadFileApiVersion(appConfig);
        logger.info("Loading AppView, page: {}, userDetails: {}", pageName, loginUserDetails);
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getPageName() {
        return pageName;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getUploadFileApiVersion() {
        return uploadFileApiVersion;
    }

    public String getIsLoginUserAdmin() {
        return isLoginUserAdmin;
    }

    public String getIsGuestEnable() {
        return isGuestEnable;
    }

    public String getIsForgotPasswordEnable() {
        return isForgotPasswordEnable;
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }
}
