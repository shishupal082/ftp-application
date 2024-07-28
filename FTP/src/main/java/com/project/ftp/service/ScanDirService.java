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
    private final String csvMappingRequestId = "api-scan-dir";
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
                                       ScanResult scanResult, String scanDirMappingId) {
        if (scanResult == null) {
            return;
        }
        FilepathDBParameters filepathDBParameters;
        PathInfo pathInfo;
        if (scanResult.getPathType() == PathType.FILE) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            filepathDBParameters = new FilepathDBParameters(pathInfo);
            filepathDBParameters.setScanDirMappingId(scanDirMappingId);
            pathInfoScanResults.add(filepathDBParameters);
        } else if (scanResult.getPathType() == PathType.FOLDER) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            pathInfo.setSizeInKb(scanResult.getPathSize());
            filepathDBParameters = new FilepathDBParameters(pathInfo);
            filepathDBParameters.setScanDirMappingId(scanDirMappingId);
            pathInfoScanResults.add(filepathDBParameters);
            ArrayList<ScanResult> childScanResult = scanResult.getScanResults();
            if (childScanResult != null) {
                for(ScanResult result: childScanResult) {
                    this.updatePathInfoDetails(pathInfoScanResults, result, scanDirMappingId);
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
    private ArrayList<FilepathDBParameters> getPathInfoScanResult(final String pathName, final ArrayList<String> fileType,
                                                                  final String recursive, final String scanDirMappingId) throws AppException {
        ArrayList<FilepathDBParameters> pathInfoScanResults = new ArrayList<>();
        boolean isRecursive = AppConstant.TRUE.equals(recursive);
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
        this.updatePathInfoDetails(pathInfoScanResults, scanResult, scanDirMappingId);
        ArrayList<FilepathDBParameters> pathInfoScanFinalResults = new ArrayList<>();
        if (fileType != null && !fileType.isEmpty()) {
            for(FilepathDBParameters dbParameters: pathInfoScanResults) {
                if (fileType.contains(dbParameters.getExtension())) {
                    pathInfoScanFinalResults.add(dbParameters);
                }
            }
            return pathInfoScanFinalResults;
        }
        return pathInfoScanResults;
    }
    private ArrayList<FilepathDBParameters> getPathInfoScanResultV2(final ArrayList<String> scanDirId,
                                                                    final String pathName,
                                                                    final ArrayList<String> fileType,
                                                                    final String recursive,
                                                                    LoginUserDetails loginUserDetails) throws AppException {
        ArrayList<ScanDirMapping> scanDirMapping = this.getScanDirMapping(scanDirId, pathName);
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
                tempPathInfoScanResults = this.getPathInfoScanResult(path, fileType, recursive, dirMapping.getId());
                if (tempPathInfoScanResults != null) {
                    if (pathInfoScanResults == null) {
                        pathInfoScanResults = new ArrayList<>();
                    }
                    if (pathName != null) {
                        tempPathInfoScanResults2 = new ArrayList<>();
                        for(FilepathDBParameters dbParameters: tempPathInfoScanResults) {
                            if (dbParameters == null || dbParameters.getFileName() == null) {
                                continue;
                            }
                            if (dbParameters.getPathName().contains(pathName)) {
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
    private ArrayList<FilepathDBParameters> getDbDataFilterResult(FilePathDAO filePathDAO, final String path,
                                                                  final ArrayList<String> fileType,
                                                                  final ArrayList<String> scanDirId,
                                                                  final boolean recursive) throws AppException {

        ArrayList<FilepathDBParameters> dbData = filePathDAO.getByFilterParameter(path, fileType, scanDirId, recursive);
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return dbData;
    }
    public ApiResponse updateScanDirectory(HttpServletRequest request, String scanDirId, final String recursive) throws AppException {
        // It will throw an error
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(scanDirId);
        ArrayList<FilepathDBParameters> pathInfoScanResults = this.getPathInfoScanResultV2(scanDirIdList,
                null, null, recursive, loginUserDetails);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        if (pathInfoScanResults != null) {
            for(FilepathDBParameters row: pathInfoScanResults) {
                this.updateFilepath(filePathDAO, row);
            }
        }
        HashMap<String, Integer> updateResult = filepathInterface.updateIntoDb(filePathDAO);
        return new ApiResponse(updateResult);
    }
    private ArrayList<ArrayList<String>> applyCsvConfig(HttpServletRequest request,
                                                        ArrayList<ArrayList<String>> data, boolean csvMappingRequired) {
        if (!csvMappingRequired) {
            return data;
        }
        try {
            return msExcelService.applyCsvConfigOnData(request, data, this.csvMappingRequestId);
        } catch (Exception e) {
            logger.info("Error in applyCsvConfig: {}", e.toString());
        }
        return data;
    }
    private ArrayList<ArrayList<String>> generateUiJsonResponse(HttpServletRequest request,
                                                                ArrayList<FilepathDBParameters> filepathDBParameters,
                                                                boolean csvMappingRequired) {
        ArrayList<ArrayList<String>> result = null;
        if (filepathDBParameters != null) {
            result = new ArrayList<>();
            for (FilepathDBParameters dbParameters: filepathDBParameters) {
                result.add(dbParameters.getJsonData());
            }
        }
        result = this.applyCsvConfig(request, result, csvMappingRequired);
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
    public ApiResponse readScanDirectory(HttpServletRequest request, String scanDirId,
                                         String path, String fileType, final String recursive,
                                         final String applyCsvMapping) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(scanDirId);
        ArrayList<String> fileTypeList = this.getTokenizedRequestParameter(fileType);
        ApiResponse apiResponse = new ApiResponse();
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getPathInfoScanResultV2(scanDirIdList, path,
                fileTypeList, recursive, loginUserDetails);
        apiResponse.setData(this.generateUiJsonResponse(request, filepathDBParameters, !AppConstant.FALSE.equals(applyCsvMapping)));
        return apiResponse;
    }
    public String readScanDirectoryCsv(HttpServletRequest request, String scanDirId,
                                       String path, String fileType, final String recursive,
                                       final String applyCsvMapping) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(scanDirId);
        ArrayList<String> fileTypeList = this.getTokenizedRequestParameter(fileType);
        ArrayList<FilepathDBParameters> response = this.getPathInfoScanResultV2(scanDirIdList, path, fileTypeList, recursive, loginUserDetails);
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = this.applyCsvConfig(request, sheetData, !AppConstant.FALSE.equals(applyCsvMapping));
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
    public ApiResponse getScanDirectory(HttpServletRequest request, String scanDirId, String path,
                                        String fileType, final String recursive,
                                        final String applyCsvMapping) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(scanDirId);
        ArrayList<String> fileTypeList = this.getTokenizedRequestParameter(fileType);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getDbDataFilterResult(filePathDAO, path,
                fileTypeList, scanDirIdList, AppConstant.TRUE.equals(recursive));
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(this.generateUiJsonResponse(request, filepathDBParameters, !AppConstant.FALSE.equals(applyCsvMapping)));
        return apiResponse;
    }
    public String getScanDirectoryCsv(HttpServletRequest request, String scanDirId, String path,
                                      String fileType, final String recursive,
                                      final String applyCsvMapping) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        ArrayList<String> scanDirIdList = this.getTokenizedRequestParameter(scanDirId);
        ArrayList<String> fileTypeList = this.getTokenizedRequestParameter(fileType);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<FilepathDBParameters> response = this.getDbDataFilterResult(filePathDAO, path,
                fileTypeList, scanDirIdList, AppConstant.TRUE.equals(recursive));
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = this.applyCsvConfig(request, sheetData, !AppConstant.FALSE.equals(applyCsvMapping));
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
}
