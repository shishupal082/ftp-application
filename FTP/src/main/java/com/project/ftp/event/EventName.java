package com.project.ftp.event;

public enum EventName {
    LOGIN("login"),
    LOGIN_OTHER_USER("login_other_user"),
    LOGIN_SOCIAL("login_social"),
    LOGOUT("logout"),
    REGISTER("register"),
    FORGOT_PASSWORD("forgot_password"),
    CHANGE_PASSWORD("change_password"),
    CREATE_PASSWORD("create_password"),
    APP_DATA("app_data"),
    UPLOAD_FILE("upload_file"),
    VIEW_FILE("view_file"),
    VIEW_ANY_FILE("view_any_file"),
    DOWNLOAD_FILE("download_file"),
    DELETE_FILE("delete_file"),
    LOG_FILE_COPIED("log_file_copied"),
    APPLICATION_START("application_start"),
    UN_HANDLE_EXCEPTION("un_handle_exception"),
    EXPIRED_USER_SESSION("expired_user_session"),
    GET_LOGIN_USER_DETAILS("get_login_user_details"),
    GET_USERS("get_users"),
    ADD_TEXT("add_text"),
    ADD_TEXT_V2("add_text_v2"),
    DELETE_TEXT("add_text"),
    GET_UPLOADED_CSV_DATA("get_uploaded_csv_data"),
    GET_UPLOADED_DATA_BY_FILENAME_PATTERN("get_uploaded_data_by_filename_pattern"),
    GET_RELATED_USERS_DATA("get_related_users_data"),
    GET_RELATED_USERS_DATA_V2("get_related_users_data_v2"),
    GET_OTHER_USER_RELATED_DATA("get_other_user_related_data"),
    GET_APP_CONFIG("get_app_config"),
    GET_SESSION_DATA("get_session_data"),
    GET_FILES_INFO("get_files_info"),
    GET_PATH_INFO("get_path_info"),
    GET_DATABASE_FILES_INFO("get_database_files_info"),
    GET_DATABASE_TABLE_DATA("get_database_table_data"),
    GET_FILES_INFO_BY_FILENAME_PATTERN("get_files_info_by_filename_pattern"),
    GET_CURRENT_USER_FILES_INFO("get_current_user_files_info"),
    UPDATE_ROLES_CONFIG("update_roles_config"),
    RESET_CHANGE_PASSWORD_COUNT("reset_change_password_count"),
    VERIFY_PERMISSION("verify_permission"),
    CALL_TCP("call_tcp"),
    SCAN_DIRECTORY("scan_directory"),
    MS_EXCEL_DATA("ms_excel_data"),
    TABLE_DATA("table_data"),
    GOOGLE_API("google_api"),
    GET_ROLES_CONFIG("get_roles_config"),
    AES_ENCRYPTION("aes_encryption"),
    AES_DECRYPTION("aes_decryption"),
    MD5_ENCRYPTION("md5_encryption");

    private final String name;
    EventName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
