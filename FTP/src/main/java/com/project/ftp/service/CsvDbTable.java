package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class CsvDbTable {
    private final static Logger logger = LoggerFactory.getLogger(CsvDbTable.class);
    private final AppConfig appConfig;
    private final UserService userService;
    private final FileServiceV3 fileServiceV3;
    public CsvDbTable(AppConfig appConfig, UserService userService) {
        this.appConfig = appConfig;
        this.userService = userService;
        this.fileServiceV3 = new FileServiceV3(appConfig, userService);
    }
    public ArrayList<String> getDeletedIds(String saveDir, LoginUserDetails loginUserDetails, boolean isAdmin) {
        ArrayList<String> response = fileServiceV3.getUsersFilePath(loginUserDetails, saveDir, true, isAdmin);
        ArrayList<String> deleteTableFilenames =
                fileServiceV3.filterFilename(AppConstant.DELETE_TABLE_FILE_NAME, response, false);
        ArrayList<TableRowResponse> rowResponses =
                fileServiceV3.getTableRowResponseByFilePath(saveDir, deleteTableFilenames);
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> text;
        String deleteId;
        if (rowResponses != null) {
            for (TableRowResponse rowResponse: rowResponses) {
                if (fileServiceV3.isValidTableRowResponse(rowResponse)) {
                    text = rowResponse.getText();
                    if (text != null && text.size() > 0) {
                        deleteId = text.get(0);
                        if (StaticService.isValidString(deleteId) && !result.contains(deleteId)) {
                            result.add(deleteId);
                        }
                    }
                }
            }
        }
        return result;
    }
    private ArrayList<TableRowResponse> getTableRowResponses(LoginUserDetails loginUserDetails, String saveDir,
                                                             boolean isAdmin, String filenames, String tableNames) {
        if (StaticService.isInValidString(saveDir)) {
            return null;
        }
        ArrayList<String> response = fileServiceV3.getUsersFilePathV2(loginUserDetails, saveDir, isAdmin);
        ArrayList<String> response2;
        if (response != null && StaticService.isValidString(filenames)) {
            response2 = fileServiceV3.filterFilename(filenames, response, false);
        } else {
            response2 = response;
        }
        ArrayList<TableRowResponse> rowResponses = fileServiceV3.getTableRowResponseByFilePath(saveDir, response2);
        ArrayList<TableRowResponse> finalResponses = new ArrayList<>();
        boolean filterTableName = StaticService.isValidString(tableNames);
        ArrayList<String> deletedIds = this.getDeletedIds(saveDir, loginUserDetails, isAdmin);
        String tempTableName;
        if (rowResponses != null) {
            if (deletedIds == null) {
                deletedIds = new ArrayList<>();
            }
            for (TableRowResponse tableRowResponse: rowResponses) {
                if (!fileServiceV3.isValidTableRowResponse(tableRowResponse)) {
                    continue;
                }
                if (deletedIds.contains(tableRowResponse.getTableUniqueId())) {
                    continue;
                }
                tempTableName = tableRowResponse.getTableName();
                if (AppConstant.DELETE_TABLE_NAME.equals(tempTableName)) {
                    continue;
                }
                if (filterTableName) {
                    if (!StaticService.isPatternMatching(tempTableName, tableNames, true)) {
                        continue;
                    }
                }
                finalResponses.add(tableRowResponse);
            }
        }
        return finalResponses;
    }
    public ApiResponse getTableData(LoginUserDetails loginUserDetails, String filenames,
                                                    String tableNames) throws AppException {
        boolean isAdmin = userService.isLoginUserAdmin(loginUserDetails);
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);// throw error when invalid
        ArrayList<TableRowResponse> rowResponses =
                this.getTableRowResponses(loginUserDetails, saveDir, isAdmin, filenames, tableNames);
        HashMap<String, ArrayList<TableRowResponse>> finalResponse = new HashMap<>();
        String tempTableName;
        ArrayList<TableRowResponse> tableData;
        int count = 0;
        if (rowResponses != null) {
            for (TableRowResponse tableRowResponse: rowResponses) {
                if (!fileServiceV3.isValidTableRowResponse(tableRowResponse)) {
                    continue;
                }
                tempTableName = tableRowResponse.getTableName();
                tableData = finalResponse.computeIfAbsent(tempTableName, k -> new ArrayList<>());
                tableData.add(tableRowResponse);
                count++;
            }
        }
        logger.info("Total row count: {}, table count: {}", count, finalResponse.keySet().size());
        return new ApiResponse(finalResponse);
    }
    public ApiResponse getTableDataV2(LoginUserDetails loginUserDetails, String filenames,
                                    String tableNames) throws AppException {
        boolean isAdmin = userService.isLoginUserAdmin(loginUserDetails);
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);// throw error when invalid
        ArrayList<TableRowResponse> rowResponses =
                this.getTableRowResponses(loginUserDetails, saveDir, isAdmin, filenames, tableNames);
        if (rowResponses == null) {
            logger.info("Error in reading table data");
            return new ApiResponse(ErrorCodes.SERVER_ERROR);
        }
        logger.info("Total table entry count: {}", rowResponses.size());
        return new ApiResponse(rowResponses);
    }
    public ApiResponse scanUserDatabaseDirectory(LoginUserDetails loginUserDetails,
                                                 String filenames, boolean isAdmin) throws AppException {
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> response = fileServiceV3.getUsersFilePath(loginUserDetails, saveDir, true, isAdmin);
        ArrayList<ResponseFilesInfo> filesInfo =
                fileServiceV3.generateFileInfoResponse(response, loginUserDetails, true);
        logger.info("Result size before filenames filter: {}", filesInfo.size());
        filesInfo = fileServiceV3.filterResponseFilesInfo(filesInfo, filenames);
        logger.info("Result size after filter: {}", filesInfo.size());
        return new ApiResponse(filesInfo);
    }
    public ApiResponse addText(LoginUserDetails userDetails, RequestAddText addText) throws AppException {

        String saveDir = appConfig.getFileSaveDirV2(userDetails);
        fileServiceV3.verifyAddTextRequestV2(saveDir, userDetails, addText);
        boolean isAuthorised = userService.isAuthorised(userDetails, AppConstant.IS_ADD_TEXT_ENABLE);
        if (!isAuthorised) {
            logger.info("addText is disabled.");
            throw new AppException(ErrorCodes.ADD_TEXT_DISABLED);
        }
        String username = userDetails.getUsername();
        boolean textAdded = fileServiceV3.saveAddText(saveDir, username, addText);
        if (textAdded) {
            return new ApiResponse();
        }
        logger.info("Error in adding text loginUserDetails: {}, addTex: {}", userDetails, addText);
        throw new AppException(ErrorCodes.ADD_TEXT_ERROR);
    }
    private String getTextForSaving(ArrayList<String> text) {
        StringBuilder result = new StringBuilder();
        boolean isAdded = false;
        if (text != null) {
            for (String str: text) {
                if (isAdded) {
                    result.append(",").append(str);
                } else {
                    result = new StringBuilder(str);
                    isAdded = true;
                }
            }
        }
        return result.toString();
    }
    public ApiResponse deleteText(LoginUserDetails loginUserDetails, RequestDeleteText deleteText) throws AppException {
        if (deleteText == null) {
            logger.info("Invalid request deleteText: null");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        boolean isAdmin = userService.isLoginUserAdmin(loginUserDetails);
        String deleteId = deleteText.getDeleteId();
        String tableName = deleteText.getTableName();
        if (StaticService.isInValidString(deleteId) || StaticService.isInValidString(tableName)) {
            logger.info("Invalid request deleteId or tableName: {}, {}", deleteId, tableName);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        ArrayList<String> deletedIds = this.getDeletedIds(saveDir, loginUserDetails, isAdmin);
        if (deletedIds != null && deletedIds.contains(deleteId)) {
            logger.info("deleteId already deleted: {}", deleteId);
            throw new AppException(ErrorCodes.DELETE_TEXT_ALREADY_DELETED);
        }
        String username = loginUserDetails.getUsername();
        ArrayList<String> tableFileNames = fileServiceV3.getUsersFilePathV2(loginUserDetails, saveDir, isAdmin);
        ArrayList<TableRowResponse> rowResponses = fileServiceV3.getTableRowResponseByFilePath(saveDir, tableFileNames);
        TableRowResponse finalRowResponse = null;
        int entryCount = 0;
        if (rowResponses != null) {
            for (TableRowResponse rowResponse: rowResponses) {
                if (tableName.equals(rowResponse.getTableName()) &&
                        deleteId.equals(rowResponse.getTableUniqueId())) {
                    entryCount++;
                    finalRowResponse = rowResponse;
                }
            }
        }
        if (entryCount > 1) {
            logger.info("Duplicate entry for deleteId and tableName: {}, {}", deleteId, tableName);
            throw new AppException(ErrorCodes.DELETE_TEXT_DUPLICATE);
        } else if (entryCount == 0) {
            logger.info("Entry not found for deleteId and tableName: {}, {}", deleteId, tableName);
            throw new AppException(ErrorCodes.DELETE_TEXT_NOT_FOUND);
        }
        RequestAddText addText = new RequestAddText();
        addText.setFilename(AppConstant.DELETE_TABLE_FILE_NAME);
        addText.setTableName(AppConstant.DELETE_TABLE_NAME);
        String[] text = new String[1];
        text[0] = deleteId + "," + this.getTextForSaving(finalRowResponse.getText());
        addText.setText(text);
        boolean textAdded = fileServiceV3.saveAddText(saveDir, username, addText);
        if (textAdded) {
            return new ApiResponse();
        }
        logger.info("Error in adding delete text: {}, {}", deleteId, tableName);
        throw new AppException(ErrorCodes.SERVER_ERROR);
    }
}
