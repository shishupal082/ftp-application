package com.project.ftp;

import com.project.ftp.bridge.mysqlTable.TableDb;
import com.project.ftp.bridge.mysqlTable.TableMysqlDb;
import com.project.ftp.bridge.mysqlTable.TableService;
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
import com.project.ftp.obj.yamlObj.DatabaseParams;
import com.project.ftp.obj.yamlObj.OracleDatabaseConfig;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.resources.AppResource;
import com.project.ftp.resources.FaviconResource;
import com.project.ftp.service.*;
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
    private DbDAO getDbDAO(AppConfig appConfig, FtpConfiguration ftpConfiguration, String source) {
        YamlFileParser yamlFileParser = new YamlFileParser();
        ArrayList<String> args = appConfig.getCmdArguments();
        if (AppConstant.SOURCE_RUNTIME.equals(source)) {
            return new DbDAO(hibernateBundle.getSessionFactory(), ftpConfiguration.getDataSourceFactory());
        } else {
            // Read database configuration for junit test case and put it into ftpConfiguration
            DatabaseParams databaseParams = yamlFileParser.getDatabaseConfig(
                    args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-2),
                    args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-1)).getDatabase();
            if (databaseParams != null) {
                ftpConfiguration.getDataSourceFactory().setDriverClass(databaseParams.getDriverClass());
                ftpConfiguration.getDataSourceFactory().setUser(databaseParams.getUser());
                ftpConfiguration.getDataSourceFactory().setPassword(databaseParams.getPassword());
                ftpConfiguration.getDataSourceFactory().setUrl(databaseParams.getUrl());
            }
            return null;
        }
    }
    public AppConfig getAppConfig(final FtpConfiguration ftpConfiguration, ArrayList<String> args, String source) {
        AppConfig appConfig = new AppConfig();
        if (args.size() < AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            LOGGER.info("getAppConfig: minimum required command line argument is: {}", AppConstant.CMD_LINE_ARG_MIN_SIZE);
            return null;
        }
        appConfig.setCmdArguments(args);
        appConfig.updateFinalFtpConfiguration(ftpConfiguration);
//        ShutdownTask shutdownTask = new ShutdownTask(appConfig);
//        appConfig.setShutdownTask(shutdownTask);
//        appConfig.setFtpConfiguration(ftpConfiguration);
        // For log config setup
        StaticService.initApplication(appConfig, args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-2), args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-1));
        appConfig.updatePageConfig404();
        LOGGER.info("appConfig: {}", appConfig);
        EventInterface eventInterface = null;
        UserInterface userInterface = null;
        FilepathInterface filepathInterface = null;
        if (appConfig.getFtpConfiguration().isMysqlEnable()) {
            LOGGER.info("mysql config enable");
            ArrayList<String> enableMysqlTable = null;
            if (ftpConfiguration.isMysqlEnable()) {
                enableMysqlTable = ftpConfiguration.getEnableMysqlTableName();
            }
            if (enableMysqlTable != null) {
                DbDAO dbDAO = this.getDbDAO(appConfig, ftpConfiguration, source);
                if (appConfig.getFtpConfiguration().isMysqlEnable()) {
                    if (enableMysqlTable.contains(AppConstant.TABLE_FILE_PATH)) {
                        filepathInterface = new FilepathDb(ftpConfiguration.getDataSourceFactory());
                        LOGGER.info("filepathInterface configured from mysql");
                    } else {
                        LOGGER.info("filepathInterface not configured from mysql");
                    }
                }
                if (AppConstant.SOURCE_RUNTIME.equals(source)) {
                    if (enableMysqlTable.contains(AppConstant.TABLE_NAME_EVENT)) {
                        eventInterface = new EventDb(dbDAO);
                        LOGGER.info("eventInterface configured from mysql");
                    } else {
                        LOGGER.info("eventInterface not configured from mysql");
                    }
                    if (enableMysqlTable.contains(AppConstant.TABLE_NAME_USER)) {
                        userInterface = new UserDb(dbDAO);
                        LOGGER.info("userInterface configured from mysql");
                    } else {
                        LOGGER.info("userInterface not configured from mysql");
                    }
                }
            }
        } else {
            LOGGER.info("mysql config not enabled, interface configure from file");
        }
        if (eventInterface == null) {
            eventInterface = new EventFile(appConfig);
            LOGGER.info("eventInterface configured from file");
        }
        if (userInterface == null) {
            userInterface = new UserFile(appConfig);
            LOGGER.info("userInterface configured from file");
        }
        if (filepathInterface == null) {
            filepathInterface = new FilepathCsv(appConfig);
            LOGGER.info("filepathInterface configured from file");
        }
        SingleThreadingService singleThreadingService = new SingleThreadingService(appConfig.getFtpConfiguration());
        appConfig.setSingleThreadingService(singleThreadingService);
        OracleDatabaseConfig oracleDatabaseConfig = ftpConfiguration.getOracleDatabaseConfig();
        if (oracleDatabaseConfig == null) {
            oracleDatabaseConfig = new OracleDatabaseConfig();
        }
        TableDb tableMysqlDb = new TableMysqlDb(ftpConfiguration.getDataSourceFactory(), oracleDatabaseConfig);
        UserService userService = new UserService(appConfig, userInterface);
        appConfig.setUserService(userService);
        EventTracking eventTracking = new EventTracking(appConfig, userService, eventInterface);
        appConfig.setEventTracking(eventTracking);
        appConfig.setMsExcelService(new MSExcelService(appConfig, eventTracking, userService));
        AuthService authService = new AuthService(userService);
        appConfig.setAuthService(authService);
        ScanDirService scanDirService = new ScanDirService(appConfig, filepathInterface);
        appConfig.setScanDirService(scanDirService);
        appConfig.setAppToBridge(new AppToBridge(appConfig, ftpConfiguration, eventTracking));
        TableService tableService = new TableService(appConfig.getFtpConfiguration(), appConfig.getMsExcelService(), tableMysqlDb);
        appConfig.setTableService(tableService);
        return appConfig;
    }
    @Override
    public void run(FtpConfiguration ftpConfiguration, Environment environment) {
        LOGGER.info("commandLineArguments: {}", arguments.toString());
        AppConfig appConfig = this.getAppConfig(ftpConfiguration, arguments, AppConstant.SOURCE_RUNTIME);
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
        // java -jar meta-data/FTP-*-SNAPSHOT.jar <serverName> <isMySqlEnable> <isStaticPath> <config file 1> <config file 2> ...
        arguments.addAll(Arrays.asList(args));
        if (arguments.size() >= AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            StaticService.renameOldLogFile(args[AppConstant.CMD_LINE_ARG_MIN_SIZE-2], args[AppConstant.CMD_LINE_ARG_MIN_SIZE-1]);
            new FtpApplication().run(AppConstant.SERVER, args[AppConstant.CMD_LINE_ARG_MIN_SIZE-1]);
        } else {
            LOGGER.info("main: minimum required command line argument is: {}", AppConstant.CMD_LINE_ARG_MIN_SIZE);
        }
    }
}
