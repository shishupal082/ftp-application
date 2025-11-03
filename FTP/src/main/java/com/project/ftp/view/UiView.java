package com.project.ftp.view;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.UiViewObject;
import com.project.ftp.obj.yamlObj.FtlConfig;
import com.project.ftp.obj.yamlObj.PageConfig404;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by shishupalkumar on 11/02/17.
 */
public class UiView extends View {
    private final static Logger logger = LoggerFactory.getLogger(UiView.class);
    private final String appVersion;
    private final String roleId;
    private final FtlConfig ftlConfig;
    private boolean forceLogin;
    private UiViewObject uiViewObject;
    public UiView(AppConfig appConfig, String ftlViewMappingId, String roleId) {
        super(AppConstant.UI_VIEW_FTL_FILENAME);
        ftlConfig = appConfig.getFtlConfig();
        this.appVersion = AppConstant.AppVersion;
        this.forceLogin = true;
        if (roleId == null) {
            this.roleId = AppConstant.DEFAULT_ROLE_ID;
        } else {
            this.roleId = roleId;
        }
        PageConfig404 pageConfig404 = appConfig.getPageConfig404();
        uiViewObject = null;
        if (pageConfig404 != null && ftlViewMappingId != null) {
            HashMap<String, UiViewObject> uiViewObjectHashMap = pageConfig404.getFtlViewMapping();
            if (uiViewObjectHashMap != null) {
                uiViewObject = uiViewObjectHashMap.get(ftlViewMappingId);
            }
        }
        if (uiViewObject == null) {
            uiViewObject = new UiViewObject();
        }
        if (uiViewObject.getForceLogin() != null) {
            forceLogin = uiViewObject.getForceLogin();
        }
        logger.info("Loading UiView.");
    }
    public String getAppVersion() {
        return appVersion;
    }

    public String getRoleId() {
        return roleId;
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }

    public boolean isForceLogin() {
        return forceLogin;
    }

    public UiViewObject getUiViewObject() {
        return uiViewObject;
    }
}
