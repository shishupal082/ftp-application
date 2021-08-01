package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.PathType;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.obj.yamlObj.FtlConfig;
import com.project.ftp.obj.yamlObj.Page404Entry;
import com.project.ftp.obj.yamlObj.PageConfig404;
import com.project.ftp.parser.JsonFileParser;
import com.project.ftp.parser.TextFileParser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileServiceV2 {
    private final static Logger logger = LoggerFactory.getLogger(FileServiceV2.class);
    private final AppConfig appConfig;
    private final FileService fileService;
    private final UserService userService;
    public FileServiceV2(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        this.userService = userService;
    }
    private String parseUserFileName(String fileName) {
        String userFilename = null;
        if (fileName == null) {
            return null;
        }
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        if (dir == null) {
            logger.info("fileSaveDir is: null");
            return null;
        }
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
                    fileName = this.parseUserFileName(scanResult.getPathName());
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
                                                                  LoginUserDetails loginUserDetails) {
        if (res == null || loginUserDetails == null) {
            return null;
        }
        ArrayList<ResponseFilesInfo> finalResponse = new ArrayList<>();
        HashMap<String, String> parsedData;
        String fileUsername, filenameStr;
        for (String filepath: res) {
            parsedData = this.parseRequestedFileStr(filepath);
            if (AppConstant.SUCCESS.equals(parsedData.get(AppConstant.STATUS))) {
                fileUsername = parsedData.get(AppConstant.FILE_USERNAME);
                filenameStr = parsedData.get(AppConstant.FILE_NAME_STR);
                finalResponse.add(new ResponseFilesInfo(fileUsername, filenameStr, loginUserDetails));
            }
        }
        return finalResponse;
    }
    public ApiResponse scanCurrentUserDirectory(LoginUserDetails loginUserDetails) throws AppException {
        ArrayList<ScanResult> scanResults = new ArrayList<>();
        String dir = appConfig.getFtpConfiguration().getFileSaveDir();
        if (dir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        String loginUserName = loginUserDetails.getUsername();
        dir = dir + loginUserName + "/";
        scanResults.add(fileService.scanDirectory(dir, dir, false));
        ArrayList<String> response = new ArrayList<>();
        this.generateApiResponse(scanResults, response);
        logger.info("scanUserDirectory result size: {}", response.size());
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(response, loginUserDetails);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse scanUserDirectory(LoginUserDetails loginUserDetails) throws AppException {
        ArrayList<String> response = this.getUsersFilePath(loginUserDetails);
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(response, loginUserDetails);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse scanUserDirectoryByPattern(LoginUserDetails loginUserDetails,
                                                  String filenamePattern, String usernamePattern)
            throws AppException {
        ArrayList<String> responseFilenames = this.getUsersFilePath(loginUserDetails);
        ArrayList<String> filterFileName = this.filterFilename(responseFilenames, filenamePattern, usernamePattern);
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(filterFileName, loginUserDetails);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public PathInfo getUserCsvData(LoginUserDetails loginUserDetails) throws AppException {
        ArrayList<String> responseFilenames = this.getUsersFilePath(loginUserDetails);
        ArrayList<String> filterFilenames =
                this.filterFilename(responseFilenames, AppConstant.CSV_FILENAME_REGEX, AppConstant.ALL_STRING_REGEX);
        return this.getFinalPathInfo(loginUserDetails, filterFilenames, AppConstant.EmptyStr);
    }
    private ArrayList<String> getUsersFilePath(LoginUserDetails loginUserDetails) {
        ArrayList<ScanResult> scanResults = new ArrayList<>();
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        ArrayList<String> response = new ArrayList<>();
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            return response;
        }
        String scanDir;
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
        this.generateApiResponse(scanResults, response);
        logger.info("scanUserDirectory complete, result size: {}", response.size());
        return response;
    }
    private PathInfo getFinalPathInfo(LoginUserDetails loginUserDetails,
                                      ArrayList<String> responseFilenames,
                                      String tempFileName) throws AppException {
        if (responseFilenames == null) {
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        logger.info("scanUserDirectory result size: {}", responseFilenames.size());
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        StringBuilder textData = new StringBuilder();
        for (String filename: responseFilenames) {
            textData.append(new TextFileParser(saveDir + filename).getTextDataV2());
        }
        String loginUserName = loginUserDetails.getUsername();
        ArrayList<String> requiredDirs = new ArrayList<>();
        requiredDirs.add(saveDir);
        requiredDirs.add(AppConstant.TEMP);
        requiredDirs.add(loginUserName);
        String trashV2Folder = fileService.createDir(requiredDirs);
        if (trashV2Folder == null) {
            logger.info("Error in creating temp folder for user: {}", requiredDirs);
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        String responseFilename = StaticService.getProperDirString(String.join("/", requiredDirs))
                + "/" + loginUserName + tempFileName +".txt";
        fileService.deleteFileV2(responseFilename);
        boolean createStatus, addTextStatus = false;
        createStatus = fileService.createNewFile(responseFilename);
        if (createStatus) {
            addTextStatus = new TextFileParser(responseFilename, true).addText(textData.toString());
        }
        if (createStatus && addTextStatus) {
            return fileService.getPathInfo(responseFilename);
        }
        logger.info("Error in creating response file: {}", responseFilename);
        throw new AppException(ErrorCodes.RUNTIME_ERROR);
    }
    private ArrayList<String> filterFilename(ArrayList<String> responseFilenames,
                                             String filenamePattern, String usernamePattern) {
        ArrayList<String> filterFileName = new ArrayList<>();
        if (responseFilenames == null) {
            return filterFileName;
        }
        for (String filename: responseFilenames) {
            if (filename == null) {
                continue;
            }
            String[] fileNameArr = filename.split("/");
            if(fileNameArr.length == 2) {
                if (StaticService.isPatternMatching(fileNameArr[1], filenamePattern, false)) {
                    if (StaticService.isPatternMatching(fileNameArr[0], usernamePattern, false)) {
                        filterFileName.add(filename);
                    }
                }
            }
        }
        return filterFileName;
    }
    public PathInfo getUserDataByFilenamePattern(LoginUserDetails loginUserDetails,
                                                 String filenamePattern, String usernamePattern,
                                                 String tempFileName)
            throws AppException {
        ArrayList<String> responseFilenames = this.getUsersFilePath(loginUserDetails);
        ArrayList<String> filterFileName = this.filterFilename(responseFilenames, filenamePattern, usernamePattern);
        return this.getFinalPathInfo(loginUserDetails, filterFileName, tempFileName);
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
        if (filePath == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        PathInfo pathInfo;
        if (AppConstant.SUCCESS.equals(parsedFileStr.get(AppConstant.STATUS))) {
            filePath += filename;
            pathInfo = fileService.getPathInfo(filePath);
            if (!AppConstant.FILE.equals(pathInfo.getType())) {
                logger.info("file not found: {}", pathInfo);
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }
            // Now file exist, checking for valid permission
            String fileUsername = parsedFileStr.get(AppConstant.FILE_USERNAME);
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
    private void deleteFile(String fileUsername, String filename) throws AppException {
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        PathInfo pathInfo = fileService.getPathInfo(saveDir + fileUsername + "/" + filename);
        Boolean permanentlyDeleteFile = appConfig.getFtpConfiguration().getPermanentlyDeleteFile();
        Boolean fileDeleteStatus;
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            if (permanentlyDeleteFile != null && permanentlyDeleteFile) {
                logger.info("Permanently deleting file: {}", pathInfo);
                fileDeleteStatus = fileService.deleteFileV2(pathInfo.getPath());
            } else {
                ArrayList<String> requiredDirs = new ArrayList<>();
                requiredDirs.add(saveDir);
                requiredDirs.add(AppConstant.TRASH);
                requiredDirs.add(fileUsername);
                String trashFolder = fileService.createDir(requiredDirs);
                if (trashFolder == null) {
                    logger.info("Error in creating trash folder for user: {}", fileUsername);
                    throw new AppException(ErrorCodes.RUNTIME_ERROR);
                }
                String currentFolder = pathInfo.getParentFolder();
                fileDeleteStatus = fileService.moveFile(currentFolder, trashFolder,
                        pathInfo.getFilenameWithoutExt(), pathInfo.getExtension());
            }
            if (!fileDeleteStatus) {
                logger.info("Error in deleting requested file: {}", pathInfo.getPath());
                throw new AppException(ErrorCodes.RUNTIME_ERROR);
            } else {
                logger.info("Requested file deleted: {}", pathInfo.getPath());
            }
        } else {
            logger.info("Requested deleteFile: {}, does not exist.", pathInfo.getPath());
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
    }
    private boolean isFileDeleteAllowed(LoginUserDetails loginUserDetails,
                                        String fileUsername, String filename) throws AppException {
        if (fileUsername != null && fileUsername.equals(loginUserDetails.getUsername())) {
            boolean isDeleteEnable = userService.isAuthorised(loginUserDetails, AppConstant.IS_DELETE_FILE_ENABLE);
            if (isDeleteEnable) {
                ArrayList<String> lockFileNamePattern = appConfig.getFtpConfiguration().getLockFileNamePattern();
                if (lockFileNamePattern != null) {
                    for (String pattern : lockFileNamePattern) {
                        if (StaticService.isPatternMatching(filename, pattern, false)) {
                            logger.info("isFileDeleteAllowed: false, file locked: filename: {}, pattern: {}",
                                    filename, pattern);
                            throw new AppException(ErrorCodes.FILE_DELETE_LOCKED);
                        }
                    }
                }
            } else {
                logger.info("isFileDeleteAllowed: false, user UnAuthorised: {}", loginUserDetails);
                throw new AppException(ErrorCodes.FILE_DELETE_UNAUTHORISED);
            }
        } else {
            logger.info("isFileDeleteAllowed: false, fileUsername and loginUsername not matching: {}, {}",
                    fileUsername, loginUserDetails);
            throw new AppException(ErrorCodes.FILE_DELETE_UNAUTHORISED);
        }
        logger.info("isFileDeleteAllowed: true, {}/{}", fileUsername, filename);
        return true;
    }
    public void deleteRequestFile(LoginUserDetails loginUserDetails,
                                    RequestDeleteFile deleteFile) throws AppException {
        // Throw error if invalid request
        HashMap<String, String> parsedFileStr = this.verifyDeleteRequestParameters(deleteFile);
        String deleteFileReq = deleteFile.getFilename();
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        if (saveDir == null) {
            logger.info("fileSaveDir is null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        String filepath = saveDir + deleteFileReq;
        // file not found
        if (!fileService.isFile(filepath)) {
            logger.info("requested delete file not found: {}", filepath);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        // file found
        // Throw error if file delete not allowed
        boolean isFileDeleteAllowed = this.isFileDeleteAllowed(loginUserDetails,
                parsedFileStr.get(AppConstant.FILE_USERNAME), parsedFileStr.get(AppConstant.FILE_NAME_STR));
        if (isFileDeleteAllowed) {
            this.deleteFile(parsedFileStr.get(AppConstant.FILE_USERNAME), parsedFileStr.get(AppConstant.FILE_NAME_STR));
        }
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
    private Page404Entry getFinal404Entry(HashMap<String, Page404Entry> pageMapping, String requestPath) {
        Page404Entry page404Entry = null;
        boolean isPatternMatch;
        if (pageMapping != null && requestPath != null) {
            page404Entry = pageMapping.get(requestPath);
            if (page404Entry == null) {
                for(Map.Entry<String, Page404Entry> el: pageMapping.entrySet()) {
                    isPatternMatch = StaticService.isPatternMatching(requestPath, el.getKey(), true);
                    if (isPatternMatch) {
                        logger.info("url-pattern matched: {}, {}", el.getKey(), requestPath);
                        return el.getValue();
                    }
                }
            }
        }
        return page404Entry;
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
                page404Entry = this.getFinal404Entry(pageMapping, requestPath);
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
        PageConfig404 pageConfig404 = appConfig.getPageConfig404();
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
    public PathInfo doUpload(InputStream uploadedInputStream, String fileName, String orgFilename,  int count) throws AppException {
        PathInfo pathInfo = fileService.getPathInfo(fileName);
        PathInfo orgPathInfo = fileService.getPathInfo(orgFilename);
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Filename: {}, already exist: {}", fileName + ":" + count, pathInfo);
            String parentFolder = orgPathInfo.getParentFolder();
            String ext = orgPathInfo.getExtension();
            String newFileName = parentFolder + "/" + orgPathInfo.getFilenameWithoutExt() + "-"+count+"." + ext;
            return this.doUpload(uploadedInputStream,  newFileName, orgFilename, count+1);
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
        String saveDir = appConfig.getFtpConfiguration().getFileSaveDir();
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
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
        HashMap<String, String> values = new HashMap<>();
        values.put("username", loginUserName);
        values.put("filename", pathInfo.getFilenameWithoutExt());
        String uploadingFileName = saveDir + loginUserName + "/" +
                StaticService.generateStringFromFormat(appConfig, values) + "." + pathInfo.getExtension();
        if (this.parseUserFileName(uploadingFileName) == null) {
            logger.info("Invalid upload filepath: {}", uploadingFileName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        boolean dirStatus = true;
        if (!fileService.isDirectory(saveDir+loginUserName)) {
            dirStatus = fileService.createFolder(saveDir, loginUserName);
        }
        if (dirStatus) {
            pathInfo = this.doUpload(uploadedInputStream, uploadingFileName, uploadingFileName,1);
        } else {
            logger.info("Error in creating directory for username: {}", loginUserName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        return pathInfo;
    }
    public ApiResponse uploadFileV1(String loginUsername,
                                  InputStream uploadedInputStream, String fileName) throws AppException {
        PathInfo pathInfo = this.uploadFile(loginUsername, uploadedInputStream, fileName);
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
            throw new AppException(ErrorCodes.FILE_UPLOAD_UNAUTHORISED);
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
        if (StaticService.isInValidString(heading)) {
            logger.info("fileUpload heading is invalid: {}", heading);
            throw new AppException(ErrorCodes.UPLOAD_FILE_HEADING_REQUIRED);
        }
        PathInfo pathInfo = this.uploadFile(loginUsername, uploadedInputStream, fileName);
        return new ApiResponse(pathInfo);
    }
    private String getAddTextV2TimeStamp() {
        String timeStampPattern;
        timeStampPattern = appConfig.getFtpConfiguration().getAddTextV2TimeStamp();
        if (timeStampPattern != null && !timeStampPattern.isEmpty()) {
            return timeStampPattern;
        }
        return AppConstant.DateTimeFormat7;
    }
    public ApiResponse addText(LoginUserDetails userDetails, RequestAddText addText, boolean isV2) throws AppException {
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
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
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
            String currentTimeStamp, timeStampPattern;
            timeStampPattern = this.getAddTextV2TimeStamp();
            for (String str : textData) {
                if (isV2) {
                    currentTimeStamp = StaticService.getDateStrFromPattern(timeStampPattern);
                    textFileParser.addText(currentTimeStamp + str);
                } else {
                    textFileParser.addText(str);
                }
            }
            return new ApiResponse();
        }
        logger.info("Error in adding text filePath: {}, data: {}", filePath, addText);
        throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
    }
    public ApiResponse getStaticData() {
        JsonFileParser jsonFileParser = new JsonFileParser(appConfig);
        AppStaticData appStaticData = new AppStaticData();
        try {
            appStaticData.setJsonFileData(jsonFileParser.getJsonObject());
        } catch (AppException ae) {
            logger.info("Error in reading app static file: {}", ae.getErrorCode().getErrorCode());
        }
        appStaticData.setAppVersion(AppConstant.AppVersion);
        appStaticData.setUploadFileApiVersion(appConfig.getFtpConfiguration().getUploadFileApiVersion());
        FtlConfig ftlConfig = appConfig.getFtpConfiguration().getFtlConfig();
        if (ftlConfig != null) {
            appStaticData.setDisplayCreatePasswordLinkEnable(ftlConfig.getDisplayCreatePasswordLinkEnable());
            appStaticData.setHeadingJson(ftlConfig.getHeadingJson());
            appStaticData.setAfterLoginLinkJson(ftlConfig.getAfterLoginLinkJson());
            appStaticData.setPageNotFoundJson(ftlConfig.getPageNotFoundJson());
            appStaticData.setFooterLinkJson(ftlConfig.getFooterLinkJson());
            appStaticData.setFooterLinkJsonAfterLogin(ftlConfig.getFooterLinkJsonAfterLogin());
        }
        return new ApiResponse(appStaticData);
    }
}
