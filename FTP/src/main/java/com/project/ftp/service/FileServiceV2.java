package com.project.ftp.service;

import com.project.ftp.config.*;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.view.CommonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileServiceV2 {
    final static Logger logger = LoggerFactory.getLogger(FileServiceV2.class);
    final AppConfig appConfig;
    final FileService fileService;
    final String savedDataFilepath;
    public FileServiceV2(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        savedDataFilepath = appConfig.getFtpConfiguration().getConfigDataFilePath()
                + AppConstant.FILE_DATA_FILENAME;
    }
    private String parseUserFileName(String fileName) {
        String userFilename = null;
        if (fileName == null) {
            return null;
        }
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        String[] fileNameArr = fileName.split(dir);
        if(fileNameArr.length == 2) {
            if (fileNameArr[1].split("/").length == 2) {
                userFilename = fileNameArr[1];
            }
        }
        return userFilename;
    }
    private void generateApiResponse(ArrayList<ScanResult> scanResults, ArrayList<String> response) {
        if (scanResults == null) {
            return;
        }
        String fileName;
        for (ScanResult scanResult: scanResults) {
            if (scanResult != null) {
                if (PathType.FILE.equals(scanResult.getPathType())) {
                    fileName = parseUserFileName(scanResult.getPathName());
                    if (fileName != null) {
                        response.add(fileName);
                    }
                } else if (PathType.FOLDER.equals(scanResult.getPathType())) {
                    generateApiResponse(scanResult.getScanResults(), response);
                }
            }
        }
    }
    private boolean isFileExist(String filepath) {
        if (filepath == null) {
            return false;
        }
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        return fileService.isFile(dir+filepath);
    }
    private ArrayList<ResponseFilesInfo> generateFileInfoResponse(ArrayList<String> res,
                                                                  LoginUserDetails loginUserDetails) {
        if (res == null || loginUserDetails == null) {
            return null;
        }
        ArrayList<ResponseFilesInfo> finalResponse = new ArrayList<>();
        FileDetails fileDetails = fileService.getAllFileDetails(savedDataFilepath);
        if (fileDetails == null) {
            fileDetails = new FileDetails(null);
        }
        HashMap<String, FileDetail> fileDetailHashMap = fileDetails.getFileDetailHashMap();
        HashMap<String, String> parsedData;
        FileDetail fileDetail;
        String fileUsername, filenameStr;
        for (String filepath: res) {
            fileDetail = fileDetailHashMap.get(filepath);
            if (fileDetail == null || fileDetail.isDeletedTrue()) {
                parsedData = this.parseRequestedFileStr(filepath);
                if (AppConstant.SUCCESS.equals(parsedData.get(AppConstant.STATUS))) {
                    fileUsername = parsedData.get(AppConstant.FILE_USERNAME);
                    filenameStr = parsedData.get(AppConstant.FILE_NAME_STR);
                    fileDetail = this.generateFileDetailsFromFilepath(fileUsername,
                            filenameStr,"getFilesInfoMigration");
                }
            }
            finalResponse.add(new ResponseFilesInfo(fileDetail, loginUserDetails));
        }
        String filepath;
        for (Map.Entry<String, FileDetail> entry: fileDetailHashMap.entrySet()) {
            filepath = entry.getKey();
            if (res.contains(filepath)) {
                continue;
            }
            if (!this.isFileExist(filepath)) {
                logger.info("filepath: {}, does not exist.", filepath);
                continue;
            }
            ResponseFilesInfo filesInfoResponse = new ResponseFilesInfo(entry.getValue(), loginUserDetails);
            if (filesInfoResponse.isViewOption()) {
                finalResponse.add(filesInfoResponse);
            }
        }
        return finalResponse;
    }
    public ApiResponse scanUserDirectory(HttpServletRequest request, final UserService userService2) {
        ApiResponse apiResponse;
        LoginUserDetails loginUserDetails = userService2.getLoginUserDetails(request);
        ArrayList<ScanResult> scanResults = new ArrayList<>();
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        String publicDir = dir+AppConstant.PUBLIC+"/";
        String loginUserName = loginUserDetails.getUsername();
        if (loginUserDetails.getLogin()) {
            if (loginUserDetails.getLoginUserAdmin()) {
                scanResults.add(fileService.scanDirectory(dir, dir, true));
            } else {
                dir = dir + loginUserName + "/";
                scanResults.add(fileService.scanDirectory(dir, dir, false));
                if (!AppConstant.PUBLIC.equals(loginUserName.toLowerCase())) {
                    scanResults.add(fileService.scanDirectory(publicDir, publicDir, false));
                }
            }
            ArrayList<String> response = new ArrayList<>();
            generateApiResponse(scanResults, response);
            logger.info("scanUserDirectory result size: {}", response.size());
            ArrayList<ResponseFilesInfo> filesInfo = this.generateFileInfoResponse(response, loginUserDetails);
            logger.info("final result size: {}", filesInfo.size());
            apiResponse = new ApiResponse(filesInfo);
        } else {
            logger.info("scanUserDirectory user is unauthorised: {}", loginUserDetails);
            apiResponse = new ApiResponse(ErrorCodes.UNAUTHORIZED_USER);
        }
        return apiResponse;
    }

    private HashMap<String, String> parseRequestedFileStr(String filename) {
        HashMap<String, String> response = new HashMap<>();
        response.put(AppConstant.STATUS, AppConstant.FAILURE);
        if (filename == null) {
            logger.info("filename can not be null");
            return response;
        }
        String[] filenameArr = filename.split("/");
        if (filenameArr.length == 2) {
            if (filenameArr[0].isEmpty()) {
                logger.info("filename does not contain username: {}", filename);
                return response;
            }
            if (filenameArr[1].isEmpty()) {
                logger.info("filename is empty in request: {}", filename);
                return response;
            }
            response.put(AppConstant.STATUS, AppConstant.SUCCESS);
            response.put(AppConstant.FILE_USERNAME, filenameArr[0]);
            response.put(AppConstant.FILE_NAME_STR, filenameArr[1]);
        }
        return response;
    }
    /**
    public PathInfo searchRequestedFile(HttpServletRequest request, final UserService userService,
                                        String filename) throws AppException {
        if (filename == null) {
            logger.info("filename can not be null");
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
        }
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String loginUserName = loginUserDetails.getUsername();
        String[] filenameArr = filename.split("/");
        String filePath = appConfig.getFtpConfiguration().getFileSaveDir();
        PathInfo pathInfo;
        if (filenameArr.length == 2) {
            if (loginUserName.isEmpty()) {
                logger.info("Invalid loginUserName: {}", loginUserName);
                throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
            }
            if (loginUserDetails.getLoginUserAdmin() || AppConstant.PUBLIC.equals(filenameArr[0])) {
                filePath += filename;
            } else if (loginUserName.equals(filenameArr[0])) {
                filePath += loginUserName + "/" + filenameArr[1];
            } else {
                logger.info("Unauthorised access loginUserName: {}, filename: {}",
                        loginUserName, filename);
                throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
            }
            pathInfo = fileService.getPathInfo(filePath);
            logger.info("Search result: {}", pathInfo);
            if (!AppConstant.FILE.equals(pathInfo.getType())) {
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }
        } else {
            logger.info("Invalid filename:{}", filename);
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
        }
        return pathInfo;
    }
     **/
    public PathInfo searchRequestedFileV2(HttpServletRequest request, final UserService userService,
                                        String filename) throws AppException {
        if (filename == null) {
            logger.info("filename can not be null");
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
        }
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!loginUserDetails.getLogin()) {
            logger.info("Invalid loginUser: {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        HashMap<String, String> parsedFileStr = this.parseRequestedFileStr(filename);
        String loginUserName = loginUserDetails.getUsername();
        String filePath = appConfig.getFtpConfiguration().getFileSaveDir();
        PathInfo pathInfo;
        if (AppConstant.SUCCESS.equals(parsedFileStr.get(AppConstant.STATUS))) {
            filePath += filename;
            pathInfo = fileService.getPathInfo(filePath);
            if (!AppConstant.FILE.equals(pathInfo.getType())) {
                logger.info("file not found: {}", pathInfo);
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }
            // Now file exist, checking for valid permission
            FileDetail fileDetail = fileService.searchFileDetails(savedDataFilepath, filename);
            String fileUsername = parsedFileStr.get(AppConstant.FILE_USERNAME);
            String filenameStr = parsedFileStr.get(AppConstant.FILE_NAME_STR);
            if (fileDetail == null || !fileDetail.isValid()) {
                logger.info("Invalid fileDetails: {}", fileDetail);
                fileDetail = this.generateFileDetailsFromFilepath(fileUsername, filenameStr,"viewMigration");
            }
            // i.e. file is deleted by user and again manually copied to server
            if (fileDetail.isDeletedTrue()) {
                logger.info("file deleted entry, but file is there: {}", fileDetail);
                fileDetail.setIsDeleted("false");
                fileService.saveFileDetailsV2(savedDataFilepath, fileDetail);
            }
            FileViewer viewer = fileDetail.getViewer();
            if (FileViewer.ALL != viewer) {
                if (FileViewer.SELF != viewer) {
                    logger.info("Invalid viewer: {}", viewer);
                    throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
                }
                if (!AppConstant.PUBLIC.equals(fileUsername)) {
                    if (!loginUserDetails.getLoginUserAdmin() && !loginUserName.equals(fileUsername)) {
                        logger.info("Unauthorised access loginUserName: {}, filename: {}",
                                loginUserName, filename);
                        throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
                    }
                }
            }
            logger.info("Search result: {}", pathInfo);
        } else {
            logger.info("Invalid filename:{}", filename);
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
        }
        return pathInfo;
    }
    /**
    public void deleteRequestFile(HttpServletRequest request,
                                         UserService userService,
                                         RequestDeleteFile deleteFile) throws AppException {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!loginUserDetails.getLogin()) {
            logger.info("Login required to deleteFile: {}", deleteFile);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        if (deleteFile == null) {
            logger.info("deleteFile request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String deleteFileReq = deleteFile.getFilename();
        HashMap<String, String> parsedFileStr = this.parseRequestedFileStr(deleteFileReq);
        if (AppConstant.FAILURE.equals(parsedFileStr.get(AppConstant.STATUS))) {
            logger.info("deleteFile invalid request: {}", deleteFile);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String fileUserName = parsedFileStr.get(AppConstant.FILE_USERNAME);
        String userName = loginUserDetails.getUsername();
        if (!fileUserName.equals(userName)) {
            logger.info("UnAuthorised user trying to deleteFile: {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        PathInfo pathInfo = fileService.getPathInfo(dir + deleteFileReq);
        Boolean permanentlyDeleteFile = appConfig.getFtpConfiguration().getPermanentlyDeleteFile();
        Boolean fileDeleteStatus;
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            if (permanentlyDeleteFile == null || permanentlyDeleteFile) {
                logger.info("Permanently deleting file: {}", deleteFile);
                fileDeleteStatus = fileService.deleteFileV2(dir+deleteFileReq);
            } else {
                ArrayList<String> requiredDirs = new ArrayList<>();
                requiredDirs.add(dir);
                requiredDirs.add("trash");
                requiredDirs.add(userName);
                String trashFolder = fileService.createDir(requiredDirs);
                if (trashFolder == null) {
                    logger.info("Error in creating trash folder for user: {}", loginUserDetails);
                    throw new AppException(ErrorCodes.RUNTIME_ERROR);
                }
                String currentFolder = pathInfo.getParentFolder();
                fileDeleteStatus = fileService.moveFile(currentFolder, trashFolder,
                        pathInfo.getFilenameWithoutExt(), pathInfo.getExtension());
            }
            if (!fileDeleteStatus) {
                logger.info("Error in deleting requested file: {}, currentUser: {}",
                        deleteFileReq, loginUserDetails);
                throw new AppException(ErrorCodes.RUNTIME_ERROR);
            } else {
                logger.info("Requested file deleted: {}", deleteFileReq);
            }
        } else {
            logger.info("Requested deleteFile: {}, does not exist.", deleteFileReq);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
    }
     **/
    private HashMap<String, String> verifyDeleteRequestParameters(RequestDeleteFile deleteFile) {
        if (deleteFile == null) {
            logger.info("deleteFile request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String deleteFileReq = deleteFile.getFilename();
        HashMap<String, String> parsedFileStr = this.parseRequestedFileStr(deleteFileReq);
        if (AppConstant.FAILURE.equals(parsedFileStr.get(AppConstant.STATUS))) {
            logger.info("deleteFile invalid request: {}", deleteFile);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        return parsedFileStr;
    }
    private void addTextInFileDetailForDelete(LoginUserDetails userDetails, FileDetail fileDetail) {
        String uploadedBy = fileDetail.getUploadedby();
        String filename = fileDetail.getFilename();
        String deletedBy = userDetails.getUsername();
        FileDetail finalFileDetail = new FileDetail(filename, uploadedBy, deletedBy);
        if (!finalFileDetail.isValid()) {
            logger.info("Invalid file details: {}", finalFileDetail);
        } else {
            finalFileDetail.setSubject(fileDetail.getSubject());
            finalFileDetail.setHeading(fileDetail.getHeading());
            finalFileDetail.setViewer(fileDetail.getViewer());
            finalFileDetail.setDeleteAccess(fileDetail.getDeleteAccess());
        }
        if (!fileDetail.isValid()) {
            logger.info("Invalid file delete data: {}", fileDetail);
        }
        fileService.saveFileDetailsV2(savedDataFilepath, finalFileDetail);
    }
    private void deleteFileV3(FileDetail fileDetail) throws AppException {
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        PathInfo pathInfo = fileService.getPathInfo(dir + fileDetail.getFilepath());
        Boolean permanentlyDeleteFile = appConfig.getFtpConfiguration().getPermanentlyDeleteFile();
        Boolean fileDeleteStatus;
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            if (permanentlyDeleteFile == null || permanentlyDeleteFile) {
                logger.info("Permanently deleting file: {}", fileDetail);
                fileDeleteStatus = fileService.deleteFileV2(pathInfo.getPath());
            } else {
                ArrayList<String> requiredDirs = new ArrayList<>();
                requiredDirs.add(dir);
                requiredDirs.add("trash");
                requiredDirs.add(fileDetail.getUploadedby());
                String trashFolder = fileService.createDir(requiredDirs);
                if (trashFolder == null) {
                    logger.info("Error in creating trash folder for user: {}", fileDetail.getUploadedby());
                    throw new AppException(ErrorCodes.RUNTIME_ERROR);
                }
                String currentFolder = pathInfo.getParentFolder();
                fileDeleteStatus = fileService.moveFile(currentFolder, trashFolder,
                        pathInfo.getFilenameWithoutExt(), pathInfo.getExtension());
            }
            if (!fileDeleteStatus) {
                logger.info("Error in deleting requested file: {}", fileDetail.getFilepath());
                throw new AppException(ErrorCodes.RUNTIME_ERROR);
            } else {
                logger.info("Requested file deleted: {}", fileDetail.getFilepath());
            }
        } else {
            logger.info("Requested deleteFile: {}, does not exist.", fileDetail.getFilepath());
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
    }
    private void deleteFileByUser(LoginUserDetails userDetails, FileDetail fileDetail) throws AppException {
        String fileUserName = fileDetail.getUploadedby();
        String userName = userDetails.getUsername();
        if (!fileUserName.equals(userName)) {
            logger.info("UnAuthorised user trying to deleteFile: {}", userDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        this.deleteFileV3(fileDetail);
    }
    // viewMigration, deleteMigration, getFilesInfoMigration, upload
    private FileDetail generateFileDetailsFromFilepath(String fileUsername, String filename,
                                                       String entryType) {
        FileViewer viewer = StaticService.getFileViewerV2(appConfig, fileUsername);
        FileDeleteAccess deleteAccess = StaticService.getFileDeleteAccessV2(appConfig);
        FileDetail fileDetail = new FileDetail(filename, fileUsername, viewer, deleteAccess, entryType);
        fileService.saveFileDetailsV2(savedDataFilepath, fileDetail);
        return fileDetail;
    }
    public void deleteRequestFileV2(HttpServletRequest request,
                                    UserService userService,
                                    RequestDeleteFile deleteFile) throws AppException {
        HashMap<String, String> parsedFileStr = this.verifyDeleteRequestParameters(deleteFile);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!loginUserDetails.getLogin()) {
            logger.info("Login required to delete file: {}", deleteFile);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        String deleteFileReq = deleteFile.getFilename();
        String filepath = appConfig.getFtpConfiguration().getFileSaveDir() + deleteFileReq;
        if (!fileService.isFile(filepath)) {
            logger.info("requested delete file not found: {}", filepath);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        FileDetail fileDetail = fileService.searchFileDetails(savedDataFilepath, deleteFileReq);
        if (fileDetail == null || !fileDetail.isValid()) {
            logger.info("Invalid fileDetails: {}", fileDetail);
            fileDetail = this.generateFileDetailsFromFilepath(parsedFileStr.get(AppConstant.FILE_USERNAME),
                    parsedFileStr.get(AppConstant.FILE_NAME_STR), "deleteMigration");
        }
        if (fileDetail.isDeletedTrue()) {
            logger.info("file already deleted: {}", fileDetail);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        FileDeleteAccess deleteAccess = fileDetail.getDeleteAccess();
        if (deleteAccess == null) {
            logger.info("deleteAccess of file is null: {}", fileDetail);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        if (deleteAccess == FileDeleteAccess.SELF) {
            this.deleteFileByUser(loginUserDetails, fileDetail);
        } else if (deleteAccess == FileDeleteAccess.ADMIN) {
            if (loginUserDetails.getLoginUserAdmin()) {
                this.deleteFileV3(fileDetail);
            } else {
                logger.info("Only admin is allowed delete this file: {}, currentUser: {}",
                        fileDetail, loginUserDetails);
                throw new AppException((ErrorCodes.UNAUTHORIZED_USER));
            }
        } else if (deleteAccess == FileDeleteAccess.SELF_ADMIN) {
            if (loginUserDetails.getLoginUserAdmin()) {
                this.deleteFileV3(fileDetail);
            } else {
                this.deleteFileByUser(loginUserDetails, fileDetail);
            }
        }
        this.addTextInFileDetailForDelete(loginUserDetails, fileDetail);
    }
    public Object handleDefaultUrl(HttpServletRequest request, UserService userService) {
        String requestedPath = StaticService.getPathUrl(request);
        logger.info("Loading defaultMethod: {}, user: {}",
                requestedPath, userService.getUserDataForLogging(request));

        PathInfo pathInfo = this.getFileResponse(requestedPath);
        Response.ResponseBuilder r;
        if (pathInfo!= null && AppConstant.FILE.equals(pathInfo.getType())) {
            File file = new File(pathInfo.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                r = Response.ok(inputStream);
                if (pathInfo.getMediaType() == null) {
                    logger.info("MediaType is not found (download now): {}", pathInfo);
                    String responseHeader = "attachment; filename=" + pathInfo.getFileName();
                    r.header(HttpHeaders.CONTENT_DISPOSITION, responseHeader);
                } else {
                    r.header(HttpHeaders.CONTENT_TYPE, pathInfo.getMediaType());
                }
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        return new CommonView(request, "page_not_found_404.ftl");
    }
    private PathInfo getFileResponse(String filePath) {
        String publicDir = appConfig.getPublicDir();
        if (publicDir == null) {
            return null;
        }
        filePath = publicDir + filePath;
        PathInfo pathInfo = fileService.getPathInfo(filePath);
        if (AppConstant.FOLDER.equals(pathInfo.getType())) {
            pathInfo = fileService.searchIndexHtmlInFolder(pathInfo);
        }
        logger.info("PathDetails: {}", pathInfo);
        return pathInfo;
    }
    public PathInfo doUpload(InputStream uploadedInputStream, String fileName) throws AppException {
        PathInfo pathInfo = fileService.getPathInfo(fileName);
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Filename: {}, already exist, re-naming it. {}", fileName, pathInfo);
            String ext = pathInfo.getExtension();
            String parentFolder = pathInfo.getParentFolder();
            String currentFileName = pathInfo.getFileName();
            String newFileName = pathInfo.getFilenameWithoutExt() + "-Copy." + ext;
            Boolean renameStatus = fileService.renameExistingFile(parentFolder, currentFileName, newFileName);
            if (!renameStatus) {
                String timeInMs = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat);
                newFileName = pathInfo.getFilenameWithoutExt() + "-" + timeInMs + "." + ext;
                fileService.renameExistingFile(parentFolder, currentFileName, newFileName);
            }
        }
        Integer maxFileSize = appConfig.getFtpConfiguration().getMaxFileSize();
        pathInfo = fileService.uploadFile(uploadedInputStream, fileName, maxFileSize);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Error in uploading file pathInfo: {}", pathInfo);
            throw new AppException(ErrorCodes.INVALID_USER_NAME);
        } else {
            logger.info("uploaded file pathInfo: {}", pathInfo);
            String filePath = parseUserFileName(pathInfo.getPath());
            if (filePath == null) {
                logger.info("File uploaded in wrong directory: {}", pathInfo);
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }
            pathInfo.setPath(filePath);
            pathInfo.setParentFolder(null);
        }
        return pathInfo;
    }
    public ApiResponse uploadFile(HttpServletRequest request, UserService userService,
                                  InputStream uploadedInputStream, String fileName) throws AppException {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (!loginUserDetails.getLogin()) {
            logger.info("UnAuthorised user trying to upload file: {}", fileName);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        String loginUserName = loginUserDetails.getUsername();
        PathInfo pathInfo = fileService.getPathInfoFromFileName(fileName);
        logger.info("PathInfo generated from request filename: {}, {}", fileName, pathInfo);
        String ext = pathInfo.getExtension();
        ArrayList<String> supportedFileType = appConfig.getFtpConfiguration().getSupportedFileType();
        if (supportedFileType == null) {
            logger.info("Config error, supportedFileType is Null.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (ext == null || !supportedFileType.contains(ext.toLowerCase())) {
            logger.info("File extension '{}', is not supported", ext);
            throw new AppException(ErrorCodes.UNSUPPORTED_FILE_TYPE);
        }
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        HashMap<String, String> values = new HashMap<>();
        values.put("username", loginUserName);
        values.put("filename", pathInfo.getFilenameWithoutExt());
        String uploadingFileName = dir + loginUserName + "/" +
                StaticService.generateStringFromFormat(appConfig, values) + "." + pathInfo.getExtension();
        if (parseUserFileName(uploadingFileName) == null) {
            logger.info("Invalid upload filepath: {}", uploadingFileName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        boolean dirStatus = true;
        if (!fileService.isDirectory(dir+loginUserName)) {
            dirStatus = fileService.createFolder(dir, loginUserName);
        }
        if (dirStatus) {
            pathInfo = this.doUpload(uploadedInputStream, uploadingFileName);
        } else {
            logger.info("Error in creating directory for username: {}", loginUserName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        this.generateFileDetailsFromFilepath(loginUserName, pathInfo.getFileName(), "upload");
        return new ApiResponse(pathInfo);
    }
}
