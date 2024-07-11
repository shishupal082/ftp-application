package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.PathType;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.PathInfoScanResult;
import com.project.ftp.obj.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ScanDirService {
    final static Logger logger = LoggerFactory.getLogger(ScanDirService.class);
    private final FileService fileService;
    public ScanDirService (final AppConfig appConfig, final UserService userService) {
        this.fileService = new FileService();
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
    private void updatePathInfoDetails(ArrayList<PathInfoScanResult> pathInfoScanResults, ScanResult scanResult) {
        if (scanResult == null) {
            return;
        }
        PathInfoScanResult pathInfoScanResult;
        PathInfo pathInfo;
        if (scanResult.getPathType() == PathType.FILE) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            pathInfoScanResult = new PathInfoScanResult(pathInfo);
            pathInfoScanResults.add(pathInfoScanResult);
        } else if (scanResult.getPathType() == PathType.FOLDER) {
            pathInfo = fileService.getPathInfo(scanResult.getPathName());
            pathInfo.setSizeInKb(scanResult.getPathSize());
            pathInfoScanResult = new PathInfoScanResult(pathInfo);
            pathInfoScanResults.add(pathInfoScanResult);
            ArrayList<ScanResult> childScanResult = scanResult.getScanResults();
            if (childScanResult != null) {
                for(ScanResult result: childScanResult) {
                    this.updatePathInfoDetails(pathInfoScanResults, result);
                }
            }
        }
    }
    private ArrayList<PathInfoScanResult> getPathInfoScanResult(final String staticFolderPath, final String path, final String recursive) {
        ArrayList<PathInfoScanResult> pathInfoScanResults = new ArrayList<>();
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
            return null;
        }
        this.updateFolderSize(scanResult);
        this.updatePathInfoDetails(pathInfoScanResults, scanResult);
        return pathInfoScanResults;
    }
    public ApiResponse getPathInfoDetails(final String path, final String recursive) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(this.getPathInfoScanResult(path, path, recursive));
        return apiResponse;
    }
}
