package com.project.ftp.event;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.obj.*;
import com.project.ftp.service.FileService;
import com.project.ftp.service.RequestService;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import com.project.ftp.session.SessionData;
import com.project.ftp.session.SessionService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class EventTracking {
    final static private Logger logger = LoggerFactory.getLogger(EventTracking.class);
    private final UserService userService;
    private final SessionService sessionService;
    private final AddEvent addEvent;
    private final String uiUserAgent = "uiUserAgent";
    private final String uiUserName = "uiUserName";
    private final String requestUserAgent = "requestUserAgent";
    private final String sessionDataStr = "sessionDataStr";
    private final String errorCodeStr = "errorCodeStr";
    private final String loginUsername = "loginUsername";
    private final String passcode = "passcode";
    private final String name = "name";
    private final String mobile = "mobile";
    private final String email = "email";
    private final String createPasswordOtp = "createPasswordOtp";
    private final String filepath = "filepath";

    public EventTracking(final AppConfig appConfig,
                         final UserService userService,
                         final EventInterface eventInterface) {
        this.userService = userService;
        this.sessionService = new SessionService(appConfig);
        this.addEvent = new AddEvent(appConfig, eventInterface);
    }

    private String generateCommentString(HashMap<String, String> commentData, ArrayList<String> sequence) {
        String comment = null;
        if (sequence != null && commentData != null) {
            for(String str: sequence) {
                comment = StaticService.joinWithComma(comment, commentData.get(str));
            }
        }
        return comment;
    }

    public void trackSuccessEvent(HttpServletRequest request, EventName eventName) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEventV2(loginUserDetails.getUsername(), eventName);
    }


    public void trackSuccessEventV2(HttpServletRequest request, EventName eventName, String comment) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), eventName, comment);
    }

    public void trackFailureEvent(HttpServletRequest request, EventName eventName, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), eventName, errorCodes, null);
    }
    public void trackFailureEventV2(HttpServletRequest request, EventName eventName, ErrorCodes errorCodes, String comment) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (comment != null) {
            comment = errorCodes.getErrorString() + "," + comment;
        }
        addEvent.addFailureEvent(loginUserDetails.getUsername(), eventName, errorCodes, comment);
    }

    public void trackEventV2(String username, String eventName, String status, String reason, String comment) {
        addEvent.addCommonEventV2(username, eventName, status, reason, comment);
    }

    public void trackChangePasswordSuccess(HttpServletRequest request, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.CHANGE_PASSWORD, uiUsername);
    }

    public void addSuccessLogin(HttpServletRequest request, RequestUserLogin userLogin) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(uiUserAgent);
        sequence.add(sessionDataStr);
        sequence.add(requestUserAgent);

        if (userLogin != null) {
            username = userLogin.getUsername();
            commentData.put(uiUserAgent, userLogin.getUser_agent());
        }
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addSuccessEvent(username, EventName.LOGIN, comment);
    }

    public void addSuccessRegister(HttpServletRequest request, RequestUserRegister userRegister) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(passcode);
        sequence.add(name);
        sequence.add(mobile);
        sequence.add(email);
        sequence.add(uiUserAgent);
        sequence.add(sessionDataStr);
        sequence.add(requestUserAgent);

        if (userRegister != null) {
            commentData.put(passcode, "passcode="+ userRegister.getPasscode());
            commentData.put(name, "name=" + userRegister.getDisplay_name());
            commentData.put(mobile, "mobile=" + userRegister.getMobile());
            commentData.put(email, "email=" + userRegister.getEmail());
            commentData.put(uiUserAgent, userRegister.getUser_agent());
            username = userRegister.getUsername();
        }
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addSuccessEvent(username, EventName.REGISTER, comment);
    }

    public void trackLoginFailure(HttpServletRequest request,
                                  RequestUserLogin requestUserLogin, ErrorCodes errorCodes) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(errorCodeStr);
        sequence.add(loginUsername);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (errorCodes != null) {
            commentData.put(errorCodeStr, errorCodes.getErrorString());
        }
        if (loginUserDetails.getLogin()) {
            commentData.put(loginUsername, "loginUsername="+loginUserDetails.getUsername());
        }
        if (requestUserLogin != null) {
            username = requestUserLogin.getUsername();
            commentData.put(uiUserAgent, requestUserLogin.getUser_agent());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(username, EventName.LOGIN, errorCodes, comment);
    }
    public void trackLoginSocialFailure(HttpServletRequest request,
                                  RequestLoginSocial loginSocial, LoginUserDetails loginUserDetails, ErrorCodes errorCodes) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(errorCodeStr);
        sequence.add(loginUsername);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);
        if (errorCodes != null) {
            commentData.put(errorCodeStr, errorCodes.getErrorString());
        }
        if (loginUserDetails.getLogin()) {
            username = loginUserDetails.getUsername();
            commentData.put(loginUsername, "loginUsername="+username);
        }
        if (loginSocial != null) {
            commentData.put(uiUserAgent, loginSocial.getUser_agent());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(username, EventName.LOGIN_SOCIAL, errorCodes, comment);
    }

    public void trackRegisterFailure(HttpServletRequest request,
                                     RequestUserRegister requestUserRegister,
                                     ErrorCodes errorCodes) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(passcode);
        sequence.add(name);
        sequence.add(mobile);
        sequence.add(email);
        sequence.add(errorCodeStr);
        sequence.add(loginUsername);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);
        if (requestUserRegister != null) {
            username = requestUserRegister.getUsername();
            commentData.put(passcode, "passcode="+requestUserRegister.getPasscode());
            commentData.put(name, "name=" + requestUserRegister.getDisplay_name());
            commentData.put(mobile, "mobile=" + requestUserRegister.getMobile());
            commentData.put(email, "email=" + requestUserRegister.getEmail());
            commentData.put(uiUserAgent, requestUserRegister.getUser_agent());
        }
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        if (loginUserDetails.getLogin()) {
            commentData.put(loginUsername, "loginUsername=" + loginUserDetails.getUsername());
        }
        if (errorCodes != null) {
            commentData.put(errorCodeStr, errorCodes.getErrorString());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(username, EventName.REGISTER, errorCodes, comment);
    }

    public void trackForgotPasswordSuccess(HttpServletRequest request,
                                           RequestForgotPassword forgotPassword) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(email);
        sequence.add(mobile);
        sequence.add(errorCodeStr);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);

        if (forgotPassword != null) {
            username = forgotPassword.getUsername();
            commentData.put(email, "email=" + forgotPassword.getEmail());
            commentData.put(mobile, "mobile="+forgotPassword.getMobile());
            commentData.put(uiUserAgent, forgotPassword.getUserAgent());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addSuccessEvent(username, EventName.FORGOT_PASSWORD, comment);
    }

    public void trackForgotPasswordFailure(HttpServletRequest request,
                                           RequestForgotPassword forgotPassword,
                                           ErrorCodes errorCode) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(mobile);
        sequence.add(email);
        sequence.add(errorCodeStr);
        sequence.add(loginUsername);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);

        if (forgotPassword != null) {
            username = forgotPassword.getUsername();
            commentData.put(mobile, "mobile="+forgotPassword.getMobile());
            commentData.put(email, "email=" + forgotPassword.getEmail());
            commentData.put(uiUserAgent, forgotPassword.getUserAgent());
        }
        if (loginUserDetails.getLogin()) {
            commentData.put(loginUsername, "loginUsername=" + loginUserDetails.getUsername());
        }
        if (errorCode != null) {
            commentData.put(errorCodeStr, errorCode.getErrorString());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(username, EventName.FORGOT_PASSWORD, errorCode, comment);
    }

    public void trackCreatePasswordSuccess(HttpServletRequest request,
                                           RequestCreatePassword createPassword) {
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(createPasswordOtp);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);

        if (createPassword != null) {
            username = createPassword.getUsername();
            commentData.put(createPasswordOtp, "otp="+createPassword.getCreatePasswordOtp());
            commentData.put(uiUserAgent, createPassword.getUserAgent());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addSuccessEvent(username, EventName.CREATE_PASSWORD, comment);
    }

    public void trackCreatePasswordFailure(HttpServletRequest request,
                                           RequestCreatePassword createPassword,
                                           ErrorCodes errorCode) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String username = null;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(createPasswordOtp);
        sequence.add(errorCodeStr);
        sequence.add(loginUsername);
        sequence.add(uiUserAgent);
        sequence.add(requestUserAgent);
        sequence.add(sessionDataStr);

        if (createPassword != null) {
            username = createPassword.getUsername();
            commentData.put(createPasswordOtp, "otp="+createPassword.getCreatePasswordOtp());
            commentData.put(uiUserAgent, createPassword.getUserAgent());
        }
        if (loginUserDetails.getLogin()) {
            commentData.put(loginUsername, "loginUsername=" + loginUserDetails.getUsername());
        }
        if (errorCode != null) {
            commentData.put(errorCodeStr, errorCode.getErrorString());
        }
        commentData.put(requestUserAgent, RequestService.getRequestUserAgent(request));
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(username, EventName.CREATE_PASSWORD, errorCode, comment);
    }

    public void trackLogout(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment;
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(errorCodeStr);
        sequence.add(sessionDataStr);
        commentData.put(sessionDataStr, sessionService.getCurrentSessionDataV2(request));
        if (loginUserDetails.getLogin()) {
            comment = this.generateCommentString(commentData, sequence);
            addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.LOGOUT, comment);
        } else {
            commentData.put(errorCodeStr, ErrorCodes.LOGOUT_USER_NOT_LOGIN.getErrorString());
            comment = this.generateCommentString(commentData, sequence);
            addEvent.addFailureEventV2(EventName.LOGOUT, ErrorCodes.LOGOUT_USER_NOT_LOGIN, comment);
        }
    }

    public void trackChangePasswordFailure(HttpServletRequest request, ErrorCodes errorCodes, String uiUsernameStr) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(uiUserName);
        sequence.add(errorCodeStr);
        commentData.put(uiUserName, uiUsernameStr);
        if (errorCodes != null) {
            commentData.put(errorCodeStr, errorCodes.getErrorString());
        }
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.CHANGE_PASSWORD, errorCodes, comment);
    }

    public void addSuccessViewFile(HttpServletRequest request,
                                   String filepath, String container, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath=" + filepath + ",container=" + container + ",uiUsername=" + uiUsername;
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE, comment);
    }

    public void addSuccessDownloadFile(HttpServletRequest request, String filepath, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath="+filepath + ",uiUsername="+uiUsername;
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.DOWNLOAD_FILE, comment);
    }

    public void addSuccessDeleteFile(HttpServletRequest request, RequestDeleteFile deleteFile, String uiUsernameStr) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(filepath);
        sequence.add(uiUserName);
        commentData.put(uiUserName, uiUsernameStr);
        if (deleteFile != null) {
            commentData.put(filepath, deleteFile.getFilename());
        }
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), EventName.DELETE_FILE, comment);
    }

    public void trackViewFileFailure(HttpServletRequest request, String filepath,
                                     ErrorCodes errorCodes, String container, String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String comment = "filepath=" + filepath + ",container=" + container + ",uiUsername=" + uiUsername;
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
                                       String uiUsernameStr) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        HashMap<String, String> commentData = new HashMap<>();
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add(filepath);
        sequence.add(uiUserName);
        commentData.put(uiUserName, uiUsernameStr);
        if (deleteFile != null) {
            commentData.put(filepath, deleteFile.getFilename());
        }
        String comment = this.generateCommentString(commentData, sequence);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.DELETE_FILE, errorCodes, comment);
    }

    public void addSuccessUploadFile(HttpServletRequest request,
                                     FormDataContentDisposition fileDetail,
                                     String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filename = null;
        if (fileDetail != null) {
            filename = fileDetail.getFileName();
        }
        String comment = "";
        comment += "filepath=" + filename;
        comment += ",uiUsername=" + uiUsername;
        EventName eventName = EventName.UPLOAD_FILE;
        addEvent.addSuccessEvent(loginUserDetails.getUsername(), eventName, comment);
    }

    public void addFailureUploadFile(HttpServletRequest request,
                                     ErrorCodes errorCodes,
                                     FormDataContentDisposition fileDetail,
                                     String uiUsername) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String filename = null;
        if (fileDetail != null) {
            filename = fileDetail.getFileName();
        }
        String comment = "";
        comment += "filepath=" + filename;
        comment += ",uiUsername=" + uiUsername;
        EventName eventName = EventName.UPLOAD_FILE;
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
        eventNameStr = StaticService.join("_", "ui", eventNameStr);
        addEvent.addCommonEvent(loginUserDetails.getUsername(), eventNameStr, status, reason, comment);
    }

    public void trackLandingPage(HttpServletRequest request, EventName eventName) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        String reason = "LANDING_PAGE";
        String comment = sessionService.getCurrentSessionDataV2(request);
        String username = loginUserDetails.getUsername();
        addEvent.addSuccessEventV3(username, eventName, reason, comment);
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
