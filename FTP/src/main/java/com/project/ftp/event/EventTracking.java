package com.project.ftp.event;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.obj.*;
import com.project.ftp.service.FileService;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import com.project.ftp.session.SessionData;
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
        this.addEvent = new AddEvent(eventInterface);
    }

    public void addSuccessGetUsers(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), EventName.GET_USERS);
    }

    public void addSuccessGetAppConfig(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), EventName.GET_APP_CONFIG);
    }

    public void addSuccessGetSessionData(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), EventName.GET_SESSION_DATA);
    }

    public void addSuccessGetFilesInfo(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
//        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), EventName.GET_FILES_INFO);
    }
    public void addSuccessLogin(RequestUserLogin userLogin) {
        String username = null;
        if (userLogin != null) {
            username = userLogin.getUsername();
        }
        addEvent.addSuccessEventV2(username, EventName.LOGIN);
    }
    public void addSuccessRegister(RequestUserRegister userRegister) {
        String comment = "", username = null;
        if (userRegister != null) {
            comment += "passcode="+ userRegister.getPasscode();
            comment += ",name=" + userRegister.getDisplay_name();
            username = userRegister.getUsername();
        }
        addEvent.addSuccessEvent(username, EventName.REGISTER, comment);
    }
    public void addForgotPassword(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String username = loginUserDetails.getUsername();
        if (loginUserDetails.getLogin()) {
            addEvent.addSuccessEventV2(username, EventName.FORGOT_PASSWORD);
        } else {
            addEvent.addFailureEvent(loginUserDetails.getUsername(),
                    EventName.FORGOT_PASSWORD,
                    ErrorCodes.FORGOT_PASSWORD_LOGIN_USER, null);
        }
    }
    public void trackLogout(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (loginUserDetails.getLogin()) {
            addEvent.addSuccessEventV2(loginUserDetails.getUsername(), EventName.LOGOUT);
        } else {
            addEvent.addFailureEventV2(EventName.LOGOUT, ErrorCodes.LOGOUT_USER_NOT_LOGIN);
        }
    }
    public void trackChangePasswordSuccess(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), EventName.CHANGE_PASSWORD);
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

    public void trackRegisterFailure(HttpServletRequest request,
                                     RequestUserRegister requestUserRegister,
                                     ErrorCodes errorCodes) {
        String username = null, comment = "";
        if (requestUserRegister != null) {
            username = requestUserRegister.getUsername();
            comment += "passcode=" + requestUserRegister.getPasscode();
            comment += ",name=" + requestUserRegister.getDisplay_name();
        }
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (loginUserDetails.getLogin()) {
            if (StaticService.isValidString(comment)) {
                comment  += ",";
            }
            comment += "loginUsername=" + loginUserDetails.getUsername();
        }
        if (errorCodes != null && StaticService.isValidString(comment)) {
            comment += "," + errorCodes.getErrorString();
        }
        addEvent.addFailureEvent(username, EventName.REGISTER, errorCodes, comment);
    }

    public void addSuccessViewFile(HttpServletRequest request, String filepath, String isIframe) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE,
                filepath+",isIframe="+isIframe);
    }
    public void addSuccessDownloadFile(HttpServletRequest request, String filepath) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE, filepath);
    }
    public void addSuccessDeleteFile(HttpServletRequest request, RequestDeleteFile deleteFile) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String deleteFilePath = null;
        if (deleteFile != null) {
            deleteFilePath = deleteFile.getFilename();
        }
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.DELETE_FILE, deleteFilePath);
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
                                     FormDataContentDisposition fileDetail,
                                     String subject, String heading) {
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
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), eventName, comment);
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
        if (errorCodes != null) {
            comment += ","+errorCodes.getErrorString();
        }
        addEvent.addFailureEvent(loginUserDetails.getUsername(), eventName, errorCodes, comment);
    }
    public void trackLogFileChange(String status, String newlyGeneratedFilename, String copiedFilename) {
        FileService fileService = new FileService();
        PathInfo pathInfo = fileService.getPathInfoFromFileName(newlyGeneratedFilename);
        PathInfo pathInfo1 = fileService.getPathInfoFromFileName(copiedFilename);
        String reason = null;
        String comment = "log file copied from " + pathInfo.getFileName() + " to " + pathInfo1.getFileName();
        if (AppConstant.FAILURE.equals(status)) {
            reason = "log file copy failed";
        }
        addEvent.addEventTextV2(null, EventName.LOG_FILE_COPIED, status, reason, comment);
    }
    public void trackUnknownException(String errorCode, String errorString) {
        addEvent.addEventTextV2(null, EventName.UN_HANDLE_EXCEPTION,
                AppConstant.FAILURE, errorCode, errorString);
    }
    public void trackExpiredUserSession(SessionData sessionData) {
        if (sessionData == null) {
            return;
        }
        if (StaticService.isInValidString(sessionData.getUsername())) {
            return;
        }
        addEvent.addEventTextV2(sessionData.getUsername(), EventName.EXPIRED_USER_SESSION,
                AppConstant.FAILURE, AppConstant.EXPIRED_USER_SESSION, sessionData.toString());
    }
    public void trackGetUsersFailure(HttpServletRequest request, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.GET_USERS, errorCodes, null);
    }
    public void trackGetAppConfigFailure(HttpServletRequest request, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.GET_APP_CONFIG, errorCodes, null);
    }
    public void trackGetSessionDataFailure(HttpServletRequest request, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.GET_SESSION_DATA, errorCodes, null);
    }

    public void trackGetFileInfoFailure(HttpServletRequest request, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.GET_FILES_INFO, errorCodes, null);
    }
}
