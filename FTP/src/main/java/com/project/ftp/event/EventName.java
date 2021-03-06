package com.project.ftp.event;

public enum EventName {
    LOGIN("login"),
    LOGOUT("logout"),
    REGISTER("register"),
    FORGOT_PASSWORD("forgot_password"),
    CHANGE_PASSWORD("change_password"),
    CREATE_PASSWORD("create_password"),
    UPLOAD_FILE("upload_file"),
    UPLOAD_FILE_V1("upload_file_v1"),
    UPLOAD_FILE_V2("upload_file_v2"),
    VIEW_FILE("view_file"),
    DOWNLOAD_FILE("download_file"),
    DELETE_FILE("delete_file"),
    LOG_FILE_COPIED("log_file_copied"),
    APPLICATION_START("application_start"),
    UN_HANDLE_EXCEPTION("un_handle_exception"),
    EXPIRED_USER_SESSION("expired_user_session"),
    GET_USERS("get_users"),
    GET_APP_CONFIG("get_app_config"),
    GET_SESSION_DATA("get_session_data"),
    GET_FILES_INFO("get_files_info"),
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
