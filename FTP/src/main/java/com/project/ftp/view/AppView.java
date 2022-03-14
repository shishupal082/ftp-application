package com.project.ftp.view;

import com.project.ftp.bridge.config.SocialLoginConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.LoginUserDetailsV2;
import com.project.ftp.obj.yamlObj.FtlConfig;
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
    private final String pageName;
    private final String appVersion;
    private final String isGuestEnable;
    private final String loginRedirectUrl;
    private final FtlConfig ftlConfig;
    private final String loginUserDetailsV2Str;
    private final String androidCheckEnable;
    private final String loginWithGmailEnable;
    private final String googleLoginClientId;
    public AppView(HttpServletRequest request, String ftl, String pageName,
                   UserService userService, AppConfig appConfig) {
        super(ftl);
        ftlConfig = appConfig.getFtlConfig();
        SocialLoginConfig socialLoginConfig = appConfig.getFtpConfiguration().getSocialLoginConfig();
        LoginUserDetailsV2 loginUserDetailsV2 = userService.getLoginUserDetailsV2Data(request,
                AppConstant.FromEnvConfig);
        this.loginRedirectUrl = userService.getLoginRedirectUrl(loginUserDetailsV2,
                ftlConfig.getLoginRedirectUrl());
        Boolean isGuestEnableTemp = appConfig.getFtpConfiguration().getGuestEnable();
        String googleLoginClientId = "";
        String loginWithGmailEnable = "false";
        if (isGuestEnableTemp == null) {
            isGuestEnableTemp = false;
        }
        Boolean androidCheckEnableTemp = appConfig.getFtpConfiguration().getAndroidCheckEnable();
        if (androidCheckEnableTemp == null) {
            androidCheckEnableTemp = false;
        }
        if (StaticService.isInValidString(pageName)) {
            pageName = "";
        }
        if (socialLoginConfig != null) {
            loginWithGmailEnable = Boolean.toString(socialLoginConfig.isLoginWithGmail());
            if (AppConstant.TRUE.equals(loginWithGmailEnable) && socialLoginConfig.getGoogleLoginClientId() != null) {
                googleLoginClientId = socialLoginConfig.getGoogleLoginClientId();
            }
        }
        this.loginWithGmailEnable = loginWithGmailEnable;
        this.googleLoginClientId = googleLoginClientId;
        this.pageName = pageName;
        this.androidCheckEnable = Boolean.toString(androidCheckEnableTemp);
        this.isGuestEnable = Boolean.toString(isGuestEnableTemp);
        this.appVersion = AppConstant.AppVersion;
        this.loginUserDetailsV2Str = loginUserDetailsV2.toJsonString();
        logger.info("Loading AppView, page: {}, userDetails: {}", pageName, loginUserDetailsV2);
    }

    public String getPageName() {
        return pageName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getIsGuestEnable() {
        return isGuestEnable;
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }

    public String getLoginUserDetailsV2Str() {
        return loginUserDetailsV2Str;
    }

    public String getLoginRedirectUrl() {
        return loginRedirectUrl;
    }

    public String getAndroidCheckEnable() {
        return androidCheckEnable;
    }

    public String getLoginWithGmailEnable() {
        return loginWithGmailEnable;
    }

    public String getGoogleLoginClientId() {
        return googleLoginClientId;
    }
}
