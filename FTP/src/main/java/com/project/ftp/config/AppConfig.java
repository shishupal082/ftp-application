package com.project.ftp.config;

/*
* Subset of DropWizard App Configuration file
* Generated after modification of parameter of config file
*/

import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.mysqlTable.TableDb;
import com.project.ftp.bridge.mysqlTable.TableMysqlDb;
import com.project.ftp.bridge.mysqlTable.TableService;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.*;
import com.project.ftp.mysql.DbDAO;
import com.project.ftp.obj.AppConfigObj;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.yamlObj.DatabaseParams;
import com.project.ftp.obj.yamlObj.FtlConfig;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.service.*;
import com.project.ftp.session.SessionData;
import io.dropwizard.hibernate.HibernateBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfig {
    private final static Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private String publicDir;
    private String configDate;
    private final String appVersion = AppConstant.AppVersion;
//    private ShutdownTask shutdownTask;
    private ArrayList<String> cmdArguments;
    private String logFilePath;
    private int requestCount = 0;
    private ArrayList<String> logFiles;
    private HashMap<String, SessionData> sessionData;
    private FtpConfiguration ftpConfiguration;
    private PageConfig404 pageConfig404;

    private EventTracking eventTracking;

    private AppToBridge appToBridge;
    private AuthService authService;
    private UserService userService;
    private MSExcelService msExcelService;
    private ScanDirService scanDirService;
    private TableService tableService;
    private SingleThreadingService singleThreadingService;
    public AppConfig() {
        this.configDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
    }

    public AppToBridge getAppToBridge() {
        return appToBridge;
    }

    public void setAppToBridge(AppToBridge appToBridge) {
        this.appToBridge = appToBridge;
    }

    public String getPublicDir() {
        return publicDir;
    }

    public void setPublicDir(String publicDir) {
        this.publicDir = publicDir;
    }

    public HashMap<String, SessionData> getSessionData() {
        return sessionData;
    }

    public void setSessionData(HashMap<String, SessionData> sessionData) {
        this.sessionData = sessionData;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public FtpConfiguration getFtpConfiguration() {
        return ftpConfiguration;
    }

    public void setFtpConfiguration(FtpConfiguration ftpConfiguration) {
        this.ftpConfiguration = ftpConfiguration;
    }
    public int getRateLimitThreshold() {
        int rateLimitThreshold = AppConstant.DEFAULT_RATE_LIMIT_THRESHOLD;
        if (ftpConfiguration.getRateLimitThreshold() > 0) {
            rateLimitThreshold = ftpConfiguration.getRateLimitThreshold();
        }
        return rateLimitThreshold;
    }
//    public ShutdownTask getShutdownTask() {
//        return shutdownTask;
//    }
//
//    public void setShutdownTask(ShutdownTask shutdownTask) {
//        this.shutdownTask = shutdownTask;
//    }

    public String getConfigDate() {
        return configDate;
    }

    public void setConfigDate(String configDate) {
        this.configDate = configDate;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public ArrayList<String> getCmdArguments() {
        return cmdArguments;
    }

    public void setCmdArguments(ArrayList<String> cmdArguments) {
        this.cmdArguments = cmdArguments;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public ArrayList<String> getLogFiles() {
        return logFiles;
    }

    public void setLogFiles(ArrayList<String> logFiles) {
        this.logFiles = logFiles;
    }

    public String getCookieName() {
        String cookieName = ftpConfiguration.getCookieName();
        if (StaticService.isInValidString(cookieName)) {
            cookieName = AppConstant.COOKIE_NAME;
        }
        return cookieName;
    }
    public FtlConfig getFtlConfig() {
        FtlConfig ftlConfig = ftpConfiguration.getFtlConfig();
        if (ftlConfig == null) {
            return new FtlConfig();
        }
        ftlConfig.setTempGaEnable(null);
        return ftlConfig;
    }
    private String getFileSaveDir(LoginUserDetails loginUserDetails) {
        String fileSaveDir = ftpConfiguration.getFileSaveDir();
        String finalSaveDir = fileSaveDir;
        if (userService != null) {
            finalSaveDir = userService.getFileSaveDirMapping(loginUserDetails, fileSaveDir);
        }
        PathInfo saveDirPathInfo = StaticService.getPathInfo(finalSaveDir);
        if (!AppConstant.FOLDER.equals(saveDirPathInfo.getType())) {
            logger.info("File save directory is not a folder: {}", finalSaveDir);
            if (fileSaveDir != null && !fileSaveDir.equals(finalSaveDir)) {
                saveDirPathInfo = StaticService.getPathInfo(fileSaveDir);
                if (!AppConstant.FOLDER.equals(saveDirPathInfo.getType())) {
                    logger.info("File save directory original is not a folder: {}", fileSaveDir);
                    finalSaveDir = null;
                } else {
                    finalSaveDir = fileSaveDir;
                }
            } else {
                finalSaveDir = null;
            }
        }
        return finalSaveDir;
    }
    public String getFileSaveDirV2(LoginUserDetails loginUserDetails) throws AppException {
        String saveDir = this.getFileSaveDir(loginUserDetails);
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (!StaticService.isDirectory(saveDir)) {
            logger.info("Invalid file save dir: {}", saveDir);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return saveDir;
    }

    public PageConfig404 getPageConfig404() {
        return pageConfig404;
    }

    public void setPageConfig404(PageConfig404 pageConfig404) {
        this.pageConfig404 = pageConfig404;
    }

    public void updateFinalFtpConfiguration(final FtpConfiguration ftpConfiguration) {
        ftpConfiguration.setMysqlEnable(StaticService.isMysqlEnable(cmdArguments));
        if (cmdArguments == null) {
            this.setFtpConfiguration(ftpConfiguration);
            logger.info("FTP configuration generate complete 1: {}", ftpConfiguration);
            return;
        }
        if (cmdArguments.size() <= AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            this.setFtpConfiguration(ftpConfiguration);
            logger.info("FTP configuration generate complete 2: {}", ftpConfiguration);
            return;
        }
        FtpConfiguration temp;
        YamlFileParser yamlFileParser = new YamlFileParser();
        //First file path already processed through main function
        for (int i = AppConstant.CMD_LINE_ARG_MIN_SIZE; i< cmdArguments.size(); i++) {
            temp = yamlFileParser.getFtpConfigurationFromPath(
                    cmdArguments.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-2),
                    cmdArguments.get(i));
            ftpConfiguration.updateFtpConfig(temp);
        }
        this.setFtpConfiguration(ftpConfiguration);
        logger.info("FTP configuration generate complete: {}", ftpConfiguration);
    }
    public void updatePageConfig404() {
        YamlFileParser yamlFileParser = new YamlFileParser();
        pageConfig404 = yamlFileParser.getPageConfig404(this);
        logger.info("PageConfig404 update complete: {}", pageConfig404);
    }
    public AppConfigObj getAppConfigObj() {
        return new AppConfigObj(publicDir, configDate, appVersion, cmdArguments,
                logFilePath, requestCount, sessionData, ftpConfiguration, pageConfig404);
    }

    public EventTracking getEventTracking() {
        return eventTracking;
    }

    public void setEventTracking(EventTracking eventTracking) {
        this.eventTracking = eventTracking;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public MSExcelService getMsExcelService() {
        return msExcelService;
    }

    public void setMsExcelService(MSExcelService msExcelService) {
        this.msExcelService = msExcelService;
    }

    public ScanDirService getScanDirService() {
        return scanDirService;
    }

    public void setScanDirService(ScanDirService scanDirService) {
        this.scanDirService = scanDirService;
    }

    public TableService getTableService() {
        return tableService;
    }

    public void setTableService(TableService tableService) {
        this.tableService = tableService;
    }

    public SingleThreadingService getSingleThreadingService() {
        return singleThreadingService;
    }

    public void setSingleThreadingService(SingleThreadingService singleThreadingService) {
        this.singleThreadingService = singleThreadingService;
    }

    @Override
    public String toString() {
        return this.getAppConfigObj().toString();
    }
    private DbDAO getDbDAO(final HibernateBundle<FtpConfiguration> hibernateBundle, FtpConfiguration ftpConfiguration,
                           String isStaticPath, String configPath, String source) {
        YamlFileParser yamlFileParser = new YamlFileParser();
        if (AppConstant.SOURCE_RUNTIME.equals(source)) {
            if (hibernateBundle != null) {
                return new DbDAO(hibernateBundle.getSessionFactory(), ftpConfiguration.getDataSourceFactory());
            }
        } else {
            // Read database configuration for junit test case and put it into ftpConfiguration
            DatabaseParams databaseParams = yamlFileParser.getDatabaseConfig(isStaticPath, configPath).getDatabase();
            if (databaseParams != null) {
                ftpConfiguration.getDataSourceFactory().setDriverClass(databaseParams.getDriverClass());
                ftpConfiguration.getDataSourceFactory().setUser(databaseParams.getUser());
                ftpConfiguration.getDataSourceFactory().setPassword(databaseParams.getPassword());
                ftpConfiguration.getDataSourceFactory().setUrl(databaseParams.getUrl());
            }
        }
        return null;
    }
    public static AppConfig getAppConfig(final HibernateBundle<FtpConfiguration> hibernateBundle, final FtpConfiguration ftpConfiguration, ArrayList<String> args, String source) {
        AppConfig appConfig = new AppConfig();
        if (args.size() < AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            logger.info("getAppConfig: minimum required command line argument is: {}", AppConstant.CMD_LINE_ARG_MIN_SIZE);
            return null;
        }
        String isStaticPath = args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-2);
        String configPath = args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-1);
        appConfig.setCmdArguments(args);
        appConfig.updateFinalFtpConfiguration(ftpConfiguration);
//        ShutdownTask shutdownTask = new ShutdownTask(appConfig);
//        appConfig.setShutdownTask(shutdownTask);
//        appConfig.setFtpConfiguration(ftpConfiguration);
        // For log config setup
        StaticService.initApplication(appConfig, args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-2), args.get(AppConstant.CMD_LINE_ARG_MIN_SIZE-1));
        appConfig.updatePageConfig404();
        logger.info("appConfig: {}", appConfig);
        EventInterface eventInterface = null;
        UserInterface userInterface = null;
        FilepathInterface filepathInterface = null;
        if (appConfig.getFtpConfiguration().isMysqlEnable()) {
            logger.info("mysql config enable");
            ArrayList<String> enableMysqlTable = null;
            if (ftpConfiguration.isMysqlEnable()) {
                enableMysqlTable = ftpConfiguration.getEnableMysqlTableName();
            }
            if (enableMysqlTable != null) {
                DbDAO dbDAO = appConfig.getDbDAO(hibernateBundle, ftpConfiguration, isStaticPath, configPath, source);
                if (appConfig.getFtpConfiguration().isMysqlEnable()) {
                    if (enableMysqlTable.contains(AppConstant.TABLE_FILE_PATH)) {
                        filepathInterface = new FilepathDb(ftpConfiguration.getDataSourceFactory());
                        logger.info("filepathInterface configured from mysql");
                    } else {
                        logger.info("filepathInterface not configured from mysql");
                    }
                }
                if (AppConstant.SOURCE_RUNTIME.equals(source)) {
                    if (enableMysqlTable.contains(AppConstant.TABLE_NAME_EVENT)) {
                        eventInterface = new EventDb(dbDAO);
                        logger.info("eventInterface configured from mysql");
                    } else {
                        logger.info("eventInterface not configured from mysql");
                    }
                    if (enableMysqlTable.contains(AppConstant.TABLE_NAME_USER)) {
                        userInterface = new UserDb(dbDAO);
                        logger.info("userInterface configured from mysql");
                    } else {
                        logger.info("userInterface not configured from mysql");
                    }
                }
            }
        } else {
            logger.info("mysql config not enabled, interface configure from file");
        }
        if (eventInterface == null) {
            eventInterface = new EventFile(appConfig);
            logger.info("eventInterface configured from file");
        }
        if (userInterface == null) {
            userInterface = new UserFile(appConfig);
            logger.info("userInterface configured from file");
        }
        if (filepathInterface == null) {
            filepathInterface = new FilepathCsv(appConfig);
            logger.info("filepathInterface configured from file");
        }
        SingleThreadingService singleThreadingService = new SingleThreadingService(appConfig.getFtpConfiguration());
        appConfig.setSingleThreadingService(singleThreadingService);
        TableDb tableMysqlDb = new TableMysqlDb(appConfig, ftpConfiguration.getDataSourceFactory(),
                ftpConfiguration.getOracleDatabaseConfigs());
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
        TableService tableService = new TableService(appConfig.getFtpConfiguration(), appConfig.getSingleThreadingService(),
                appConfig.getMsExcelService(), tableMysqlDb);
        appConfig.setTableService(tableService);
        return appConfig;
    }
}
