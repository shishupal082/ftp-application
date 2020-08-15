package com.project.ftp.event;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.RequestDeleteFile;
import com.project.ftp.obj.RequestUserLogin;
import com.project.ftp.obj.RequestUserRegister;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletRequest;

public class EventTracking {
    final UserService userService;
    final AppConfig appConfig;
    final AddEvent addEvent;
    public EventTracking(final AppConfig appConfig, final UserService userService, final EventInterface eventInterface) {
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
    public void addForgotPassword() {
        addEvent.addForgotPassword();
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
    public void trackLoginFailure(RequestUserLogin requestUserLogin, ErrorCodes errorCodes) {
        String username = null;
        if (requestUserLogin != null) {
            username = requestUserLogin.getUsername();
        }
        addEvent.addFailureEvent(username, EventName.LOGIN, errorCodes, null);
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

    public void addSuccessViewFile(HttpServletRequest request, String filepath) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessViewFile(loginUserDetails.getUsername(), filepath);
    }
    public void addSuccessDownloadFile(HttpServletRequest request, String filepath) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessDownloadFile(loginUserDetails.getUsername(), filepath);
    }
    public void addSuccessDeleteFile(HttpServletRequest request, RequestDeleteFile deleteFile) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addSuccessDeleteFile(loginUserDetails.getUsername(), deleteFile);
    }
    public void trackViewFileFailure(HttpServletRequest request, String filepath, ErrorCodes errorCodes) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        addEvent.addFailureEvent(loginUserDetails.getUsername(), EventName.VIEW_FILE, errorCodes, filepath);
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
        } else if (!AppConstant.V2.equals(apiVersion)) {
            eventName = EventName.UPLOAD_FILE_V2;
        }
        addEvent.addFailureEvent(loginUserDetails.getUsername(), eventName, errorCodes, comment);
    }
}
