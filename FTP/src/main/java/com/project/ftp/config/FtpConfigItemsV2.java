package com.project.ftp.config;

import java.util.ArrayList;
import java.util.Arrays;

public enum FtpConfigItemsV2 {
    fileSaveDir,
    staticDataFilename,
    configDataFilePath,
    fileMappingConfigFilePath,
    splitTextFileConfigPath,
    standAloneConfigPath,
    tableDbConfigFilePath,
    scanDirConfigFilePath,
    assetsDir,
    publicPostDir;
    public static ArrayList<FtpConfigItemsV2> getAllFtpConfigItems() {
        return new ArrayList<>(Arrays.asList(FtpConfigItemsV2.values()));
    }
}
