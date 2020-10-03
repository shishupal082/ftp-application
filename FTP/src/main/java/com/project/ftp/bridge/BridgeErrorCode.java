package com.project.ftp.bridge;

/**
 * Created by shishupalkumar on 10/02/17.
 */

public enum BridgeErrorCode {
    INVALID_REQUEST_OTP("INVALID_REQUEST_OTP", "OTP is null", 400),
    INVALID_REQUEST_EMAIL("INVALID_REQUEST_EMAIL", "Email is null", 400),
    ERROR_IN_MESSAGE("ERROR_IN_MESSAGE", "ERROR_IN_MESSAGE", 400),
    CONFIG_ERROR_INVALID_EMAIL("CONFIG_ERROR_INVALID_EMAIL", "Sender email is null", 400),
    GMAIL_SMTP_ERROR("GMAIL_SMTP_ERROR", "GMAIL_SMTP_ERROR", 500);
    private final String errorCode;
    private final int statusCode;
    private String errorString;

    BridgeErrorCode(String errorCode, String errorString, int statusCode) {
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    @Override
    public String toString() {
        return "BridgeErrorCode{" +
                "errorCode='" + errorCode + '\'' +
                ", statusCode=" + statusCode +
                ", errorString='" + errorString + '\'' +
                '}';
    }
}
