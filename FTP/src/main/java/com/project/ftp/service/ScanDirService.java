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
import java.util.Arrays;
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
                                       ScanResult scanResult, String scanDirMappingId,
                                       String reqPathName, ArrayList<String> reqFileType,
                                       boolean isRecursive, ArrayList<String> reqScanDirIdList, String reqCsvMappingId) {

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
            filepathDBParameters.setReqScanDirId(this.combineReqParameter(reqScanDirIdList));
            filepathDBParameters.setReqPathName(reqPathName);
            filepathDBParameters.setReqFileType(this.combineReqParameter(reqFileType));
            filepathDBParameters.setReqRecursive(Boolean.toString(isRecursive));
            filepathDBParameters.setReqCsvMappingId(reqCsvMappingId);
            // For each entry end
            pathInfoScanResults.add(filepathDBParameters);
        } else if (scanResult.getPathType() == PathType.FOLDER) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            pathInfo.setSizeInKb(scanResult.getPathSize());
            filepathDBParameters = new FilepathDBParameters(pathInfo);
            filepathDBParameters.setScanDirMappingId(scanDirMappingId);
            // For each entry
            filepathDBParameters.setReqScanDirId(this.combineReqParameter(reqScanDirIdList));
            filepathDBParameters.setReqPathName(reqPathName);
            filepathDBParameters.setReqFileType(this.combineReqParameter(reqFileType));
            filepathDBParameters.setReqRecursive(Boolean.toString(isRecursive));
            filepathDBParameters.setReqCsvMappingId(reqCsvMappingId);
            // For each entry end
            pathInfoScanResults.add(filepathDBParameters);
            ArrayList<ScanResult> childScanResult = scanResult.getScanResults();
            if (childScanResult != null) {
                for(ScanResult result: childScanResult) {
                    this.updatePathInfoDetails(pathInfoScanResults, result, scanDirMappingId,
                            reqPathName, reqFileType, isRecursive, reqScanDirIdList, reqCsvMappingId);
                }
            }
        }
    }
    private ArrayList<String> getTokenizedRequestParameter(String requestParameter) {
        if (StaticService.isInValidString(requestParameter)) {
            return null;
        }
        String[] splitResult = requestParameter.split("\\|");
        return new ArrayList<>(Arrays.asList(splitResult));
    }
    private String combineReqParameter(ArrayList<String> reqParam) {
        if (reqParam == null) {
            return null;
        }
        ArrayList<String> reqParam2 = new ArrayList<>();
        for(String param: reqParam) {
            if (StaticService.isValidString(param)) {
                reqParam2.add(param);
            }
        }
        return String.join("|", reqParam2);
    }
    private ArrayList<FilepathDBParameters> getPathInfoScanResult(final String scanDirMappingId,
                                                                  final String pathName, final String reqPathName,
                                                                  final ArrayList<String> reqFileType,
                                                                  final boolean isRecursive,
                                                                  final ArrayList<String> reqScanDirIdList, final String csvMappingId) throws AppException {
        ArrayList<FilepathDBParameters> pathInfoScanResults = new ArrayList<>();
        if (StaticService.isInValidString(pathName)) {
            logger.info("getPathInfoScanResult-1: scanResult: null for pathName: {}", pathName);
            return null;
        }
        ScanResult scanResult = fileService.scanDirectory(pathName, pathName, isRecursive, false);
        if (scanResult == null || scanResult.getPathType() == null) {
            logger.info("getPathInfoScanResult-2: scanResult: {} for pathName: {}", scanResult, pathName);
            return null;
        }
        this.updateFolderSize(scanResult);


        this.updatePathInfoDetails(pathInfoScanResults, scanResult, scanDirMappingId,
                reqPathName, reqFileType, isRecursive, reqScanDirIdList, csvMappingId);
        ArrayList<FilepathDBParameters> pathInfoScanFinalResults = new ArrayList<>();
        if (reqFileType != null && !reqFileType.isEmpty()) {
            for(FilepathDBParameters dbParameters: pathInfoScanResults) {
                if (reqFileType.contains(dbParameters.getExtension())) {
                    pathInfoScanFinalResults.add(dbParameters);
                }
            }
            return pathInfoScanFinalResults;
        }
        return pathInfoScanResults;
    }
    private ArrayList<FilepathDBParameters> getPathInfoScanResultV2(final String reqScanDirId,
                                                                    final String reqPathName,
                                                                    final String reqFileType,
                                                                    final String reqRecursive,
                                                                    LoginUserDetails loginUserDetails,
                                                                    final String reqCsvMappingId) throws AppException {
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(reqScanDirId);
        ArrayList<String> fileTypeList = this.getTokenizedRequestParameter(reqFileType);
        ArrayList<ScanDirMapping> scanDirMapping = this.getScanDirMapping(scanDirIdList, reqPathName);
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
                tempPathInfoScanResults = this.getPathInfoScanResult(dirMapping.getId(), path, reqPathName, fileTypeList,
                        AppConstant.TRUE.equals(reqRecursive), scanDirIdList, reqCsvMappingId);
                if (tempPathInfoScanResults != null) {
                    if (pathInfoScanResults == null) {
                        pathInfoScanResults = new ArrayList<>();
                    }
                    if (reqPathName != null) {
                        tempPathInfoScanResults2 = new ArrayList<>();
                        for(FilepathDBParameters dbParameters: tempPathInfoScanResults) {
                            if (dbParameters == null || dbParameters.getFileName() == null) {
                                continue;
                            }
                            if (dbParameters.getPathName().contains(reqPathName)) {
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
                filepathDBParameters1.setScanDirMappingId(scanDirMapping.getId());
            }
        }
    }
    private void updateFilepath(FilePathDAO filePathDAO, FilepathDBParameters filepathDBParameters) {
        if (filepathDBParameters == null) {
            return;
        }
        String currentTime;
        String pathname = filepathDBParameters.getPathName();
        FilepathDBParameters filepathDBParameters1 = filePathDAO.getByPathname(pathname);
        if (filepathDBParameters1 == null) {
            currentTime = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
            filepathDBParameters.setUpdated(true);
            filepathDBParameters.setTableUniqueId(StaticService.createUUIDNumber());
            filepathDBParameters.setEntryTime(currentTime);
            filepathDBParameters.setUiEntryTime(currentTime);
            filepathDBParameters.setScannedDate(currentTime);
            filePathDAO.save(filepathDBParameters);
        } else if (!filepathDBParameters.getSizeInKb().equals(filepathDBParameters1.getSizeInKb())) {
            currentTime = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
            filepathDBParameters1.setUpdated(true);
            filepathDBParameters1.setEditedAt(currentTime);
            filepathDBParameters1.setSizeInKb(filepathDBParameters.getSizeInKb());
            filepathDBParameters1.setSize(filepathDBParameters.getSize());
            filepathDBParameters1.setScannedDate(currentTime);
            filePathDAO.updateById(filepathDBParameters1);
        } else {
            filePathDAO.updateById(filepathDBParameters1);
        }
    }
    private ArrayList<FilepathDBParameters> getDbDataFilterResult(FilePathDAO filePathDAO, final String reqScanDirId,
                                                                  final String reqPathName,
                                                                  final String reqFileType,
                                                                  final boolean recursive,
                                                                  final String csvMappingId) throws AppException {

        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(reqScanDirId);
        ArrayList<String> fileTypeList = this.getTokenizedRequestParameter(reqFileType);
        ArrayList<FilepathDBParameters> dbData = filePathDAO.getByFilterParameter(reqPathName, scanDirIdList, fileTypeList, recursive);
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        for(FilepathDBParameters dbParameters: dbData) {
            if (dbParameters == null) {
                continue;
            }
            dbParameters.setReqScanDirId(this.combineReqParameter(scanDirIdList));
            dbParameters.setReqPathName(reqPathName);
            dbParameters.setReqFileType(this.combineReqParameter(fileTypeList));
            dbParameters.setReqRecursive(Boolean.toString(recursive));
            dbParameters.setReqCsvMappingId(csvMappingId);
        }
        return dbData;
    }
    public ApiResponse updateScanDirectory(HttpServletRequest request, String reqScanDirId, final String reqRecursive) throws AppException {
        // It will throw an error
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<FilepathDBParameters> pathInfoScanResults = this.getPathInfoScanResultV2(reqScanDirId,
                null, null, reqRecursive, loginUserDetails, null);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(reqScanDirId);
        filePathDAO.updateFromReqScanDir(scanDirIdList, AppConstant.TRUE.equals(reqRecursive));
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
    private ArrayList<ArrayList<String>> generateUiJsonResponse(HttpServletRequest request,
                                                                ArrayList<FilepathDBParameters> filepathDBParameters,
                                                                String csvMappingId) {
        ArrayList<ArrayList<String>> result = null;
        if (filepathDBParameters != null) {
            result = new ArrayList<>();
            for (FilepathDBParameters dbParameters: filepathDBParameters) {
                result.add(dbParameters.getJsonData());
            }
        }
        result = this.applyCsvConfig(request, result, csvMappingId);
        return result;
    }
    private ArrayList<ScanDirMapping> getAllScanDirMappings() {
        String scanDirConfigFilePath = appConfig.getFtpConfiguration().getScanDirConfigFilePath();
        ScanDirConfig scanDirConfig = yamlFileParser.getScanDirConfigFromPath(scanDirConfigFilePath);
        if (scanDirConfig == null) {
            return null;
        }
        return scanDirConfig.getScanDirConfig();
    }
    private ArrayList<ScanDirMapping> getScanDirMapping(ArrayList<String> scanDirId, String pathName) throws AppException {
        if (scanDirId == null) {
            logger.info("Invalid request parameter scanDirId is null");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<ScanDirMapping> scanDirMappings = this.getAllScanDirMappings();
        ArrayList<ScanDirMapping> scanDirMappingById = new ArrayList<>();
        if (scanDirMappings == null) {
            logger.info("scanDirMappings is null in the config.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        for(ScanDirMapping scanDirMapping: scanDirMappings) {
            if (scanDirMapping == null) {
                continue;
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
                                              String scanDirId, String pathName) throws AppException {
        pathName = StaticService.replaceBackSlashToSlash(pathName);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(scanDirId);
        ArrayList<ScanDirMapping> scanDirMapping = this.getScanDirMapping(scanDirIdList, pathName);
        return new ApiResponse(scanDirMapping);
    }
    public ApiResponse readScanDirectory(HttpServletRequest request, String reqScanDirId,
                                         String reqPathName, String reqFileType, final String reqRecursive,
                                         final String reqCsvMappingId) throws AppException {
        reqPathName = StaticService.replaceBackSlashToSlash(reqPathName);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ApiResponse apiResponse = new ApiResponse();
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getPathInfoScanResultV2(reqScanDirId, reqPathName,
                reqFileType, reqRecursive, loginUserDetails, reqCsvMappingId);
        apiResponse.setData(this.generateUiJsonResponse(request, filepathDBParameters, reqCsvMappingId));
        return apiResponse;
    }
    public String readScanDirectoryCsv(HttpServletRequest request, String reqScanDirId,
                                       String reqPathName, String reqFileType, final String reqRecursive,
                                       final String reqCsvMappingId) throws AppException {
        reqPathName = StaticService.replaceBackSlashToSlash(reqPathName);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<FilepathDBParameters> response = this.getPathInfoScanResultV2(reqScanDirId, reqPathName,
                reqFileType, reqRecursive, loginUserDetails, reqCsvMappingId);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = this.applyCsvConfig(request, sheetData, reqCsvMappingId);
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
    public ApiResponse getScanDirectory(HttpServletRequest request, String reqScanDirId, String reqPathName,
                                        String reqFileType, final String recursive,
                                        final String csvMappingId) throws AppException {
        reqPathName = StaticService.replaceBackSlashToSlash(reqPathName);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getDbDataFilterResult(filePathDAO, reqScanDirId, reqPathName,
                reqFileType, AppConstant.TRUE.equals(recursive), csvMappingId);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(this.generateUiJsonResponse(request, filepathDBParameters, csvMappingId));
        return apiResponse;
    }
    public String getScanDirectoryCsv(HttpServletRequest request, String reqScanDirId, String reqPathName,
                                      String reqFileType, final String recursive,
                                      final String csvMappingId) throws AppException {
        reqPathName = StaticService.replaceBackSlashToSlash(reqPathName);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<FilepathDBParameters> response = this.getDbDataFilterResult(filePathDAO, reqScanDirId, reqPathName,
                reqFileType, AppConstant.TRUE.equals(recursive), csvMappingId);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = this.applyCsvConfig(request, sheetData, csvMappingId);
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
}
