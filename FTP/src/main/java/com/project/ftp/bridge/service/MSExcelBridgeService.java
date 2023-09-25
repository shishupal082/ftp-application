package com.project.ftp.bridge.service;

import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.ExcelFileConfig;
import com.project.ftp.bridge.obj.yamlObj.FileConfigMapping;
import com.project.ftp.common.StrUtils;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.parser.TextFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MSExcelBridgeService {
    final static Logger logger = LoggerFactory.getLogger(MSExcelBridgeService.class);
    private final ExcelToCsvDataConvertService excelToCsvDataConvertService;
    private final GoogleOAuthClientConfig googleOAuthClientConfig;
    public MSExcelBridgeService(GoogleOAuthClientConfig googleOAuthClientConfig){
        this.googleOAuthClientConfig = googleOAuthClientConfig;
        this.excelToCsvDataConvertService = new ExcelToCsvDataConvertService();
    }
    private ArrayList<ArrayList<String>> readCsvData(String srcFilepath) {
        ArrayList<ArrayList<String>> csvData = null;
        try {
            TextFileParser textFileParser = new TextFileParser((srcFilepath));
            csvData = textFileParser.getTextData();
        } catch (Exception e) {
            logger.info("Error in reading csvData for filePath: {}", srcFilepath);
        }
        return csvData;
    }
    private ArrayList<ArrayList<String>> readCsvFilePath(String srcFilepath,
                                                           ExcelDataConfig excelDataConfigById) throws AppException{
        File file1 = new File(srcFilepath);
        if (!file1.isFile()) {
            logger.info("Source csv filepath: {} does not exist, {}", srcFilepath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        ArrayList<ArrayList<String>> sheetData = this.readCsvData(srcFilepath);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readExcelFilePath(String srcFilepath, String sheetName,
                                                           ExcelDataConfig excelDataConfigById) throws AppException{
        if (sheetName == null || sheetName.isEmpty()) {
            return this.readCsvFilePath(srcFilepath, excelDataConfigById);
        }
        MSExcelServiceUtils msExcelServiceUtils = new MSExcelServiceUtils();
        ArrayList<ArrayList<String>> sheetData = msExcelServiceUtils.readExcelSheetData(srcFilepath,
                sheetName, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readGoogleSheetData(String spreadSheetId, String sheetName,
                                                           ExcelDataConfig excelDataConfigById) throws AppException{
        GoogleSheetsOAuthApi googleSheetsOAuthApi = new GoogleSheetsOAuthApi(googleOAuthClientConfig);
        ArrayList<ArrayList<String>> sheetData = googleSheetsOAuthApi.readSheetData(spreadSheetId, sheetName);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        return sheetData;
    }
    public ExcelDataConfig getExcelDataConfigByIdV1(String requestId,
                                                    HashMap<String, ExcelDataConfig> excelConfig) {
        if (requestId == null || excelConfig == null) {
            logger.info("Request id or excelDataConfigHashMap is null: {},{}", requestId, excelConfig);
            return null;
        }
        ExcelDataConfig excelDataConfigById = excelConfig.get(requestId);
        if (excelDataConfigById == null) {
            logger.info("excelDataConfigById is null for request id: {}", requestId);
        } else {
            excelDataConfigById.setExcelConfig(null);
            excelDataConfigById.setCsvConfig(null);
            excelDataConfigById.setGsConfig(null);
            logger.info("excelDataConfigById for requestId: {}, {}", requestId, excelDataConfigById);
        }
        return excelDataConfigById;
    }
    private ArrayList<ExcelFileConfig> getFileConfigByRequestId(String requestId, FileConfigMapping fileConfigMapping,
                                                                ArrayList<ArrayList<String>> csvData) {
        if (fileConfigMapping == null) {
            return null;
        }
        ArrayList<Integer> requiredColIndex = fileConfigMapping.getRequiredColIndex();
        if (requiredColIndex == null || requiredColIndex.size() < 4) {
            logger.info("requiredColIndex size is less than 4: {}", requiredColIndex);
            return null;
        }
        int requestIdCol, sourceCol, sheetNameCol, destinationCol;
        Integer copyDestinationCol = null;
        StrUtils strUtils = new StrUtils();
        requestIdCol = requiredColIndex.get(0);
        sourceCol = requiredColIndex.get(1);
        sheetNameCol = requiredColIndex.get(2);
        destinationCol = requiredColIndex.get(3);
        if (requiredColIndex.size() >= 5) {
            copyDestinationCol = requiredColIndex.get(4);
        }
        String req, srcPath, sheet, dest, copyDest;
        ArrayList<ExcelFileConfig> fileConfig = new ArrayList<>();
        ExcelFileConfig excelFileConfig;
        for(ArrayList<String> row: csvData) {
            if (row == null) {
                continue;
            }
            req = null;
            copyDest = null;
            if (row.size() > requestIdCol) {
                req = row.get(requestIdCol);
            }
            if (!requestId.equals(req)) {
                continue;
            }
            if (row.size() > sourceCol) {
                srcPath = row.get(sourceCol);
            } else {
                continue;
            }
            if (row.size() > sheetNameCol) {
                sheet = row.get(sheetNameCol);
            } else {
                continue;
            }
            if (row.size() > destinationCol) {
                dest = row.get(destinationCol);
            } else {
                continue;
            }
            if (copyDestinationCol != null && row.size() > copyDestinationCol) {
                copyDest = row.get(copyDestinationCol);
            }
            excelFileConfig = new ExcelFileConfig();
            excelFileConfig.setSource(strUtils.formatString(srcPath));
            excelFileConfig.setSheetName(strUtils.formatString(sheet));
            excelFileConfig.setDestination(strUtils.formatString(dest));
            excelFileConfig.setCopyDestination(strUtils.formatString(copyDest));
            fileConfig.add(excelFileConfig);
        }
        if (fileConfig.size() == 0) {
            fileConfig = null;
        }
        return fileConfig;
    }
    public ExcelDataConfig updateExcelDataConfigFromGoogle(ExcelDataConfig excelDataConfigById, String requestId,
                                                    FileConfigMapping fileConfigMapping) {
        if (fileConfigMapping == null || requestId == null) {
            return excelDataConfigById;
        }
        ArrayList<String> fileConfig = fileConfigMapping.getFileConfig();
        if (fileConfig == null) {
            return excelDataConfigById;
        }
        if (fileConfig.size() < 2) {
            logger.info("Invalid fileConfigMapping for requestId: {},{}", requestId, fileConfigMapping);
            return excelDataConfigById;
        }
        String srcFilepath = fileConfig.get(0);
        String sheetName = fileConfig.get(1);
        ArrayList<ArrayList<String>> sheetData = this.readGoogleSheetData(srcFilepath, sheetName, null);
        if (sheetData != null) {
            if (fileConfigMapping.getFileConfigSourceGoogle()) {
                ArrayList<ExcelFileConfig> gsConfig = this.getFileConfigByRequestId(requestId, fileConfigMapping, sheetData);
                if (gsConfig != null && fileConfig.size() > 0) {
                    if (excelDataConfigById == null) {
                        excelDataConfigById = new ExcelDataConfig();
                    }
                    excelDataConfigById.setGsConfig(gsConfig);
                } else {
                    logger.info("fileConfig not found in googleSheetData for requestId: {}, {}",
                            requestId, fileConfigMapping);
                }
            } else {
                excelDataConfigById = this.updateExcelDataConfigByIdFromCsv(requestId, excelDataConfigById,
                        fileConfigMapping, sheetData, AppConstant.FALSE);
            }
        }
        logger.info("excelDataConfigById generated from googleSheetData for requestId: {}, {}",
                requestId, excelDataConfigById);
        return excelDataConfigById;
    }
    private ExcelDataConfig updateExcelDataConfigByIdFromCsv(String requestId,
                                                             ExcelDataConfig excelDataConfigById,
                                                             FileConfigMapping fileConfigMapping,
                                                             ArrayList<ArrayList<String>> csvData,
                                                             String isCsv) {
        if (csvData != null) {
            ArrayList<ExcelFileConfig> fileConfig = this.getFileConfigByRequestId(requestId, fileConfigMapping, csvData);
            if (fileConfig != null && fileConfig.size() > 0) {
                if (excelDataConfigById == null) {
                    excelDataConfigById = new ExcelDataConfig();
                }
                if (AppConstant.TRUE.equals(isCsv)) {
                    excelDataConfigById.setCsvConfig(fileConfig);
                } else {
                    excelDataConfigById.setExcelConfig(fileConfig);
                }
            } else {
                logger.info("fileConfig not found in csvData for requestId: {}, {}, {}",
                        requestId, csvData, fileConfigMapping);
            }
        }
        return excelDataConfigById;
    }
    public ExcelDataConfig updateExcelDataConfigFromCsv(ExcelDataConfig excelDataConfigById, String requestId,
                                                           FileConfigMapping fileConfigMapping) {
        if (fileConfigMapping == null || requestId == null) {
            return excelDataConfigById;
        }
        ArrayList<String> fileConfigArray = fileConfigMapping.getFileConfig();
        if (fileConfigArray == null) {
            return excelDataConfigById;
        }
        String srcFilepath;
        if (fileConfigArray.size() > 0) {
            srcFilepath = fileConfigArray.get(0);
        } else {
            logger.info("srcFilePathCsv is not found.");
            return null;
        }
        ArrayList<ArrayList<String>> csvData = this.readCsvData(srcFilepath);
        excelDataConfigById = this.updateExcelDataConfigByIdFromCsv(requestId, excelDataConfigById,
                fileConfigMapping, csvData, AppConstant.FALSE);
        logger.info("excelDataConfigById generated from csv for requestId: {}, {}", requestId, excelDataConfigById);
        return excelDataConfigById;
    }
    public ExcelDataConfig updateExcelDataConfigFromCsv2(ExcelDataConfig excelDataConfigById, String requestId,
                                                        FileConfigMapping fileConfigMapping) {
        if (fileConfigMapping == null || requestId == null) {
            return excelDataConfigById;
        }
        ArrayList<String> fileConfigArray = fileConfigMapping.getFileConfig();
        if (fileConfigArray == null) {
            return excelDataConfigById;
        }
        String srcFilepath;
        if (fileConfigArray.size() > 0) {
            srcFilepath = fileConfigArray.get(0);
        } else {
            logger.info("srcFilePathCsv is not found.");
            return null;
        }
        ArrayList<ArrayList<String>> csvData = this.readCsvData(srcFilepath);
        excelDataConfigById = this.updateExcelDataConfigByIdFromCsv(requestId, excelDataConfigById,
                fileConfigMapping, csvData, AppConstant.TRUE);
        logger.info("excelDataConfigById generated from csv for requestId: {}, {}", requestId, excelDataConfigById);
        return excelDataConfigById;
    }
    public ArrayList<BridgeResponseSheetData> readExcelSheetData(ExcelDataConfig excelDataConfigById) throws AppException {
        if (excelDataConfigById == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<ExcelFileConfig> excelFileConfig = excelDataConfigById.getExcelConfig();
        ArrayList<ExcelFileConfig> csvFileConfig = excelDataConfigById.getCsvConfig();
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
        if (csvFileConfig != null && csvFileConfig.size() > 0) {
            for (ExcelFileConfig fileConfig : csvFileConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource();
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readCsvFilePath(srcFilepath, excelDataConfigById);
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
                (csvFileConfig == null || csvFileConfig.size() == 0) &&
                (gsFileConfig == null || gsFileConfig.size() == 0)) {
            logger.info("Invalid excelFileConfig, csvFileConfig and gsFileConfig: {}", excelDataConfigById);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return bridgeResponseSheetsData;
    }
    public FileConfigMapping getValidFileConfigMapping(String requestId,
                                                       ArrayList<FileConfigMapping> fileConfigMappings) {
        if (fileConfigMappings == null || requestId == null) {
            return null;
        }
        ArrayList<String> validRequestIds;
        for(FileConfigMapping fileConfigMapping: fileConfigMappings) {
            if (fileConfigMapping != null) {
                validRequestIds = fileConfigMapping.getValidRequestId();
                if (validRequestIds != null && validRequestIds.contains(requestId)) {
                    return fileConfigMapping;
                }
            }
        }
        return null;
    }
}
