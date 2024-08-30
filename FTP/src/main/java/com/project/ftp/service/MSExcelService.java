package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.FileMappingConfig;
import com.project.ftp.bridge.service.MSExcelBridgeService;
import com.project.ftp.common.StrUtils;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class MSExcelService {
    private final static Logger logger = LoggerFactory.getLogger(MSExcelService.class);
    private final AppConfig appConfig;
    private final FtpConfiguration ftpConfiguration;
    private final FileService fileService;
    private final FileServiceV3 fileServiceV3;
    private final EventTracking eventTracking;
    private final StrUtils strUtils;
    private final MiscService miscService;
    public MSExcelService(final AppConfig appConfig, final EventTracking eventTracking, final UserService userService) {
        this.appConfig = appConfig;
        this.eventTracking = eventTracking;
        this.ftpConfiguration = appConfig.getFtpConfiguration();
        this.fileService = new FileService();
        this.fileServiceV3 = new FileServiceV3(appConfig, userService);
        this.strUtils = new StrUtils();
        this.miscService = new MiscService();
    }
    private void saveCsvData(BridgeResponseSheetData bridgeResponseSheetData, ArrayList<String> tempSavedFilePath) {
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
        for(ArrayList<String> rowData: sheetData) {
            if (rowData != null) {
                csvData.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
            }
        }
        if (copyOldData) {
            fileService.copyFile(destination, destination, true);
        }
        if (!tempSavedFilePath.contains(destination)) {
            fileService.deleteFileV2(destination);
        }
        tempSavedFilePath.add(destination);
        boolean status = fileServiceV3.saveAddTextV3(destination, csvData, false);
        if (status) {
            logger.info("csv data saved: {}", destination);
            if (!destination.equals(copyDestination) && StaticService.isValidString(copyDestination)) {
                fileService.copyFile(destination, copyDestination, false);
            }
        } else {
            logger.info("Error in saving csv data: {}", destination);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
    }
    private ArrayList<BridgeResponseSheetData> getActualMSExcelSheetData(HttpServletRequest request,
                                                                         ArrayList<ExcelDataConfig> excelDataConfigs,
                                                                         boolean addConfigDataById) throws AppException {
        ArrayList<BridgeResponseSheetData> response = null;
        ArrayList<BridgeResponseSheetData> result;
        if (excelDataConfigs != null) {
            for(ExcelDataConfig excelDataConfigById: excelDataConfigs) {
                if (excelDataConfigById != null) {
                    result = appConfig.getAppToBridge().getExcelData(request, excelDataConfigById);
                    if (result == null) {
                        logger.info("Error in reading excelSheetData for id: {}", excelDataConfigById.getId());
                    } else {
                        if (response == null) {
                            response = new ArrayList<>();
                        }
                        for(BridgeResponseSheetData bridgeResponseSheetData: result) {
                            if (addConfigDataById) {
                                bridgeResponseSheetData.setExcelDataConfigById(excelDataConfigById);
                            }
                            response.add(bridgeResponseSheetData);
                        }
                    }
                }
            }
        }
        if (response == null) {
            throw new AppException(ErrorCodes.SERVER_ERROR);
        }
        return response;
    }
    public ArrayList<ExcelDataConfig> getActualMSExcelSheetDataConfig(HttpServletRequest request, String requestId,
                                                                      boolean updateGsConfig) throws AppException {
        if (requestId == null || requestId.isEmpty()) {
            logger.info("requestId required: {}", requestId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        YamlFileParser yamlFileParser = new YamlFileParser();
        FileMappingConfig fileMappingConfig =
                yamlFileParser.getFileMappingConfigFromPath(ftpConfiguration.getFileMappingConfigFilePath());
        if (fileMappingConfig == null) {
            logger.info("fileMappingConfig is null.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        HashMap<String, ExcelDataConfig> excelDataConfigHashMap = yamlFileParser.getExcelDataConfig(
                fileMappingConfig.getExcelFileConfigPath());
        HashMap<String, ArrayList<String>> combineRequestIds = fileMappingConfig.getCombineRequestIds();
        ArrayList<String> combinedIds;
        if (excelDataConfigHashMap == null) {
            logger.info("excelDataConfigHashMap is null.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        ArrayList<ExcelDataConfig> response = null;
        ExcelDataConfig result;
        MSExcelBridgeService msExcelBridgeService = new MSExcelBridgeService(request, eventTracking,
                ftpConfiguration.getGoogleOAuthClientConfig(), null);
        if (combineRequestIds != null && combineRequestIds.containsKey(requestId)) {
            combinedIds = combineRequestIds.get(requestId);
            if (combinedIds != null) {
                for (String str: combinedIds) {
                    result = appConfig.getAppToBridge().getExcelDataConfig(request, str, fileMappingConfig, excelDataConfigHashMap);
                    if (result == null) {
                        logger.info("Error in reading requestId: {}, partialId: {}", requestId, str);
                    } else {
                        if (updateGsConfig) {
                            result = msExcelBridgeService.updateExcelDataConfigFromGoogle(result);
                        }
                        if (response == null) {
                            response = new ArrayList<>();
                        }
                        response.add(result);
                    }
                }
            }
        }
        if (response == null) {
            result = appConfig.getAppToBridge().getExcelDataConfig(request, requestId, fileMappingConfig, excelDataConfigHashMap);
            if (updateGsConfig) {
                result = msExcelBridgeService.updateExcelDataConfigFromGoogle(result);
            }
            response = new ArrayList<>();
            response.add(result);
        }
        return response;
    }
    public ArrayList<ArrayList<String>> applyCsvConfigOnData(HttpServletRequest request, ArrayList<ArrayList<String>> sheetData, String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, false);
        MSExcelBridgeService msExcelBridgeService = new MSExcelBridgeService(request, eventTracking,
                ftpConfiguration.getGoogleOAuthClientConfig(), null);
        ArrayList<ArrayList<String>> response = null;
        ArrayList<ArrayList<String>> result;
        BridgeResponseSheetData bridgeResponseSheetData;
        if (excelDataConfigs != null) {
            for(ExcelDataConfig excelDataConfigById: excelDataConfigs) {
                if (excelDataConfigById != null) {
                    result = msExcelBridgeService.applyCsvConfigOnData(sheetData, null, null, excelDataConfigById, null);;
                    if (result == null) {
                        logger.info("Error in applyCsvConfigOnData for id: {}", excelDataConfigById.getId());
                    } else {
                        if (response == null) {
                            response = new ArrayList<>();
                        }
                        response.addAll(result);
                    }
                }
            }
        }
        return response;
    }
    public ArrayList<HashMap<String, String>> applyCsvConfigOnDataOutputJson(HttpServletRequest request,
                                                                       ArrayList<ArrayList<String>> sheetData,
                                                                       String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, false);
        MSExcelBridgeService msExcelBridgeService = new MSExcelBridgeService(request, eventTracking,
                ftpConfiguration.getGoogleOAuthClientConfig(), null);
        ArrayList<HashMap<String, String>> response = null;
        ArrayList<ArrayList<String>> result;
        if (excelDataConfigs != null) {
            for(ExcelDataConfig excelDataConfigById: excelDataConfigs) {
                if (excelDataConfigById != null) {
                    result = msExcelBridgeService.applyCsvConfigOnData(sheetData, null, null, excelDataConfigById, null);;
                    if (result == null) {
                        logger.info("Error in applyCsvConfigOnDataOutputJson for id: {}", excelDataConfigById.getId());
                    } else {
                        if (response == null) {
                            response = new ArrayList<>();
                        }
                        response.addAll(miscService.convertArraySheetDataToJsonData(result, excelDataConfigById.getTableMappingIndex()));
                    }
                }
            }
        }
        return response;
    }
    public ArrayList<BridgeResponseSheetData> getMSExcelSheetData(HttpServletRequest request, String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, true);
        return this.getActualMSExcelSheetData(request, excelDataConfigs, false);
    }
    public ArrayList<HashMap<String, String>> getMSExcelSheetDataJson(HttpServletRequest request, String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, true);
        ArrayList<BridgeResponseSheetData> bridgeResponseSheetData = this.getActualMSExcelSheetData(request, excelDataConfigs, true);
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        ArrayList<String> tableIndex;
        for (BridgeResponseSheetData bridgeResponseSheetData1: bridgeResponseSheetData) {
            if (bridgeResponseSheetData1 == null || bridgeResponseSheetData1.getExcelDataConfigById() == null) {
                continue;
            }
            tableIndex = bridgeResponseSheetData1.getExcelDataConfigById().getTableMappingIndex();
            result.addAll(miscService.convertArraySheetDataToJsonData(bridgeResponseSheetData1.getSheetData(), tableIndex));
        }
        return result;
    }
    public String getMSExcelSheetDataCsv(HttpServletRequest request, String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, true);
        ArrayList<BridgeResponseSheetData> response = this.getActualMSExcelSheetData(request, excelDataConfigs, false);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        for (BridgeResponseSheetData bridgeResponseSheetData: response) {
            if (bridgeResponseSheetData != null && bridgeResponseSheetData.getSheetData() != null) {
                sheetData.addAll(bridgeResponseSheetData.getSheetData());
            }
        }
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
    public ArrayList<ArrayList<String>> getMSExcelSheetDataArray(HttpServletRequest request, String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, true);
        ArrayList<BridgeResponseSheetData> response = this.getActualMSExcelSheetData(request, excelDataConfigs, false);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        for (BridgeResponseSheetData bridgeResponseSheetData: response) {
            if (bridgeResponseSheetData != null && bridgeResponseSheetData.getSheetData() != null) {
                sheetData.addAll(bridgeResponseSheetData.getSheetData());
            }
        }
        return sheetData;
    }
    public ApiResponse updateMSExcelSheetData(HttpServletRequest request, String requestId) throws AppException {
        ArrayList<ExcelDataConfig> excelDataConfigs = this.getActualMSExcelSheetDataConfig(request, requestId, true);
        ArrayList<BridgeResponseSheetData> response = this.getActualMSExcelSheetData(request, excelDataConfigs, false);
        ArrayList<String> tempSavedFilePath = new ArrayList<>();
        for (BridgeResponseSheetData bridgeResponseSheetData: response) {
            this.saveCsvData(bridgeResponseSheetData, tempSavedFilePath);
        }
        return new ApiResponse(AppConstant.SUCCESS);
    }
    public ApiResponse getMSExcelSheetDataConfig(HttpServletRequest request, String requestId, String updateGsConfig) throws AppException {
        return new ApiResponse(this.getActualMSExcelSheetDataConfig(request, requestId, AppConstant.TRUE.equals(updateGsConfig)));
    }
}
