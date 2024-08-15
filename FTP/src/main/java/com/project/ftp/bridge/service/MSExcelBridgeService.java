package com.project.ftp.bridge.service;

import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.ExcelFileConfig;
import com.project.ftp.bridge.obj.yamlObj.FileConfigMapping;
import com.project.ftp.common.StrUtils;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.parser.MSExcelSheetParser;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MSExcelBridgeService {
    final static Logger logger = LoggerFactory.getLogger(MSExcelBridgeService.class);
    private final ExcelToCsvDataConvertService excelToCsvDataConvertService;
    private final GoogleOAuthClientConfig googleOAuthClientConfig;
    private final HttpServletRequest request;
    private final EventTracking eventTracking;
    public MSExcelBridgeService(HttpServletRequest request, EventTracking eventTracking,
                                GoogleOAuthClientConfig googleOAuthClientConfig){
        this.googleOAuthClientConfig = googleOAuthClientConfig;
        this.eventTracking = eventTracking;
        this.excelToCsvDataConvertService = new ExcelToCsvDataConvertService();
        this.request = request;
    }
    private ArrayList<ArrayList<String>> readCsvData(String srcFilepath) {
        TextFileParser textFileParser = new TextFileParser((srcFilepath));
        return textFileParser.readCsvData();
    }
    public ArrayList<ArrayList<String>> applyCsvConfigOnData(ArrayList<ArrayList<String>> sheetData,
                                                              String srcFilepath, String sheetName,
                                                              ExcelDataConfig excelDataConfigById,
                                                              ArrayList<String> uniqueStrings) throws AppException{
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, srcFilepath, sheetName);
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyRemoveColumnConfig(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyUniqueEntry(sheetData, excelDataConfigById, uniqueStrings);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readCsvFilePath(String srcFilepath, String sheetName,
                                                           ExcelDataConfig excelDataConfigById,
                                                         ArrayList<String> uniqueStrings) throws AppException{
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
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, srcFilepath, sheetName);
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyRemoveColumnConfig(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyUniqueEntry(sheetData, excelDataConfigById, uniqueStrings);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readExcelFilePath(String srcFilepath, String sheetName,
                                                           ExcelDataConfig excelDataConfigById,
                                                           ArrayList<String> uniqueStrings) throws AppException{
        if (sheetName == null || sheetName.isEmpty()) {
            return this.readCsvFilePath(srcFilepath, sheetName, excelDataConfigById, uniqueStrings);
        }
        MSExcelSheetParser msExcelSheetParser = new MSExcelSheetParser();
        ArrayList<ArrayList<String>> sheetData = msExcelSheetParser.readExcelSheetData(srcFilepath,
                sheetName, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, srcFilepath, sheetName);
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyRemoveColumnConfig(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyUniqueEntry(sheetData, excelDataConfigById, uniqueStrings);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readGoogleSheetData(String spreadSheetId, String sheetName,
                                                             ExcelDataConfig excelDataConfigById,
                                                             ArrayList<String> uniqueStrings) throws AppException{
        GoogleSheetsOAuthApi googleSheetsOAuthApi = new GoogleSheetsOAuthApi(eventTracking, googleOAuthClientConfig);
        ArrayList<ArrayList<String>> sheetData = googleSheetsOAuthApi.readSheetData(request, spreadSheetId, sheetName);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, spreadSheetId, sheetName);
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyColumnMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyRemoveColumnConfig(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyUniqueEntry(sheetData, excelDataConfigById, uniqueStrings);
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
    private ExcelDataConfig getExcelDataConfigByIdV2(String requestId,
                                                     FileConfigMapping fileConfigMapping) {
        if (requestId == null || fileConfigMapping == null) {
            logger.info("Request id or fileConfigMapping is null: {},{}", requestId, fileConfigMapping);
            return null;
        }
        ArrayList<String> validIds = fileConfigMapping.getValidRequestId();
        ExcelDataConfig excelDataConfigById = null;
        if (validIds != null && validIds.contains(requestId)) {
            excelDataConfigById = new ExcelDataConfig();
            excelDataConfigById.setExcelConfig(null);
            excelDataConfigById.setCsvConfig(null);
            excelDataConfigById.setGsConfig(null);
            logger.info("excelDataConfigById for requestId: {}, {}", requestId, excelDataConfigById);
        } else {
            logger.info("excelDataConfigById is null for request id: {}", requestId);
        }
        return excelDataConfigById;
    }
    private ArrayList<ExcelFileConfig> getFileConfigByRequestId(String requestId, FileConfigMapping fileConfigMapping,
                                                                ArrayList<ArrayList<String>> csvData) {
        if (fileConfigMapping == null || requestId == null) {
            return null;
        }
        ArrayList<Integer> requiredColIndex = fileConfigMapping.getRequiredColIndex();
        if (requiredColIndex == null || requiredColIndex.size() < 4) {
            logger.info("requiredColIndex size is less than 4: {}", requiredColIndex);
            return null;
        }
        int requestIdCol, sourceCol, sheetNameCol, destinationCol;
        int copyDestinationCol = -1;
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
            if (requestIdCol >= 0 && row.size() > requestIdCol) {
                req = row.get(requestIdCol);
            }
            if (!requestId.equals(req)) {
                continue;
            }
            if (sourceCol >= 0 && row.size() > sourceCol) {
                srcPath = row.get(sourceCol);
            } else {
                continue;
            }
            if (sheetNameCol >= 0 && row.size() > sheetNameCol) {
                sheet = row.get(sheetNameCol);
            } else {
                sheet = null;
            }
            if (destinationCol >= 0 && row.size() > destinationCol) {
                dest = row.get(destinationCol);
            } else {
                dest = null;
            }
            if (copyDestinationCol >= 0 && row.size() > copyDestinationCol) {
                copyDest = row.get(copyDestinationCol);
            }
            excelFileConfig = new ExcelFileConfig();
            excelFileConfig.setSource(strUtils.formatString(srcPath));
            excelFileConfig.setSheetName(strUtils.formatString(sheet));
            excelFileConfig.setDestination(strUtils.formatString(dest));
            excelFileConfig.setCopyDestination(strUtils.formatString(copyDest));
            excelFileConfig.setFileConfigMapping(fileConfigMapping);
            fileConfig.add(excelFileConfig);
        }
        if (fileConfig.isEmpty()) {
            fileConfig = null;
        }
        return fileConfig;
    }
    public ExcelDataConfig updateExcelDataConfigFromGoogle2(ExcelDataConfig excelDataConfigById,
                                                           FileConfigMapping fileConfigMapping) {
        if (fileConfigMapping == null) {
            return excelDataConfigById;
        }
        ExcelFileConfig excelFileConfig = new ExcelFileConfig();
        excelFileConfig.setFileConfigMapping(fileConfigMapping);

        ArrayList<ExcelFileConfig> gsConfig = new ArrayList<>();
        gsConfig.add(excelFileConfig);

        if (excelDataConfigById == null) {
            excelDataConfigById = new ExcelDataConfig();
            excelDataConfigById.setGsConfig(gsConfig);
            logger.info("excelDataConfigById generated: {}", excelDataConfigById);
        } else {
            excelDataConfigById.setGsConfig(gsConfig);
            logger.info("excelDataConfigById updated: {}", excelDataConfigById);
        }
        return excelDataConfigById;
    }
    public ExcelDataConfig updateExcelDataConfigById(ExcelDataConfig excelDataConfigById, String id,
                                          FileConfigMapping fileConfigMapping, ArrayList<ArrayList<String>> sheetData) {
        if (excelDataConfigById == null) {
            excelDataConfigById = this.getExcelDataConfigByIdV2(id, fileConfigMapping);
            if (excelDataConfigById == null) {
                return null;
            }
        }
        if (id == null || fileConfigMapping == null || sheetData == null) {
            return excelDataConfigById;
        }
        ArrayList<ExcelFileConfig> excelFileConfigs = this.getFileConfigByRequestId(id, fileConfigMapping, sheetData);
        if (excelFileConfigs == null || excelFileConfigs.isEmpty()) {
            return excelDataConfigById;
        }
        excelDataConfigById.setCsvConfig(null);
        excelDataConfigById.setExcelConfig(null);
        excelDataConfigById.setGsConfig(null);
        if (AppConstant.CSV.equals(fileConfigMapping.getFileDataSource())) {
            excelDataConfigById.setCsvConfig(excelFileConfigs);
        } else if (AppConstant.MS_EXCEL.equals(fileConfigMapping.getFileDataSource())) {
            excelDataConfigById.setExcelConfig(excelFileConfigs);
        } else {
            excelDataConfigById.setGsConfig(excelFileConfigs);
        }
        return excelDataConfigById;
    }
    private ArrayList<ExcelFileConfig> getGsConfigEntry(String id, ExcelFileConfig excelFileConfig) {
        FileConfigMapping fileConfigMapping;
        ArrayList<String> fileConfig;
        ArrayList<ExcelFileConfig> gsConfig2 = new ArrayList<>();
        if (excelFileConfig == null) {
            return null;
        }
        if (StaticService.isValidString(excelFileConfig.getSource()) && StaticService.isValidString(excelFileConfig.getSheetName())) {
            gsConfig2.add(excelFileConfig);
            return gsConfig2;
        }
        fileConfigMapping = excelFileConfig.getFileConfigMapping();
        if (fileConfigMapping == null) {
            gsConfig2.add(excelFileConfig);
            return gsConfig2;
        }
        fileConfig = fileConfigMapping.getFileConfig();
        if (fileConfig == null || fileConfig.size() < 2) {
            gsConfig2.add(excelFileConfig);
            return gsConfig2;
        }
        ArrayList<String> uniqueStrings = new ArrayList<>();
        String srcFilepath = fileConfig.get(0);
        String sheetName = fileConfig.get(1);
        ArrayList<ArrayList<String>> sheetData = this.readGoogleSheetData(srcFilepath, sheetName, null, uniqueStrings);
        if (sheetData != null) {
            gsConfig2 = this.getFileConfigByRequestId(id, fileConfigMapping, sheetData);
        }
        return gsConfig2;
    }
    public ExcelDataConfig updateExcelDataConfigFromGoogle(ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null) {
            return null;
        }
        String id = excelDataConfigById.getId();
        ArrayList<ExcelFileConfig> gsConfig = excelDataConfigById.getGsConfig();
        ArrayList<ExcelFileConfig> gsConfig2, gsConfigTemp;
        if (gsConfig == null) {
            return excelDataConfigById;
        }
        if (id == null) {
            logger.info("Invalid excelDataConfigById, id is null");
            return excelDataConfigById;
        }
        gsConfig2 = new ArrayList<>();
        FileConfigMapping fileConfigMapping = null;
        for(ExcelFileConfig excelFileConfig: gsConfig) {
            fileConfigMapping = excelFileConfig.getFileConfigMapping();
            gsConfigTemp = this.getGsConfigEntry(id, excelFileConfig);
            if (gsConfigTemp != null) {
                gsConfig2.addAll(gsConfigTemp);
            }
        }
        excelDataConfigById.setGsConfig(null);
        excelDataConfigById.setCsvConfig(null);
        excelDataConfigById.setExcelConfig(null);
        if (!gsConfig2.isEmpty() && fileConfigMapping != null) {
            if (AppConstant.CSV.equals(fileConfigMapping.getFileDataSource())) {
                excelDataConfigById.setCsvConfig(gsConfig2);
            } else if (AppConstant.MS_EXCEL.equals(fileConfigMapping.getFileDataSource())) {
                excelDataConfigById.setExcelConfig(gsConfig2);
            } else {
                excelDataConfigById.setGsConfig(gsConfig2);
            }
        }
        return excelDataConfigById;
    }
    public ExcelDataConfig updateExcelDataConfigFromExcel(ExcelDataConfig excelDataConfigById, String requestId,
                                                           FileConfigMapping fileConfigMapping) {
        if (fileConfigMapping == null || requestId == null) {
            return excelDataConfigById;
        }
        ArrayList<String> fileConfigArray = fileConfigMapping.getFileConfig();
        if (fileConfigArray == null) {
            return excelDataConfigById;
        }
        String srcFilepath;
        String sheetName;
        if (fileConfigArray.size() > 1) {
            srcFilepath = fileConfigArray.get(0);
            sheetName = fileConfigArray.get(1);
        } else {
            logger.info("srcFilePathCsv is not found.");
            return null;
        }
        ArrayList<ArrayList<String>> csvData = this.readExcelFilePath(srcFilepath, sheetName, null, null);
        excelDataConfigById = this.updateExcelDataConfigById(excelDataConfigById, requestId, fileConfigMapping, csvData);
        logger.info("excelDataConfigById generated from excel for requestId: {}, {}", requestId, excelDataConfigById);
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
        if (!fileConfigArray.isEmpty()) {
            srcFilepath = fileConfigArray.get(0);
        } else {
            logger.info("srcFilePathCsv is not found.");
            return null;
        }
        ArrayList<ArrayList<String>> csvData = this.readCsvData(srcFilepath);
        csvData = excelToCsvDataConvertService.removeFirstEmptyRow(csvData);
        excelDataConfigById = this.updateExcelDataConfigById(excelDataConfigById, requestId, fileConfigMapping, csvData);
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
        ArrayList<String> uniqueStrings = new ArrayList<>();
        String srcFilepath, sheetName, destination, copyDestination;
        boolean copyOldData;
        ArrayList<ArrayList<String>> sheetData;
        ArrayList<BridgeResponseSheetData> bridgeResponseSheetsData = new ArrayList<>();
        if (excelFileConfig != null && !excelFileConfig.isEmpty()) {
            for (ExcelFileConfig fileConfig : excelFileConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readExcelFilePath(srcFilepath, sheetName, excelDataConfigById, uniqueStrings);
                bridgeResponseSheetsData.add(new BridgeResponseSheetData(copyOldData,
                        destination, copyDestination, sheetData));
            }
        }
        if (csvFileConfig != null && !csvFileConfig.isEmpty()) {
            for (ExcelFileConfig fileConfig : csvFileConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readCsvFilePath(srcFilepath, sheetName, excelDataConfigById, uniqueStrings);
                bridgeResponseSheetsData.add(new BridgeResponseSheetData(copyOldData,
                        destination, copyDestination, sheetData));
            }
        }
        if (gsFileConfig != null && !gsFileConfig.isEmpty()) {
            for (ExcelFileConfig fileConfig : gsFileConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readGoogleSheetData(srcFilepath, sheetName, excelDataConfigById, uniqueStrings);
                bridgeResponseSheetsData.add(new BridgeResponseSheetData(copyOldData,
                        destination, copyDestination, sheetData));
            }
        }
        if ((excelFileConfig == null || excelFileConfig.isEmpty()) &&
                (csvFileConfig == null || csvFileConfig.isEmpty()) &&
                (gsFileConfig == null || gsFileConfig.isEmpty())) {
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
