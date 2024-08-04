package com.project.ftp.service;

import com.project.ftp.common.StrUtils;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.PathType;
import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.FilepathInterface;
import com.project.ftp.obj.*;
import com.project.ftp.obj.yamlObj.ScanDirConfig;
import com.project.ftp.obj.yamlObj.ScanDirMapping;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanDirService {
    final static Logger logger = LoggerFactory.getLogger(ScanDirService.class);
    private final AppConfig appConfig;
    private final FileService fileService;
    private final FilepathInterface filepathInterface;
    private final UserService userService;
    private final MSExcelService msExcelService;
    private final StrUtils strUtils;
    private final YamlFileParser yamlFileParser;
    public ScanDirService (final AppConfig appConfig, final FilepathInterface filepathInterface) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        this.filepathInterface = filepathInterface;
        this.userService = appConfig.getUserService();
        this.msExcelService = appConfig.getMsExcelService();
        this.yamlFileParser = new YamlFileParser();
        this.strUtils = new StrUtils();
    }
    private void updateFolderSize(ScanResult scanResult) {
        if (scanResult == null) {
            return;
        }
        if (scanResult.getPathType() == PathType.FOLDER) {
            double folderSize = 0;
            ArrayList<ScanResult> childScanResult = scanResult.getScanResults();
            if (childScanResult != null) {
                for(ScanResult result: childScanResult) {
                    if (result == null) {
                        continue;
                    }
                    if (result.getPathType() == PathType.FOLDER) {
                        this.updateFolderSize(result);
                    }
                    folderSize += result.getPathSize();
                }
            }
            scanResult.setPathSize(folderSize);
        }
    }
    private void updatePathInfoDetails(ArrayList<FilepathDBParameters> pathInfoScanResults,
                                       ScanResult scanResult, String scanDirMappingId, final RequestScanDir requestScanDir) {

        if (scanResult == null) {
            return;
        }
        FilepathDBParameters filepathDBParameters;
        PathInfo pathInfo;
        if (scanResult.getPathType() == PathType.FILE) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            filepathDBParameters = new FilepathDBParameters(pathInfo);
            filepathDBParameters.setScanDirMappingId(scanDirMappingId);
            // For each entry
            filepathDBParameters.setReqScanDirId(requestScanDir.getReqScanDirId());
            filepathDBParameters.setReqPathName(requestScanDir.getReqPathName());
            filepathDBParameters.setReqFileType(requestScanDir.getReqFileType());
            filepathDBParameters.setReqRecursive(requestScanDir.getReqRecursive());
            filepathDBParameters.setReqCsvMappingId(requestScanDir.getReqCsvMappingId());
            // For each entry end
            pathInfoScanResults.add(filepathDBParameters);
        } else if (scanResult.getPathType() == PathType.FOLDER) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            pathInfo.setSizeInKb(scanResult.getPathSize());
            filepathDBParameters = new FilepathDBParameters(pathInfo);
            filepathDBParameters.setScanDirMappingId(scanDirMappingId);
            // For each entry
            filepathDBParameters.setReqScanDirId(requestScanDir.getReqScanDirId());
            filepathDBParameters.setReqPathName(requestScanDir.getReqPathName());
            filepathDBParameters.setReqFileType(requestScanDir.getReqFileType());
            filepathDBParameters.setReqRecursive(requestScanDir.getReqRecursive());
            filepathDBParameters.setReqCsvMappingId(requestScanDir.getReqCsvMappingId());
            // For each entry end
            pathInfoScanResults.add(filepathDBParameters);
            ArrayList<ScanResult> childScanResult = scanResult.getScanResults();
            if (childScanResult != null) {
                for(ScanResult result: childScanResult) {
                    this.updatePathInfoDetails(pathInfoScanResults, result, scanDirMappingId, requestScanDir);
                }
            }
        }
    }
    private ArrayList<FilepathDBParameters> getPathInfoScanResult(String scanMappingDirId, String pathName, final RequestScanDir requestScanDir) throws AppException {
        ArrayList<FilepathDBParameters> pathInfoScanResults = new ArrayList<>();
        if (StaticService.isInValidString(pathName)) {
            logger.info("getPathInfoScanResult-1: scanResult: null for pathName: {}", pathName);
            return null;
        }
        ScanResult scanResult = fileService.scanDirectory(pathName, pathName, requestScanDir.getFinalRecursive(), false);
        if (scanResult == null || scanResult.getPathType() == null) {
            logger.info("getPathInfoScanResult-2: scanResult: {} for pathName: {}", scanResult, pathName);
            return null;
        }
        this.updateFolderSize(scanResult);


        this.updatePathInfoDetails(pathInfoScanResults, scanResult, scanMappingDirId, requestScanDir);
        ArrayList<FilepathDBParameters> pathInfoScanFinalResults = new ArrayList<>();
        ArrayList<String> fileTypeList = requestScanDir.getFinalFiletypeList();
        if (fileTypeList != null && !fileTypeList.isEmpty()) {
            for(FilepathDBParameters dbParameters: pathInfoScanResults) {
                if (fileTypeList.contains(dbParameters.getExtension())) {
                    pathInfoScanFinalResults.add(dbParameters);
                }
            }
            return pathInfoScanFinalResults;
        }
        return pathInfoScanResults;
    }
    private ArrayList<FilepathDBParameters> getPathInfoScanResultV2(final RequestScanDir requestScanDir,
                                                                    LoginUserDetails loginUserDetails) throws AppException {
        ArrayList<ScanDirMapping> scanDirMapping = this.getScanDirMapping(requestScanDir, true);
        ArrayList<FilepathDBParameters> pathInfoScanResults = null;
        ArrayList<FilepathDBParameters> tempPathInfoScanResults, tempPathInfoScanResults2;
        ArrayList<String> pathIndex;
        for(ScanDirMapping dirMapping: scanDirMapping) {
            if(dirMapping == null || dirMapping.getPathIndex() == null) {
                continue;
            }
            pathIndex = dirMapping.getPathIndex();
            for(String path: pathIndex) {
                path = StaticService.replaceBackSlashToSlash(path);
                tempPathInfoScanResults = this.getPathInfoScanResult(dirMapping.getId(), path, requestScanDir);
                if (tempPathInfoScanResults != null) {
                    if (pathInfoScanResults == null) {
                        pathInfoScanResults = new ArrayList<>();
                    }
                    if (requestScanDir.getReqPathName() != null) {
                        tempPathInfoScanResults2 = new ArrayList<>();
                        for(FilepathDBParameters dbParameters: tempPathInfoScanResults) {
                            if (dbParameters == null || dbParameters.getFileName() == null) {
                                continue;
                            }
                            if (dbParameters.getPathName().contains(requestScanDir.getReqPathName())) {
                                tempPathInfoScanResults2.add(dbParameters);
                            }
                        }
                        this.updateDBParameter(tempPathInfoScanResults2, loginUserDetails, dirMapping);
                        pathInfoScanResults.addAll(tempPathInfoScanResults2);
                    } else {
                        this.updateDBParameter(tempPathInfoScanResults, loginUserDetails, dirMapping);
                        pathInfoScanResults.addAll(tempPathInfoScanResults);
                    }
                }
            }
        }
        return pathInfoScanResults;
    }
    private void updateDBParameter(ArrayList<FilepathDBParameters> filepathDBParameters,
                                   LoginUserDetails loginUserDetails, ScanDirMapping scanDirMapping) {
        if (filepathDBParameters == null) {
            return;
        }
        String orgUsername = null;
        String loginUsername = null;
        if (loginUserDetails != null) {
            orgUsername = loginUserDetails.getOrgUsername();
            loginUsername = loginUserDetails.getUsername();
        }
        for(FilepathDBParameters filepathDBParameters1: filepathDBParameters) {
            if (filepathDBParameters1 == null) {
                continue;
            }
            filepathDBParameters1.setOrgUsername(orgUsername);
            filepathDBParameters1.setLoginUsername(loginUsername);
            if (scanDirMapping != null) {
                filepathDBParameters1.setDeviceName(scanDirMapping.getDevice_name());
                filepathDBParameters1.setScanDirMappingId(scanDirMapping.getId());
            }
        }
    }
    private boolean isFilepathParameterUpdated(FilepathDBParameters scanResult, FilepathDBParameters dbResult) {
        if (scanResult == null || dbResult == null) {
            return false;
        }
        if (scanResult.getDeviceName() != null) {
            return !scanResult.getDeviceName().equals(dbResult.getTableName());
        }
        return false;
    }
    private void updateFilepath(FilePathDAO filePathDAO, FilepathDBParameters filepathDBParameters) {
        if (filepathDBParameters == null) {
            return;
        }
        String currentTime;
        FilepathDBParameters filepathDBParameters1 = filePathDAO.findByData(filepathDBParameters);
        if (filepathDBParameters1 == null) {
            currentTime = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
            filepathDBParameters.setUpdated(true);
            filepathDBParameters.setTableUniqueId(StaticService.createUUIDNumber());
            filepathDBParameters.setEntryTime(currentTime);
            filepathDBParameters.setUiEntryTime(currentTime);
            filepathDBParameters.setScannedDate(currentTime);
            filePathDAO.add(filepathDBParameters);
        } else if (!filepathDBParameters.getSizeInKb().equals(filepathDBParameters1.getSizeInKb())) {
            currentTime = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
            filepathDBParameters1.setUpdated(true);
            filepathDBParameters1.setEditedAt(currentTime);
            filepathDBParameters1.setSizeInKb(filepathDBParameters.getSizeInKb());
            filepathDBParameters1.setSize(filepathDBParameters.getSize());
            filepathDBParameters1.setScannedDate(currentTime);
            filepathDBParameters1.setDeviceName(filepathDBParameters.getDeviceName());
            filePathDAO.updateById(filepathDBParameters1);
        } else if (this.isFilepathParameterUpdated(filepathDBParameters, filepathDBParameters1)) {
            currentTime = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
            filepathDBParameters1.setUpdated(true);
            filepathDBParameters1.setEditedAt(currentTime);
            filepathDBParameters1.setSizeInKb(filepathDBParameters.getSizeInKb());
            filepathDBParameters1.setSize(filepathDBParameters.getSize());
            filepathDBParameters1.setScannedDate(currentTime);
            filepathDBParameters1.setDeviceName(filepathDBParameters.getDeviceName());
            filePathDAO.updateById(filepathDBParameters1);
        } else {
            filePathDAO.updateById(filepathDBParameters1);
        }
    }
    private ArrayList<FilepathDBParameters> getDbDataFilterResult(final RequestScanDir requestScanDir) throws AppException {

        ArrayList<String> scanDirIdList = requestScanDir.getScanDirIdList();
        String reqPathName = requestScanDir.getReqPathName();
        ArrayList<String> fileTypeList = requestScanDir.getFinalFiletypeList();
        boolean recursive = requestScanDir.getFinalRecursive();
        ArrayList<FilepathDBParameters> dbData = filepathInterface.getByFilterParameter(reqPathName, scanDirIdList, fileTypeList, recursive);
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        for(FilepathDBParameters dbParameters: dbData) {
            if (dbParameters == null) {
                continue;
            }
            dbParameters.setReqScanDirId(requestScanDir.getReqScanDirId());
            dbParameters.setReqPathName(reqPathName);
            dbParameters.setReqFileType(requestScanDir.getReqFileType());
            dbParameters.setReqRecursive(requestScanDir.getReqRecursive());
            dbParameters.setReqCsvMappingId(requestScanDir.getReqCsvMappingId());
        }
        return dbData;
    }
    public ApiResponse updateScanDirectory(HttpServletRequest request, String reqScanDirId, final String reqRecursive) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, null, null, reqRecursive, null);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<FilepathDBParameters> pathInfoScanResults = this.getPathInfoScanResultV2(requestScanDir, loginUserDetails);
        FilePathDAO filePathDAO = new FilePathDAO();
        filepathInterface.updateFromReqScanDir(filePathDAO, requestScanDir.getScanDirIdList(), requestScanDir.getFinalRecursive());
        if (pathInfoScanResults != null) {
            for(FilepathDBParameters row: pathInfoScanResults) {
                this.updateFilepath(filePathDAO, row);
            }
        }
        HashMap<String, Integer> updateResult = filepathInterface.updateIntoDb(filePathDAO);
        return new ApiResponse(updateResult);
    }
    private ArrayList<ArrayList<String>> applyCsvConfig(HttpServletRequest request,
                                                        ArrayList<ArrayList<String>> data, String csvMappingId) {
        if (StaticService.isInValidString(csvMappingId)) {
            return data;
        }
        try {
            return msExcelService.applyCsvConfigOnData(request, data, csvMappingId);
        } catch (Exception e) {
            logger.info("Error in applyCsvConfig: {}", e.toString());
        }
        return data;
    }
    private ArrayList<HashMap<String, String>> applyCsvConfigOutputJson(HttpServletRequest request,
                                                        ArrayList<ArrayList<String>> data, String csvMappingId) {
        if (StaticService.isInValidString(csvMappingId)) {
            return null;
        }
        try {
            return msExcelService.applyCsvConfigOnDataOutputJson(request, data, csvMappingId);
        } catch (Exception e) {
            logger.info("Error in applyCsvConfigOutputJson: {}", e.toString());
        }
        return null;
    }
    private ArrayList<ArrayList<String>> generateUiJsonResponse(HttpServletRequest request,
                                                                ArrayList<FilepathDBParameters> filepathDBParameters,
                                                                String csvMappingId) {
        ArrayList<ArrayList<String>> result = null;
        if (filepathDBParameters != null) {
            result = new ArrayList<>();
            for (FilepathDBParameters dbParameters: filepathDBParameters) {
                result.add(dbParameters.getArrayData());
            }
        }
        result = this.applyCsvConfig(request, result, csvMappingId);
        return result;
    }
    private ArrayList<HashMap<String, String>> generateUiJsonResponseJson(HttpServletRequest request,
                                                                ArrayList<FilepathDBParameters> filepathDBParameters,
                                                                String csvMappingId) {
        ArrayList<ArrayList<String>> result = null;
        if (filepathDBParameters != null) {
            result = new ArrayList<>();
            for (FilepathDBParameters dbParameters: filepathDBParameters) {
                result.add(dbParameters.getArrayData());
            }
        }
        if (result == null || result.isEmpty()) {
            return null;
        }
        ArrayList<HashMap<String, String>> mappingOutput = this.applyCsvConfigOutputJson(request, result, csvMappingId);
        if (mappingOutput == null) {
            mappingOutput = new ArrayList<>();
            for (FilepathDBParameters dbParameters: filepathDBParameters) {
                mappingOutput.add(dbParameters.getJsonData());
            }
        }
        return mappingOutput;
    }
    private ArrayList<ScanDirMapping> getAllScanDirMappings() {
        String scanDirConfigFilePath = appConfig.getFtpConfiguration().getScanDirConfigFilePath();
        ScanDirConfig scanDirConfig = yamlFileParser.getScanDirConfigFromPath(scanDirConfigFilePath);
        if (scanDirConfig == null) {
            return null;
        }
        return scanDirConfig.getScanDirConfig();
    }
    private ArrayList<ScanDirMapping> getScanDirMapping(final RequestScanDir requestScanDir,
                                                        boolean checkScanDirId) throws AppException {
        ArrayList<String> scanDirId = requestScanDir.getScanDirIdList();
        String pathName = requestScanDir.getReqPathName();
        String reqScanDirId = requestScanDir.getReqScanDirId();
        if (checkScanDirId) {
            if (scanDirId == null) {
                logger.info("Invalid request parameter scanDirId is null");
                throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
            }
        }
        ArrayList<ScanDirMapping> scanDirMappings = this.getAllScanDirMappings();
        ArrayList<ScanDirMapping> scanDirMappingById = new ArrayList<>();
        if (scanDirMappings == null) {
            logger.info("scanDirMappings is null in the config.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (!checkScanDirId && scanDirId == null || scanDirId.isEmpty()) {
            return scanDirMappings;
        }
        boolean isAdded = false;
        for(ScanDirMapping scanDirMapping: scanDirMappings) {
            if (scanDirMapping == null) {
                continue;
            }
            if (!isAdded && reqScanDirId != null && reqScanDirId.equals(scanDirMapping.getId())) {
                requestScanDir.setScanDirMapping(scanDirMapping);
                isAdded = true;
            }
            if (scanDirId.contains(scanDirMapping.getId())) {
                scanDirMappingById.add(scanDirMapping);
            }
        }
        if (scanDirMappingById.isEmpty()) {
            logger.info("scanDirId: {}, is not available in scanDirMappings: {}", scanDirId, scanDirMappings);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (pathName == null) {
            return scanDirMappingById;
        }
        ArrayList<String> pathIndex;
        ArrayList<String> finalPathIndex;
        for(ScanDirMapping dirMapping: scanDirMappingById) {
            if (dirMapping == null || dirMapping.getPathIndex() == null) {
                continue;
            }
            pathIndex = dirMapping.getPathIndex();
            finalPathIndex = new ArrayList<>();
            for(String configPath: pathIndex) {
                if (configPath == null) {
                    continue;
                }
                if (pathName.contains(configPath)) {
                    finalPathIndex.add(configPath);
                }
            }
            dirMapping.setPathIndex(finalPathIndex);
        }
        return scanDirMappingById;
    }
    public ApiResponse getScanDirectoryConfig(HttpServletRequest request,
                                              String reqScanDirId, String reqPathName) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, null, null, null);
        ArrayList<ScanDirMapping> scanDirMapping = this.getScanDirMapping(requestScanDir, false);
        return new ApiResponse(scanDirMapping);
    }
    public ArrayList<ArrayList<String>> readScanDirectory(HttpServletRequest request, String reqScanDirId,
                                         String reqPathName, String reqFileType, final String reqRecursive,
                                         final String reqCsvMappingId) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, reqFileType, reqRecursive, reqCsvMappingId);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getPathInfoScanResultV2(requestScanDir, loginUserDetails);
        return this.generateUiJsonResponse(request, filepathDBParameters, requestScanDir.getFinalCsvMappingId());
    }
    public ArrayList<HashMap<String, String>> readScanDirectoryJson(HttpServletRequest request, String reqScanDirId,
                                                          String reqPathName, String reqFileType, final String reqRecursive,
                                                          final String reqCsvMappingId) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, reqFileType, reqRecursive, reqCsvMappingId);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getPathInfoScanResultV2(requestScanDir, loginUserDetails);
        return this.generateUiJsonResponseJson(request, filepathDBParameters, requestScanDir.getFinalCsvMappingId());
    }
    public String readScanDirectoryCsv(HttpServletRequest request, String reqScanDirId,
                                       String reqPathName, String reqFileType, final String reqRecursive,
                                       final String reqCsvMappingId) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, reqFileType, reqRecursive, reqCsvMappingId);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<FilepathDBParameters> response = this.getPathInfoScanResultV2(requestScanDir, loginUserDetails);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = this.applyCsvConfig(request, sheetData, requestScanDir.getFinalCsvMappingId());
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
    public ArrayList<ArrayList<String>> getScanDirectory(HttpServletRequest request, String reqScanDirId, String reqPathName,
                                        String reqFileType, final String reqRecursive,
                                        final String reqCsvMappingId) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, reqFileType, reqRecursive, reqCsvMappingId);
        this.getScanDirMapping(requestScanDir, true); // For updating scanDirMapping with reqScanDirId
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getDbDataFilterResult(requestScanDir);
        return this.generateUiJsonResponse(request, filepathDBParameters, requestScanDir.getFinalCsvMappingId());
    }
    public ArrayList<HashMap<String, String>> getScanDirectoryJson(HttpServletRequest request, String reqScanDirId, String reqPathName,
                                        String reqFileType, final String reqRecursive,
                                        final String reqCsvMappingId) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, reqFileType, reqRecursive, reqCsvMappingId);
        this.getScanDirMapping(requestScanDir, true); // For updating scanDirMapping with reqScanDirId
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getDbDataFilterResult(requestScanDir);
        return this.generateUiJsonResponseJson(request, filepathDBParameters, requestScanDir.getFinalCsvMappingId());
    }
    public String getScanDirectoryCsv(HttpServletRequest request, String reqScanDirId, String reqPathName,
                                      String reqFileType, final String reqRecursive,
                                      final String reqCsvMappingId) throws AppException {
        RequestScanDir requestScanDir = new RequestScanDir(reqScanDirId, reqPathName, reqFileType, reqRecursive, reqCsvMappingId);
        this.getScanDirMapping(requestScanDir, true); // For updating scanDirMapping with reqScanDirId
        ArrayList<FilepathDBParameters> response = this.getDbDataFilterResult(requestScanDir);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = this.applyCsvConfig(request, sheetData, requestScanDir.getFinalCsvMappingId());
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
}
