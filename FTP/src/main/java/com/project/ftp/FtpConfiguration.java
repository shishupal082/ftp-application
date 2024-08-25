package com.project.ftp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.bridge.config.CreatePasswordEmailConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.bridge.config.SocialLoginConfig;
import com.project.ftp.bridge.obj.yamlObj.CommunicationConfig;
import com.project.ftp.obj.yamlObj.EventConfig;
import com.project.ftp.obj.yamlObj.FtlConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FtpConfiguration extends Configuration {
    private String indexPageReRoute;
    private String configDataFilePath;
    private String fileSaveDir;
    private String publicDir;
    private String publicPostDir;
    private String assetsDir;
    private String fileMappingConfigFilePath;
    private String scanDirConfigFilePath;
    private ArrayList<String> tableDbConfigFilePath;
    private String filenameFormat;
    private String instance;
    private String appRestartCommand;
    private String cookieName;

    private String forgotPasswordMessage;
    private String loadRoleStatusOnPageLoad;
    private String staticDataFilename;
    private String userDataFilename;
    private String aesEncryptionPassword;

    // Defined as Boolean but not as boolean
    // Because while updating if not found shall not assume false
    private Boolean createReadmePdf;
    private Boolean forgotPasswordEnable;
    private Boolean guestEnable;
    private Boolean androidCheckEnable;
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
    private HashMap<String, String> fileSaveDirMapping;
    private HashMap<String, String> tempConfig;
    private EmailConfig emailConfig;
    private CreatePasswordEmailConfig createPasswordEmailConfig;
    private FtlConfig ftlConfig;
    private EventConfig eventConfig;
    private CommunicationConfig communicationConfig;
    private SocialLoginConfig socialLoginConfig;
    private GoogleOAuthClientConfig googleOAuthClientConfig;
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    public String getIndexPageReRoute() {
        return indexPageReRoute;
    }

    public void setIndexPageReRoute(String indexPageReRoute) {
        this.indexPageReRoute = indexPageReRoute;
    }

    public String getConfigDataFilePath() {
        return configDataFilePath;
    }

    public void setConfigDataFilePath(String configDataFilePath) {
        this.configDataFilePath = configDataFilePath;
    }

    public String getFileSaveDir() {
        return fileSaveDir;
    }

    public void setFileSaveDir(String fileSaveDir) {
        this.fileSaveDir = fileSaveDir;
    }

    public String getPublicDir() {
        return publicDir;
    }

    public void setPublicDir(String publicDir) {
        this.publicDir = publicDir;
    }

    public String getPublicPostDir() {
        return publicPostDir;
    }

    public void setPublicPostDir(String publicPostDir) {
        this.publicPostDir = publicPostDir;
    }

    public String getAssetsDir() {
        return assetsDir;
    }

    public void setAssetsDir(String assetsDir) {
        this.assetsDir = assetsDir;
    }

    public String getFileMappingConfigFilePath() {
        return fileMappingConfigFilePath;
    }

    public void setFileMappingConfigFilePath(String fileMappingConfigFilePath) {
        this.fileMappingConfigFilePath = fileMappingConfigFilePath;
    }

    public String getScanDirConfigFilePath() {
        return scanDirConfigFilePath;
    }

    public void setScanDirConfigFilePath(String scanDirConfigFilePath) {
        this.scanDirConfigFilePath = scanDirConfigFilePath;
    }

    public ArrayList<String> getTableDbConfigFilePath() {
        return tableDbConfigFilePath;
    }

    public void setTableDbConfigFilePath(ArrayList<String> tableDbConfigFilePath) {
        this.tableDbConfigFilePath = tableDbConfigFilePath;
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

    public String getStaticDataFilename() {
        return staticDataFilename;
    }

    public void setStaticDataFilename(String staticDataFilename) {
        this.staticDataFilename = staticDataFilename;
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

    public HashMap<String, String> getFileSaveDirMapping() {
        return fileSaveDirMapping;
    }

    public void setFileSaveDirMapping(HashMap<String, String> fileSaveDirMapping) {
        this.fileSaveDirMapping = fileSaveDirMapping;
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

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public void updateFtpConfig(final FtpConfiguration tempFtpConfiguration) {
        if (tempFtpConfiguration == null) {
            return;
        }
        String indexPageReRoute = tempFtpConfiguration.getIndexPageReRoute();
        if (indexPageReRoute != null) {
            this.indexPageReRoute = indexPageReRoute;
        }
        String configDataFilePath = tempFtpConfiguration.getConfigDataFilePath();
        if (configDataFilePath != null) {
            this.configDataFilePath = configDataFilePath;
        }
        String fileSaveDir = tempFtpConfiguration.getFileSaveDir();
        if (fileSaveDir != null) {
            this.fileSaveDir = fileSaveDir;
        }
        String publicDir = tempFtpConfiguration.getPublicDir();
        if (publicDir != null) {
            this.publicDir = publicDir;
        }
        String publicPostDir = tempFtpConfiguration.getPublicPostDir();
        if (publicPostDir != null) {
            this.publicPostDir = publicPostDir;
        }
        String assetsDir = tempFtpConfiguration.getAssetsDir();
        if (assetsDir != null) {
            this.assetsDir = assetsDir;
        }
        String fileMappingConfigFilePath = tempFtpConfiguration.getFileMappingConfigFilePath();
        if (fileMappingConfigFilePath != null) {
            this.fileMappingConfigFilePath = fileMappingConfigFilePath;
        }
        String scanDirConfigFilePath = tempFtpConfiguration.getScanDirConfigFilePath();
        if (scanDirConfigFilePath != null) {
            this.scanDirConfigFilePath = scanDirConfigFilePath;
        }
        ArrayList<String> tableDbConfigFilePath = tempFtpConfiguration.getTableDbConfigFilePath();
        if (tableDbConfigFilePath != null) {
            this.tableDbConfigFilePath = tableDbConfigFilePath;
        }
        String filenameFormat = tempFtpConfiguration.getFilenameFormat();
        if (filenameFormat != null) {
            this.filenameFormat = filenameFormat;
        }
        String instance = tempFtpConfiguration.getInstance();
        if (instance != null) {
            this.instance = instance;
        }
        String appRestartCommand = tempFtpConfiguration.getAppRestartCommand();
        if (appRestartCommand != null) {
            this.appRestartCommand = appRestartCommand;
        }
        String cookieName = tempFtpConfiguration.getCookieName();
        if (cookieName != null) {
            this.cookieName = cookieName;
        }
        String forgotPasswordMessage = tempFtpConfiguration.getForgotPasswordMessage();
        if (forgotPasswordMessage != null) {
            this.forgotPasswordMessage = forgotPasswordMessage;
        }
        String loadRoleStatusOnPageLoad = tempFtpConfiguration.getLoadRoleStatusOnPageLoad();
        if (loadRoleStatusOnPageLoad != null) {
            this.loadRoleStatusOnPageLoad = loadRoleStatusOnPageLoad;
        }
        String staticDataFilename = tempFtpConfiguration.getStaticDataFilename();
        if (staticDataFilename != null) {
            this.staticDataFilename = staticDataFilename;
        }
        String userDataFilename = tempFtpConfiguration.getUserDataFilename();
        if (userDataFilename != null) {
            this.userDataFilename = userDataFilename;
        }
        String aesEncryptionPassword = tempFtpConfiguration.getAesEncryptionPassword();
        if (aesEncryptionPassword != null) {
            this.aesEncryptionPassword = aesEncryptionPassword;
        }
        Boolean createReadmePdf = tempFtpConfiguration.getCreateReadmePdf();
        if (createReadmePdf != null) {
            this.createReadmePdf = createReadmePdf;
        }
        Boolean forgotPasswordEnable = tempFtpConfiguration.getForgotPasswordEnable();
        if (forgotPasswordEnable != null) {
            this.forgotPasswordEnable = forgotPasswordEnable;
        }
        Boolean guestEnable = tempFtpConfiguration.getGuestEnable();
        if (guestEnable != null) {
            this.guestEnable = guestEnable;
        }
        Boolean androidCheckEnable = tempFtpConfiguration.getAndroidCheckEnable();
        if (androidCheckEnable != null) {
            this.androidCheckEnable = androidCheckEnable;
        }
        int maxFileSize = tempFtpConfiguration.getMaxFileSize();
        if (maxFileSize > 0) {
            this.maxFileSize = maxFileSize;
        }
        int rateLimitThreshold = tempFtpConfiguration.getRateLimitThreshold();
        if (rateLimitThreshold > 0) {
            this.rateLimitThreshold = rateLimitThreshold;
        }
        ArrayList<String> allowedOrigin = tempFtpConfiguration.getAllowedOrigin();
        if (allowedOrigin != null && allowedOrigin.size() > 0) {
            this.allowedOrigin = allowedOrigin;
        }
        ArrayList<String> supportedFileType = tempFtpConfiguration.getSupportedFileType();
        if (supportedFileType != null && supportedFileType.size() > 0) {
            this.supportedFileType = supportedFileType;
        }
        ArrayList<String> enableMysqlTableName = tempFtpConfiguration.getEnableMysqlTableName();
        if (enableMysqlTableName != null && enableMysqlTableName.size() > 0) {
            this.enableMysqlTableName = enableMysqlTableName;
        }
        ArrayList<String> fileNotFoundMapping = tempFtpConfiguration.getFileNotFoundMapping();
        if (fileNotFoundMapping != null && fileNotFoundMapping.size() > 0) {
            this.fileNotFoundMapping = fileNotFoundMapping;
        }
        ArrayList<String> rolesFileName = tempFtpConfiguration.getRolesFileName();
        if (rolesFileName != null && rolesFileName.size() > 0) {
            this.rolesFileName = rolesFileName;
        }
        ArrayList<String> allowedTableFilename = tempFtpConfiguration.getAllowedTableFilename();
        if (allowedTableFilename != null && allowedTableFilename.size() > 0) {
            this.allowedTableFilename = allowedTableFilename;
        }
        ArrayList<String> enabledAuthPages = tempFtpConfiguration.getEnabledAuthPages();
        if (enabledAuthPages != null && enabledAuthPages.size() > 0) {
            this.enabledAuthPages = enabledAuthPages;
        }
        HashMap<String, String> loginRedirectMapping = tempFtpConfiguration.getLoginRedirectMapping();
        if (loginRedirectMapping != null) {
            this.loginRedirectMapping = loginRedirectMapping;
        }
        HashMap<String, String> fileSaveDirMapping = tempFtpConfiguration.getFileSaveDirMapping();
        if (fileSaveDirMapping != null) {
            this.fileSaveDirMapping = fileSaveDirMapping;
        }
        HashMap<String, String> tempConfig = tempFtpConfiguration.getTempConfig();
        if (tempConfig != null) {
            this.tempConfig = tempConfig;
        }
        EmailConfig emailConfig = tempFtpConfiguration.getEmailConfig();
        if (emailConfig != null) {
            this.emailConfig = emailConfig;
        }
        CreatePasswordEmailConfig createPasswordEmailConfig = tempFtpConfiguration.getCreatePasswordEmailConfig();
        if (createPasswordEmailConfig != null) {
            this.createPasswordEmailConfig = createPasswordEmailConfig;
        }
        FtlConfig ftlConfig = tempFtpConfiguration.getFtlConfig();
        if (ftlConfig != null) {
            this.ftlConfig = ftlConfig;
        }
        EventConfig eventConfig = tempFtpConfiguration.getEventConfig();
        if (eventConfig != null) {
            this.eventConfig = eventConfig;
        }
        CommunicationConfig communicationConfig = tempFtpConfiguration.getCommunicationConfig();
        if (communicationConfig != null) {
            this.communicationConfig = communicationConfig;
        }
        SocialLoginConfig socialLoginConfig = tempFtpConfiguration.getSocialLoginConfig();
        if (socialLoginConfig != null) {
            this.socialLoginConfig = socialLoginConfig;
        }
        GoogleOAuthClientConfig googleOAuthClientConfig = tempFtpConfiguration.getGoogleOAuthClientConfig();
        if (googleOAuthClientConfig != null) {
            this.googleOAuthClientConfig = googleOAuthClientConfig;
        }
    }

    @Override
    public String toString() {
        return "FtpConfiguration{" +
                "indexPageReRoute='" + indexPageReRoute + '\'' +
                ", configDataFilePath='" + configDataFilePath + '\'' +
                ", fileSaveDir='" + fileSaveDir + '\'' +
                ", publicDir='" + publicDir + '\'' +
                ", publicPostDir='" + publicPostDir + '\'' +
                ", assetsDir='" + assetsDir + '\'' +
                ", fileMappingConfigFilePath='" + fileMappingConfigFilePath + '\'' +
                ", scanDirConfigFilePath='" + scanDirConfigFilePath + '\'' +
                ", tableDbConfigFilePath=" + tableDbConfigFilePath +
                ", filenameFormat='" + filenameFormat + '\'' +
                ", instance='" + instance + '\'' +
                ", appRestartCommand='" + appRestartCommand + '\'' +
                ", cookieName='" + cookieName + '\'' +
                ", forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                ", staticDataFilename='" + staticDataFilename + '\'' +
                ", userDataFilename='" + userDataFilename + '\'' +
                ", aesEncryptionPassword='" + "*****" + '\'' +
                ", createReadmePdf=" + createReadmePdf +
                ", forgotPasswordEnable=" + forgotPasswordEnable +
                ", guestEnable=" + guestEnable +
                ", androidCheckEnable=" + androidCheckEnable +
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
                ", fileSaveDirMapping=" + fileSaveDirMapping +
                ", tempConfig=" + tempConfig +
                ", emailConfig=" + emailConfig +
                ", createPasswordEmailConfig=" + createPasswordEmailConfig +
                ", ftlConfig=" + ftlConfig +
                ", eventConfig=" + eventConfig +
                ", communicationConfig=" + communicationConfig +
                ", socialLoginConfig=" + socialLoginConfig +
                ", googleOAuthClientConfig=" + googleOAuthClientConfig +
                ", dataSourceFactory=" + "*****" +
                '}';
    }
}
