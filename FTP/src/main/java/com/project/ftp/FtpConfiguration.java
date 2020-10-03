package com.project.ftp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.bridge.config.CreatePasswordEmailConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.obj.FtlConfig;
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
    private String defaultFileViewer;
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

    private Integer maxFileSize;
    private boolean createReadmePdf;
    private boolean permanentlyDeleteFile;
    private boolean mysqlEnable;
    private boolean guestEnable;
    private ArrayList<String> allowedOrigin;
    private ArrayList<String> adminUsersName;
    private ArrayList<String> devUsersName;
    private ArrayList<String> supportedFileType;
    private FtlConfig ftlConfig;
    private HashMap<String, String> tempConfig;

    public EmailConfig getEmailConfig() {
        if (emailConfig == null) {
            return new EmailConfig();
        }
        return emailConfig;
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public CreatePasswordEmailConfig getCreatePasswordEmailConfig() {
        if (createPasswordEmailConfig == null) {
            return new CreatePasswordEmailConfig();
        }
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

    public String getDefaultFileViewer() {
        return defaultFileViewer;
    }

    public void setDefaultFileViewer(String defaultFileViewer) {
        this.defaultFileViewer = defaultFileViewer;
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

    public ArrayList<String> getAdminUsersName() {
        return adminUsersName;
    }

    public void setAdminUsersName(ArrayList<String> adminUsersName) {
        this.adminUsersName = adminUsersName;
    }

    public ArrayList<String> getDevUsersName() {
        return devUsersName;
    }

    public void setDevUsersName(ArrayList<String> devUsersName) {
        this.devUsersName = devUsersName;
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

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isCreateReadmePdf() {
        return createReadmePdf;
    }

    public void setCreateReadmePdf(boolean createReadmePdf) {
        this.createReadmePdf = createReadmePdf;
    }

    public boolean isPermanentlyDeleteFile() {
        return permanentlyDeleteFile;
    }

    public void setPermanentlyDeleteFile(boolean permanentlyDeleteFile) {
        this.permanentlyDeleteFile = permanentlyDeleteFile;
    }

    public boolean isMysqlEnable() {
        return mysqlEnable;
    }

    public void setMysqlEnable(boolean mysqlEnable) {
        this.mysqlEnable = mysqlEnable;
    }

    public boolean isGuestEnable() {
        return guestEnable;
    }

    public void setGuestEnable(boolean guestEnable) {
        this.guestEnable = guestEnable;
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
                ", defaultFileViewer='" + defaultFileViewer + '\'' +
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
                ", adminUsersName=" + adminUsersName +
                ", devUsersName=" + devUsersName +
                ", supportedFileType=" + supportedFileType +
                ", ftlConfig=" + ftlConfig +
                ", tempConfig=" + tempConfig +
                '}';
    }
}
