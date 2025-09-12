package com.project.ftp.config;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiRolesMapping {
    public static HashMap<String, ArrayList<String>> getPreDefinedApiRoleMapping() {
        HashMap<String, ArrayList<String>> result = new HashMap<>();

        result.put(ApiIdentifier.GET_APP_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_APP_CONFIG.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.GET_SESSION_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SESSION_CONFIG.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.AES_ENCRYPT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.AES_ENCRYPT.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.AES_DECRYPT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.AES_DECRYPT.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.MD5_ENCRYPT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.MD5_ENCRYPT.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.GET_ROLES_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_ROLES_CONFIG.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.UPDATE_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_CONFIG.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());

        result.put(ApiIdentifier.GET_DATABASE_FILES_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_DATABASE_FILES_INFO.getApiName()).add(ApiRoleAccess.IS_DEV_USER.getRoleAccessName());


        result.put(ApiIdentifier.LOGIN_OTHER_USER.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.LOGIN_OTHER_USER.getApiName()).add(ApiRoleAccess.IS_LOGIN_OTHER_USER_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.RESET_COUNT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.RESET_COUNT.getApiName()).add(ApiRoleAccess.IS_USERS_CONTROL_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.DELETE_FILE.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.DELETE_FILE.getApiName()).add(ApiRoleAccess.IS_DELETE_FILE_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.UPLOAD_FILE.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPLOAD_FILE.getApiName()).add(ApiRoleAccess.IS_UPLOAD_FILE_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.ADD_TEXT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.ADD_TEXT.getApiName()).add(ApiRoleAccess.IS_ADD_TEXT_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.ADD_TEXT_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.ADD_TEXT_V2.getApiName()).add(ApiRoleAccess.IS_ADD_TEXT_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.DELETE_TEXT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.DELETE_TEXT.getApiName()).add(ApiRoleAccess.IS_DELETE_TEXT_ENABLE.getRoleAccessName());

        result.put(ApiIdentifier.GET_ALL_USERS.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_ALL_USERS.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_RELATED_USERS.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_RELATED_USERS.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_RELATED_USERS_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_RELATED_USERS_V2.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_FILES_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_FILES_INFO.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_FILES_INFO_BY_FILENAME_PATTERN.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_FILES_INFO_BY_FILENAME_PATTERN.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_PATH_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_PATH_INFO.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_TABLE_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_TABLE_DATA.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_TABLE_DATA_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_TABLE_DATA_V2.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_CURRENT_USER_FILES_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_CURRENT_USER_FILES_INFO.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.UPDATE_MYSQL_TABLE_DATA_FROM_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_MYSQL_TABLE_DATA_FROM_CSV.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_MYSQL_TABLE_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_MYSQL_TABLE_DATA.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_UPLOADED_CSV_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_UPLOADED_CSV_DATA.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_UPLOADED_CSV_DATA_BY_FILENAME_PATTERN.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_UPLOADED_CSV_DATA_BY_FILENAME_PATTERN.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.CHANGE_PASSWORD.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.CHANGE_PASSWORD.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_SCAN_DIR_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR_CSV.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_SCAN_DIR_JSON.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR_JSON.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_SCAN_DIR.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.CALL_TCP.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.CALL_TCP.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.UPDATE_SCAN_DIR.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_SCAN_DIR.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.READ_SCAN_DIR_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.READ_SCAN_DIR_CSV.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.READ_SCAN_DIR_JSON.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.READ_SCAN_DIR_JSON.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.READ_SCAN_DIR.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.READ_SCAN_DIR.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_SCAN_DIR_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR_CONFIG.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.UPDATE_EXCEL_DATA_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_EXCEL_DATA_V2.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_EXCEL_DATA_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_EXCEL_DATA_CONFIG.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.UPDATE_EXCEL_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_EXCEL_DATA.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_EXCEL_DATA_JSON.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_EXCEL_DATA_JSON.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_EXCEL_DATA_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_EXCEL_DATA_CSV.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.SPLIT_FILE.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.SPLIT_FILE.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        result.put(ApiIdentifier.GET_LOGIN_USER_DETAILS.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_LOGIN_USER_DETAILS.getApiName()).add(ApiRoleAccess.IS_LOGIN.getRoleAccessName());
        return result;
    }
}
