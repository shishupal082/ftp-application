package com.project.ftp.view;

import com.project.ftp.config.AppConfig;
import com.project.ftp.obj.yamlObj.FtlConfig;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by shishupalkumar on 11/02/17.
 */
public class CommonView extends View {
    final static Logger logger = LoggerFactory.getLogger(CommonView.class);
    private final FtlConfig ftlConfig;
    private String pageData;
    public CommonView(String pageName, AppConfig appConfig, String viewPageData) {
        super(pageName);
        ftlConfig = appConfig.getFtlConfig();
        if (viewPageData == null) {
            pageData = "";
        } else {
            pageData = viewPageData;
        }
        logger.info("Loading CommonView with page: {} and pageData: {}", pageName, pageData);
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }
    public String getPageData() {
        return pageData;
    }
}
