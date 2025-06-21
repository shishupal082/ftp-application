package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.obj.yamlObj.StandAloneConfig;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.resources.ApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class StandAlone {
    final static Logger logger = LoggerFactory.getLogger(StandAlone.class);
    private final ArrayList<String> cmdArguments;
    private final AppConfig appConfig;
    private final YamlFileParser yamlFileParser = new YamlFileParser();
    private ApiResource apiResource;
    public StandAlone(ArrayList<String> cmdArgument) {
        cmdArguments = cmdArgument;
        appConfig = AppConfig.getAppConfigFromCmdArgs(cmdArgument,AppConstant.SOURCE_STANDALONE);
        try {
            apiResource = new ApiResource(appConfig);
        } catch (AppException e) {
            apiResource = null;
            e.printStackTrace();
        }
    }
    private void handleApiUpdateExcelDataV2(StandAloneConfig standAloneConfig, ArrayList<String> params) {
        if (params == null || params.isEmpty()) {
            logger.info("Invalid update-excel-data-v2 config: {}", standAloneConfig);
            return;
        }
        apiResource.updateMSExcelDataV2(null, params.get(0));
    }
    private void handleApiSplitFile(StandAloneConfig standAloneConfig, ArrayList<String> params) {
        if (params == null || params.isEmpty()) {
            logger.info("Invalid split file config: {}", standAloneConfig);
            return;
        }
        apiResource.splitFile(null, params.get(0));
    }
    public void handleRequest() {
        if (appConfig == null || apiResource == null) {
            logger.info("Error in reading config files: {}", cmdArguments);
            logger.info("Press any key...");
            return;
        }
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        StandAloneConfig standAloneConfig = yamlFileParser.getStandAloneConfig(ftpConfiguration.getStandAloneConfigPath());
        if (standAloneConfig == null) {
            waitForInput();
            return;
        }
//        String resource = standAloneConfig.getResource();
        String path = standAloneConfig.getPath();
        ArrayList<String> params = standAloneConfig.getParams();
        if ("split_file".equals(path)) {
            this.handleApiSplitFile(standAloneConfig, params);
        } else if ("update_excel_data_v2".equals(path)) {
            this.handleApiUpdateExcelDataV2(standAloneConfig, params);
        } else {
            logger.info("Invalid standAloneConfig: {}", standAloneConfig);
            logger.info("Press any key to exit...");
        }
    }

    private void waitForInput() {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        logger.info("{}",str);
    }
}
