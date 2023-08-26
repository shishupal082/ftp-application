package com.project.ftp.service;

import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.FileMappingConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class MSExcelService {
    private final static Logger logger = LoggerFactory.getLogger(MSExcelService.class);
    private final AppConfig appConfig;
    private final FileService fileService;
    private final FileServiceV3 fileServiceV3;

    public MSExcelService(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        this.fileServiceV3 = new FileServiceV3(appConfig, userService);
    }
    private void saveCsvData(BridgeResponseSheetData bridgeResponseSheetData) {
        if (bridgeResponseSheetData == null) {
            logger.info("Invalid bridgeResponseSheetData: null");
            return;
        }
        boolean copyOldData = bridgeResponseSheetData.isCopyOldData();
        String destination = bridgeResponseSheetData.getDestination();
        String copyDestination = bridgeResponseSheetData.getCopyDestination();
        ArrayList<ArrayList<String>> sheetData = bridgeResponseSheetData.getSheetData();
        if (destination == null || sheetData == null) {
            logger.info("Invalid bridgeResponseSheetData: {}", bridgeResponseSheetData);
            return;
        }
        ArrayList<String> csvData = new ArrayList<>();
        String temp;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData != null) {
                temp = "";
                for(int i=0; i<rowData.size(); i++) {
                    if (i==0) {
                        temp = rowData.get(i);
                    } else {
                        temp = temp.concat("," + rowData.get(i));
                    }
                }
                csvData.add(temp);
            }
        }
        if (copyOldData) {
            fileService.copyFile(destination, destination, true);
        }
        fileService.deleteFileV2(destination);
        boolean status = fileServiceV3.saveAddTextV3(destination, csvData, false);
        if (status) {
            logger.info("csv data saved: {}", destination);
            if (!destination.equals(copyDestination) && StaticService.isValidString(copyDestination)) {
                fileService.copyFile(destination, copyDestination, false);
            }
        } else {
            logger.info("Error in saving csv data: {}", destination);
        }
    }
    public ApiResponse updateMSExcelSheetData(String requestId) throws AppException {
        if (requestId == null || requestId.isEmpty()) {
            logger.info("requestId required: {}", requestId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        YamlFileParser yamlFileParser = new YamlFileParser();
        FileMappingConfig fileMappingConfig =
                    yamlFileParser.getFileMappingConfigFromPath(
                            appConfig.getFtpConfiguration().getFileMappingConfigFilePath());
        if (fileMappingConfig == null) {
            logger.info("fileMappingConfig is null.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        HashMap<String, ExcelDataConfig> excelDataConfigHashMap = yamlFileParser.getExcelDataConfig(
                fileMappingConfig.getExcelFileConfigPath());

        ArrayList<BridgeResponseSheetData> response =
                appConfig.getAppToBridge().getExcelData(requestId, fileMappingConfig, excelDataConfigHashMap);
        if (response == null) {
            throw new AppException(ErrorCodes.SERVER_ERROR);
        }
        for (BridgeResponseSheetData bridgeResponseSheetData: response) {
            this.saveCsvData(bridgeResponseSheetData);
        }
        return new ApiResponse(AppConstant.SUCCESS);
    }
}
