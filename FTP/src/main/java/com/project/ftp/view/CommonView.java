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
    public CommonView(String pageName, AppConfig appConfig) {
        super(pageName);
        ftlConfig = appConfig.getFtlConfig();
        logger.info("Loading CommonView with page : {}", pageName);
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }
}
