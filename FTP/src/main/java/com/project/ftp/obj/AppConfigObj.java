package com.project.ftp.obj;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.ApiRoleMappingData;
import com.project.ftp.config.FtpConfigItems;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.session.SessionData;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfigObj {
    private final String configDate;
    private final String appVersion;
    private final ArrayList<String> cmdArguments;
    private final String logFilePath;
    private final int requestCount;
    private final String logFiles;
    private final HashMap<String, SessionData> sessionData;
    private final FtpConfiguration ftpConfiguration;
    private final PageConfig404 pageConfig404;
    private HashMap<String, ArrayList<ApiRoleMappingData>> apiRoleMappingList;
    private ArrayList<FtpConfigItems> firstPageConfigItems;
    public AppConfigObj(String configDate, String appVersion,
                        ArrayList<String> cmdArguments, String logFilePath, int requestCount,
                        HashMap<String, SessionData> sessionData,
                        FtpConfiguration ftpConfiguration, PageConfig404 pageConfig404,
                        HashMap<String, ArrayList<ApiRoleMappingData>> apiRoleMappingList,
                        ArrayList<FtpConfigItems> firstPageConfigItems) {
        this.configDate = configDate;
        this.appVersion = appVersion;
        this.cmdArguments = cmdArguments;
        this.logFilePath = logFilePath;
        this.requestCount = requestCount;
        this.logFiles = "*****";
        this.sessionData = sessionData;
        this.ftpConfiguration = ftpConfiguration;
        this.pageConfig404 = pageConfig404;
        this.apiRoleMappingList = apiRoleMappingList;
        this.firstPageConfigItems = firstPageConfigItems;
    }
    public static boolean checkOrClearFtpConfiguration(final FtpConfiguration ftpConfiguration,
                                                       final FtpConfigItems ftpConfigItems, boolean clear) {
        if (ftpConfiguration == null) {
            return false;
        }
        if (ftpConfigItems == null) {
            return false;
        }
        switch (ftpConfigItems) {
            case dirConfigParam:
                if (clear) {
                    ftpConfiguration.setDirConfigParam(null);
                } else {
                    return ftpConfiguration.getDirConfigParam() != null;
                }
                break;
            case commonConfigFilePath:
                if (clear) {
                    ftpConfiguration.setCommonConfigFilePath(null);
                } else {
                    return ftpConfiguration.getCommonConfigFilePath() != null;
                }
                break;
            case indexPageReRoute:
                if (clear) {
                    ftpConfiguration.setIndexPageReRoute(null);
                } else {
                    return ftpConfiguration.getIndexPageReRoute() != null;
                }
                break;
            case filenameFormat:
                if (clear) {
                    ftpConfiguration.setFilenameFormat(null);
                } else {
                    return ftpConfiguration.getFilenameFormat() != null;
                }
                break;
            case instance:
                if (clear) {
                    ftpConfiguration.setInstance(null);
                } else {
                    return ftpConfiguration.getInstance() != null;
                }
                break;
            case appRestartCommand:
                if (clear) {
                    ftpConfiguration.setAppRestartCommand(null);
                } else {
                    return ftpConfiguration.getAppRestartCommand() != null;
                }
                break;
            case cookieName:
                if (clear) {
                    ftpConfiguration.setCookieName(null);
                } else {
                    return ftpConfiguration.getCookieName() != null;
                }
                break;
            case forgotPasswordMessage:
                if (clear) {
                    ftpConfiguration.setForgotPasswordMessage(null);
                } else {
                    return ftpConfiguration.getForgotPasswordMessage() != null;
                }
                break;
            case loadRoleStatusOnPageLoad:
                if (clear) {
                    ftpConfiguration.setLoadRoleStatusOnPageLoad(null);
                } else {
                    return ftpConfiguration.getLoadRoleStatusOnPageLoad() != null;
                }
                break;
            case userDataFilename:
                if (clear) {
                    ftpConfiguration.setUserDataFilename(null);
                } else {
                    return ftpConfiguration.getUserDataFilename() != null;
                }
                break;
            case aesEncryptionPassword:
                if (clear) {
                    ftpConfiguration.setAesEncryptionPassword(null);
                } else {
                    return ftpConfiguration.getAesEncryptionPassword() != null;
                }
                break;
            case createReadmePdf:
                if (clear) {
                    ftpConfiguration.setCreateReadmePdf(null);
                } else {
                    return ftpConfiguration.getCreateReadmePdf() != null;
                }
                break;
            case forgotPasswordEnable:
                if (clear) {
                    ftpConfiguration.setForgotPasswordEnable(null);
                } else {
                    return ftpConfiguration.getForgotPasswordEnable() != null;
                }
                break;
            case guestEnable:
                if (clear) {
                    ftpConfiguration.setGuestEnable(null);
                } else {
                    return ftpConfiguration.getGuestEnable() != null;
                }
                break;
            case androidCheckEnable:
                if (clear) {
                    ftpConfiguration.setAndroidCheckEnable(null);
                } else {
                    return ftpConfiguration.getAndroidCheckEnable() != null;
                }
                break;
            case singleThreadingEnable:
                if (clear) {
                    ftpConfiguration.setSingleThreadingEnable(null);
                } else {
                    return ftpConfiguration.getSingleThreadingEnable() != null;
                }
                break;
            case mysqlEnable:
                if (clear) {
                    ftpConfiguration.setMysqlEnable(false);
                } else {
                    return ftpConfiguration.isMysqlEnable();
                }
                break;
            case maxFileSize:
                if (clear) {
                    ftpConfiguration.setMaxFileSize(0);
                } else {
                    return ftpConfiguration.getMaxFileSize() > 0;
                }
                break;
            case rateLimitThreshold:
                if (clear) {
                    ftpConfiguration.setRateLimitThreshold(0);
                } else {
                    return ftpConfiguration.getRateLimitThreshold() > 0;
                }
                break;
            case allowedOrigin:
                if (clear) {
                    ftpConfiguration.setAllowedOrigin(null);
                } else {
                    return ftpConfiguration.getAllowedOrigin() != null;
                }
                break;
            case supportedFileType:
                if (clear) {
                    ftpConfiguration.setSupportedFileType(null);
                } else {
                    return ftpConfiguration.getSupportedFileType() != null;
                }
                break;
            case enableMysqlTableName:
                if (clear) {
                    ftpConfiguration.setEnableMysqlTableName(null);
                } else {
                    return ftpConfiguration.getEnableMysqlTableName() != null;
                }
                break;
            case fileNotFoundMapping:
                if (clear) {
                    ftpConfiguration.setFileNotFoundMapping(null);
                } else {
                    return ftpConfiguration.getFileNotFoundMapping() != null;
                }
                break;
            case rolesFileName:
                if (clear) {
                    ftpConfiguration.setRolesFileName(null);
                } else {
                    return ftpConfiguration.getRolesFileName() != null;
                }
                break;
            case allowedTableFilename:
                if (clear) {
                    ftpConfiguration.setAllowedTableFilename(null);
                } else {
                    return ftpConfiguration.getAllowedTableFilename() != null;
                }
                break;
            case enabledAuthPages:
                if (clear) {
                    ftpConfiguration.setEnabledAuthPages(null);
                } else {
                    return ftpConfiguration.getEnabledAuthPages() != null;
                }
                break;
            case loginRedirectMapping:
                if (clear) {
                    ftpConfiguration.setLoginRedirectMapping(null);
                } else {
                    return ftpConfiguration.getLoginRedirectMapping() != null;
                }
                break;
            case tempConfig:
                if (clear) {
                    ftpConfiguration.setTempConfig(null);
                } else {
                    return ftpConfiguration.getTempConfig() != null;
                }
                break;
            case emailConfig:
                if (clear) {
                    ftpConfiguration.setEmailConfig(null);
                } else {
                    return ftpConfiguration.getEmailConfig() != null;
                }
                break;
            case createPasswordEmailConfig:
                if (clear) {
                    ftpConfiguration.setCreatePasswordEmailConfig(null);
                } else {
                    return ftpConfiguration.getCreatePasswordEmailConfig() != null;
                }
                break;
            case ftlConfig:
                if (clear) {
                    ftpConfiguration.setFtlConfig(null);
                } else {
                    return ftpConfiguration.getFtlConfig() != null;
                }
                break;
            case eventConfig:
                if (clear) {
                    ftpConfiguration.setEventConfig(null);
                } else {
                    return ftpConfiguration.getEventConfig() != null;
                }
                break;
            case communicationConfig:
                if (clear) {
                    ftpConfiguration.setCommunicationConfig(null);
                } else {
                    return ftpConfiguration.getCommunicationConfig() != null;
                }
                break;
            case socialLoginConfig:
                if (clear) {
                    ftpConfiguration.setSocialLoginConfig(null);
                } else {
                    return ftpConfiguration.getSocialLoginConfig() != null;
                }
                break;
            case googleOAuthClientConfig:
                if (clear) {
                    ftpConfiguration.setGoogleOAuthClientConfig(null);
                } else {
                    return ftpConfiguration.getGoogleOAuthClientConfig() != null;
                }
                break;
            case apiAuthorisationConfig:
                if (clear) {
                    ftpConfiguration.setApiAuthorisationConfig(null);
                } else {
                    return ftpConfiguration.getApiAuthorisationConfig() != null;
                }
                break;
            case dataSourceFactory:
                if (clear) {
                    ftpConfiguration.setDataSourceFactory(null);
                } else {
                    return ftpConfiguration.getDataSourceFactory() != null;
                }
                break;
            case oracleDatabaseConfigs:
                if (clear) {
                    ftpConfiguration.setOracleDatabaseConfigs(null);
                } else {
                    return ftpConfiguration.getOracleDatabaseConfigs() != null;
                }
                break;
            case mysqlDatabaseConfigs:
                if (clear) {
                    ftpConfiguration.setMysqlDatabaseConfigs(null);
                } else {
                    return ftpConfiguration.getMysqlDatabaseConfigs() != null;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public String getConfigDate() {
        return configDate;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public ArrayList<String> getCmdArguments() {
        return cmdArguments;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public String getLogFiles() {
        return logFiles;
    }

    public HashMap<String, SessionData> getSessionData() {
        return sessionData;
    }

    public FtpConfiguration getFtpConfiguration() {
        return ftpConfiguration;
    }

    public PageConfig404 getPageConfig404() {
        return pageConfig404;
    }

    public HashMap<String, ArrayList<ApiRoleMappingData>> getApiRoleMappingList() {
        return apiRoleMappingList;
    }

    public void setApiRoleMappingList(HashMap<String, ArrayList<ApiRoleMappingData>> apiRoleMappingList) {
        this.apiRoleMappingList = apiRoleMappingList;
    }

    public ArrayList<FtpConfigItems> getFirstPageConfigItems() {
        return firstPageConfigItems;
    }

    public void setFirstPageConfigItems(ArrayList<FtpConfigItems> firstPageConfigItems) {
        this.firstPageConfigItems = firstPageConfigItems;
    }

    @Override
    public String toString() {
        return "AppConfigObj{" +
                "configDate='" + configDate + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", cmdArguments=" + cmdArguments +
                ", logFilePath='" + logFilePath + '\'' +
                ", requestCount=" + requestCount +
                ", logFiles='" + logFiles + '\'' +
                ", sessionData=" + sessionData +
                ", ftpConfiguration=" + ftpConfiguration +
                ", pageConfig404=" + pageConfig404 +
                ", apiRoleMappingList=" + apiRoleMappingList +
                ", firstPageConfigItems=" + firstPageConfigItems +
                '}';
    }
}
