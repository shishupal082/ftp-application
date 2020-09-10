package com.project.ftp.view;

import com.project.ftp.config.AppConfig;
import com.project.ftp.obj.FtlConfig;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shishupalkumar on 11/02/17.
 */
public class CommonView extends View {
    final static Logger logger = LoggerFactory.getLogger(CommonView.class);
    private final FtlConfig ftlConfig;
    public CommonView(HttpServletRequest httpServletRequest, String pageName, AppConfig appConfig) {
        super(pageName);
        ftlConfig = appConfig.getFtlConfig();
        logger.info("Loading CommonView with page : {}", pageName);
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }
}
