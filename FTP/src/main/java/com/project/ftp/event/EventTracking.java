package com.project.ftp.event;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.obj.*;
import com.project.ftp.service.FileService;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletRequest;

public class EventTracking {
    private final UserService userService;
    private final AppConfig appConfig;
    private final AddEvent addEvent;
    public EventTracking(final AppConfig appConfig,
                         final UserService userService,
                         final EventInterface eventInterface) {
        this.appConfig = appConfig;
        this.userService = userService;
        this.addEvent = new AddEvent(appConfig, eventInterface);
    }
    public void addSuccessLogin(RequestUserLogin userLogin) {
        addEvent.addSuccessLogin(userLogin);
    }
    public void addSuccessRegister(RequestUserRegister userRegister) {
        addEvent.addSuccessRegister(userRegister);
    }
    public void addForgotPassword(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addForgotPassword(loginUserDetails.getUsername());
    }
    public void trackLogout(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addLogout(loginUserDetails.getUsername());
    }
    public void trackChangePasswordSuccess(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessChangePassword(loginUserDetails.getUsername());
    }
    public void trackChangePasswordFailure(HttpServletRequest request, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(),
                EventName.CHANGE_PASSWORD, errorCodes, null);
    }
    public void trackLoginFailure(HttpServletRequest request, RequestUserLogin requestUserLogin, ErrorCodes errorCodes) {
        String username = null;
        String comment = null;
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (loginUserDetails.getLogin()) {
            if (errorCodes != null) {
                comment = errorCodes.getErrorString() + "," + loginUserDetails.getUsername();
            } else {
                comment = loginUserDetails.getUsername();
            }
        }
        if (requestUserLogin != null) {
            username = requestUserLogin.getUsername();
        }
        addEvent.addFailureEvent(username, EventName.LOGIN, errorCodes, comment);
    }

    public void trackRegisterFailure(RequestUserRegister requestUserRegister, ErrorCodes errorCodes) {
        String username = null, comment = "";
        if (requestUserRegister != null) {
            username = requestUserRegister.getUsername();
            comment += "passcode=" + requestUserRegister.getPasscode();
            comment += ",name=" + requestUserRegister.getDisplay_name();
        }
        addEvent.addFailureEvent(username, EventName.REGISTER, errorCodes, comment);
    }

    public void addSuccessViewFile(HttpServletRequest request, String filepath, String isIframe) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessViewFile(loginUserDetails.getUsername(), filepath, isIframe);
    }
    public void addSuccessDownloadFile(HttpServletRequest request, String filepath) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessDownloadFile(loginUserDetails.getUsername(), filepath);
    }
    public void addSuccessDeleteFile(HttpServletRequest request, RequestDeleteFile deleteFile) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessDeleteFile(loginUserDetails.getUsername(), deleteFile);
    }
    public void trackViewFileFailure(HttpServletRequest request, String filepath, ErrorCodes errorCodes, String isIframe) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE,
                errorCodes, filepath + ",isIframe=" + isIframe);
    }

    public void trackDownloadFileFailure(HttpServletRequest request, String filepath, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.DOWNLOAD_FILE, errorCodes, filepath);
    }

    public void trackDeleteFileFailure(HttpServletRequest request,
                                       RequestDeleteFile deleteFile,
                                       ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filepath = null;
        if (deleteFile != null) {
            filepath = deleteFile.getFilename();
        }
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.DELETE_FILE, errorCodes, filepath);
    }

    public void addSuccessUploadFile(HttpServletRequest request,
                                     FormDataContentDisposition fileDetail, String subject, String heading) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filename = null;
        if (fileDetail != null) {
            filename = fileDetail.getFileName();
        }
        addEvent.addSuccessUploadFile(loginUserDetails.getUsername(), filename, subject, heading);
    }

    public void addFailureUploadFile(HttpServletRequest request,
                                     ErrorCodes errorCodes,
                                     FormDataContentDisposition fileDetail, String subject, String heading) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filename = null;
        if (fileDetail != null) {
            filename = fileDetail.getFileName();
        }
        String comment = "";
        comment += "filepath=" + filename;
        comment += ",subject=" + subject;
        comment += ",heading=" + heading;
        EventName eventName = EventName.UPLOAD_FILE;
        String apiVersion = StaticService.getUploadFileApiVersion(appConfig);
        if (AppConstant.V1.equals(apiVersion)) {
            eventName = EventName.UPLOAD_FILE_V1;
        } else if (AppConstant.V2.equals(apiVersion)) {
            eventName = EventName.UPLOAD_FILE_V2;
        }
        addEvent.addFailureEvent(loginUserDetails.getUsername(), eventName, errorCodes, comment);
    }
    public void trackLogFileChange(String status, String newlyGeneratedFilename, String copiedFilename) {
        FileService fileService = new FileService();
        PathInfo pathInfo = fileService.getPathInfoFromFileName(newlyGeneratedFilename);
        PathInfo pathInfo1 = fileService.getPathInfoFromFileName(copiedFilename);
        addEvent.trackLogFileChange(status, pathInfo.getFileName(), pathInfo1.getFileName());
    }
    public void trackUnknownException(String errorCode, String errorString) {
        addEvent.trackUnknownException(errorCode, errorString);
    }
}