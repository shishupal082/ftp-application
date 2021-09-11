package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
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
    private final FileServiceV3 fileServiceV3;
    private final CsvDbTable csvDbTable;
    public FileServiceV2(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        this.userService = userService;
        this.fileServiceV3 = new FileServiceV3(appConfig, userService);
        this.csvDbTable = new CsvDbTable(appConfig, userService);
    }
    private String parseUserFileName(String fileSaveDir, String fileName) {
        return fileServiceV3.parseUserFileName(fileSaveDir, fileName);
    }
    private ArrayList<ResponseFilesInfo> generateFileInfoResponse(ArrayList<String> res,
                                                          LoginUserDetails loginUserDetails, boolean addDatabasePath) {
        return fileServiceV3.generateFileInfoResponse(res, loginUserDetails, addDatabasePath);
    }
    public ApiResponse scanCurrentUserDirectory(LoginUserDetails loginUserDetails) throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> response =
                fileServiceV3.getCurrentUsersFilePath(loginUserDetails, saveDir, false);
        if (response == null) {
            response = new ArrayList<>();
        }
        logger.info("scanUserDirectory result size: {}", response.size());
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(response, loginUserDetails, false);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse scanUserDirectory(LoginUserDetails loginUserDetails) throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> response = this.getUsersFilePath(loginUserDetails, saveDir, false);
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(response, loginUserDetails, false);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse scanUserDirectoryByPattern(LoginUserDetails loginUserDetails,
                                                  String filenamePattern, String usernamePattern)
            throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> responseFilenames = this.getUsersFilePath(loginUserDetails, saveDir, false);
        ArrayList<String> filterFileName = this.filterFilename(responseFilenames, filenamePattern, usernamePattern);
        ArrayList<ResponseFilesInfo> filesInfo =
                this.generateFileInfoResponse(filterFileName, loginUserDetails, false);
        logger.info("final result size: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse scanUserDatabaseDirectory(LoginUserDetails loginUserDetails, String filenamePattern,
                                                 boolean isAdmin) throws AppException {
        return csvDbTable.scanUserDatabaseDirectory(loginUserDetails, filenamePattern, isAdmin);
    }
    public ApiResponse getTableData(LoginUserDetails loginUserDetails,
                                    String filenames, String tableNames) throws AppException {
        return csvDbTable.getTableData(loginUserDetails, filenames, tableNames);
    }
    public ApiResponse getTableDataV2(LoginUserDetails loginUserDetails,
                                    String filenames, String tableNames) throws AppException {
        return csvDbTable.getTableDataV2(loginUserDetails, filenames, tableNames);
    }
    public PathInfo getUserCsvData(LoginUserDetails loginUserDetails) throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> responseFilenames = this.getUsersFilePath(loginUserDetails, saveDir, false);
        ArrayList<String> filterFilenames =
                this.filterFilename(responseFilenames, AppConstant.CSV_FILENAME_REGEX, AppConstant.ALL_STRING_REGEX);
        return this.getFinalPathInfo(loginUserDetails, filterFilenames, saveDir, AppConstant.EmptyStr);
    }
    public ArrayList<String> getUsersFilePath(LoginUserDetails loginUserDetails,
                                               String saveDir, boolean isAddDatabaseDir) {
        boolean isAdmin = userService.isLoginUserAdmin(loginUserDetails);
        return fileServiceV3.getUsersFilePath(loginUserDetails, saveDir, isAddDatabaseDir, isAdmin);
    }
    private PathInfo getFinalPathInfo(LoginUserDetails loginUserDetails,
                                      ArrayList<String> responseFilenames,
                                      String saveDir, String tempFileName) throws AppException {
        if (responseFilenames == null) {
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        logger.info("scanUserDirectory result size: {}", responseFilenames.size());
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
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> responseFilenames = this.getUsersFilePath(loginUserDetails, saveDir, false);
        ArrayList<String> filterFileName = this.filterFilename(responseFilenames, filenamePattern, usernamePattern);
        return this.getFinalPathInfo(loginUserDetails, filterFileName, saveDir, tempFileName);
    }

    private HashMap<String, String> parseRequestedFileStr(String filename, boolean containsDatabaseDir) {
        return fileServiceV3.parseRequestedFileStr(filename, containsDatabaseDir);
    }
    public PathInfo searchRequestedFileV2(LoginUserDetails loginUserDetails,
                                          String filename) throws AppException {
        if (filename == null) {
            logger.info("filename can not be null");
            throw new AppException(ErrorCodes.INVALID_QUERY_PARAMS);
        }
        String filePath = appConfig.getFileSaveDirV2(loginUserDetails);
        HashMap<String, String> parsedFileStr = this.parseRequestedFileStr(filename, false);
        String loginUserName = loginUserDetails.getUsername();
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
    private HashMap<String, String> verifyDeleteRequestParameters(RequestDeleteFile deleteFile) throws AppException {
        if (deleteFile == null) {
            logger.info("deleteFile request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String deleteFileReq = deleteFile.getFilename();
        HashMap<String, String> parsedFileStr = this.parseRequestedFileStr(deleteFileReq, false);
        if (AppConstant.FAILURE.equals(parsedFileStr.get(AppConstant.STATUS))) {
            logger.info("deleteFile invalid request: {}", deleteFile);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        return parsedFileStr;
    }
    private void deleteFile(String saveDir, String fileUsername,
                            String filename) throws AppException {
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        PathInfo pathInfo = fileService.getPathInfo(saveDir + fileUsername + "/" + filename);
        Boolean fileDeleteStatus;
        if (AppConstant.FILE.equals(pathInfo.getType())) {
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
                logger.info("isFileDeleteAllowed: true, {}/{}", fileUsername, filename);
            } else {
                logger.info("isFileDeleteAllowed: false, user UnAuthorised: {}", loginUserDetails);
                throw new AppException(ErrorCodes.FILE_DELETE_UNAUTHORISED);
            }
        } else {
            logger.info("isFileDeleteAllowed: false, fileUsername and loginUsername not matching: {}, {}",
                    fileUsername, loginUserDetails);
            throw new AppException(ErrorCodes.FILE_DELETE_UNAUTHORISED);
        }
        return true;
    }
    public void deleteRequestFile(LoginUserDetails loginUserDetails,
                                    RequestDeleteFile deleteFile) throws AppException {
        // Throw error if invalid request
        HashMap<String, String> parsedFileStr = this.verifyDeleteRequestParameters(deleteFile);
        String deleteFileReq = deleteFile.getFilename();
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
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
            this.deleteFile(saveDir, parsedFileStr.get(AppConstant.FILE_USERNAME), parsedFileStr.get(AppConstant.FILE_NAME_STR));
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
    public PathInfo doUpload(String fileSaveDir, InputStream uploadedInputStream,
                             String fileName, String orgFilename,  int count) throws AppException {
        PathInfo pathInfo = fileService.getPathInfo(fileName);
        PathInfo orgPathInfo = fileService.getPathInfo(orgFilename);
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Filename: {}, already exist: {}", fileName + ":" + count, pathInfo);
            String parentFolder = orgPathInfo.getParentFolder();
            String ext = orgPathInfo.getExtension();
            String newFileName = parentFolder + "/" + orgPathInfo.getFilenameWithoutExt() + "-"+count+"." + ext;
            return this.doUpload(fileSaveDir, uploadedInputStream,  newFileName, orgFilename, count+1);
        }
        Integer maxFileSize = appConfig.getFtpConfiguration().getMaxFileSize();
        pathInfo = fileService.uploadFile(uploadedInputStream, fileName, maxFileSize);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Error in uploading file pathInfo: {}", pathInfo);
            throw new AppException(ErrorCodes.INVALID_USER_NAME);
        } else {
            logger.info("uploaded file pathInfo: {}", pathInfo);
            String filePath = this.parseUserFileName(fileSaveDir, pathInfo.getPath());
            if (filePath == null) {
                logger.info("File uploaded in wrong directory: {}", pathInfo);
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }
            pathInfo.setPath(filePath);
            pathInfo.setParentFolder(null);
        }
        return pathInfo;
    }
    public ApiResponse addText(LoginUserDetails loginUserDetails, RequestAddText addText) throws AppException {
        return csvDbTable.addText(loginUserDetails, addText);
    }
    public ApiResponse addTextV2(LoginUserDetails loginUserDetails, RequestAddText addText) throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        fileServiceV3.verifyAddTextRequest(addText);
        boolean isAuthorised = userService.isAuthorised(loginUserDetails, AppConstant.IS_ADD_TEXT_ENABLE);
        if (!isAuthorised) {
            logger.info("addText is disabled.");
            throw new AppException(ErrorCodes.ADD_TEXT_DISABLED);
        }
        String username = loginUserDetails.getUsername();
        boolean textAdded = fileServiceV3.saveAddTextV2(saveDir, username, addText);
        if (textAdded) {
            return new ApiResponse();
        }
        logger.info("Error in adding text loginUserDetails: {}, addTex: {}", loginUserDetails, addText);
        throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
    }
    public ApiResponse deleteText(LoginUserDetails loginUserDetails, RequestDeleteText deleteText) throws AppException {
        return csvDbTable.deleteText(loginUserDetails, deleteText);
    }
    private PathInfo uploadFile(LoginUserDetails loginUserDetails, String fileSaveDir,
                               InputStream uploadedInputStream, String fileName) throws AppException {
        if (fileSaveDir == null) {
            logger.info("fileSaveDir is: null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (loginUserDetails == null || loginUserDetails.getUsername().isEmpty()) {
            logger.info("loginUserDetails is invalid: {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
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
        String loginUserName = loginUserDetails.getUsername();
        HashMap<String, String> values = new HashMap<>();
        values.put("username", loginUserName);
        values.put("filename", pathInfo.getFilenameWithoutExt());
        String uploadingFileName = fileSaveDir + loginUserName + "/" +
                StaticService.generateStringFromFormat(appConfig, values) + "." + pathInfo.getExtension();
        if (this.parseUserFileName(fileSaveDir, uploadingFileName) == null) {
            logger.info("Invalid upload filepath: {}", uploadingFileName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        boolean dirStatus = true;
        if (!fileService.isDirectory(fileSaveDir+loginUserName)) {
            dirStatus = fileService.createFolder(fileSaveDir, loginUserName);
        }
        if (dirStatus) {
            pathInfo = this.doUpload(fileSaveDir, uploadedInputStream, uploadingFileName, uploadingFileName,1);
        } else {
            logger.info("Error in creating directory for username: {}", loginUserName);
            throw new AppException(ErrorCodes.INVALID_FILE_SAVE_PATH);
        }
        return pathInfo;
    }
    public ApiResponse uploadFileV2(LoginUserDetails loginUserDetails,
                                  InputStream uploadedInputStream, FormDataContentDisposition fileDetails) throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
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
        PathInfo pathInfo = this.uploadFile(loginUserDetails, saveDir, uploadedInputStream, fileName);
        return new ApiResponse(pathInfo);
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
        FtlConfig ftlConfig = appConfig.getFtpConfiguration().getFtlConfig();
        if (ftlConfig != null) {
            appStaticData.setTitle(ftlConfig.getTitle());
            appStaticData.setHeadingJson(ftlConfig.getHeadingJson());
            appStaticData.setAfterLoginLinkJson(ftlConfig.getAfterLoginLinkJson());
            appStaticData.setPageNotFoundJson(ftlConfig.getPageNotFoundJson());
            appStaticData.setFooterLinkJson(ftlConfig.getFooterLinkJson());
            appStaticData.setFooterLinkJsonAfterLogin(ftlConfig.getFooterLinkJsonAfterLogin());
        }
        return new ApiResponse(appStaticData);
    }
}
