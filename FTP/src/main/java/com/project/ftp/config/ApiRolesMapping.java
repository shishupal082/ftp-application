package com.project.ftp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiRolesMapping {
    private final static Logger logger = LoggerFactory.getLogger(ApiRolesMapping.class);
    public static HashMap<String, ArrayList<ApiRoleMappingData>> getPreDefinedApiRoleMapping() {
        HashMap<String, ArrayList<ApiRoleMappingData>> result = new HashMap<>();

        result.put(ApiIdentifier.GET_APP_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_APP_CONFIG.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(), AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.GET_SESSION_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SESSION_CONFIG.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(), AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.AES_ENCRYPT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.AES_ENCRYPT.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.AES_DECRYPT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.AES_DECRYPT.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.MD5_ENCRYPT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.MD5_ENCRYPT.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.GET_ROLES_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_ROLES_CONFIG.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.UPDATE_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_CONFIG.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.GET_DATABASE_FILES_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_DATABASE_FILES_INFO.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.GET_API_ROLE_MAPPING.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_API_ROLE_MAPPING.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DEV_USER.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.LOGIN_OTHER_USER.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.LOGIN_OTHER_USER.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN_OTHER_USER_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.RESET_COUNT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.RESET_COUNT.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_USERS_CONTROL_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.DELETE_FILE.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.DELETE_FILE.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DELETE_FILE_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.UPLOAD_FILE.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPLOAD_FILE.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_UPLOAD_FILE_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.ADD_TEXT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.ADD_TEXT.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_ADD_TEXT_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.ADD_TEXT_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.ADD_TEXT_V2.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_ADD_TEXT_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.DELETE_TEXT.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.DELETE_TEXT.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_DELETE_TEXT_ENABLE.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        result.put(ApiIdentifier.GET_ALL_USERS.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_ALL_USERS.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_RELATED_USERS.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_RELATED_USERS.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_RELATED_USERS_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_RELATED_USERS_V2.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_FILES_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_FILES_INFO.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_FILES_INFO_BY_FILENAME_PATTERN.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_FILES_INFO_BY_FILENAME_PATTERN.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_PATH_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_PATH_INFO.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_TABLE_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_TABLE_DATA.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_TABLE_DATA_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_TABLE_DATA_V2.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_CURRENT_USER_FILES_INFO.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_CURRENT_USER_FILES_INFO.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.UPDATE_MYSQL_TABLE_DATA_FROM_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_MYSQL_TABLE_DATA_FROM_CSV.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_MYSQL_TABLE_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_MYSQL_TABLE_DATA.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_UPLOADED_CSV_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_UPLOADED_CSV_DATA.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_UPLOADED_CSV_DATA_BY_FILENAME_PATTERN.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_UPLOADED_CSV_DATA_BY_FILENAME_PATTERN.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.CHANGE_PASSWORD.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.CHANGE_PASSWORD.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_SCAN_DIR_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR_CSV.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_SCAN_DIR_JSON.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR_JSON.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_SCAN_DIR.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.CALL_TCP.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.CALL_TCP.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.UPDATE_SCAN_DIR.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_SCAN_DIR.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.READ_SCAN_DIR_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.READ_SCAN_DIR_CSV.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.READ_SCAN_DIR_JSON.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.READ_SCAN_DIR_JSON.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.READ_SCAN_DIR.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.READ_SCAN_DIR.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_SCAN_DIR_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_SCAN_DIR_CONFIG.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.UPDATE_EXCEL_DATA_V2.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_EXCEL_DATA_V2.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_EXCEL_DATA_CONFIG.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_EXCEL_DATA_CONFIG.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.UPDATE_EXCEL_DATA.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.UPDATE_EXCEL_DATA.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_EXCEL_DATA_JSON.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_EXCEL_DATA_JSON.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_EXCEL_DATA_CSV.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_EXCEL_DATA_CSV.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.SPLIT_FILE.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.SPLIT_FILE.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));
        result.put(ApiIdentifier.GET_LOGIN_USER_DETAILS.getApiName(), new ArrayList<>());
        result.get(ApiIdentifier.GET_LOGIN_USER_DETAILS.getApiName()).add(new ApiRoleMappingData(
                ApiRoleAccess.IS_LOGIN.getRoleAccessName(),AppConstant.roleAccessTypeDirect));

        return result;
    }
    public static Boolean isRoleMappingAlreadyAvailable(ArrayList<ApiRoleMappingData> staticRoleMappingData,
                                                        ApiRoleMappingData apiRoleMappingData) {
        if (staticRoleMappingData == null) {
            return null;
        }
        if (apiRoleMappingData == null) {
            return null;
        }
        String role = apiRoleMappingData.getRole();
        for (ApiRoleMappingData apiRoleMappingData1: staticRoleMappingData) {
            if (apiRoleMappingData1 == null) {
                continue;
            }
            if (apiRoleMappingData1.getRole() == null) {
                continue;
            }
            if (apiRoleMappingData1.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }
    public static HashMap<String, ArrayList<ApiRoleMappingData>> getFinalApiRoleMapping(HashMap<String, ArrayList<String>> configApiRolesMapping) {
        HashMap<String, ArrayList<ApiRoleMappingData>> staticApiRolesMapping = ApiRolesMapping.getPreDefinedApiRoleMapping();
        if (configApiRolesMapping == null) {
            return staticApiRolesMapping;
        }
        String key;
        ArrayList<String> values;
        ArrayList<ApiRoleMappingData> staticValues;
        ApiRoleMappingData configApiRoleMappingData;
        Boolean roleConfigCheckStatus;
        for (Map.Entry<String, ArrayList<String>> entry: configApiRolesMapping.entrySet()) {
            if (entry == null) {
                continue;
            }
            key = entry.getKey();
            values = entry.getValue();
            if (key != null && values != null) {
                staticValues = staticApiRolesMapping.get(key);
                if (staticValues == null) {
                    staticValues = new ArrayList<>();
                }
                for (String str: values) {
                    if (str != null && !str.isEmpty()) {
                        configApiRoleMappingData = new ApiRoleMappingData(str,AppConstant.roleAccessTypeConfig);
                        roleConfigCheckStatus = isRoleMappingAlreadyAvailable(staticValues,
                                configApiRoleMappingData);
                        if (roleConfigCheckStatus == null) {
                            logger.info("role checking failed for, api: {}, role: {}", key, str);
                            continue;
                        } else if (roleConfigCheckStatus) {
                            logger.info("{} role already exist for api: {}", str, key);
                            continue;
                        }
                        staticValues.add(configApiRoleMappingData);
                    }
                }
                staticApiRolesMapping.put(key, staticValues);
            }
        }
        return staticApiRolesMapping;
    }
}
