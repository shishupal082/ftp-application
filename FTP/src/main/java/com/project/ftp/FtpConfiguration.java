package com.project.ftp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.bridge.config.CreatePasswordEmailConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.obj.yamlObj.BackendConfig;
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
    private String fileDeleteAccess;
    private String filenameFormat;
    private String instance;
    private String appRestartCommand;
    private String uploadFileApiVersion;
    private String cookieName;

    private String aesEncryptionPassword;
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();
    private EmailConfig emailConfig;
    private CreatePasswordEmailConfig createPasswordEmailConfig;

    private int maxFileSize;
    private Boolean createReadmePdf;
    private Boolean permanentlyDeleteFile;
    private boolean mysqlEnable;
    private Boolean guestEnable;
    private ArrayList<String> allowedOrigin;
    private ArrayList<String> supportedFileType;
    private FtlConfig ftlConfig;
    private BackendConfig backendConfig;
    private HashMap<String, String> loginRedirectMapping;
    private HashMap<String, String> tempConfig;

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

    public String getFileDeleteAccess() {
        return fileDeleteAccess;
    }

    public void setFileDeleteAccess(String fileDeleteAccess) {
        this.fileDeleteAccess = fileDeleteAccess;
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

    public String getUploadFileApiVersion() {
        return uploadFileApiVersion;
    }

    public void setUploadFileApiVersion(String uploadFileApiVersion) {
        this.uploadFileApiVersion = uploadFileApiVersion;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getAesEncryptionPassword() {
        return aesEncryptionPassword;
    }

    public void setAesEncryptionPassword(String aesEncryptionPassword) {
        this.aesEncryptionPassword = aesEncryptionPassword;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
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

    public HashMap<String, String> getTempConfig() {
        return tempConfig;
    }

    public void setTempConfig(HashMap<String, String> tempConfig) {
        this.tempConfig = tempConfig;
    }

    public HashMap<String, String> getLoginRedirectMapping() {
        return loginRedirectMapping;
    }

    public void setLoginRedirectMapping(HashMap<String, String> loginRedirectMapping) {
        this.loginRedirectMapping = loginRedirectMapping;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Boolean isCreateReadmePdf() {
        return createReadmePdf;
    }

    public void setCreateReadmePdf(Boolean createReadmePdf) {
        this.createReadmePdf = createReadmePdf;
    }

    public Boolean isPermanentlyDeleteFile() {
        return permanentlyDeleteFile;
    }

    public void setPermanentlyDeleteFile(Boolean permanentlyDeleteFile) {
        this.permanentlyDeleteFile = permanentlyDeleteFile;
    }

    public boolean isMysqlEnable() {
        return mysqlEnable;
    }

    public void setMysqlEnable(boolean mysqlEnable) {
        this.mysqlEnable = mysqlEnable;
    }

    public Boolean isGuestEnable() {
        return guestEnable;
    }

    public void setGuestEnable(Boolean guestEnable) {
        this.guestEnable = guestEnable;
    }

    public BackendConfig getBackendConfig() {
        return backendConfig;
    }

    public void setBackendConfig(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
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
        String fileDeleteAccess = tempFtpConfiguration.getFileDeleteAccess();
        if (fileDeleteAccess != null) {
            this.fileDeleteAccess = fileDeleteAccess;
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
        String uploadFileApiVersion = tempFtpConfiguration.getUploadFileApiVersion();
        if (uploadFileApiVersion != null) {
            this.uploadFileApiVersion = uploadFileApiVersion;
        }
        String cookieName = tempFtpConfiguration.getCookieName();
        if (cookieName != null) {
            this.cookieName = cookieName;
        }
        String aesEncryptionPassword = tempFtpConfiguration.getAesEncryptionPassword();
        if (aesEncryptionPassword != null) {
            this.aesEncryptionPassword = aesEncryptionPassword;
        }
        EmailConfig emailConfig = tempFtpConfiguration.getEmailConfig();
        if (emailConfig != null) {
            this.emailConfig = emailConfig;
        }
        CreatePasswordEmailConfig createPasswordEmailConfig = tempFtpConfiguration.getCreatePasswordEmailConfig();
        if (createPasswordEmailConfig != null) {
            this.createPasswordEmailConfig = createPasswordEmailConfig;
        }
        int maxFileSize = tempFtpConfiguration.getMaxFileSize();
        if (maxFileSize > 0) {
            this.maxFileSize = maxFileSize;
        }
        Boolean createReadmePdf = tempFtpConfiguration.isCreateReadmePdf();
        if (createReadmePdf != null) {
            this.createReadmePdf = createReadmePdf;
        }
        Boolean permanentlyDeleteFile = tempFtpConfiguration.isPermanentlyDeleteFile();
        if (permanentlyDeleteFile != null) {
            this.permanentlyDeleteFile = permanentlyDeleteFile;
        }
        Boolean guestEnable = tempFtpConfiguration.isGuestEnable();
        if (guestEnable != null) {
            this.guestEnable = guestEnable;
        }
        ArrayList<String> allowedOrigin = tempFtpConfiguration.getAllowedOrigin();
        if (allowedOrigin != null && allowedOrigin.size() > 0) {
            this.allowedOrigin = allowedOrigin;
        }
        ArrayList<String> supportedFileType = tempFtpConfiguration.getSupportedFileType();
        if (supportedFileType != null && supportedFileType.size() > 0) {
            this.supportedFileType = supportedFileType;
        }
        FtlConfig ftlConfig = tempFtpConfiguration.getFtlConfig();
        if (ftlConfig != null) {
            this.ftlConfig = ftlConfig;
        }
        BackendConfig backendConfig = tempFtpConfiguration.getBackendConfig();
        if (backendConfig != null) {
            this.backendConfig = backendConfig;
        }
        HashMap<String, String> tempConfig = tempFtpConfiguration.getTempConfig();
        if (tempConfig != null) {
            this.tempConfig = tempConfig;
        }
        HashMap<String, String> loginRedirectMapping = tempFtpConfiguration.getLoginRedirectMapping();
        if (loginRedirectMapping != null) {
            this.loginRedirectMapping = loginRedirectMapping;
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
                ", fileDeleteAccess='" + fileDeleteAccess + '\'' +
                ", filenameFormat='" + filenameFormat + '\'' +
                ", instance='" + instance + '\'' +
                ", appRestartCommand='" + appRestartCommand + '\'' +
                ", uploadFileApiVersion='" + uploadFileApiVersion + '\'' +
                ", cookieName='" + cookieName + '\'' +
                ", aesEncryptionPassword='" + "*****" +
                ", dataSourceFactory=" + "*****" +
                ", emailConfig=" + emailConfig +
                ", createPasswordEmailConfig=" + createPasswordEmailConfig +
                ", maxFileSize=" + maxFileSize +
                ", createReadmePdf=" + createReadmePdf +
                ", permanentlyDeleteFile=" + permanentlyDeleteFile +
                ", mysqlEnable=" + mysqlEnable +
                ", guestEnable=" + guestEnable +
                ", allowedOrigin=" + allowedOrigin +
                ", supportedFileType=" + supportedFileType +
                ", ftlConfig=" + ftlConfig +
                ", backendConfig=" + backendConfig +
                ", loginRedirectMapping=" + loginRedirectMapping +
                ", tempConfig=" + tempConfig +
                '}';
    }
}
