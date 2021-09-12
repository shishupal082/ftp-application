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
        ArrayList<String> response =
                fileServiceV3.getUsersFilePath(loginUserDetails, saveDir, AppConstant.DB_TRUE, isAdmin);
        ArrayList<String> deleteTableFilenames =
                fileServiceV3.filterFilename(AppConstant.DELETE_TABLE_FILE_NAME, response, !AppConstant.REVERT_RESULT);
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
        ArrayList<String> response =
                fileServiceV3.getUsersFilePath(loginUserDetails, saveDir, AppConstant.DB_TRUE, isAdmin);
        response = fileServiceV3.removeDeleteFileName(response);
        ArrayList<String> response2;
        if (response != null && StaticService.isValidString(filenames)) {
            response2 = fileServiceV3.filterFilename(filenames, response, !AppConstant.REVERT_RESULT);
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
                    if (!StaticService.isPatternMatching(tempTableName, tableNames, AppConstant.EXACT_MATCH)) {
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
        HashMap<String, ArrayList<TableRowResponse>> finalResponse = new HashMap<>();
        finalResponse.put(AppConstant.DEFAULT_TABLE_NAME, rowResponses);
        logger.info("Total table entry count: {}", rowResponses.size());
        return new ApiResponse(finalResponse);
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
        String deleteId = deleteText.getDeleteId();
        if (StaticService.isInValidString(deleteId)) {
            logger.info("Invalid request deleteId: {}", deleteId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String saveDir = appConfig.getFileSaveDirV2(loginUserDetails);
        // Check whether it is already deleted by this user or any other user
        ArrayList<String> deletedIds = this.getDeletedIds(saveDir, loginUserDetails, AppConstant.ADMIN_TRUE);
        if (deletedIds != null && deletedIds.contains(deleteId)) {
            logger.info("deleteId already deleted: {}", deleteId);
            throw new AppException(ErrorCodes.DELETE_TEXT_ALREADY_DELETED);
        }
        String username = loginUserDetails.getUsername();
        // Users can delete there own text only
        ArrayList<String> currentUserTableFileNames =
                fileServiceV3.getCurrentUsersFilePath(loginUserDetails, saveDir, AppConstant.DB_TRUE);
        ArrayList<String> allUserTableFileNames =
                fileServiceV3.getUsersFilePath(loginUserDetails, saveDir, AppConstant.DB_TRUE, AppConstant.ADMIN_TRUE);
        currentUserTableFileNames = fileServiceV3.removeDeleteFileName(currentUserTableFileNames);
        allUserTableFileNames = fileServiceV3.removeDeleteFileName(allUserTableFileNames);
        ArrayList<String> otherUserTableFileNames = new ArrayList<>();
        if (allUserTableFileNames != null && currentUserTableFileNames != null) {
            for(String str: allUserTableFileNames) {
                if (!currentUserTableFileNames.contains(str)) {
                    otherUserTableFileNames.add(str);
                }
            }
        }
        ArrayList<TableRowResponse> currentUserRowResponses =
                fileServiceV3.getTableRowResponseByFilePath(saveDir, currentUserTableFileNames);
        ArrayList<TableRowResponse> otherUserRowResponses =
                fileServiceV3.getTableRowResponseByFilePath(saveDir, otherUserTableFileNames);
        TableRowResponse finalRowResponse = null;
        int entryCount = 0, otherUserEntryCount = 0;
        if (otherUserRowResponses != null) {
            for (TableRowResponse rowResponse1: otherUserRowResponses) {
                if (deleteId.equals(rowResponse1.getTableUniqueId())) {
                    logger.info("Entry found in other user: {}", rowResponse1);
                    otherUserEntryCount++;
                }
            }
        }
        if (currentUserRowResponses != null) {
            for (TableRowResponse rowResponse: currentUserRowResponses) {
                if (deleteId.equals(rowResponse.getTableUniqueId())) {
                    entryCount++;
                    logger.info("Entry found in current user: {}", rowResponse);
                    finalRowResponse = rowResponse;
                }
            }
        }
        if (otherUserEntryCount > 0) {
            if (entryCount > 0) {
                logger.info("Entry found in other user dir also: {}", deleteId);
                throw new AppException(ErrorCodes.DELETE_TEXT_DUPLICATE_OTHER_USER);
            } else {
                logger.info("Entry found in other user dir: {}", deleteId);
                throw new AppException(ErrorCodes.DELETE_TEXT_UNAUTHORISED);
            }
        }
        if (entryCount > 1) {
            logger.info("Duplicate entry for deleteId: {}", deleteId);
            throw new AppException(ErrorCodes.DELETE_TEXT_DUPLICATE);
        } else if (entryCount == 0) {
            logger.info("Entry not found for deleteId: {}", deleteId);
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
        logger.info("Error in adding deleteText: {}", deleteText);
        throw new AppException(ErrorCodes.SERVER_ERROR);
    }
}
