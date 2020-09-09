package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppExceptionMapper;
import com.project.ftp.filters.LogFilter;
import com.project.ftp.filters.RequestFilter;
import com.project.ftp.filters.ResponseFilter;
import com.project.ftp.intreface.*;
import com.project.ftp.mysql.DbDAO;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.resources.AppResource;
import com.project.ftp.resources.FaviconResource;
import com.project.ftp.service.AuthService;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
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


public class FtpApplication  extends Application<FtpConfiguration> {
    final static Logger LOGGER = LoggerFactory.getLogger(FtpApplication.class);
    final static ArrayList<String> arguments = new ArrayList<String>();

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
        bootstrap.addBundle(new ViewBundle<FtpConfiguration>());
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets"));
        if (StaticService.isMysqlEnable(arguments.get(0))) {
            bootstrap.addBundle(hibernateBundle);
        }
    }
    @Override
    public void run(FtpConfiguration ftpConfiguration, Environment environment) throws Exception {
        LOGGER.info("commandLineArguments: " + arguments.toString());
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigPath(arguments.get(0));
//        ShutdownTask shutdownTask = new ShutdownTask(appConfig);
//        appConfig.setShutdownTask(shutdownTask);
        appConfig.setFtpConfiguration(ftpConfiguration);
        StaticService.initApplication(appConfig);
        LOGGER.info("appConfig: {}", appConfig);

        EventInterface eventInterface;
        UserInterface userInterface;
        if (appConfig.getFtpConfiguration().isMysqlEnable()) {
            DbDAO dbDAO = new DbDAO(hibernateBundle.getSessionFactory(), ftpConfiguration.getDataSourceFactory());
            eventInterface = new EventDb(dbDAO);
            userInterface = new UserDb(dbDAO);
            LOGGER.info("user interface configured from database");
        } else {
            eventInterface = new EventFile(appConfig);
            userInterface = new UserFile(appConfig);
            LOGGER.info("mysql is not enabled, configure user interface from file");
        }
        UserService userService = new UserService(appConfig, userInterface);
        AuthService authService = new AuthService(userService);
        EventTracking eventTracking = new EventTracking(appConfig, userService, eventInterface);
        environment.servlets().setSessionHandler(new SessionHandler());
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(new AppExceptionMapper(eventTracking));
        environment.jersey().register(new LogFilter(appConfig));
        environment.jersey().register(new RequestFilter(appConfig, eventTracking));
        environment.jersey().register(new ResponseFilter(appConfig));
        environment.jersey().register(new FaviconResource(appConfig));

        environment.jersey().register(new ApiResource(appConfig, userService, eventTracking, authService));
        environment.jersey().register(new AppResource(appConfig, userService, eventTracking, authService));
//        environment.admin().addTask(shutdownTask);
        eventTracking.trackApplicationStart(ftpConfiguration.getInstance());
    }
    public static void main(String[] args) throws Exception {
        StaticService.renameOldLogFile(args[0]);
        arguments.addAll(Arrays.asList(args));
        new FtpApplication().run(AppConstant.server, args[0]);
    }
}
