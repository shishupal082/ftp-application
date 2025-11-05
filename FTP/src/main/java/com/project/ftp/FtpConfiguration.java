package com.project.ftp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.bridge.config.CreatePasswordEmailConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.bridge.config.SocialLoginConfig;
import com.project.ftp.bridge.obj.yamlObj.CommunicationConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItems;
import com.project.ftp.obj.yamlObj.DirConfigParam;
import com.project.ftp.obj.yamlObj.EventConfig;
import com.project.ftp.obj.yamlObj.FtlConfig;
import com.project.ftp.obj.yamlObj.OracleDatabaseConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FtpConfiguration extends Configuration {
    private HashMap<String, DirConfigParam> dirConfigParam;
    private String commonConfigFilePath;
    private String indexPageReRoute;
    private String filenameFormat;
    private String instance;
    private String appRestartCommand;
    private String cookieName;

    private String forgotPasswordMessage;
    private String loadRoleStatusOnPageLoad;
    private String userDataFilename;
    private String aesEncryptionPassword;
    // Defined as Boolean but not as boolean
    // Because while updating if not found shall not assume false
    private Boolean createReadmePdf;
    private Boolean forgotPasswordEnable;
    private Boolean guestEnable;
    private Boolean androidCheckEnable;
    private Boolean singleThreadingEnable;
    private boolean mysqlEnable;
    private int maxFileSize;
    private int rateLimitThreshold;// used for register and create_password
    private ArrayList<String> allowedOrigin;
    private ArrayList<String> supportedFileType;
    private ArrayList<String> enableMysqlTableName;
    private ArrayList<String> fileNotFoundMapping;
    private ArrayList<String> rolesFileName;
    private ArrayList<String> allowedTableFilename;
    private ArrayList<String> enabledAuthPages;
    private HashMap<String, String> loginRedirectMapping;
    private HashMap<String, String> tempConfig;
    private EmailConfig emailConfig;
    private CreatePasswordEmailConfig createPasswordEmailConfig;
    private FtlConfig ftlConfig;
    private EventConfig eventConfig;
    private CommunicationConfig communicationConfig;
    private SocialLoginConfig socialLoginConfig;
    private GoogleOAuthClientConfig googleOAuthClientConfig;
    private HashMap<String, ArrayList<String>> apiAuthorisationConfig;
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();
    private HashMap<String, OracleDatabaseConfig> oracleDatabaseConfigs;
    private OracleDatabaseConfig mysqlDatabaseConfigs;

    public HashMap<String, DirConfigParam> getDirConfigParam() {
        return dirConfigParam;
    }

    public void setDirConfigParam(HashMap<String, DirConfigParam> dirConfigParam) {
        this.dirConfigParam = dirConfigParam;
    }

    public String getCommonConfigFilePath() {
        return commonConfigFilePath;
    }

    public void setCommonConfigFilePath(String commonConfigFilePath) {
        this.commonConfigFilePath = commonConfigFilePath;
    }

    public String getIndexPageReRoute() {
        return indexPageReRoute;
    }

    public void setIndexPageReRoute(String indexPageReRoute) {
        this.indexPageReRoute = indexPageReRoute;
    }

    public String getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getAppRestartCommand() {
        return appRestartCommand;
    }

    public void setAppRestartCommand(String appRestartCommand) {
        this.appRestartCommand = appRestartCommand;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getForgotPasswordMessage() {
        return forgotPasswordMessage;
    }

    public void setForgotPasswordMessage(String forgotPasswordMessage) {
        this.forgotPasswordMessage = forgotPasswordMessage;
    }

    public String getLoadRoleStatusOnPageLoad() {
        return loadRoleStatusOnPageLoad;
    }

    public void setLoadRoleStatusOnPageLoad(String loadRoleStatusOnPageLoad) {
        this.loadRoleStatusOnPageLoad = loadRoleStatusOnPageLoad;
    }

    public String getUserDataFilename() {
        return userDataFilename;
    }

    public void setUserDataFilename(String userDataFilename) {
        this.userDataFilename = userDataFilename;
    }

    public String getAesEncryptionPassword() {
        return aesEncryptionPassword;
    }

    public void setAesEncryptionPassword(String aesEncryptionPassword) {
        this.aesEncryptionPassword = aesEncryptionPassword;
    }

    public Boolean getCreateReadmePdf() {
        return createReadmePdf;
    }

    public void setCreateReadmePdf(Boolean createReadmePdf) {
        this.createReadmePdf = createReadmePdf;
    }

    public Boolean getForgotPasswordEnable() {
        return forgotPasswordEnable;
    }

    public void setForgotPasswordEnable(Boolean forgotPasswordEnable) {
        this.forgotPasswordEnable = forgotPasswordEnable;
    }

    public Boolean getGuestEnable() {
        return guestEnable;
    }

    public void setGuestEnable(Boolean guestEnable) {
        this.guestEnable = guestEnable;
    }

    public Boolean getAndroidCheckEnable() {
        return androidCheckEnable;
    }

    public void setAndroidCheckEnable(Boolean androidCheckEnable) {
        this.androidCheckEnable = androidCheckEnable;
    }

    public Boolean getSingleThreadingEnable() {
        return singleThreadingEnable;
    }

    public void setSingleThreadingEnable(Boolean singleThreadingEnable) {
        this.singleThreadingEnable = singleThreadingEnable;
    }

    public boolean isMysqlEnable() {
        return mysqlEnable;
    }

    public void setMysqlEnable(boolean mysqlEnable) {
        this.mysqlEnable = mysqlEnable;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getRateLimitThreshold() {
        return rateLimitThreshold;
    }

    public void setRateLimitThreshold(int rateLimitThreshold) {
        this.rateLimitThreshold = rateLimitThreshold;
    }

    public ArrayList<String> getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(ArrayList<String> allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    public ArrayList<String> getSupportedFileType() {
        return supportedFileType;
    }

    public void setSupportedFileType(ArrayList<String> supportedFileType) {
        this.supportedFileType = supportedFileType;
    }

    public ArrayList<String> getEnableMysqlTableName() {
        return enableMysqlTableName;
    }

    public void setEnableMysqlTableName(ArrayList<String> enableMysqlTableName) {
        this.enableMysqlTableName = enableMysqlTableName;
    }

    public ArrayList<String> getFileNotFoundMapping() {
        return fileNotFoundMapping;
    }

    public void setFileNotFoundMapping(ArrayList<String> fileNotFoundMapping) {
        this.fileNotFoundMapping = fileNotFoundMapping;
    }

    public ArrayList<String> getRolesFileName() {
        return rolesFileName;
    }

    public void setRolesFileName(ArrayList<String> rolesFileName) {
        this.rolesFileName = rolesFileName;
    }

    public ArrayList<String> getAllowedTableFilename() {
        return allowedTableFilename;
    }

    public void setAllowedTableFilename(ArrayList<String> allowedTableFilename) {
        this.allowedTableFilename = allowedTableFilename;
    }

    public ArrayList<String> getEnabledAuthPages() {
        return enabledAuthPages;
    }

    public void setEnabledAuthPages(ArrayList<String> enabledAuthPages) {
        this.enabledAuthPages = enabledAuthPages;
    }

    public HashMap<String, String> getLoginRedirectMapping() {
        return loginRedirectMapping;
    }

    public void setLoginRedirectMapping(HashMap<String, String> loginRedirectMapping) {
        this.loginRedirectMapping = loginRedirectMapping;
    }

    public HashMap<String, String> getTempConfig() {
        return tempConfig;
    }

    public void setTempConfig(HashMap<String, String> tempConfig) {
        this.tempConfig = tempConfig;
    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public CreatePasswordEmailConfig getCreatePasswordEmailConfig() {
        return createPasswordEmailConfig;
    }

    public void setCreatePasswordEmailConfig(CreatePasswordEmailConfig createPasswordEmailConfig) {
        this.createPasswordEmailConfig = createPasswordEmailConfig;
    }

    public FtlConfig getFtlConfig() {
        return ftlConfig;
    }

    public void setFtlConfig(FtlConfig ftlConfig) {
        this.ftlConfig = ftlConfig;
    }

    public EventConfig getEventConfig() {
        return eventConfig;
    }

    public void setEventConfig(EventConfig eventConfig) {
        this.eventConfig = eventConfig;
    }

    public CommunicationConfig getCommunicationConfig() {
        return communicationConfig;
    }

    public void setCommunicationConfig(CommunicationConfig communicationConfig) {
        this.communicationConfig = communicationConfig;
    }

    public SocialLoginConfig getSocialLoginConfig() {
        return socialLoginConfig;
    }

    public void setSocialLoginConfig(SocialLoginConfig socialLoginConfig) {
        this.socialLoginConfig = socialLoginConfig;
    }

    public GoogleOAuthClientConfig getGoogleOAuthClientConfig() {
        return googleOAuthClientConfig;
    }

    public void setGoogleOAuthClientConfig(GoogleOAuthClientConfig googleOAuthClientConfig) {
        this.googleOAuthClientConfig = googleOAuthClientConfig;
    }

    public HashMap<String, ArrayList<String>> getApiAuthorisationConfig() {
        return apiAuthorisationConfig;
    }

    public void setApiAuthorisationConfig(HashMap<String, ArrayList<String>> apiAuthorisationConfig) {
        this.apiAuthorisationConfig = apiAuthorisationConfig;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public HashMap<String, OracleDatabaseConfig> getOracleDatabaseConfigs() {
        return oracleDatabaseConfigs;
    }

    public void setOracleDatabaseConfigs(HashMap<String, OracleDatabaseConfig> oracleDatabaseConfigs) {
        this.oracleDatabaseConfigs = oracleDatabaseConfigs;
    }

    public OracleDatabaseConfig getMysqlDatabaseConfigs() {
        return mysqlDatabaseConfigs;
    }

    public void setMysqlDatabaseConfigs(OracleDatabaseConfig mysqlDatabaseConfigs) {
        this.mysqlDatabaseConfigs = mysqlDatabaseConfigs;
    }

    public void updateFtpConfig(final FtpConfiguration tempFtpConfiguration,
                                ArrayList<FtpConfigItems> firstPageConfigItems) {
        if (tempFtpConfiguration == null) {
            return;
        }
        if (firstPageConfigItems == null) {
            firstPageConfigItems = new ArrayList<>();
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.dirConfigParam)) {
            HashMap<String, DirConfigParam> dirConfigParam1 = tempFtpConfiguration.getDirConfigParam();
            if (dirConfigParam1 != null) {
                this.dirConfigParam = dirConfigParam1;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.commonConfigFilePath)) {
            String commonConfigFilePath = tempFtpConfiguration.getCommonConfigFilePath();
            if (commonConfigFilePath != null) {
                this.commonConfigFilePath = commonConfigFilePath;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.indexPageReRoute)) {
            String indexPageReRoute = tempFtpConfiguration.getIndexPageReRoute();
            if (indexPageReRoute != null) {
                this.indexPageReRoute = indexPageReRoute;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.filenameFormat)) {
            String filenameFormat = tempFtpConfiguration.getFilenameFormat();
            if (filenameFormat != null) {
                this.filenameFormat = filenameFormat;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.instance)) {
            String instance = tempFtpConfiguration.getInstance();
            if (instance != null) {
                this.instance = instance;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.appRestartCommand)) {
            String appRestartCommand = tempFtpConfiguration.getAppRestartCommand();
            if (appRestartCommand != null) {
                this.appRestartCommand = appRestartCommand;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.cookieName)) {
            String cookieName = tempFtpConfiguration.getCookieName();
            if (cookieName != null) {
                this.cookieName = cookieName;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.forgotPasswordMessage)) {
            String forgotPasswordMessage = tempFtpConfiguration.getForgotPasswordMessage();
            if (forgotPasswordMessage != null) {
                this.forgotPasswordMessage = forgotPasswordMessage;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.loadRoleStatusOnPageLoad)) {
            String loadRoleStatusOnPageLoad = tempFtpConfiguration.getLoadRoleStatusOnPageLoad();
            if (loadRoleStatusOnPageLoad != null) {
                this.loadRoleStatusOnPageLoad = loadRoleStatusOnPageLoad;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.userDataFilename)) {
            String userDataFilename = tempFtpConfiguration.getUserDataFilename();
            if (userDataFilename != null) {
                this.userDataFilename = userDataFilename;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.aesEncryptionPassword)) {
            String aesEncryptionPassword = tempFtpConfiguration.getAesEncryptionPassword();
            if (aesEncryptionPassword != null) {
                this.aesEncryptionPassword = aesEncryptionPassword;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.createReadmePdf)) {
            Boolean createReadmePdf = tempFtpConfiguration.getCreateReadmePdf();
            if (createReadmePdf != null) {
                this.createReadmePdf = createReadmePdf;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.forgotPasswordEnable)) {
            Boolean forgotPasswordEnable = tempFtpConfiguration.getForgotPasswordEnable();
            if (forgotPasswordEnable != null) {
                this.forgotPasswordEnable = forgotPasswordEnable;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.guestEnable)) {
            Boolean guestEnable = tempFtpConfiguration.getGuestEnable();
            if (guestEnable != null) {
                this.guestEnable = guestEnable;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.androidCheckEnable)) {
            Boolean androidCheckEnable = tempFtpConfiguration.getAndroidCheckEnable();
            if (androidCheckEnable != null) {
                this.androidCheckEnable = androidCheckEnable;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.singleThreadingEnable)) {
            Boolean singleThreadingEnable = tempFtpConfiguration.getSingleThreadingEnable();
            if (singleThreadingEnable != null) {
                this.singleThreadingEnable = singleThreadingEnable;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.maxFileSize)) {
            int maxFileSize = tempFtpConfiguration.getMaxFileSize();
            if (maxFileSize > 0) {
                this.maxFileSize = maxFileSize;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.rateLimitThreshold)) {
            int rateLimitThreshold = tempFtpConfiguration.getRateLimitThreshold();
            if (rateLimitThreshold > 0) {
                this.rateLimitThreshold = rateLimitThreshold;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.allowedOrigin)) {
            ArrayList<String> allowedOrigin = tempFtpConfiguration.getAllowedOrigin();
            if (allowedOrigin != null && !allowedOrigin.isEmpty()) {
                this.allowedOrigin = allowedOrigin;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.supportedFileType)) {
            ArrayList<String> supportedFileType = tempFtpConfiguration.getSupportedFileType();
            if (supportedFileType != null && !supportedFileType.isEmpty()) {
                this.supportedFileType = supportedFileType;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.enableMysqlTableName)) {
            ArrayList<String> enableMysqlTableName = tempFtpConfiguration.getEnableMysqlTableName();
            if (enableMysqlTableName != null && !enableMysqlTableName.isEmpty()) {
                this.enableMysqlTableName = enableMysqlTableName;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.fileNotFoundMapping)) {
            ArrayList<String> fileNotFoundMapping = tempFtpConfiguration.getFileNotFoundMapping();
            if (fileNotFoundMapping != null && !fileNotFoundMapping.isEmpty()) {
                this.fileNotFoundMapping = fileNotFoundMapping;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.rolesFileName)) {
            ArrayList<String> rolesFileName = tempFtpConfiguration.getRolesFileName();
            if (rolesFileName != null && !rolesFileName.isEmpty()) {
                this.rolesFileName = rolesFileName;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.allowedTableFilename)) {
            ArrayList<String> allowedTableFilename = tempFtpConfiguration.getAllowedTableFilename();
            if (allowedTableFilename != null && !allowedTableFilename.isEmpty()) {
                this.allowedTableFilename = allowedTableFilename;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.enabledAuthPages)) {
            ArrayList<String> enabledAuthPages = tempFtpConfiguration.getEnabledAuthPages();
            if (enabledAuthPages != null && !enabledAuthPages.isEmpty()) {
                this.enabledAuthPages = enabledAuthPages;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.loginRedirectMapping)) {
            HashMap<String, String> loginRedirectMapping = tempFtpConfiguration.getLoginRedirectMapping();
            if (loginRedirectMapping != null) {
                this.loginRedirectMapping = loginRedirectMapping;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.tempConfig)) {
            HashMap<String, String> tempConfig = tempFtpConfiguration.getTempConfig();
            if (tempConfig != null) {
                this.tempConfig = tempConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.emailConfig)) {
            EmailConfig emailConfig = tempFtpConfiguration.getEmailConfig();
            if (emailConfig != null) {
                this.emailConfig = emailConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.createPasswordEmailConfig)) {
            CreatePasswordEmailConfig createPasswordEmailConfig = tempFtpConfiguration.getCreatePasswordEmailConfig();
            if (createPasswordEmailConfig != null) {
                this.createPasswordEmailConfig = createPasswordEmailConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.ftlConfig)) {
            FtlConfig ftlConfig = tempFtpConfiguration.getFtlConfig();
            if (ftlConfig != null) {
                this.ftlConfig = ftlConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.eventConfig)) {
            EventConfig eventConfig = tempFtpConfiguration.getEventConfig();
            if (eventConfig != null) {
                this.eventConfig = eventConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.communicationConfig)) {
            CommunicationConfig communicationConfig = tempFtpConfiguration.getCommunicationConfig();
            if (communicationConfig != null) {
                this.communicationConfig = communicationConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.socialLoginConfig)) {
            SocialLoginConfig socialLoginConfig = tempFtpConfiguration.getSocialLoginConfig();
            if (socialLoginConfig != null) {
                this.socialLoginConfig = socialLoginConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.googleOAuthClientConfig)) {
            GoogleOAuthClientConfig googleOAuthClientConfig = tempFtpConfiguration.getGoogleOAuthClientConfig();
            if (googleOAuthClientConfig != null) {
                this.googleOAuthClientConfig = googleOAuthClientConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.apiAuthorisationConfig)) {
            HashMap<String, ArrayList<String>> apiAuthorisationConfig = tempFtpConfiguration.getApiAuthorisationConfig();
            if (apiAuthorisationConfig != null) {
                this.apiAuthorisationConfig = apiAuthorisationConfig;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.oracleDatabaseConfigs)) {
            HashMap<String, OracleDatabaseConfig> tempOracleDatabaseConfigs = tempFtpConfiguration.getOracleDatabaseConfigs();
            if (tempOracleDatabaseConfigs != null) {
                this.oracleDatabaseConfigs = tempOracleDatabaseConfigs;
            }
        }
        if (!firstPageConfigItems.contains(FtpConfigItems.mysqlDatabaseConfigs)) {
            OracleDatabaseConfig tempMysqlDatabaseConfigs = tempFtpConfiguration.getMysqlDatabaseConfigs();
            if (mysqlDatabaseConfigs != null) {
                this.mysqlDatabaseConfigs = tempMysqlDatabaseConfigs;
            }
        }
    }

    @Override
    public String toString() {
        return "FtpConfiguration{" +
                "dirConfigParam=" + dirConfigParam +
                ", commonConfigFilePath='" + commonConfigFilePath + '\'' +
                ", indexPageReRoute='" + indexPageReRoute + '\'' +
                ", filenameFormat='" + filenameFormat + '\'' +
                ", instance='" + instance + '\'' +
                ", appRestartCommand='" + appRestartCommand + '\'' +
                ", cookieName='" + cookieName + '\'' +
                ", forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                ", userDataFilename='" + userDataFilename + '\'' +
                ", aesEncryptionPassword='" + AppConstant.MaskDataString + '\'' +
                ", createReadmePdf=" + createReadmePdf +
                ", forgotPasswordEnable=" + forgotPasswordEnable +
                ", guestEnable=" + guestEnable +
                ", androidCheckEnable=" + androidCheckEnable +
                ", singleThreadingEnable=" + singleThreadingEnable +
                ", mysqlEnable=" + mysqlEnable +
                ", maxFileSize=" + maxFileSize +
                ", rateLimitThreshold=" + rateLimitThreshold +
                ", allowedOrigin=" + allowedOrigin +
                ", supportedFileType=" + supportedFileType +
                ", enableMysqlTableName=" + enableMysqlTableName +
                ", fileNotFoundMapping=" + fileNotFoundMapping +
                ", rolesFileName=" + rolesFileName +
                ", allowedTableFilename=" + allowedTableFilename +
                ", enabledAuthPages=" + enabledAuthPages +
                ", loginRedirectMapping=" + loginRedirectMapping +
                ", tempConfig=" + tempConfig +
                ", emailConfig=" + emailConfig +
                ", createPasswordEmailConfig=" + createPasswordEmailConfig +
                ", ftlConfig=" + ftlConfig +
                ", eventConfig=" + eventConfig +
                ", communicationConfig=" + communicationConfig +
                ", socialLoginConfig=" + socialLoginConfig +
                ", googleOAuthClientConfig=" + googleOAuthClientConfig +
                ", apiAuthorisationConfig=" + apiAuthorisationConfig +
                ", oracleDatabaseConfigs=" + AppConstant.MaskDataString +
                ", mysqlDatabaseConfigs=" + AppConstant.MaskDataString +
                ", dataSourceFactory=" + AppConstant.MaskDataString +
                '}';
    }
}
