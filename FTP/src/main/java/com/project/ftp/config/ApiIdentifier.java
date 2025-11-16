package com.project.ftp.config;

import java.util.HashMap;

public enum ApiIdentifier {
    GET_ALL_USERS("get_users"),
    GET_RELATED_USERS("get_related_users_data"),
    GET_RELATED_USERS_V2("get_related_users_data_v2"),
    GET_LOGIN_USER_DETAILS("get_login_user_details"),
    DELETE_FILE("delete_file"),
    MOVE_FILE("move_file"),
    GET_FILES_INFO("get_files_info"),
    GET_FILES_INFO_BY_FILENAME_PATTERN("get_files_info_by_filename_pattern"),
    GET_PATH_INFO("get_path_info"),
    GET_DATABASE_FILES_INFO("get_database_files_info"),
    GET_TABLE_DATA("get_table_data"),
    GET_TABLE_DATA_V2("get_table_data_v2"),
    GET_CURRENT_USER_FILES_INFO("get_current_user_files_info"),
    GET_APP_CONFIG("get_app_config"),
    GET_SESSION_CONFIG("get_session_config"),
    GET_API_ROLE_MAPPING("get_api_role_mapping"),
    UPLOAD_FILE("upload_file"),
    ADD_TEXT("add_text"),
    ADD_TEXT_V2("add_text_v2"),
    DELETE_TEXT("delete_text"),
    GET_UPLOADED_CSV_DATA("get_uploaded_csv_data"),
    GET_UPLOADED_CSV_DATA_BY_FILENAME_PATTERN("get_uploaded_data_by_filename_pattern"),
    LOGIN_USER("login_user"),
    LOGIN_OTHER_USER("login_other_user"),
    LOGIN_SOCIAL("login_social"),
    REGISTER_USER("register_user"),
    CHANGE_PASSWORD("change_password"),
    FORGOT_PASSWORD("forgot_password"),
    CREATE_PASSWORD("create_password"),
    RESET_COUNT("reset_count"),
    UPDATE_CONFIG("update_config"),
    AES_ENCRYPT("aes_encrypt"),
    AES_DECRYPT("aes_decrypt"),
    MD5_ENCRYPT("md5_encrypt"),
    VERIFY_PERMISSION("verify_permission"),
    GET_ROLES_CONFIG("get_roles_config"),
    CALL_TCP("call_tcp"),
    GET_EXCEL_DATA_CONFIG("get_excel_data_config"),
    GET_EXCEL_DATA("get_excel_data"),
    GET_EXCEL_DATA_ARRAY("get_excel_data_json_array"),
    GET_EXCEL_DATA_JSON("get_excel_data_json"),
    GET_EXCEL_DATA_CSV("get_excel_data_csv"),
    UPDATE_EXCEL_DATA("update_excel_data"),
    UPDATE_EXCEL_DATA_V2("update_excel_data_v2"),
    GET_SCAN_DIR_CONFIG("get_scan_dir_config"),
    READ_SCAN_DIR("read_scan_dir"),
    READ_SCAN_DIR_JSON("read_scan_dir_json"),
    READ_SCAN_DIR_CSV("read_scan_dir_csv"),
    UPDATE_SCAN_DIR("update_scan_dir"),
    GET_SCAN_DIR("get_scan_dir"),
    GET_SCAN_DIR_JSON("get_scan_dir_json"),
    GET_SCAN_DIR_CSV("get_scan_dir_csv"),
    GET_MYSQL_TABLE_DATA("get_mysql_table_data"),
    UPDATE_MYSQL_TABLE_DATA_FROM_CSV("update_mysql_table_data_from_csv"),
    GET_SINGLE_THREAD_STATUS("get_single_thread_status"),
    STOP_SINGLE_THREAD("stop_single_thread"),
    SPLIT_FILE("split_file");
    private final String apiName;
    ApiIdentifier(String name) {
        this.apiName = name;
    }
    public String getApiName() {
        return apiName;
    }

    private static final HashMap<String, ApiIdentifier> lookup = new HashMap<>();

    static {
        for (ApiIdentifier apiIdentifier : ApiIdentifier.values()) {
            lookup.put(apiIdentifier.getApiName(), apiIdentifier);
        }
    }
    public static ApiIdentifier get(String apiName) {
        if (apiName == null) {
            return null;
        }
        return lookup.get(apiName);
    }
}
