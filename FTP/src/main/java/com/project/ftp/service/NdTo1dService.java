package com.project.ftp.service;

import com.project.ftp.bridge.obj.yamlObj.SkipRowCriteria;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItemsV2;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.RolesMappingInterface;
import com.project.ftp.obj.yamlObj.NdTo1dConfig;
import com.project.ftp.obj.yamlObj.NdTo1dConfigParam;
import com.project.ftp.obj.yamlObj.NdTo1dSkipRowCriteria;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class NdTo1dService {
    private final static Logger logger = LoggerFactory.getLogger(NdTo1dService.class);
    private final AppConfig appConfig;
    private final MSExcelService msExcelService;
    private final UserService userService;
    public NdTo1dService(final AppConfig appConfig) {
        this.appConfig = appConfig;
        if (appConfig != null) {
            this.msExcelService = appConfig.getMsExcelService();
            this.userService = appConfig.getUserService();
        } else {
            this.msExcelService = null;
            this.userService = null;
        }
    }
    private NdTo1dConfig getNdTo1dConfigV2(String requestId, ArrayList<String> ndTo1dConfigPath) throws AppException {
        if (requestId == null || requestId.isEmpty()) {
            logger.info("Invalid requestId for reading configData: {}", requestId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (ndTo1dConfigPath == null || ndTo1dConfigPath.isEmpty()) {
            logger.info("Invalid ndTo1dConfigPath for reading configData: {}", ndTo1dConfigPath);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        YamlFileParser yamlFileParser = new YamlFileParser();
        NdTo1dConfig ndTo1dConfig = null;
        NdTo1dConfigParam ndTo1dConfigParam;
        HashMap<String, NdTo1dConfig> ndTo1dConfigHashMap;
        for(String configPath: ndTo1dConfigPath) {
            ndTo1dConfigParam = yamlFileParser.getNdTo1dConfigFromPath(configPath);
            if (ndTo1dConfigParam == null) {
                continue;
            }
            ndTo1dConfigHashMap = ndTo1dConfigParam.getNdTo1dConfig();
            if (ndTo1dConfigHashMap == null) {
                continue;
            }
            ndTo1dConfig = ndTo1dConfigHashMap.get(requestId);
            if (ndTo1dConfig != null) {
                break;
            }
        }
        if (ndTo1dConfig == null) {
            logger.info("ndTo1dConfig not found for requestId: {}, ndTo1dConfigPath: {}", requestId, ndTo1dConfigPath);
        }
        return ndTo1dConfig;
    }
    public NdTo1dConfig getNdTo1dConfigV1(HttpServletRequest request, String requestId, String roleId) throws AppException {
        ArrayList<String> ndTo1dConfigPath = appConfig.getDirectoryService().getDirConfigParamFromRequestV2(request, FtpConfigItemsV2.ndTo1dConfigFilePath, roleId);
        return this.getNdTo1dConfigV2(requestId, ndTo1dConfigPath);
    }
    private String getCellData(ArrayList<String> rowData, Integer index) {
        if (rowData == null || rowData.isEmpty()) {
            return null;
        }
        if (index == null || index < 0 || index >= rowData.size()) {
            return null;
        }
        return rowData.get(index);
    }
    private boolean checkIndividualSkipRow(SkipRowCriteria skipRowCriteria,
                                           ArrayList<String> rowAsPerDataColIndex) {
        if (skipRowCriteria == null || rowAsPerDataColIndex == null || rowAsPerDataColIndex.isEmpty()) {
            return false;
        }
        Integer colIndex = skipRowCriteria.getCol_index();
        if (colIndex == null || colIndex < 0 || colIndex >= rowAsPerDataColIndex.size()) {
            return false;
        }
        String cellData = rowAsPerDataColIndex.get(colIndex);
        Boolean status = RolesMappingInterface.isValidCondition(cellData, skipRowCriteria);
        if (status == null) {
            return false;
        }
        return status;
    }
    private boolean isSkipRowCriteriaTrue(ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria,
                                          ArrayList<String> rowAsPerDataColIndex, int dataColRowId) {
        if (skipRowCriteria == null || skipRowCriteria.isEmpty()) {
            return false;
        }
        if (rowAsPerDataColIndex == null) {
            return false;
        }
        if (dataColRowId < 0) {
            return false;
        }
        ArrayList<Integer> dataColIndex;
        String operation;
        ArrayList<SkipRowCriteria> skipRowCriteria2;
        boolean status;
        ArrayList<Boolean> allStatus;
        for(NdTo1dSkipRowCriteria skipRowCriteria1: skipRowCriteria) {
            if (skipRowCriteria1 == null) {
                continue;
            }
            dataColIndex = skipRowCriteria1.getDataColIndex();
            if (dataColIndex == null) {
                continue;
            }
            if (!dataColIndex.contains(dataColRowId)) {
                continue;
            }
            operation = skipRowCriteria1.getOperation();
            skipRowCriteria2 = skipRowCriteria1.getCriteria();
            if (skipRowCriteria2 == null) {
                continue;
            }
            if (AppConstant.AND.equals(operation)) {
                allStatus = new ArrayList<>();
                for(SkipRowCriteria criteria: skipRowCriteria2) {
                    status = this.checkIndividualSkipRow(criteria, rowAsPerDataColIndex);
                    allStatus.add(status);
                }
                if (!allStatus.isEmpty()) {
                    for (Boolean b: allStatus) {
                        if (!b) {
                            return false;
                        }
                    }
                    return true;
                }
            } else {
                for(SkipRowCriteria criteria: skipRowCriteria2) {
                    status = this.checkIndividualSkipRow(criteria, rowAsPerDataColIndex);
                    if (status) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private ArrayList<String> getEachRowData(ArrayList<String> rowData, ArrayList<Integer> textColIndex,
                                             int dataColRowId, ArrayList<Integer> dataColIndex, ArrayList<String> heading,
                                             Integer dimension, ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria) {
        if (rowData == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        String cellData;
        if (textColIndex != null && !textColIndex.isEmpty()) {
            for(Integer index: textColIndex) {
                if (index == null) {
                    continue;
                }
                cellData = this.getCellData(rowData, index);
                if (cellData == null) {
                    cellData = "";
                }
                result.add(cellData);
            }
        }
        if (dataColIndex == null || dataColIndex.isEmpty()) {
            return result;
        }
        if (dimension == null || dimension < 1) {
            return result;
        }
        Integer index2;
        boolean headingAdded = false;
        ArrayList<String> rowAsPerDataColIndex = new ArrayList<>();
        for (int i=0; i<dimension-1; i++) {
            cellData = null;
            if (i < dataColIndex.size()) {
                index2 = dataColIndex.get(i);
                if (!headingAdded) {
                    cellData = this.getCellData(heading, index2);
                    if (cellData == null) {
                        cellData = "";
                    }
                    result.add(cellData);
                    headingAdded = true;
                }
                cellData = this.getCellData(rowData, index2);
            }
            if (cellData == null) {
                cellData = "";
            }
            rowAsPerDataColIndex.add(cellData);
        }
        if (this.isSkipRowCriteriaTrue(skipRowCriteria, rowAsPerDataColIndex, dataColRowId)) {
            return null;
        }
        result.addAll(rowAsPerDataColIndex);
        return result;
    }
    private ArrayList<ArrayList<String>> convertNdTo1dRow(ArrayList<String> rowData, ArrayList<String> heading,
                                               NdTo1dConfig ndTo1dConfig) {
        if (ndTo1dConfig == null) {
            return null;
        }
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> eachRowData;
        ArrayList<Integer> textColIndex = ndTo1dConfig.getTextColIndex();
        ArrayList<ArrayList<Integer>> dataColIndex = ndTo1dConfig.getDataColIndex();
        Integer dimension = ndTo1dConfig.getDataDimension();
        ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria = ndTo1dConfig.getSkipRowCriteria();
        int dataColRowId = 0;
        if (dataColIndex != null && !dataColIndex.isEmpty()) {
            for(ArrayList<Integer> dataCol: dataColIndex) {
                eachRowData = this.getEachRowData(rowData, textColIndex, dataColRowId, dataCol, heading, dimension, skipRowCriteria);
                dataColRowId++;
                if (eachRowData == null || eachRowData.isEmpty()) {
                    continue;
                }
                result.add(eachRowData);
            }
        } else {
            eachRowData = this.getEachRowData(rowData, textColIndex, dataColRowId, null, heading, dimension, skipRowCriteria);
            if (eachRowData == null || eachRowData.isEmpty()) {
                return result;
            }
            result.add(eachRowData);
        }
        return result;
    }
    private ArrayList<ArrayList<String>> convertNdTo1dData(ArrayList<ArrayList<String>> excelData,
                                                           NdTo1dConfig ndTo1dConfig) {
        if (ndTo1dConfig == null || excelData == null || excelData.size() <= 1) {
            return null;
        }
        Integer startIndex = ndTo1dConfig.getDataStartIndex();
        if (startIndex == null || startIndex < 0) {
            return null;
        }
        ArrayList<String> rowData;
        ArrayList<ArrayList<String>> newRowData;
        int size = excelData.size();
        ArrayList<String> heading = excelData.get(0);
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (int i = startIndex; i<size; i++) {
            rowData = excelData.get((i));
            if (rowData == null) {
                continue;
            }
            newRowData = this.convertNdTo1dRow(rowData, heading, ndTo1dConfig);
            if (newRowData == null || newRowData.isEmpty()) {
                continue;
            }
            result.addAll(newRowData);
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    private ArrayList<ArrayList<String>> addHeadingField(ArrayList<ArrayList<String>> ndTo1dData,
                                                           NdTo1dConfig ndTo1dConfig) {
        if (ndTo1dConfig == null) {
            return ndTo1dData;
        }
        return StaticService.applyHeadingField(ndTo1dData, ndTo1dConfig.getHeadingField());
    }
    public ArrayList<ArrayList<String>> getNdTo1dData(HttpServletRequest request, String requestId, String roleId) throws AppException {
        NdTo1dConfig ndTo1dConfig = this.getNdTo1dConfigV1(request, requestId, roleId);
        Object userDetail = null;
        if (userService != null) {
            userDetail = userService.getUserDataForLogging(request);
        }
        logger.info("getNdTo1dData in: user: {}, requestId: {}, roleId: {}", userDetail, requestId, roleId);
        if (msExcelService == null) {
            logger.info("msExcelService is null.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (ndTo1dConfig == null) {
            logger.info("ndTo1dConfig is null for requestId: {}", requestId);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        ArrayList<String> sourceExcelId = ndTo1dConfig.getSourceExcelId();
        if (sourceExcelId == null || sourceExcelId.isEmpty()) {
            logger.info("Invalid sourceExcelId: {}, requestId: {}, ndTo1dConfig: {}",
                    sourceExcelId, requestId, ndTo1dConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        Integer dataDimension = ndTo1dConfig.getDataDimension();
        if (dataDimension == null || dataDimension < 1) {
            logger.info("Invalid dataDimension: {}, ndTo1dConfig: {}", dataDimension, ndTo1dConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        Integer dataStartIndex = ndTo1dConfig.getDataStartIndex();
        if (dataStartIndex == null) {
            logger.info("Invalid dataStartIndex: null, ndTo1dConfig: {}", ndTo1dConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        ArrayList<ArrayList<String>> excelData, ndTo1dData;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for(String excelId: sourceExcelId) {
            excelData = msExcelService.getMSExcelSheetDataArrayV2(request, excelId, roleId);
            ndTo1dData = this.convertNdTo1dData(excelData, ndTo1dConfig);
            if (ndTo1dData == null || ndTo1dData.isEmpty()) {
                continue;
            }
            result.addAll(ndTo1dData);
        }
        if (result.isEmpty()) {
            return null;
        }
        result = this.addHeadingField(result, ndTo1dConfig);
        return result;
    }
}
