package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItems;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppExceptionMapper;
import com.project.ftp.filters.LogFilter;
import com.project.ftp.filters.RequestFilter;
import com.project.ftp.filters.ResponseFilter;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.resources.AppResource;
import com.project.ftp.resources.FaviconResource;
import com.project.ftp.service.StaticService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.server.session.SessionHandler;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;


public class FtpApplication extends Application<FtpConfiguration> {
    final static Logger logger = LoggerFactory.getLogger(FtpApplication.class);
    final static ArrayList<String> arguments = new ArrayList<>();

    private final HibernateBundle<FtpConfiguration> hibernateBundle
            = new HibernateBundle<FtpConfiguration>(
            MysqlUser.class
    ) {
        @Override
        public DataSourceFactory getDataSourceFactory(FtpConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
    @Override
    public void initialize(Bootstrap<FtpConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets"));
        if (StaticService.isMysqlEnable(arguments)) {
            bootstrap.addBundle(hibernateBundle);
        }
    }
    @Override
    public void run(FtpConfiguration ftpConfiguration, Environment environment) {
        logger.info("commandLineArguments: {}", arguments.toString());
        ArrayList<FtpConfigItems> firstPageConfigItems = AppConfig.getFirstPageConfigItems(ftpConfiguration);
        AppConfig appConfig = AppConfig.getAppConfig(hibernateBundle,  ftpConfiguration,
                arguments, firstPageConfigItems, AppConstant.SOURCE_RUNTIME);
        if (appConfig == null) {
            logger.info("Error in generating appConfig");
            return;
        }
        EventTracking eventTracking = appConfig.getEventTracking();
        environment.servlets().setSessionHandler(new SessionHandler());
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(new AppExceptionMapper(eventTracking));
        environment.jersey().register(new LogFilter(appConfig));
        environment.jersey().register(new RequestFilter(appConfig, eventTracking));
        environment.jersey().register(new ResponseFilter(appConfig));
        environment.jersey().register(new FaviconResource(appConfig));

        environment.jersey().register(new ApiResource(appConfig));
        environment.jersey().register(new AppResource(appConfig));
//        environment.admin().addTask(shutdownTask);
        eventTracking.trackApplicationStart(ftpConfiguration.getInstance());
    }
    public static void main(String[] args) throws Exception {
        // java -jar meta-data/FTP-*-SNAPSHOT.jar <server/standalone> <isMySqlEnable> <isStaticPath> <config file 1> <config file 2> ...
        arguments.addAll(Arrays.asList(args));
        logger.info("main: command line argument: {}", arguments);
        if (arguments.size() >= AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            if (AppConstant.SERVER.equals(arguments.get(AppConstant.CMD_APPLICATION_TYPE))) {
                StaticService.renameOldLogFile(args[AppConstant.CMD_IS_STATIC_PATH], args[AppConstant.CMD_FIRST_CONFIG_PATH]);
                new FtpApplication().run(AppConstant.SERVER, args[AppConstant.CMD_FIRST_CONFIG_PATH]);
            } else {
                StandAlone standAlone = new StandAlone(arguments);
                standAlone.handleRequest();
            }
        } else {
            logger.info("main: minimum required command line argument is: {}, found: {}",
                    AppConstant.CMD_LINE_ARG_MIN_SIZE, arguments);
        }
    }
}
