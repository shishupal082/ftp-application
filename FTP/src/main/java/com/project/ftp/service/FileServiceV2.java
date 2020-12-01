package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FileDeleteAccess;
import com.project.ftp.config.PathType;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.parser.YamlFileParser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileServiceV2 {
    private final static Logger logger = LoggerFactory.getLogger(FileServiceV2.class);
    private final AppConfig appConfig;
    private final FileService fileService;
    private final UserService userService;
    final String savedDataFilepath;
    public FileServiceV2(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        this.userService = userService;
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
                    this.generateApiResponse(scanResult.getScanResults(), response);
                }
            }
        }
    }
    private ArrayList<ResponseFilesInfo> generateFileInfoResponse(ArrayList<String> res,
                                                                  LoginUserDetails loginUserDetails,
                                                                  boolean isLoginUserAdmin) {
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
            //Update delete access
            if (fileDetail == null) {
                parsedData = this.parseRequestedFileStr(filepath);
                if (AppConstant.SUCCESS.equals(parsedData.get(AppConstant.STATUS))) {
                    fileUsername = parsedData.get(AppConstant.FILE_USERNAME);
                    filenameStr = parsedData.get(AppConstant.FILE_NAME_STR);
                    fileDetail = new FileDetail(filenameStr, fileUsername,
                            FileDeleteAccess.SELF, "getFileSInfo");
                } else {
                    continue;
                }
            }
            fileDetail.setIsDeleted(AppConstant.FALSE);
            finalResponse.add(new ResponseFilesInfo(isLoginUserAdmin, fileDetail, loginUserDetails));
        }
        return finalResponse;
    }
    public ApiResponse scanCurrentUserDirectory(LoginUserDetails loginUserDetails) {
        ArrayList<ScanResult> scanResults = new ArrayList<>();
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        String loginUserName = loginUserDetails.getUsername();
        dir = dir + loginUserName + "/";
        scanResults.add(fileService.scanDirectory(dir, dir, false));
        ArrayList<String> response = new ArrayList<>();
        this.generateApiResponse(scanResults, response);
        logger.info("scanUserDirectory result size: {}", response.size());
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(response, loginUserDetails,false);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse scanUserDirectory(LoginUserDetails loginUserDetails) {
        ApiResponse apiResponse;
        ArrayList<ScanResult> scanResults = new ArrayList<>();
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir(), scanDir;
        String loginUserName = loginUserDetails.getUsername();
        boolean isLoginUserAdmin = userService.isLoginUserAdmin(loginUserDetails);
        if (isLoginUserAdmin) {
            scanResults.add(fileService.scanDirectory(saveDir, saveDir, true));
        } else {
            ArrayList<String> relatedUsers = userService.getRelatedUsers(loginUserName);
            for (String username: relatedUsers) {
                scanDir = saveDir + username + "/";
                scanResults.add(fileService.scanDirectory(scanDir, scanDir, false));
            }
        }
        ArrayList<String> response = new ArrayList<>();
        this.generateApiResponse(scanResults, response);
        logger.info("scanUserDirectory result size: {}", response.size());
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(response, loginUserDetails, isLoginUserAdmin);
        logger.info("final result size: {}", filesInfo.size());
        apiResponse = new ApiResponse(filesInfo);
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
    public PathInfo searchRequestedFile(LoginUserDetails loginUserDetails,
                                        final UserService userService,
                                        String filename) throws AppException {
        if (filename == null) {
            logger.info("filename can not be null");
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
        }
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
    public PathInfo searchRequestedFileV2(LoginUserDetails loginUserDetails,
                                          String filename) throws AppException {
        if (filename == null) {
            logger.info("filename can not be null");
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
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
                fileDetail = this.generateFileDetailsFromFilepath(fileUsername, filenameStr,"viewFile");
            }
            // i.e. file is deleted by user and again manually copied to server
            if (fileDetail.isDeletedTrue()) {
                logger.info("file deleted entry, but file is there: {}", fileDetail);
            }
            if (!userService.isLoginUserAdmin(loginUserDetails)) {
                ArrayList<String> relatedUsers = userService.getRelatedUsers(loginUserName);
                // Need not to check public separately
                if (!relatedUsers.contains(fileUsername)) {
                    logger.info("Unauthorised access loginUserName: {}, filename: {}",
                            loginUserName, filename);
                    throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
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
    public void deleteRequestFile(LoginUserDetails loginUserDetails,
                                    UserService userService,
                                    RequestDeleteFile deleteFile) throws AppException {
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
    private HashMap<String, String> verifyDeleteRequestParameters(RequestDeleteFile deleteFile) throws AppException {
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
        String filename = StaticService.replaceComma(fileDetail.getFilename());
        String deletedBy = userDetails.getUsername();
        FileDetail finalFileDetail = new FileDetail(filename, uploadedBy, deletedBy);
        if (!finalFileDetail.isValid()) {
            logger.info("Invalid file details: {}", finalFileDetail);
        } else {
            finalFileDetail.setSubject(fileDetail.getSubject());
            finalFileDetail.setHeading(fileDetail.getHeading());
            finalFileDetail.setDeleteAccess(fileDetail.getDeleteAccess());
        }
        if (!fileDetail.isValid()) {
            logger.info("Invalid file delete data: {}", fileDetail);
        }
        fileService.saveFileDetails(savedDataFilepath, finalFileDetail);
    }
    private void deleteFileV3(FileDetail fileDetail) throws AppException {
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        PathInfo pathInfo = fileService.getPathInfo(saveDir + fileDetail.getFilepath());
        boolean permanentlyDeleteFile = appConfig.getFtpConfiguration().isPermanentlyDeleteFile();
        Boolean fileDeleteStatus;
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            if (permanentlyDeleteFile) {
                logger.info("Permanently deleting file: {}", fileDetail);
                fileDeleteStatus = fileService.deleteFileV2(pathInfo.getPath());
            } else {
                ArrayList<String> requiredDirs = new ArrayList<>();
                requiredDirs.add(saveDir);
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
    // uploadFileV1
    private FileDetail generateFileDetailsFromFilepath(String fileUsername, String filename,
                                                       String entryType) {
        FileDeleteAccess deleteAccess = StaticService.getFileDeleteAccessV2(appConfig);
        return new FileDetail(filename, fileUsername, deleteAccess, entryType);
    }
    // uploadFileV2
    private void generateFileDetailsFromFilepathV2(String loginUsername, String filename,
                                                       String subject, String heading) {
        FileDeleteAccess deleteAccess = StaticService.getFileDeleteAccessV2(appConfig);
        FileDetail fileDetail = new FileDetail(filename, loginUsername,
                                    subject, heading, deleteAccess);
        fileService.saveFileDetails(savedDataFilepath, fileDetail);
    }
    public void deleteRequestFileV2(LoginUserDetails loginUserDetails,
                                    RequestDeleteFile deleteFile) throws AppException {
        HashMap<String, String> parsedFileStr = this.verifyDeleteRequestParameters(deleteFile);
        String deleteFileReq = deleteFile.getFilename();
        String filepath = appConfig.getFtpConfiguration().getFileSaveDir() + deleteFileReq;
        // file not found
        if (!fileService.isFile(filepath)) {
            logger.info("requested delete file not found: {}", filepath);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        FileDetail fileDetail = fileService.searchFileDetails(savedDataFilepath, deleteFileReq);
        if (fileDetail == null || !fileDetail.isValid()) {
            logger.info("fileDetails not found for file path: {}, {}", deleteFileReq, fileDetail);
            fileDetail = this.generateFileDetailsFromFilepath(parsedFileStr.get(AppConstant.FILE_USERNAME),
                    parsedFileStr.get(AppConstant.FILE_NAME_STR), "deleteRequest");
        }
        // file found and file details found or not, does not matter
        FileDeleteAccess deleteAccess = fileDetail.getDeleteAccess();
        if (deleteAccess == null) {
            logger.info("deleteAccess of file is null: {}", fileDetail);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        boolean isLoginUserAdmin = userService.isLoginUserAdmin(loginUserDetails);
        if (deleteAccess == FileDeleteAccess.SELF) {
            this.deleteFileByUser(loginUserDetails, fileDetail);
        } else if (deleteAccess == FileDeleteAccess.ADMIN) {
            if (isLoginUserAdmin) {
                this.deleteFileV3(fileDetail);
            } else {
                logger.info("Only admin is allowed delete this file: {}, currentUser: {}",
                        fileDetail, loginUserDetails);
                throw new AppException((ErrorCodes.UNAUTHORIZED_USER));
            }
        } else if (deleteAccess == FileDeleteAccess.SELF_ADMIN) {
            if (isLoginUserAdmin) {
                this.deleteFileV3(fileDetail);
            } else {
                this.deleteFileByUser(loginUserDetails, fileDetail);
            }
        }
        this.addTextInFileDetailForDelete(loginUserDetails, fileDetail);
    }
    // By default folder is authorised
    private boolean isFolderAuthorised(LoginUserDetails userDetails,
                                       PageConfig404 pageConfig404, String fileParentFolder) {
        String publicDir = appConfig.getPublicDir();
        if (publicDir == null) {
            return  true;
        }
        if (fileParentFolder == null) {
            return true;
        }
        fileParentFolder = String.join("/", fileParentFolder.split(publicDir));
        fileParentFolder = StaticService.getProperDirString(fileParentFolder);
        if (pageConfig404 == null || pageConfig404.getPageMapping404() == null) {
            return true;
        }
        Page404Entry page404Entry1;
        String[] filenameArr = StaticService.splitStringOnLimit(fileParentFolder, "/", -1);
        String folderPath = "", rollAccess;
        for (int i=0; i<filenameArr.length; i++) {
            folderPath += filenameArr[i]+ "/";
            page404Entry1 = pageConfig404.getPageMapping404().get(folderPath);
            if (page404Entry1 != null) {
                rollAccess = page404Entry1.getRoleAccess();
                if (!userService.isAuthorised(userDetails, rollAccess)) {
                    logger.info("folderPath not authorised: {}, {}", folderPath, page404Entry1);
                    return false;
                } else {
                    logger.info("folderPath authorised: {}", folderPath);
                }
            }
        }
        return true;
    }
    private Page404Entry getFileNotFoundMapping(PageConfig404 pageConfig404, String requestPath) {
        String publicDir = appConfig.getPublicDir();
        if (publicDir == null) {
            return  null;
        }
        Page404Entry page404Entry;
        if (pageConfig404 != null) {
            HashMap<String, Page404Entry> pageMapping = pageConfig404.getPageMapping404();
            if (pageMapping != null) {
                page404Entry = pageMapping.get(requestPath);
                if (page404Entry != null && page404Entry.getFileName() != null) {
                    if (fileService.isFile(publicDir + page404Entry.getFileName())) {
                        logger.info("page404Entry found for '{}', {}", requestPath, page404Entry);
                        return page404Entry;
                    } else {
                        logger.info("invalid filename for '{}', {}", requestPath, page404Entry);
                    }
                }
            }
        }
        return null;
    }
    public PathInfo getFileResponse(String filePath, LoginUserDetails userDetails) {
        String publicDir = appConfig.getPublicDir();
        if (publicDir == null) {
            return null;
        }
        if (filePath == null) {
            return null;
        }
        YamlFileParser yamlFileParser = new YamlFileParser();
        PageConfig404 pageConfig404 = yamlFileParser.getPageConfig404(appConfig);
        Page404Entry page404Entry;
        if (pageConfig404 != null) {
            page404Entry = this.getFileNotFoundMapping(pageConfig404, filePath);
            if (page404Entry != null) {
                String rollAccess = page404Entry.getRoleAccess();
                if (StaticService.isValidString(rollAccess)) {
                    if (userService.isAuthorised(userDetails, rollAccess)) {
                        filePath = page404Entry.getFileName();
                    } else {
                        logger.info("unAuthorised page404Entry: {}", page404Entry);
                        page404Entry = this.getFileNotFoundMapping(pageConfig404, AppConstant.UN_AUTHORISED);
                        if (page404Entry != null) {
                            filePath = page404Entry.getFileName();
                        } else {
                            filePath = null;
                        }
                    }
                } else {
                    filePath = page404Entry.getFileName();
                }
            }
        }
        PathInfo pathInfo = null;
        if (StaticService.isValidString(filePath)) {
            pathInfo = fileService.getPathInfo(publicDir + filePath);
            if (AppConstant.FOLDER.equals(pathInfo.getType())) {
                pathInfo = fileService.searchIndexHtmlInFolder(pathInfo);
            }
        }
        if (pathInfo == null || !AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("pathInfo is not found for '{}': searching default404 page.", filePath);
            page404Entry = this.getFileNotFoundMapping(pageConfig404, AppConstant.DEFAULT);
            if (page404Entry != null) {
                pathInfo = fileService.getPathInfo(publicDir + page404Entry.getFileName());
            }
        }
        if (pathInfo != null) {
            if (!this.isFolderAuthorised(userDetails, pageConfig404, pathInfo.getParentFolder())) {
                page404Entry = this.getFileNotFoundMapping(pageConfig404, AppConstant.UN_AUTHORISED);
                if (page404Entry != null) {
                    pathInfo = fileService.getPathInfo(publicDir + page404Entry.getFileName());
                } else {
                    pathInfo = null;
                }
            }
        }
        logger.info("final pathInfo: {}", pathInfo);
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
            String filePath = this.parseUserFileName(pathInfo.getPath());
            if (filePath == null) {
                logger.info("File uploaded in wrong directory: {}", pathInfo);
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }
            pathInfo.setPath(filePath);
            pathInfo.setParentFolder(null);
        }
        return pathInfo;
    }

    private PathInfo uploadFile(String loginUserName,
                               InputStream uploadedInputStream, String fileName) throws AppException {
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
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        HashMap<String, String> values = new HashMap<>();
        values.put("username", loginUserName);
        values.put("filename", pathInfo.getFilenameWithoutExt());
        String uploadingFileName = saveDir + loginUserName + "/" +
                StaticService.generateStringFromFormat(appConfig, values) + "." + pathInfo.getExtension();
        if (parseUserFileName(uploadingFileName) == null) {
            logger.info("Invalid upload filepath: {}", uploadingFileName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        boolean dirStatus = true;
        if (!fileService.isDirectory(saveDir+loginUserName)) {
            dirStatus = fileService.createFolder(saveDir, loginUserName);
        }
        if (dirStatus) {
            pathInfo = this.doUpload(uploadedInputStream, uploadingFileName);
        } else {
            logger.info("Error in creating directory for username: {}", loginUserName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        return pathInfo;
    }
    public ApiResponse uploadFileV1(String loginUsername,
                                  InputStream uploadedInputStream, String fileName) throws AppException {
        PathInfo pathInfo = this.uploadFile(loginUsername, uploadedInputStream, fileName);
        FileDetail fileDetail = this.generateFileDetailsFromFilepath(loginUsername,
                pathInfo.getFileName(), "upload");
        fileService.saveFileDetails(savedDataFilepath, fileDetail);
        return new ApiResponse(pathInfo);
    }
    public ApiResponse uploadFileV2(LoginUserDetails loginUserDetails,
                                  InputStream uploadedInputStream, FormDataContentDisposition fileDetails,
                                  String subject, String heading) throws AppException {
        if (fileDetails == null) {
            logger.info("fileDetails is: null");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String fileName = fileDetails.getFileName();
        if (fileName == null) {
            logger.info("fileName is: null");
            throw new AppException(ErrorCodes.UPLOAD_FILE_FILENAME_REQUIRED);
        }
        boolean isAuthorised = userService.isAuthorised(loginUserDetails, AppConstant.IS_UPLOAD_FILE_ENABLE);
        if (!isAuthorised) {
            logger.info("fileUpload is disabled.");
            throw new AppException(ErrorCodes.FILE_UPLOAD_DISABLED);
        }
        fileName = StaticService.replaceComma(fileName);
        String loginUsername = loginUserDetails.getUsername();
        String apiVersion = StaticService.getUploadFileApiVersion(appConfig);
        if (AppConstant.V1.equals(apiVersion)) {
            logger.info("uploadFileApiVersion is v1");
            return this.uploadFileV1(loginUsername, uploadedInputStream, fileName);
        } else if (!AppConstant.V2.equals(apiVersion)) {
            logger.info("uploadFileApiVersion is not {}: {}", AppConstant.V2, apiVersion);
            throw new AppException(ErrorCodes.UPLOAD_FILE_VERSION_MISMATCH);
        }
        logger.info("uploadFileApiVersion is v2");
        if (StaticService.isInValidString(subject)) {
            logger.info("fileUpload subject is invalid: {}", subject);
            throw new AppException(ErrorCodes.UPLOAD_FILE_SUBJECT_REQUIRED);
        }
        subject = subject.trim();
        if (StaticService.isInValidString(heading)) {
            logger.info("fileUpload heading is invalid: {}", heading);
            throw new AppException(ErrorCodes.UPLOAD_FILE_HEADING_REQUIRED);
        }
        heading = heading.trim();
        PathInfo pathInfo = this.uploadFile(loginUsername, uploadedInputStream, fileName);
        this.generateFileDetailsFromFilepathV2(loginUserDetails.getUsername(),
                pathInfo.getFileName(), subject, heading);
        return new ApiResponse(pathInfo);
    }
    public ApiResponse addText(LoginUserDetails userDetails, RequestAddText addText) throws AppException {
        if (addText == null) {
            logger.info("Invalid addText request: null");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String filename = addText.getFilename();
        String[] textData = addText.getText();
        if (StaticService.isInValidString(filename)) {
            logger.info("Invalid addText.filename: {}", addText);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (textData == null || textData.length < 1) {
            logger.info("Invalid addText.text: {}", addText);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        boolean isAuthorised = userService.isAuthorised(userDetails, AppConstant.IS_ADD_TEXT_ENABLE);
        if (!isAuthorised) {
            logger.info("addText is disabled.");
            throw new AppException(ErrorCodes.ADD_TEXT_DISABLED);
        }
        String username = userDetails.getUsername();
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        String userDir = saveDir + username + "/";
        String filePath = userDir + filename;
        String userFilename = this.parseUserFileName(filePath);
        if (userFilename == null) {
            logger.info("Invalid final filePath: {}", filePath);
            throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
        }
        if (!fileService.isDirectory(saveDir)) {
            logger.info("Invalid file save dir: {}", saveDir);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        boolean dirExist = true;
        if (!fileService.isDirectory(userDir)) {
            logger.info("Directory: {}, does not exist creating new.", userDir);
            dirExist = fileService.createFolder(saveDir, username);
        }
        if (!dirExist) {
            logger.info("userDir: {}, does not exist", userDir);
            throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
        }
        boolean fileExist = true;
        if (!fileService.isFile(filePath)) {
            fileExist = fileService.createNewFile(filePath);
        }
        if (fileExist) {
            TextFileParser textFileParser = new TextFileParser(filePath);
            for (String str : textData) {
                textFileParser.addText(str);
            }
            return new ApiResponse();
        }
        logger.info("Error in adding text filePath: {}, data: {}", filePath, addText);
        throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
    }
}
