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
import com.project.ftp.session.SessionService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class EventTracking {
    final static private Logger logger = LoggerFactory.getLogger(EventTracking.class);
    private final UserService userService;
    private final SessionService sessionService;
    private final AppConfig appConfig;
    private final AddEvent addEvent;
    public EventTracking(final AppConfig appConfig,
                         final UserService userService,
                         final EventInterface eventInterface) {
        this.appConfig = appConfig;
        this.userService = userService;
        this.sessionService = new SessionService(appConfig);
        this.addEvent = new AddEvent(eventInterface);
    }

    public void trackSuccessEvent(HttpServletRequest request, EventName eventName) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), eventName);
    }

    public void trackFailureEvent(HttpServletRequest request, EventName eventName, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), eventName, errorCodes, null);
    }

    public void trackChangePasswordSuccess(HttpServletRequest request, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.CHANGE_PASSWORD, uiUsername);
    }
    public void addSuccessLogin(HttpServletRequest request, RequestUserLogin userLogin) {
        String username = null, comment = "";
        if (userLogin != null) {
            username = userLogin.getUsername();
            comment = userLogin.getUser_agent();
        }
        String sessionDataStr = sessionService.getCurrentSessionDataV2(request);
        if (StaticService.isValidString(sessionDataStr)) {
            if (StaticService.isValidString(comment)) {
                comment += "," + sessionDataStr;
            } else {
                comment = sessionDataStr;
            }
        }
        addEvent.addSuccessEvent(username, EventName.LOGIN, comment);
    }
    public void addSuccessRegister(HttpServletRequest request, RequestUserRegister userRegister) {
        String comment = "", username = null;
        if (userRegister != null) {
            comment += "passcode="+ userRegister.getPasscode();
            comment += ",name=" + userRegister.getDisplay_name();
            comment += ",user_agent=" + userRegister.getUser_agent();
            username = userRegister.getUsername();
        }
        String sessionDataStr = sessionService.getCurrentSessionDataV2(request);
        if (StaticService.isValidString(sessionDataStr)) {
            comment += "," + sessionDataStr;
        } else {
            comment = sessionDataStr;
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
    public void trackChangePasswordFailure(HttpServletRequest request, ErrorCodes errorCodes, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "";
        if (errorCodes != null) {
            comment = uiUsername + "," + errorCodes.getErrorString();
        } else {
            comment = uiUsername;
        }
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.CHANGE_PASSWORD, errorCodes, comment);
    }
    public void trackLoginFailure(HttpServletRequest request,
                                  RequestUserLogin requestUserLogin, ErrorCodes errorCodes) {
        String username = null;
        String comment = null;
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (loginUserDetails.getLogin()) {
            if (errorCodes != null) {
                comment = errorCodes.getErrorString() + "," + loginUserDetails.getUsername();
            } else {
                comment = loginUserDetails.getUsername();
            }
        } else if (errorCodes != null) {
            comment = errorCodes.getErrorString();
        }
        if (requestUserLogin != null) {
            username = requestUserLogin.getUsername();
            if (StaticService.isValidString(comment)) {
                comment += ";" + requestUserLogin.getUser_agent();
            } else {
                comment = requestUserLogin.getUser_agent();
            }
            String encryptedPassword = StaticService.encryptAesPassword(appConfig, requestUserLogin.getPassword());
            logger.info("Encrypted password: {}", encryptedPassword);
        }
        String sessionDataStr = sessionService.getCurrentSessionDataV2(request);
        if (StaticService.isValidString(sessionDataStr)) {
            if (StaticService.isValidString(comment)) {
                comment += ";" + sessionDataStr;
            } else {
                comment = sessionDataStr;
            }
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
            comment += ",user_agent=" + requestUserRegister.getUser_agent();
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
        String sessionDataStr = sessionService.getCurrentSessionDataV2(request);
        if (StaticService.isValidString(comment)) {
            comment += "," + sessionDataStr;
        } else {
            comment = sessionDataStr;
        }
        addEvent.addFailureEvent(username, EventName.REGISTER, errorCodes, comment);
    }

    public void addSuccessViewFile(HttpServletRequest request,
                                   String filepath, String isIframe, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath=" + filepath + ",isIframe=" + isIframe + ",uiUsername=" + uiUsername;
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE, comment);
    }
    public void addSuccessDownloadFile(HttpServletRequest request, String filepath, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath="+filepath + ",uiUsername="+uiUsername;
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.DOWNLOAD_FILE, comment);
    }
    public void addSuccessDeleteFile(HttpServletRequest request, RequestDeleteFile deleteFile, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String deleteFilePath = null;
        if (deleteFile != null) {
            deleteFilePath = deleteFile.getFilename();
        }
        String comment = "filepath="+deleteFilePath + ",uiUsername="+uiUsername;
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.DELETE_FILE, comment);
    }
    public void trackViewFileFailure(HttpServletRequest request, String filepath,
                                     ErrorCodes errorCodes, String isIframe, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath=" + filepath + ",isIframe=" + isIframe + ",uiUsername=" + uiUsername;
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE, errorCodes, comment);
    }

    public void trackDownloadFileFailure(HttpServletRequest request, String filepath,
                                         ErrorCodes errorCodes, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath="+filepath + ",uiUsername="+uiUsername;
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.DOWNLOAD_FILE, errorCodes, comment);
    }

    public void trackDeleteFileFailure(HttpServletRequest request,
                                       RequestDeleteFile deleteFile,
                                       ErrorCodes errorCodes,
                                       String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filepath = null;
        if (deleteFile != null) {
            filepath = deleteFile.getFilename();
        }
        String comment = "filepath="+filepath + ",uiUsername="+uiUsername;
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.DELETE_FILE, errorCodes, comment);
    }

    public void addSuccessUploadFile(HttpServletRequest request,
                                     FormDataContentDisposition fileDetail,
                                     String subject, String heading, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filename = null;
        if (fileDetail != null) {
            filename = fileDetail.getFileName();
        }
        String comment = "";
        comment += "filepath=" + filename;
        comment += ",subject=" + subject;
        comment += ",heading=" + heading;
        comment += ",uiUsername=" + uiUsername;
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
                                     FormDataContentDisposition fileDetail,
                                     String subject, String heading, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filename = null;
        if (fileDetail != null) {
            filename = fileDetail.getFileName();
        }
        String comment = "";
        comment += "filepath=" + filename;
        comment += ",subject=" + subject;
        comment += ",heading=" + heading;
        comment += ",uiUsername=" + uiUsername;
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
    public void trackUIEvent(HttpServletRequest request, RequestEventTracking eventTracking) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String eventNameStr = null, reason = null, comment = null, status = null;
        if (eventTracking != null) {
            eventNameStr = eventTracking.getEvent();
            status = eventTracking.getStatus();
            reason = eventTracking.getReason();
            comment = eventTracking.getComment();
        }
        String sessionDataStr = sessionService.getCurrentSessionDataV2(request);
        if (StaticService.isInValidString(comment)) {
            comment = sessionDataStr;
        } else {
            comment += "," + sessionDataStr;
        }
        addEvent.addCommonEvent(loginUserDetails.getUsername(), eventNameStr, status, reason, comment);
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

    public void trackApplicationStart(String instance) {
        String comment = "appVersion=" + AppConstant.AppVersion + ",instance="+instance;
        addEvent.addEventTextV2(null, EventName.APPLICATION_START,
                AppConstant.SUCCESS, null, comment);
    }

    public void trackUnknownException(String errorCode, String errorString) {
        addEvent.addEventTextV2(null, EventName.UN_HANDLE_EXCEPTION,
                AppConstant.FAILURE, errorCode, errorString);
    }

}
