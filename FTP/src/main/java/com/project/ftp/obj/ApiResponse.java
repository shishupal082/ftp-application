package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;

public class ApiResponse {
    private String status;
    private String failureCode;
    private String error;
    private Object data;
    public ApiResponse() {
        this.status = AppConstant.SUCCESS;
    }
    public ApiResponse(Object data) {
        this.status = AppConstant.SUCCESS;
        this.data = data;
    }
    public ApiResponse(ErrorCodes errorCodes) {
        this.status = AppConstant.FAILURE;
        this.failureCode = errorCodes.getErrorCode();
        this.error = errorCodes.getErrorString();
        if (errorCodes.getStatusCode() == 200) {
            this.status = AppConstant.SUCCESS;
            this.failureCode = null;
            this.error = null;
        }
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toJsonString() {
        return "{" +
                "\"status\":\"" + status + "\"" +
                ", \"failureCode\":\"" + failureCode + "\"" +
                ", \"error\":\"" + error + "\"" +
                ", \"data\":" + null +
                "}";
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status='" + status + '\'' +
                ", failureCode='" + failureCode + '\'' +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}
