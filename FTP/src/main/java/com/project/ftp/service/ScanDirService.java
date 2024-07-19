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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ScanDirService {
    final static Logger logger = LoggerFactory.getLogger(ScanDirService.class);
    private final FileService fileService;
    private final FilepathInterface filepathInterface;
    private final UserService userService;
    private final MSExcelService msExcelService;
    private final StrUtils strUtils;
    private final String csvMappingRequestId = "api-scan-dir";
    public ScanDirService (final AppConfig appConfig, final FilepathInterface filepathInterface) {
        this.fileService = new FileService();
        this.filepathInterface = filepathInterface;
        this.userService = appConfig.getUserService();
        this.msExcelService = appConfig.getMsExcelService();
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
    private void verifyRequestParameter(String path) throws AppException {
        if (path == null || path.isEmpty()) {
            logger.info("Invalid requested query parameter, path: {}", path);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
    }
    private ArrayList<FilepathDBParameters> getPathInfoScanResult(final String staticFolderPath,
                                                                  final String path, final String recursive) throws AppException {
        this.verifyRequestParameter(path);
        ArrayList<FilepathDBParameters> pathInfoScanResults = new ArrayList<>();
        boolean isRecursive = false;
        if (recursive != null) {
            isRecursive = recursive.equals(AppConstant.TRUE);
        }
        if (path == null) {
            logger.info("getPathInfoScanResult: path is null");
            return null;
        }
        ScanResult scanResult = fileService.scanDirectory(staticFolderPath, path, isRecursive, false);
        if (scanResult == null || scanResult.getPathType() == null) {
            logger.info("getPathInfoScanResult: invalid: {}, staticFolderPath: {}, path: {}",
                    scanResult, staticFolderPath, path);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        this.updateFolderSize(scanResult);
        this.updatePathInfoDetails(pathInfoScanResults, scanResult, staticFolderPath);
        return pathInfoScanResults;
    }
    private void updateDBParameter(ArrayList<FilepathDBParameters> filepathDBParameters,
                                   LoginUserDetails loginUserDetails, String scanDirMappingId) {
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
            filepathDBParameters1.setScanDirMappingId(scanDirMappingId);
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
    private ArrayList<FilepathDBParameters> getScanDirectoryResult(FilePathDAO filePathDAO, final String path,
                                                                  final String fileType, final String scanDirId,
                                                                  final boolean recursive) throws AppException {

        ArrayList<FilepathDBParameters> scanDirectoryResult = filePathDAO.getByFilterParameter(path, fileType, scanDirId, recursive);
        if (scanDirectoryResult == null || scanDirectoryResult.isEmpty()) {
            return null;
        }
        return scanDirectoryResult;
    }
    public ApiResponse updateScanDirectory(HttpServletRequest request, String path, final String recursive) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        ApiResponse apiResponse = new ApiResponse();
        ArrayList<FilepathDBParameters> pathInfoScanResults = this.getPathInfoScanResult(path, path, recursive);

        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        if (pathInfoScanResults != null) {
            this.updateDBParameter(pathInfoScanResults, userService.getLoginUserDetails(request), path);
            for(FilepathDBParameters row: pathInfoScanResults) {
                this.updateFilepath(filePathDAO, row);
            }
        }
        filepathInterface.updateIntoDb(filePathDAO);
        return apiResponse;
    }
    private ArrayList<ArrayList<String>> generateUiJsonResponse(HttpServletRequest request, ArrayList<FilepathDBParameters> filepathDBParameters) {
        ArrayList<ArrayList<String>> result = null;
        if (filepathDBParameters != null) {
            result = new ArrayList<>();
            for (FilepathDBParameters dbParameters: filepathDBParameters) {
                result.add(dbParameters.getJsonData());
            }
        }
        result = msExcelService.applyCsvConfigOnData(request, result, this.csvMappingRequestId);
        return result;
    }
    public ApiResponse readScanDirectory(HttpServletRequest request, String path, final String recursive) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        ApiResponse apiResponse = new ApiResponse();
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getPathInfoScanResult(path, path, recursive);
        this.updateDBParameter(filepathDBParameters, userService.getLoginUserDetails(request), path);
        apiResponse.setData(this.generateUiJsonResponse(request, filepathDBParameters));
        return apiResponse;
    }
    public ApiResponse getScanDirectory(HttpServletRequest request, String path,
                                        String fileType, String scanDirId, final String recursive) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getScanDirectoryResult(filePathDAO, path,
                fileType, scanDirId, AppConstant.TRUE.equals(recursive));
        if (filepathDBParameters == null) {
            logger.info("Invalid requested path: {}", path);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(this.generateUiJsonResponse(request, filepathDBParameters));
        return apiResponse;
    }

    public String getScanDirectoryCsv(HttpServletRequest request, String path,
                                      String fileType, String scanDirId, final String recursive) throws AppException {
        path = StaticService.replaceBackSlashToSlash(path);
        FilePathDAO filePathDAO = new FilePathDAO(filepathInterface);
        ArrayList<FilepathDBParameters> response = this.getScanDirectoryResult(filePathDAO, path,
                fileType, scanDirId, AppConstant.TRUE.equals(recursive));
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            for (FilepathDBParameters dbParameters: response) {
                sheetData.add(dbParameters.getCsvData());
            }
        }
        sheetData = msExcelService.applyCsvConfigOnData(request, sheetData, this.csvMappingRequestId);
        for(ArrayList<String> rowData: sheetData) {
            result.add(strUtils.joinArrayList(rowData, AppConstant.commaDelimater));
        }
        return strUtils.joinArrayList(result, AppConstant.NEW_LINE_STRING);
    }
}
