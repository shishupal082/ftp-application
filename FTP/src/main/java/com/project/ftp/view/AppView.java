package com.project.ftp.view;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.FtlConfig;
import com.project.ftp.obj.LoginUserDetailsV2;
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
    private final String uploadFileApiVersion;
    private final String isGuestEnable;
    private final FtlConfig ftlConfig;
    private final String loginUserDetailsV2Str;
    public AppView(HttpServletRequest request, String ftl, String pageName,
                   UserService userService, AppConfig appConfig) {
        super(ftl);
        ftlConfig = appConfig.getFtlConfig();
        LoginUserDetailsV2 loginUserDetailsV2 = userService.getLoginUserDetailsV2Data(request,
                AppConstant.FromEnvConfig);
        this.pageName = pageName;
        this.isGuestEnable = Boolean.toString(appConfig.getFtpConfiguration().isGuestEnable());
        this.appVersion = AppConstant.AppVersion;
        this.uploadFileApiVersion = StaticService.getUploadFileApiVersion(appConfig);
        this.loginUserDetailsV2Str = loginUserDetailsV2.toJsonString();
        logger.info("Loading AppView, page: {}, userDetails: {}", pageName, loginUserDetailsV2);
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getPageName() {
        return pageName;
    }

    public String getUploadFileApiVersion() {
        return uploadFileApiVersion;
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
}
