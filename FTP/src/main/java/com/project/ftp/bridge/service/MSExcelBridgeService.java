package com.project.ftp.bridge.service;

import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.ExcelFileConfig;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MSExcelBridgeService {
    final static Logger logger = LoggerFactory.getLogger(MSExcelBridgeService.class);
    private final ExcelToCsvDataConvertService excelToCsvDataConvertService;
    private final GoogleOAuthClientConfig googleOAuthClientConfig;
    public MSExcelBridgeService(GoogleOAuthClientConfig googleOAuthClientConfig){
        this.googleOAuthClientConfig = googleOAuthClientConfig;
        this.excelToCsvDataConvertService = new ExcelToCsvDataConvertService();
    }
    private ArrayList<ArrayList<String>> readExcelFilePath(String srcFilepath, String sheetName,
                                                           ExcelDataConfig excelDataConfigById) throws AppException{
        MSExcelServiceUtils msExcelServiceUtils = new MSExcelServiceUtils();
        ArrayList<ArrayList<String>> sheetData = msExcelServiceUtils.readExcelSheetData(srcFilepath,
                sheetName, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readGoogleSheetData(String spreadSheetId, String sheetName,
                                                           ExcelDataConfig excelDataConfigById) throws AppException{
        GoogleSheetsOAuthApi googleSheetsOAuthApi = new GoogleSheetsOAuthApi(googleOAuthClientConfig);
        ArrayList<ArrayList<String>> sheetData = googleSheetsOAuthApi.readSheetData(spreadSheetId, sheetName);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById);
        return sheetData;
    }
    public ArrayList<BridgeResponseSheetData> readExcelSheetData(ExcelDataConfig excelDataConfigById) throws AppException {
        if (excelDataConfigById == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<ExcelFileConfig> excelFileConfig = excelDataConfigById.getFileConfig();
        ArrayList<ExcelFileConfig> gsFileConfig = excelDataConfigById.getGsConfig();
        String srcFilepath, sheetName, destination, copyDestination;
        boolean copyOldData;
        ArrayList<ArrayList<String>> sheetData;
        ArrayList<BridgeResponseSheetData> bridgeResponseSheetsData = new ArrayList<>();
        if (excelFileConfig != null && excelFileConfig.size() > 0) {
            for (ExcelFileConfig fileConfig : excelFileConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readExcelFilePath(srcFilepath, sheetName, excelDataConfigById);
                bridgeResponseSheetsData.add(new BridgeResponseSheetData(copyOldData,
                        destination, copyDestination, sheetData));
            }
        }
        if (gsFileConfig != null && gsFileConfig.size() > 0) {
            for (ExcelFileConfig fileConfig : gsFileConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readGoogleSheetData(srcFilepath, sheetName, excelDataConfigById);
                bridgeResponseSheetsData.add(new BridgeResponseSheetData(copyOldData,
                        destination, copyDestination, sheetData));
            }
        }
        if ((excelFileConfig == null || excelFileConfig.size() == 0) &&
                (gsFileConfig == null || gsFileConfig.size() == 0)) {
            logger.info("Invalid excelFileConfig and gsFileConfig: {}", excelDataConfigById);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return bridgeResponseSheetsData;
    }
}
