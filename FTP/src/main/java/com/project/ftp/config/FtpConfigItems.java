package com.project.ftp.config;

import java.util.ArrayList;
import java.util.Arrays;

public enum FtpConfigItems {
    dirConfigParam,
    indexPageReRoute,
    filenameFormat,
    instance,
    appRestartCommand,
    cookieName,
    forgotPasswordMessage,
    loadRoleStatusOnPageLoad,
    staticDataFilename,
    userDataFilename,
    aesEncryptionPassword,
    createReadmePdf,
    forgotPasswordEnable,
    guestEnable,
    androidCheckEnable,
    singleThreadingEnable,
    mysqlEnable,
    maxFileSize,
    rateLimitThreshold,
    allowedOrigin,
    supportedFileType,
    enableMysqlTableName,
    fileNotFoundMapping,
    rolesFileName,
    allowedTableFilename,
    enabledAuthPages,
    loginRedirectMapping,
    tempConfig,
    emailConfig,
    createPasswordEmailConfig,
    ftlConfig,
    eventConfig,
    communicationConfig,
    socialLoginConfig,
    googleOAuthClientConfig,
    apiAuthorisationConfig,
    dataSourceFactory,
    oracleDatabaseConfigs,
    mysqlDatabaseConfigs;
    public static ArrayList<FtpConfigItems> getAllFtpConfigItems() {
        return new ArrayList<>(Arrays.asList(FtpConfigItems.values()));
    }
}
