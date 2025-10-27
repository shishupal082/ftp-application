package com.project.ftp;

import com.project.ftp.bridge.config.StandAloneApiIdentifier;
import com.project.ftp.bridge.service.StandAloneService;
import com.project.ftp.bridge.standalone.obj.ApiDetail;
import com.project.ftp.bridge.standalone.obj.StandAloneConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.resources.ApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class StandAlone {
    final static Logger logger = LoggerFactory.getLogger(StandAlone.class);
    private final ArrayList<String> cmdArguments;
    private final StandAloneService standAloneService;
    private final AppConfig appConfig;
    private ApiResource apiResource;
    public StandAlone(ArrayList<String> cmdArgument) {
        cmdArguments = cmdArgument;
        standAloneService = new StandAloneService();
        appConfig = AppConfig.getAppConfigFromCmdArgs(cmdArgument,AppConstant.SOURCE_STANDALONE);
        try {
            apiResource = new ApiResource(appConfig);
        } catch (AppException e) {
            apiResource = null;
            e.printStackTrace();
        }
    }
    private void handleApiUpdateMysql(ApiDetail apiDetail, ArrayList<String> params) {
        if (params == null || params.isEmpty()) {
            logger.info("handleApiUpdateMysql: Invalid config: {}", apiDetail);
            return;
        }
        apiResource.updateMySqlTableDataFromCsv(null, params.get(0));
    }
    private void handleApiUpdateExcelData(ApiDetail apiDetail, ArrayList<String> params) {
        if (params == null || params.isEmpty()) {
            logger.info("handleApiUpdateExcelData: Invalid config: {}", apiDetail);
            return;
        }
        apiResource.updateMSExcelData(null, params.get(0));
    }
    private void handleApiUpdateExcelDataV2(ApiDetail apiDetail, ArrayList<String> params) {
        if (params == null || params.isEmpty()) {
            logger.info("handleApiUpdateExcelDataV2: Invalid config: {}", apiDetail);
            return;
        }
        apiResource.updateMSExcelDataV2(null, params.get(0));
    }
    private void handleApiSplitFile(ApiDetail apiDetail, ArrayList<String> params) {
        if (params == null || params.isEmpty()) {
            logger.info("handleApiSplitFile: Invalid config: {}", apiDetail);
            return;
        }
        apiResource.splitFile(null, params.get(0));
    }
    private void askConfirmation(ApiDetail apiDetail) {
        if (apiDetail == null) {
            return;
        }
        Boolean confirmationRequired = apiDetail.getConfirmationRequired();
        if (confirmationRequired == null) {
            return;
        }
        if (confirmationRequired) {
            logger.info("Press any key to continue...");
            waitForInput();
        }
    }
    private void handleApiSequentially(ApiDetail apiDetail) {
        if (apiDetail == null) {
            logger.info("Invalid apiDetail: null");
            return;
        }
        String path = apiDetail.getPath();
        ArrayList<String> params = apiDetail.getParams();
        StandAloneApiIdentifier apiIdentifier = StandAloneApiIdentifier.get(path);
        if (apiIdentifier != null) {
            switch (apiIdentifier) {
                case UPDATE_MYSQL_TABLE_DATA_FROM_CSV:
                    this.handleApiUpdateMysql(apiDetail, params);
                    this.askConfirmation(apiDetail);
                    break;
                case UPDATE_EXCEL_DATA:
                    this.handleApiUpdateExcelData(apiDetail, params);
                    this.askConfirmation(apiDetail);
                    break;
                case UPDATE_EXCEL_DATA_V2:
                    this.handleApiUpdateExcelDataV2(apiDetail, params);
                    this.askConfirmation(apiDetail);
                    break;
                case SPLIT_FILE:
                    this.handleApiSplitFile(apiDetail, params);
                    this.askConfirmation(apiDetail);
                    break;
                default:
                    logger.info("Invalid apiDetail: {}, apiIdentifier: {}", apiDetail, apiIdentifier);
            }
        } else {
            logger.info("Invalid apiDetail: {}, apiIdentifier: null", apiDetail);
        }
    }
    public void handleRequest() {
        if (appConfig == null || apiResource == null) {
            logger.info("Error in reading config files: {}", cmdArguments);
            logger.info("Press any key...");
            return;
        }
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        ArrayList<String> standAloneConfigPath = ftpConfiguration.getStandAloneConfigPath();
        StandAloneConfig standAloneConfig = standAloneService.getStandaloneConfig(standAloneConfigPath);
        if (standAloneConfig == null) {
            logger.info("standAloneConfig is null, Press any key to exit...");
            waitForInput();
            return;
        }
        ArrayList<ApiDetail> currentApiList = standAloneService.getApiList(standAloneConfig);
        if (currentApiList == null) {
            logger.info("apiList is null, Press any key to exit...");
            waitForInput();
            return;
        }
        for (ApiDetail currentApi: currentApiList) {
            this.handleApiSequentially(currentApi);
        }
        logger.info("Press any key to exit...");
        waitForInput();
    }

    private void waitForInput() {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        logger.info("{}",str);
    }
}
