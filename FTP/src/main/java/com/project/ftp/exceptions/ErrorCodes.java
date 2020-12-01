package com.project.ftp.exceptions;

/**
 * Created by shishupalkumar on 10/02/17.
 */

public enum ErrorCodes {
    REDIRECTION_ERROR("REDIRECTION_ERROR", "Routing error", 300),
    UNABLE_TO_PARSE_JSON("Unable to parse Json", "Unable to parse Json", 400),
    BAD_REQUEST_ERROR("BAD_REQUEST_ERROR", "Bad request error", 401),
    UNAUTHORIZED_ORIGIN("UNAUTHORIZED_ORIGIN", "UnAuthorized Origin", 401),
    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "UnAuthorized Access", 401),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "File not found", 402),
    FILE_UPLOAD_DISABLED("FILE_UPLOAD_DISABLED", "File upload disabled", 402),
    ADD_TEXT_DISABLED("ADD_TEXT_DISABLED", "Add text disabled", 402),
    GET_ALL_USERS_DISABLED("GET_ALL_USERS_DISABLED", "Get all users api disabled", 402),
    ADD_TEXT_ERROR("ADD_TEXT_ERROR", "Error in adding text", 402),
    VERIFY_PERMISSION_ERROR("VERIFY_PERMISSION_ERROR", "Un authorised permission", 402),
    INVALID_QUERY_PARAMS("Invalid query params", "Invalid query params", 403),
    INVALID_SESSION("INVALID_SESSION", "Invalid session", 403),
    INVALID_USER_NAME("INVALID_USER_NAME", "Invalid user name", 403),
    INVALID_INPUT("INVALID_INPUT", "Invalid request input", 403),
    // Login, forgot_password, change_password, create_password, register
    USER_BLOCKED("USER_BLOCKED", "User blocked, Please contact admin.", 403),
    // Login, Register, Forgot password, Create password
    USER_NAME_REQUIRED("USER_NAME_REQUIRED", "Username required.", 403),
    USER_ALREADY_LOGIN("USER_ALREADY_LOGIN", "User already login.", 403),
    // Login
    PASSWORD_NOT_MATCHING("PASSWORD_NOT_MATCHING", "Username password not matching.", 403),
    PASSWORD_REQUIRED("PASSWORD_REQUIRED", "Password required.", 403),
    // Login, Forgot password, Create password
    USER_NOT_REGISTERED("USER_NOT_REGISTERED", "User is not registered, Please register.", 403),
    // Login, Change password, Register, Forgot password, Create password
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found.", 403),
    // Change password
    PASSWORD_CHANGE_OLD_REQUIRED("PASSWORD_CHANGE_OLD_REQUIRED", "Old password required.", 403),
    // Register, Change password, Create password
    NEW_PASSWORD_REQUIRED("NEW_PASSWORD_REQUIRED", "New password required.", 403),
    // Change password, Create password
    CONFIRM_PASSWORD_REQUIRED("CONFIRM_PASSWORD_REQUIRED", "Confirm password required.", 403),
    // Change password
    PASSWORD_CHANGE_COUNT_EXCEED("PASSWORD_CHANGE_COUNT_EXCEED", "Password change count exceed limit.", 403),
    PASSWORD_CHANGE_OLD_NOT_MATCHING("PASSWORD_CHANGE_OLD_NOT_MATCHING", "Old password not matching.", 403),
    // Change password, Register, Create password
    PASSWORD_LENGTH_MISMATCH("PASSWORD_LENGTH_MISMATCH", "New password length should between 8 to 14.", 403),
    PASSWORD_ENCRYPTION_ERROR("PASSWORD_ENCRYPTION_ERROR", "Error in password generate, Please try again.", 403),
    // Register
    REGISTER_PASSCODE_REQUIRED("REGISTER_PASSCODE_REQUIRED", "Passcode required.", 403),
    REGISTER_NAME_REQUIRED("REGISTER_NAME_REQUIRED", "Name required.", 403),
    REGISTER_PASSCODE_NOT_MATCHING("REGISTER_PASSCODE_NOT_MATCHING", "Passcode not matching.", 403),
    REGISTER_ALREADY("REGISTER_ALREADY", "User already registered, Try login.", 403),
    // Register, Forgot password
    MOBILE_REQUIRED("MOBILE_REQUIRED", "Mobile number required.", 403),
    EMAIL_REQUIRED("EMAIL_REQUIRED", "Email required.", 403),
    MOBILE_INVALID("MOBILE_INVALID", "Please enter valid mobile number.", 403),
    EMAIL_INVALID("EMAIL_INVALID", "Please enter valid email.", 403),
    MOBILE_INVALID_LENGTH("MOBILE_INVALID_LENGTH", "Mobile number should be 10 digit.", 403),
    EMAIL_INVALID_LENGTH("EMAIL_INVALID_LENGTH", "Email length should be less than 63.", 403),
    FORGOT_PASSWORD_LOGIN_USER("FORGOT_PASSWORD_LOGIN_USER", "User login, requested forgot password.", 403),
    LOGOUT_USER_NOT_LOGIN("LOGOUT_USER_NOT_LOGIN", "User not logged in, requested logout.", 403),
    // create password
    CREATE_PASSWORD_OTP_REQUIRED("CREATE_PASSWORD_OTP_REQUIRED", "Create password otp required.", 403),
    CREATE_PASSWORD_OTP_MISMATCH("CREATE_PASSWORD_OTP_MISMATCH", "Create password otp not matching.", 403),
    CREATE_PASSWORD_NOT_REQUESTED_FORGOT("CREATE_PASSWORD_NOT_REQUESTED_FORGOT", "Forgot password request not found.", 403),
    // create password, change password
    NEW_AND_CONFIRM_PASSWORD_NOT_MATCHING("NEW_AND_CONFIRM_PASSWORD_NOT_MATCHING", "New password and confirm password are not matching.", 403),
    // forgot password
    FORGOT_PASSWORD_NOT_ENABLE("FORGOT_PASSWORD_NOT_ENABLE", "Forgot password is not enable.", 403),
    FORGOT_PASSWORD_MOBILE_MISMATCH("FORGOT_PASSWORD_MOBILE_MISMATCH", "Username and mobile number not matching.", 403),
    FORGOT_PASSWORD_EMAIL_MISMATCH("FORGOT_PASSWORD_EMAIL_MISMATCH", "Username and email not matching.", 403),
    FORGOT_PASSWORD_REPEAT_REQUEST("FORGOT_PASSWORD_REPEAT_REQUEST", "Forgot password request submitted, Please create password.", 403),
    INVALID_FILE_DATA("INVALID_FILE_DATA", "Invalid file data", 403),
    INVALID_FILE_SAVE_PATH("INVALID_FILE_SAVE_PATH", "Invalid final save file path", 403),
    INVALID_SERVICE_NAME_EMPTY("Invalid service name Empty", "Invalid service name Empty", 403),
    INVALID_SERVICE_NAME("Invalid service name", "Invalid service name", 403),
    TASK_COMPONENT_NOT_FOUND("Component not found", "Component not found", 403),
    TASK_APPLICATION_NOT_FOUND("Application not found", "Application not found", 403),
    COMMAND_ID_NULL("Invalid commandId", "Invalid commandId", 403),
    COMMAND_NOT_FOUND("Command not found", "Command not found", 403),
    CONFIG_ERROR_INVALID_SAVE_MSG_PATH("Invalid save message path", "Invalid save message path", 403),
    CONFIG_ERROR_INVALID_PATH("Invalid path", "Invalid path", 403),
    CONFIG_ERROR_INVALID_STORAGE_TYPE("Invalid storage type", "Invalid storage type", 403),
    UNSUPPORTED_FILE_TYPE("UNSUPPORTED_FILE_TYPE", "Unsupported file type", 403),
    UPLOAD_FILE_VERSION_MISMATCH("UPLOAD_FILE_VERSION_MISMATCH", "Upload file api version mismatch", 403),
    UPLOAD_FILE_SUBJECT_REQUIRED("UPLOAD_FILE_SUBJECT_REQUIRED", "Subject required.", 403),
    UPLOAD_FILE_HEADING_REQUIRED("UPLOAD_FILE_HEADING_REQUIRED", "Heading required.", 403),
    UPLOAD_FILE_FILENAME_REQUIRED("UPLOAD_FILE_FILENAME_REQUIRED", "Upload file required", 403),
    CONFIG_ERROR("CONFIG_ERROR", "Configuration error", 500),
    FILE_SIZE_EXCEEDED("FILE_SIZE_EXCEEDED", "File size exceeded", 500),
    SERVER_ERROR("SERVER_ERROR", "Server error", 500),
    NULL_POINTER_EXCEPTION("NULL_POINTER_EXCEPTION", "Null pointer exception", 500),
    TIME_OUT_EXCEPTION("TIME_OUT_EXCEPTION", "Down stream service down", 500),
    SERVLET_EXCEPTION("SERVLET_EXCEPTION", "Something went wrong", 500),
    RUNTIME_ERROR("RUN_TIME_ERROR", "Run time error", 599);

    private final String errorCode;
    private final Integer statusCode;
    private String errorString;

    ErrorCodes(String errorCode, String errorString, Integer statusCode) {
        this.errorCode = errorCode;
        this.errorString = errorString;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    @Override
    public String toString() {
        return "ErrorCodes{" +
                "errorCode='" + errorCode + '\'' +
                ", statusCode=" + statusCode +
                ", errorString='" + errorString + '\'' +
                '}';
    }
}
