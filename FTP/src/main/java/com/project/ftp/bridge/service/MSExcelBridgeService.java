package com.project.ftp.bridge.service;

import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.bridge.mysqlTable.SaveTableParameter;
import com.project.ftp.bridge.mysqlTable.TableService;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.ExcelFileConfig;
import com.project.ftp.bridge.obj.yamlObj.FileConfigMapping;
import com.project.ftp.bridge.obj.yamlObj.MysqlCsvDataConfig;
import com.project.ftp.common.StrUtils;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.parser.MSExcelSheetParser;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.FileService;
import com.project.ftp.service.MiscService;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MSExcelBridgeService {
    final static Logger logger = LoggerFactory.getLogger(MSExcelBridgeService.class);
    private final ExcelToCsvDataConvertServiceV2 excelToCsvDataConvertService;
    private final GoogleOAuthClientConfig googleOAuthClientConfig;
    private final HttpServletRequest request;
    private final EventTracking eventTracking;
    private final TableService tableService;
    private final FileService fileService = new FileService();
    private final StrUtils strUtils = new StrUtils();
    private final MiscService miscService = new MiscService();
    public MSExcelBridgeService(HttpServletRequest request, EventTracking eventTracking,
                                GoogleOAuthClientConfig googleOAuthClientConfig,
                                TableService tableService){
        this.googleOAuthClientConfig = googleOAuthClientConfig;
        this.eventTracking = eventTracking;
        this.excelToCsvDataConvertService = new ExcelToCsvDataConvertServiceV2();
        this.tableService = tableService;
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
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, srcFilepath, sheetName);
        sheetData = excelToCsvDataConvertService.applyMergeColumnMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyRemoveColumnConfig(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyUniqueEntry(sheetData, excelDataConfigById, uniqueStrings);
        return sheetData;
    }
    public ArrayList<HashMap<String, String>> applyCsvConfigOnTableData(String requestTableConfigId,
                                                                        String requestDefaultFilterMappingId,
                                                                        ArrayList<HashMap<String, String>> tableData,
                                                            TableConfiguration tableConfiguration) throws AppException{
        tableData = excelToCsvDataConvertService.applySkipRowCriteriaV2(tableData, tableConfiguration);
        tableData = excelToCsvDataConvertService.applyCellMappingV2(requestTableConfigId, requestDefaultFilterMappingId,
                                    tableData, tableConfiguration);
        return tableData;
    }
    public ArrayList<String> applyCsvConfigOnRowData(ArrayList<String> rowData, String srcFilepath, String sheetName,
                                                     ExcelDataConfig excelDataConfigById,
                                                     ArrayList<String> uniqueStrings) {
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        if (rowData == null) {
            return null;
        }
        sheetData.add(rowData);
        sheetData = this.applyCsvConfigOnData(sheetData,srcFilepath,sheetName,excelDataConfigById,uniqueStrings);
        if (!sheetData.isEmpty()) {
            return sheetData.get(0);
        }
        return null;
    }
    private boolean saveRowData(ArrayList<String> rowData, Writer writer, boolean isNewFile) {
        if (rowData == null || rowData.isEmpty()) {
            return false;
        }
        try {
            if (!isNewFile) {
                writer.append("\n");
            }
            String str = strUtils.joinArrayList(rowData,AppConstant.commaDelimater);
            writer.append(str);
        } catch (Exception e) {
            logger.info("saveRowData: Error in saving row data");
            return false;
        }
        return true;
    }
    private boolean callBackSaveTableRow(ArrayList<String> rowData, ExcelDataConfig excelDataConfig,
                                         SaveTableParameter saveTableParameter) {
        if (rowData == null || rowData.isEmpty()) {
            return false;
        }
        ArrayList<HashMap<String,String>> rowJsonData;
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        sheetData.add(rowData);
        rowJsonData = miscService.convertArraySheetDataToJsonData(sheetData, excelDataConfig.getTableMappingIndex());
        if (rowJsonData != null && !rowJsonData.isEmpty()) {
            return tableService.saveTableRowData(rowJsonData.get(0),saveTableParameter);
        }
        return false;
    }
    public boolean writerService(String writerType, Writer writer, ArrayList<String> rowData,
                                 boolean isNewFile,
                                 String sourceFilePath,
                                 String sheetName,
                                 ExcelDataConfig excelDataConfigById,
                                 ArrayList<String> uniqueStrings,
                                 SaveTableParameter saveTableParameter) {
        if (writerType == null) {
            return false;
        }
        ArrayList<String> rowData2 = this.applyCsvConfigOnRowData(rowData,sourceFilePath,
                sheetName,excelDataConfigById,uniqueStrings);
        if (writerType.equals("destinationFile")) {
            return this.saveRowData(rowData2, writer, isNewFile);
        } else if (writerType.equals("saveAsTableRow")) {
            return this.callBackSaveTableRow(rowData2,excelDataConfigById, saveTableParameter);
        }
        return false;
    }
    // (1/2) readCsvFile --> writeDbTableRow
    private boolean readCsvAndWriteToTableRow(String srcFilepath, String sheetName,
                                              ExcelDataConfig excelDataConfigById,
                                              ArrayList<String> uniqueStrings,
                                              SaveTableParameter saveTableParameter) throws AppException {
        File file1 = new File(srcFilepath);
        if (!file1.isFile()) {
            logger.info("readAndWriteToDbCsvFilePath: Source csv filepath: {} does not exist, {}", srcFilepath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        TextFileParser textFileParser = new TextFileParser();
        textFileParser.readAndWriteCsvData("saveAsTableRow",srcFilepath, null, false,
                this, sheetName,excelDataConfigById,uniqueStrings,saveTableParameter);
        return true;
    }
    // (2/4) readCsvFile --> writeCsvFile
    private boolean readAndWriteCsvFilePath(String srcFilepath, String destinationFilePath, String sheetName,
                                            ExcelDataConfig excelDataConfigById,
                                            ArrayList<String> uniqueStrings,
                                            boolean isNewFile) throws AppException {
        File file1 = new File(srcFilepath);
        if (!file1.isFile()) {
            logger.info("readAndWriteCsvFilePath: Source csv filepath: {} does not exist, {}", srcFilepath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        File file2 = new File(destinationFilePath);
        if (!file2.isFile()) {
            logger.info("readAndWriteCsvFilePath: Destination csv filepath: {} does not exist, {}", destinationFilePath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        TextFileParser textFileParser = new TextFileParser();
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file2, true), AppConstant.UTF8));
            textFileParser.readAndWriteCsvData("destinationFile",srcFilepath, writer, isNewFile, this,
                    sheetName,excelDataConfigById,uniqueStrings,null);
            writer.close();
        } catch (Exception e) {
            logger.info("readAndWriteCsvFilePath: Error in writer service");
            return false;
        }
        return true;
    }

    // (3/4) readExcelFile --> writeDbTableRow
    private boolean readExcelAndWriteToTableRow(String srcFilepath, String sheetName,
                                              ExcelDataConfig excelDataConfigById,
                                              ArrayList<String> uniqueStrings,
                                              SaveTableParameter saveTableParameter) throws AppException {
        File file1 = new File(srcFilepath);
        if (!file1.isFile()) {
            logger.info("readExcelAndWriteToTableRow: Source csv filepath: {} does not exist, {}", srcFilepath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        MSExcelSheetParser msExcelSheetParser = new MSExcelSheetParser();
        msExcelSheetParser.readExcelSheetDataV2("saveAsTableRow", null, false,
                srcFilepath, sheetName, excelDataConfigById,
                this,uniqueStrings,saveTableParameter);
        return true;
    }
    // (4/4) readExcelFile --> writeCsvFile
    private boolean readExcelAndWriteCsvFilePath(String srcFilepath, String destinationFilePath, String sheetName,
                                            ExcelDataConfig excelDataConfigById,
                                            ArrayList<String> uniqueStrings,
                                            boolean isNewFile) throws AppException {
        File file1 = new File(srcFilepath);
        if (!file1.isFile()) {
            logger.info("readExcelAndWriteCsvFilePath: Source csv filepath: {} does not exist, {}", srcFilepath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        File file2 = new File(destinationFilePath);
        if (!file2.isFile()) {
            logger.info("readExcelAndWriteCsvFilePath: Destination csv filepath: {} does not exist, {}", destinationFilePath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        MSExcelSheetParser msExcelSheetParser = new MSExcelSheetParser();
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file2, true), AppConstant.UTF8));
            msExcelSheetParser.readExcelSheetDataV2("destinationFile", writer, isNewFile,
                    srcFilepath, sheetName, excelDataConfigById,
                    this,uniqueStrings,null);
            writer.close();
        } catch (Exception e) {
            logger.info("readExcelAndWriteCsvFilePath: Error in writer service");
            return false;
        }
        return true;
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
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, srcFilepath, sheetName);
        sheetData = excelToCsvDataConvertService.applyMergeColumnMapping(sheetData, excelDataConfigById);
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
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, srcFilepath, sheetName);
        sheetData = excelToCsvDataConvertService.applyMergeColumnMapping(sheetData, excelDataConfigById);
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
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, spreadSheetId, sheetName);
        sheetData = excelToCsvDataConvertService.applyMergeColumnMapping(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyRemoveColumnConfig(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyUniqueEntry(sheetData, excelDataConfigById, uniqueStrings);
        return sheetData;
    }
    private ArrayList<ArrayList<String>> readMysqlData(String mysqlTableConfigId, String sheetName,
                                                             ExcelDataConfig excelDataConfigById,
                                                             ArrayList<String> uniqueStrings) throws AppException{
        if (tableService == null) {
            logger.info("readMysqlData: tableService is not defined: {}, {}", mysqlTableConfigId, excelDataConfigById);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (excelDataConfigById == null) {
            logger.info("readMysqlData: excelDataConfigById is null for mysqlTableConfigId: {}", mysqlTableConfigId);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        MysqlCsvDataConfig mysqlCsvDataConfig = excelDataConfigById.getMysqlCsvDataConfig();
        ArrayList<String> filterValues = null;
        String defaultFilterMappingId = null;
        if (mysqlCsvDataConfig != null) {
            filterValues = mysqlCsvDataConfig.getFilterValues();
            defaultFilterMappingId = mysqlCsvDataConfig.getDefaultFilterMappingId();
        }
        ArrayList<ArrayList<String>> sheetData = tableService.getTableDataArray(request, mysqlTableConfigId, filterValues, defaultFilterMappingId);
        sheetData = excelToCsvDataConvertService.formatCellData(sheetData);
        excelToCsvDataConvertService.applyReplaceCellString(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowEntry(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.skipEmptyRows(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applySkipRowCriteria(sheetData, excelDataConfigById);
        excelToCsvDataConvertService.copyCellDataIndex(sheetData, excelDataConfigById);
        sheetData = excelToCsvDataConvertService.applyCellMapping(sheetData, excelDataConfigById, mysqlTableConfigId, sheetName);
        sheetData = excelToCsvDataConvertService.applyMergeColumnMapping(sheetData, excelDataConfigById);
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
            logger.info("getExcelDataConfigByIdV1: excelDataConfigById is null for request id: {}", requestId);
        } else {
            excelDataConfigById.setExcelConfig(null);
            excelDataConfigById.setCsvConfig(null);
            excelDataConfigById.setGsConfig(null);
            logger.info("getExcelDataConfigByIdV1: excelDataConfigById for requestId: {}, {}", requestId, excelDataConfigById);
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
        excelDataConfigById.setMysqlConfig(null);
        if (AppConstant.CSV.equals(fileConfigMapping.getFileDataSource())) {
            excelDataConfigById.setCsvConfig(excelFileConfigs);
        } else if (AppConstant.MYSQL.equals(fileConfigMapping.getFileDataSource())) {
            excelDataConfigById.setMysqlConfig(excelFileConfigs);
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
            logger.info("updateExcelDataConfigFromCsv: srcFilePathCsv is not found.");
            return null;
        }
        ArrayList<ArrayList<String>> csvData = this.readCsvData(srcFilepath);
        csvData = excelToCsvDataConvertService.removeFirstEmptyRow(csvData);
        excelDataConfigById = this.updateExcelDataConfigById(excelDataConfigById, requestId, fileConfigMapping, csvData);
        logger.info("excelDataConfigById generated from csv for requestId: {}, {}", requestId, excelDataConfigById);
        return excelDataConfigById;
    }
    public boolean readAndWriteExcelSheetData(ExcelDataConfig excelDataConfigById,
                                              SaveTableParameter saveTableParameter) throws AppException {
        if (excelDataConfigById == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<ExcelFileConfig> excelFileConfig = excelDataConfigById.getExcelConfig();
        ArrayList<ExcelFileConfig> csvFileConfig = excelDataConfigById.getCsvConfig();
        ArrayList<String> uniqueStrings;
        String srcFilepath, sheetName, destination;
        ArrayList<Boolean> finalResult = new ArrayList<>();
        boolean result, isNewFile;
        HashMap<String,Boolean> deletedDestination = new HashMap<>();
        Boolean isDestinationDeleted;
        if (excelFileConfig != null && !excelFileConfig.isEmpty()) {
            uniqueStrings = new ArrayList<>();
            for (ExcelFileConfig fileConfig : excelFileConfig) {
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                isDestinationDeleted = deletedDestination.get(destination);
                if (saveTableParameter == null) {
                    if (isDestinationDeleted == null || !isDestinationDeleted) {
                        fileService.deleteFileV2(destination);
                        fileService.createNewFile(destination);
                        isNewFile = true;
                        deletedDestination.put(destination,true);
                    } else {
                        isNewFile = false;
                    }
                    result = this.readExcelAndWriteCsvFilePath(srcFilepath, destination, sheetName,
                            excelDataConfigById, uniqueStrings, isNewFile);
                } else {
                    result = this.readExcelAndWriteToTableRow(srcFilepath, sheetName,
                            excelDataConfigById, uniqueStrings, saveTableParameter);
                }
                finalResult.add(result);
            }
        }
        if (csvFileConfig != null && !csvFileConfig.isEmpty()) {
            uniqueStrings = new ArrayList<>();
            for (ExcelFileConfig fileConfig : csvFileConfig) {
                srcFilepath = fileConfig.getSource();
                sheetName = fileConfig.getSheetName();
                destination = fileConfig.getDestination();
                isDestinationDeleted = deletedDestination.get(destination);
                if (saveTableParameter == null) {
                    if (isDestinationDeleted == null || !isDestinationDeleted) {
                        fileService.deleteFileV2(destination);
                        fileService.createNewFile(destination);
                        isNewFile = true;
                        deletedDestination.put(destination,true);
                    } else {
                        isNewFile = false;
                    }
                    result = this.readAndWriteCsvFilePath(srcFilepath, destination, sheetName,
                            excelDataConfigById, uniqueStrings, isNewFile);
                } else {
                    result = this.readCsvAndWriteToTableRow(srcFilepath, sheetName,
                            excelDataConfigById, uniqueStrings, saveTableParameter);
                }
                finalResult.add(result);
            }
        }
        if ((excelFileConfig == null || excelFileConfig.isEmpty()) &&
                (csvFileConfig == null || csvFileConfig.isEmpty())
        ) {
            logger.info("readAndWriteExcelSheetData: invalid excelFileConfig and csvFileConfig: {}", excelDataConfigById);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        for (boolean r : finalResult) {
            if (r) {
                return true;
            }
        }
        return false;
    }
    public ArrayList<BridgeResponseSheetData> readExcelSheetData(ExcelDataConfig excelDataConfigById) throws AppException {
        if (excelDataConfigById == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<ExcelFileConfig> excelFileConfig = excelDataConfigById.getExcelConfig();
        ArrayList<ExcelFileConfig> csvFileConfig = excelDataConfigById.getCsvConfig();
        ArrayList<ExcelFileConfig> gsFileConfig = excelDataConfigById.getGsConfig();
        ArrayList<ExcelFileConfig> mysqlConfig = excelDataConfigById.getMysqlConfig();
        ArrayList<String> uniqueStrings;
        String srcFilepath, sheetName, destination, copyDestination;
        boolean copyOldData;
        ArrayList<ArrayList<String>> sheetData;
        ArrayList<BridgeResponseSheetData> bridgeResponseSheetsData = new ArrayList<>();
        if (excelFileConfig != null && !excelFileConfig.isEmpty()) {
            uniqueStrings = new ArrayList<>();
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
            uniqueStrings = new ArrayList<>();
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
            uniqueStrings = new ArrayList<>();
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
        if (mysqlConfig != null && !mysqlConfig.isEmpty()) {
            uniqueStrings = new ArrayList<>();
            for (ExcelFileConfig fileConfig : mysqlConfig) {
                copyOldData = excelDataConfigById.isCopyOldData();
                srcFilepath = fileConfig.getSource(); // mysqlTableConfigId
                sheetName = fileConfig.getSheetName(); // may be used as external parameter if required in output
                destination = fileConfig.getDestination();
                copyDestination = fileConfig.getCopyDestination();
                sheetData = this.readMysqlData(srcFilepath, sheetName, excelDataConfigById, uniqueStrings);
                bridgeResponseSheetsData.add(new BridgeResponseSheetData(copyOldData,
                        destination, copyDestination, sheetData));
            }
        }
        if ((excelFileConfig == null || excelFileConfig.isEmpty()) &&
                (csvFileConfig == null || csvFileConfig.isEmpty()) &&
                (gsFileConfig == null || gsFileConfig.isEmpty()) &&
                (mysqlConfig == null || mysqlConfig.isEmpty())
        ) {
            logger.info("Invalid excelFileConfig, csvFileConfig, gsFileConfig and mysqlConfig: {}", excelDataConfigById);
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
