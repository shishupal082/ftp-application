package com.project.ftp.exceptions;


import com.project.ftp.obj.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.concurrent.TimeoutException;

@Provider
public class AppExceptionMapper implements ExceptionMapper<Exception> {
    final static Logger logger = LoggerFactory.getLogger(AppExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        if (exception instanceof AppException) {
            AppException appException = ((AppException) exception);
            AppError appError = new AppError(appException.getErrorCode());
            logger.info("AppException found: {}", appError);
            return Response.status(appException.getStatusCode()).entity(
                    appError.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (exception instanceof TimeoutException) {
            AppError appError = new AppError(ErrorCodes.TIME_OUT_EXCEPTION);
            logger.info("TimeoutException found: {}, {}", appError, exception.getMessage());
            return Response.status(Response.Status.GATEWAY_TIMEOUT).entity(appError.toString()).build();
        } else if (exception instanceof ServletException) {
            AppError appError = new AppError(ErrorCodes.SERVLET_EXCEPTION);
            logger.info("ServletException found: {}, {}", appError, exception.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(appError.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (exception instanceof NullPointerException) {
            AppError appError = new AppError(ErrorCodes.NULL_POINTER_EXCEPTION);
            logger.info("NullPointerException found: {}, {}", appError, exception.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    appError.toString()).type(MediaType.APPLICATION_JSON).build();
        }
        AppError appError = new AppError(ErrorCodes.SERVER_ERROR);
        appError.setError(exception.getMessage());
        logger.info("UnknownException found: {}, {}", appError, exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                appError.toString()).type(MediaType.APPLICATION_JSON).build();
    }
}

class AppError {

    final ApiResponse apiResponse;
    final ErrorCodes errorCodes;

    public AppError(ErrorCodes errorCodes) {
        this.errorCodes = errorCodes;
        this.apiResponse = new ApiResponse(errorCodes);
    }
    public void setError(String str) {
        this.apiResponse.setError(str);
    }
    @Override
    public String toString() {
        return  "{"+
                    "\"code\":\""+errorCodes.getStatusCode()+"\""+
                    ", \"error\":\""+apiResponse.getError()+"\""+
                    ", \"status\":\""+apiResponse.getStatus()+"\""+ //It is required for UI
                    ", \"failureCode\":\""+apiResponse.getFailureCode()+"\""+ //It is required for UI error mapping
                "}";
    }
}
