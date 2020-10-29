package com.project.ftp.view;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.FtlConfig;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by shishupalkumar on 11/02/17.
 */
public class IndexView extends View {
    final static Logger logger = LoggerFactory.getLogger(IndexView.class);
    private String indexPageReRoute;
    final String appVersion;
    private final FtlConfig ftlConfig;
    public IndexView(String indexPageReRoute, AppConfig appConfig) {
        super("index.ftl");
        ftlConfig = appConfig.getFtlConfig();
        this.indexPageReRoute = indexPageReRoute;
        if (this.indexPageReRoute == null) {
            this.indexPageReRoute = "";
        }
        this.appVersion = AppConstant.AppVersion;
        logger.info("Loading IndexView.");
    }
    public String getAppVersion() {
        return appVersion;
    }
    public String getIndexPageReRoute() {
        return indexPageReRoute;
    }
    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }
}
