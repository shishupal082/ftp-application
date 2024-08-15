package com.project.ftp.service;

import com.project.ftp.common.StrUtils;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.PathType;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.parser.TextFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;

public class FileServiceV3 {
    private final static Logger logger = LoggerFactory.getLogger(FileServiceV3.class);
    private final AppConfig appConfig;
    private final FileService fileService;
    private final UserService userService;
    private final StrUtils strUtils = new StrUtils();
    public FileServiceV3(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
        this.userService = userService;
        this.fileService = new FileService();
    }
    public String parseUserFileName(String fileSaveDir, String fileName) {
        String userFilename = null;
        if (fileName == null) {
            return null;
        }
        if (fileSaveDir == null) {
            logger.info("fileSaveDir is: null");
            return null;
        }
        String[] fileNameArr = fileName.split(fileSaveDir);
        if(fileNameArr.length == 2) {
            userFilename = fileNameArr[1];
        }
        return userFilename;
    }
    public String parseAssetsDirFilepath(String filePath) {
        if (filePath == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        filePath = strUtils.replaceDynamicPathDir(filePath, "");
        String[] filePathArr = strUtils.stringSplit(filePath, AppConstant.ASSETS_DIR, -1);
        if (filePathArr.length >= 2) {
            for(int i=1; i<filePathArr.length; i++) {
                if (!filePathArr[i].isEmpty()) {
                    if (i>1) {
                        result.append(AppConstant.ASSETS_DIR);
                    }
                    result.append(filePathArr[i]);
                } else {
                    result.append(AppConstant.ASSETS_DIR);
                }
            }
        }
        return result.toString();
    }
    public void generateApiResponse(String fileSaveDir, ArrayList<ScanResult> scanResults,
                                     ArrayList<String> response) {
        if (scanResults == null) {
            return;
        }
        String fileName;
        for (ScanResult scanResult: scanResults) {
            if (scanResult != null) {
                if (PathType.FILE.equals(scanResult.getPathType())) {
                    fileName = this.parseUserFileName(fileSaveDir, scanResult.getPathName());
                    if (fileName != null) {
                        response.add(fileName);
                    }
                } else if (PathType.FOLDER.equals(scanResult.getPathType())) {
                    this.generateApiResponse(fileSaveDir, scanResult.getScanResults(), response);
                }
            }
        }
    }
    public ArrayList<String> removeDeleteFileName(ArrayList<String> response) {
        return this.filterFilename(AppConstant.DELETE_TABLE_FILE_NAME, response, AppConstant.REVERT_RESULT);
    }
    public ArrayList<String> filterFilename(String requiredFilenamePattern,
                                            ArrayList<String> allUserFilename, boolean revertResult) {
        if (requiredFilenamePattern == null || allUserFilename == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        String[] temp;
        boolean isMatched;
        for(String userFilename: allUserFilename) {
            temp = userFilename.split("/");
            if (temp.length == 3) {
                isMatched = StaticService.isPatternMatching(temp[2], requiredFilenamePattern, AppConstant.EXACT_MATCH);
                if (revertResult) {
                    if (!isMatched) {
                        result.add(userFilename);
                    }
                } else if (isMatched) {
                    result.add(userFilename);
                }
            }
        }
        return result;
    }
    public ArrayList<String> getUsersFilePathByRelatedUsers(ArrayList<String> relatedUsers, String saveDir,
                                                boolean isAddDatabaseDir, boolean isAdmin) {
        ArrayList<ScanResult> scanResults = new ArrayList<>();
        ArrayList<String> response = new ArrayList<>();
        if (saveDir == null) {
            logger.info("fileSaveDir is: null");
            return response;
        }
        String scanDir;
        if (isAdmin) {
            scanResults.add(fileService.scanDirectory(saveDir, saveDir, true, isAddDatabaseDir));
        } else if (relatedUsers != null) {
            for (String username: relatedUsers) {
                scanDir = saveDir + username + "/";
                if (isAddDatabaseDir) {
                    scanDir += AppConstant.DATABASE + "/";
                }
                scanResults.add(fileService.scanDirectory(scanDir, scanDir, false, !AppConstant.DB_TRUE));
            }
        }
        this.generateApiResponse(saveDir, scanResults, response);
        return response;
    }
    public ArrayList<String> getUsersFilePath(LoginUserDetails loginUserDetails, String saveDir,
                                              boolean isDBDir, boolean isAdmin) {
        ArrayList<String> response;
        String loginUserName = loginUserDetails.getUsername();
        ArrayList<String> relatedUsers = new ArrayList<>();
        if (isAdmin) {
            response = this.getUsersFilePathByRelatedUsers(relatedUsers, saveDir, isDBDir, AppConstant.ADMIN_TRUE);
        } else {
            relatedUsers = userService.getRelatedUsers(loginUserName);
            response = this.getUsersFilePathByRelatedUsers(relatedUsers, saveDir, isDBDir, !AppConstant.ADMIN_TRUE);
        }
        return response;
    }
    public ArrayList<String> getCurrentUsersFilePath(@NotNull LoginUserDetails loginUserDetails, String saveDir,
                                                     boolean isAddDatabaseDir) {
        ArrayList<String> relatedUsers = new ArrayList<>();
        relatedUsers.add(loginUserDetails.getUsername());
        return this.getUsersFilePathByRelatedUsers(relatedUsers, saveDir, isAddDatabaseDir, !AppConstant.ADMIN_TRUE);
    }
    public HashMap<String, String> parseRequestedFileStr(String filename, boolean containsDatabaseDir) {
        HashMap<String, String> response = new HashMap<>();
        response.put(AppConstant.STATUS, AppConstant.FAILURE);
        if (filename == null) {
            logger.info("filename can not be null");
            return response;
        }
        String[] filenameArr = filename.split("/");
        String fileUsername = null, filenameStr = null;
        if (containsDatabaseDir) {
            if (filenameArr.length == 3 && AppConstant.DATABASE.equals(filenameArr[1])) {
                fileUsername = filenameArr[0];
                filenameStr = filenameArr[2];
            }
        } else if (filenameArr.length == 2) {
            fileUsername = filenameArr[0];
            filenameStr = filenameArr[1];
        }

        if (StaticService.isInValidString(fileUsername)) {
            logger.info("filename does not contain username: {}", filename);
            return response;
        }
        if (StaticService.isInValidString(filenameStr)) {
            logger.info("filename is empty in request: {}", filename);
            return response;
        }
        response.put(AppConstant.STATUS, AppConstant.SUCCESS);
        response.put(AppConstant.FILE_USERNAME, fileUsername);
        response.put(AppConstant.FILE_NAME_STR, filenameStr);
        return response;
    }
    public ArrayList<ResponseFilesInfo> filterResponseFilesInfo(ArrayList<ResponseFilesInfo> filesInfo,
                                                                String filenames) {
        ArrayList<ResponseFilesInfo> result = new ArrayList<>();
        if (filenames == null || StaticService.isInValidString(filenames)) {
            result = filesInfo;
        } else {
            for(ResponseFilesInfo responseFilesInfo:filesInfo) {
                if (StaticService.isPatternMatching(responseFilesInfo.getFilename(), filenames, true)) {
                    result.add(responseFilesInfo);
                }
            }
        }
        return result;
    }
    public ArrayList<ResponseFilesInfo> generateFileInfoResponse(ArrayList<String> res,
                                                                 LoginUserDetails loginUserDetails, boolean addDatabasePath) {
        if (res == null || loginUserDetails == null) {
            return null;
        }
        ArrayList<ResponseFilesInfo> finalResponse = new ArrayList<>();
        HashMap<String, String> parsedData;
        String fileUsername, filenameStr;
        for (String filepath: res) {
            parsedData = this.parseRequestedFileStr(filepath, addDatabasePath);
            if (AppConstant.SUCCESS.equals(parsedData.get(AppConstant.STATUS))) {
                fileUsername = parsedData.get(AppConstant.FILE_USERNAME);
                filenameStr = parsedData.get(AppConstant.FILE_NAME_STR);
                finalResponse.add(new ResponseFilesInfo(fileUsername, filenameStr, loginUserDetails, addDatabasePath));
            }
        }
        return finalResponse;
    }
    public boolean isValidTableRowResponse(TableRowResponse rowResponse) {
        if (rowResponse == null) {
            return false;
        }
        return StaticService.isValidString(rowResponse.getOrgUsername());
    }
    private ArrayList<TableRowResponse> convertTokensToTableRow(ArrayList<ArrayList<String>> tokens,
                                                                String tableFilename) {
        if (tokens == null) {
            return null;
        }
        ArrayList<TableRowResponse> result = new ArrayList<>();
        TableRowResponse temp;
        for(ArrayList<String> token: tokens) {
            temp = new TableRowResponse(token, tableFilename);
            if (this.isValidTableRowResponse(temp)) {
                result.add(temp);
            }
        }
        return result;
    }
    public ArrayList<TableRowResponse> getTableRowResponseByFilePath(String saveDir, ArrayList<String> tableFilenames) {
        if (tableFilenames == null) {
            return null;
        }
        if (StaticService.isInValidString(saveDir)) {
            return null;
        }
        ArrayList<TableRowResponse> finalResult = new ArrayList<>();
        ArrayList<TableRowResponse> temp2;
        ArrayList<ArrayList<String>> temp;
        for (String tableFilename: tableFilenames) {
            TextFileParser textFileParser = new TextFileParser(saveDir + tableFilename);
            temp = textFileParser.readCsvData();
            temp2 = this.convertTokensToTableRow(temp, tableFilename);
            if (temp2 != null) {
                finalResult.addAll(temp2);
            }
        }
        return finalResult;
    }
    public void verifyAddTextRequest(RequestAddText addText) throws AppException {
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
        for (int i=0; i<textData.length; i++) {
            textData[i] = strUtils.replaceString(textData[i], "\n", ";");
        }
        addText.setText(textData);
    }
    public void verifyAddTextRequestV2(String saveDir, LoginUserDetails loginUserDetails,
                                     RequestAddText addText) throws AppException {
        this.verifyAddTextRequest(addText);
        String filename = addText.getFilename();
        if (AppConstant.DELETE_TABLE_FILE_NAME.equals(filename)) {
            logger.info("Invalid addText.filename delete not allowed: {}", addText);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (AppConstant.DELETE_TABLE_NAME.equals(addText.getTableName())) {
            logger.info("Invalid addText.tableName: {}", addText);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = loginUserDetails.getUsername();
        ArrayList<String> requiredDir = new ArrayList<>();
        requiredDir.add(saveDir);
        requiredDir.add(username);
        requiredDir.add(AppConstant.DATABASE);
        String finalDir = fileService.createDir(requiredDir);
        if (StaticService.isInValidString(finalDir)) {
            logger.info("Error in creating required dir: {}", requiredDir);
            throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
        }
        finalDir += "/";
        String filePath = finalDir + filename;
        String userFilename = this.parseUserFileName(saveDir, filePath);
        if (userFilename == null) {
            logger.info("Invalid final filePath: {}", filePath);
            throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
        }
        boolean fileExist = true;
        if (!fileService.isFile(filePath)) {
            fileExist = fileService.createNewFile(filePath);
        }
        if (!fileExist) {
            logger.info("Error in creating text filePath: {}, data: {}", filePath, addText);
            throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
        }
    }
    public boolean isValidAddTextFilename(String filename) {
        ArrayList<String> allowedAddTextFilename = appConfig.getFtpConfiguration().getAllowedTableFilename();
        if (allowedAddTextFilename == null) {
            return false;
        }
        for(String filenamePattern: allowedAddTextFilename) {
            if (StaticService.isPatternMatching(filename, filenamePattern, true)) {
                return true;
            }
        }
        return false;
    }
    public boolean saveAddText(String saveDir, LoginUserDetails loginUserDetails, RequestAddText addText) {
        String loginUsername = loginUserDetails.getUsername();
        String orgUsername = loginUserDetails.getOrgUsername();
        if (StaticService.isInValidString(saveDir) || StaticService.isInValidString(loginUsername) || addText == null) {
            logger.info("Invalid saveDir or loginUsername or addText");
            return false;
        }
        String addTextFilename = addText.getFilename();
        if (!this.isValidAddTextFilename(addTextFilename)) {
            logger.info("Invalid addTextFilename: {}", addTextFilename);
            return false;
        }
        ArrayList<String> requiredDir = new ArrayList<>();
        requiredDir.add(saveDir);
        requiredDir.add(loginUsername);
        requiredDir.add(AppConstant.DATABASE);
        String finalDir = fileService.createDir(requiredDir);
        if (StaticService.isInValidString(finalDir)) {
            logger.info("Error in creating required dir: {}", requiredDir);
            return false;
        }
        finalDir += "/";
        String filePath = finalDir + addTextFilename;
        String userFilename = this.parseUserFileName(saveDir, filePath);
        if (userFilename == null) {
            logger.info("Invalid final filePath: {}", filePath);
            return false;
        }
        boolean fileExist = true;
        if (!fileService.isFile(filePath)) {
            fileExist = fileService.createNewFile(filePath);
        }
        ArrayList<String> finalSavingData;
        if (fileExist) {
            TextFileParser textFileParser = new TextFileParser(filePath);
            String currentTimeStamp;
            currentTimeStamp = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
            finalSavingData = addText.generateAddTextResponse(orgUsername, loginUsername, currentTimeStamp);
            for (String str : finalSavingData) {
                textFileParser.addText(str, false);
            }
            return true;
        }
        return false;
    }
    public boolean saveAddTextV2(String saveDir, String loginUsername, RequestAddText addText) {
        if (StaticService.isInValidString(saveDir) || StaticService.isInValidString(loginUsername) || addText == null) {
            logger.info("Invalid saveDir or loginUsername or addText");
            return false;
        }
        ArrayList<String> requiredDir = new ArrayList<>();
        requiredDir.add(saveDir);
        requiredDir.add(loginUsername);
        String finalDir = fileService.createDir(requiredDir);
        if (StaticService.isInValidString(finalDir)) {
            logger.info("Error in creating required dir: {}", requiredDir);
            return false;
        }
        finalDir += "/";
        String filePath = finalDir + addText.getFilename();
        String userFilename = this.parseUserFileName(saveDir, filePath);
        if (userFilename == null) {
            logger.info("Invalid final filePath: {}", filePath);
            return false;
        }
        String[] text = addText.getText();
        ArrayList<String> textData = new ArrayList<>();
        if (text != null) {
            for (String s : text) {
                if (StaticService.isValidString(s)) {
                    textData.add(s);
                }
            }
        }
        return this.saveAddTextV3(filePath, textData, true);
    }
    public boolean saveAddTextV3(String filePath, ArrayList<String> textData, boolean logFilename) {
        boolean fileExist = true;
        if (!fileService.isFile(filePath)) {
            fileExist = fileService.createNewFile(filePath);
        }
        if (fileExist) {
            TextFileParser textFileParser = new TextFileParser(filePath);
            if (textData != null) {
                for (String s : textData) {
                    if (s != null) {
                        textFileParser.addText(s, logFilename);
                    }
                }
            }
            return true;
        }
        return false;
    }
}
