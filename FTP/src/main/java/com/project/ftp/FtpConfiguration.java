package com.project.ftp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Integer maxFileSize;
    private String fileDeleteAccess;
    private String defaultFileViewer;
    private String filenameFormat;
    private ArrayList<String> allowedOrigin;
    private ArrayList<String> adminUsersName;
    private ArrayList<String> devUsersName;
    private ArrayList<String> supportedFileType;
    private Boolean createReadmePdf;
    private String instance;
    private Boolean permanentlyDeleteFile;
    private String appRestartCommand;
    private String uploadFileApiVersion;
    private boolean mysqlEnable;

    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    private HashMap<String, String> tempConfig;

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public String getIndexPageReRoute() {
        return indexPageReRoute;
    }

    public void setIndexPageReRoute(String indexPageReRoute) {
        this.indexPageReRoute = indexPageReRoute;
    }

    public Boolean getCreateReadmePdf() {
        return createReadmePdf;
    }

    public void setCreateReadmePdf(Boolean createReadmePdf) {
        this.createReadmePdf = createReadmePdf;
    }

    public String getConfigDataFilePath() {
        return configDataFilePath;
    }

    public void setConfigDataFilePath(String configDataFilePath) {
        this.configDataFilePath = configDataFilePath;
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

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public String getFileSaveDir() {
        return fileSaveDir;
    }

    public void setFileSaveDir(String fileSaveDir) {
        this.fileSaveDir = fileSaveDir;
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


    public Boolean getPermanentlyDeleteFile() {
        return permanentlyDeleteFile;
    }

    public void setPermanentlyDeleteFile(Boolean permanentlyDeleteFile) {
        this.permanentlyDeleteFile = permanentlyDeleteFile;
    }

    public String getAppRestartCommand() {
        return appRestartCommand;
    }

    public void setAppRestartCommand(String appRestartCommand) {
        this.appRestartCommand = appRestartCommand;
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

    public String getUploadFileApiVersion() {
        return uploadFileApiVersion;
    }

    public void setUploadFileApiVersion(String uploadFileApiVersion) {
        this.uploadFileApiVersion = uploadFileApiVersion;
    }

    public boolean getMysqlEnable() {
        return mysqlEnable;
    }

    public void setMysqlEnable(boolean mysqlEnable) {
        this.mysqlEnable = mysqlEnable;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        return "FtpConfiguration{" +
                "indexPageReRoute='" + indexPageReRoute + '\'' +
                ", configDataFilePath='" + configDataFilePath + '\'' +
                ", fileSaveDir='" + fileSaveDir + '\'' +
                ", publicDir='" + publicDir + '\'' +
                ", publicPostDir='" + publicPostDir + '\'' +
                ", maxFileSize=" + maxFileSize +
                ", fileDeleteAccess='" + fileDeleteAccess + '\'' +
                ", defaultFileViewer='" + defaultFileViewer + '\'' +
                ", filenameFormat='" + filenameFormat + '\'' +
                ", allowedOrigin=" + allowedOrigin +
                ", adminUsersName=" + adminUsersName +
                ", devUsersName=" + devUsersName +
                ", supportedFileType=" + supportedFileType +
                ", createReadmePdf=" + createReadmePdf +
                ", instance='" + instance + '\'' +
                ", permanentlyDeleteFile=" + permanentlyDeleteFile +
                ", appRestartCommand='" + appRestartCommand + '\'' +
                ", uploadFileApiVersion='" + uploadFileApiVersion + '\'' +
                ", mysqlEnable=" + mysqlEnable +
                ", dataSourceFactory=" + "*****" +
                ", tempConfig=" + tempConfig +
                '}';
    }
}
