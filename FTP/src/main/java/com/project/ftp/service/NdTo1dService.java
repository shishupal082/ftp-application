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
    private ArrayList<String> getHeadingData(ArrayList<ArrayList<String>> excelData,
                                             ArrayList<ArrayList<Integer>> dataColIndex) {
        if (dataColIndex == null || dataColIndex.size() < 2) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        String cellData;
        Integer i, j;
        int index = 0;
        for(ArrayList<Integer> headingCellIndex: dataColIndex) {
            if (index == 0) {
                index++;
                continue;
            }
            if (headingCellIndex == null || headingCellIndex.size() < 2) {
                continue;
            }
            i = headingCellIndex.get(0);
            j = headingCellIndex.get(1);
            if (i == null || j == null) {
                cellData = null;
            } else {
                cellData = StaticService.getCellDataFromSheet(excelData,i,j);
            }
            if (cellData == null) {
                cellData = AppConstant.EmptyStr;
            }
            result.add(cellData);
        }
        return result;
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
                                          ArrayList<String> rowAsPerDataColIndex, int dataCol_i) {
        if (skipRowCriteria == null || skipRowCriteria.isEmpty()) {
            return false;
        }
        if (rowAsPerDataColIndex == null) {
            return false;
        }
        if (dataCol_i < 0) {
            return false;
        }
        ArrayList<ArrayList<Integer>> dataColIndex;
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
            if (!StaticService.isLiesInRange(dataColIndex, dataCol_i)) {
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
    private ArrayList<String> getRowDataAsPerDataColIndex(ArrayList<ArrayList<String>> excelData, ArrayList<String> rowData,
                                                          int dataCol_i, ArrayList<ArrayList<Integer>> dataColIndex,
                                                          ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria) {
        if (rowData == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        String cellData;
        if (dataColIndex == null || dataColIndex.isEmpty()) {
            return result;
        }
        boolean headingAdded = false;
        ArrayList<String> rowAsPerDataColIndex = new ArrayList<>();
        ArrayList<String> headingData = null;
        ArrayList<Integer> dataColIndex2 = dataColIndex.get(0);
        if (dataColIndex2 == null) {
            return result;
        }
        for (Integer index2 : dataColIndex2) {
            if (!headingAdded) {
                headingData = this.getHeadingData(excelData, dataColIndex);
                headingAdded = true;
            }
            cellData = StaticService.getCellDataFromRow(rowData, index2);
            if (cellData == null) {
                cellData = "";
            }
            rowAsPerDataColIndex.add(cellData);
        }
        if (this.isSkipRowCriteriaTrue(skipRowCriteria, rowAsPerDataColIndex, dataCol_i)) {
            return null;
        }
        if (headingData != null && !headingData.isEmpty()) {
            result.addAll(headingData);
        }
        if (!rowAsPerDataColIndex.isEmpty()) {
            result.addAll(rowAsPerDataColIndex);
        }
        return result;
    }
    private ArrayList<ArrayList<String>> convert1RowToMultiRow(ArrayList<ArrayList<String>> excelData,
                                                               ArrayList<String> rowData, NdTo1dConfig ndTo1dConfig) {
        if (ndTo1dConfig == null) {
            return null;
        }
        ArrayList<String> rowDataAsPerTextColIndex = new ArrayList<>();
        ArrayList<Integer> textColIndex = ndTo1dConfig.getTextColIndex();
        String cellData;
        if (textColIndex != null && !textColIndex.isEmpty()) {
            for(Integer index: textColIndex) {
                if (index == null) {
                    continue;
                }
                cellData = StaticService.getCellDataFromRow(rowData, index);
                if (cellData == null) {
                    cellData = "";
                }
                rowDataAsPerTextColIndex.add(cellData);
            }
        }
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<ArrayList<String>> rowAsPerDataColIndex = new ArrayList<>();
        ArrayList<String> eachRowData, fullRowData;
        ArrayList<ArrayList<ArrayList<Integer>>> dataColIndex = ndTo1dConfig.getDataColIndex();
        ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria = ndTo1dConfig.getSkipRowCriteria();
        int dataCol_i = 0;
        if (dataColIndex != null && !dataColIndex.isEmpty()) {
            for(ArrayList<ArrayList<Integer>> dataCol: dataColIndex) {
                eachRowData = this.getRowDataAsPerDataColIndex(excelData, rowData, dataCol_i, dataCol, skipRowCriteria);
                dataCol_i++;
                if (eachRowData == null || eachRowData.isEmpty()) {
                    continue;
                }
                rowAsPerDataColIndex.add(eachRowData);
            }
            for (ArrayList<String> r: rowAsPerDataColIndex) {
                fullRowData = new ArrayList<>(rowDataAsPerTextColIndex);
                fullRowData.addAll(r);
                if (!fullRowData.isEmpty()) {
                    result.add(fullRowData);
                }
            }
        } else {
            if (!rowDataAsPerTextColIndex.isEmpty()) {
                result.add(rowDataAsPerTextColIndex);
            }
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
            startIndex = 1;//Assuming 1st row as heading
        }
        ArrayList<String> rowData;
        ArrayList<ArrayList<String>> newRowData;
        int size = excelData.size();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (int i = startIndex; i<size; i++) {
            rowData = excelData.get((i));
            if (rowData == null) {
                continue;
            }
            newRowData = this.convert1RowToMultiRow(excelData, rowData, ndTo1dConfig);
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
