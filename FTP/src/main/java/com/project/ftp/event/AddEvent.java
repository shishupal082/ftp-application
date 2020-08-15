package com.project.ftp.event;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.obj.RequestDeleteFile;
import com.project.ftp.obj.RequestUserLogin;
import com.project.ftp.obj.RequestUserRegister;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddEvent {
    final static Logger logger = LoggerFactory.getLogger(AddEvent.class);
    final AppConfig appConfig;
    final EventInterface eventInterface;
    public AddEvent(final AppConfig appConfig, final EventInterface eventInterface) {
        this.appConfig = appConfig;
        this.eventInterface = eventInterface;
    }
    public void addFailureEvent(String username, EventName eventName, ErrorCodes errorCodes, String comment) {
        String errorCode = null;
        if (errorCodes != null) {
            errorCode = errorCodes.getErrorCode();
        }
        eventInterface.addText(username, eventName.getName(), AppConstant.FAILURE, errorCode, comment);
    }
    public void addSuccessLogin(RequestUserLogin userLogin) {
        String username = null;
        if (userLogin != null) {
            username = userLogin.getUsername();
        }
        eventInterface.addText(username, EventName.LOGIN.getName(), AppConstant.SUCCESS,
                "", "");
    }
    public void addSuccessRegister(RequestUserRegister requestUserRegister) {
        String comment = "", username = null;
        if (requestUserRegister != null) {
            comment += "passcode="+ requestUserRegister.getPasscode();
            comment += ",name=" + requestUserRegister.getDisplay_name();
            username = requestUserRegister.getUsername();
        }
        eventInterface.addText(username, EventName.REGISTER.getName(),
                AppConstant.SUCCESS, "", comment);
    }

    public void addSuccessChangePassword(String username) {
        eventInterface.addText(username, EventName.CHANGE_PASSWORD.getName(), AppConstant.SUCCESS, "", "");
    }
    public void addLogout(String username) {
        String status = AppConstant.SUCCESS;
        if (StaticService.isInValidString(username)) {
            status = AppConstant.FAILURE;
        }
        eventInterface.addText(username, EventName.LOGOUT.getName(), status,
                "", "");
    }
    public void addForgotPassword() {
        eventInterface.addText("", EventName.FORGOT_PASSWORD.getName(), "", "", "");
    }

    public void addSuccessViewFile(String username, String filepath) {
        eventInterface.addText(username, EventName.VIEW_FILE.getName(), AppConstant.SUCCESS,
                "", filepath);
    }
    public void addSuccessDownloadFile(String username, String filepath) {
        eventInterface.addText(username, EventName.DOWNLOAD_FILE.getName(), AppConstant.SUCCESS,
                "", filepath);
    }
    public void addSuccessDeleteFile(String username, RequestDeleteFile deleteFile) {
        String deleteFilePath = null;
        if (deleteFile != null) {
            deleteFilePath = deleteFile.getFilename();
        }
        eventInterface.addText(username, EventName.DELETE_FILE.getName(), AppConstant.SUCCESS,
                "", deleteFilePath);
    }
    public void addSuccessUploadFile(String username, String filepath, String subject, String heading) {
        String comment = "";
        comment += "filepath=" + filepath;
        comment += ",subject=" + subject;
        comment += ",heading=" + heading;
        EventName eventName = EventName.UPLOAD_FILE;
        String apiVersion = StaticService.getUploadFileApiVersion(appConfig);
        if (AppConstant.V1.equals(apiVersion)) {
            eventName = EventName.UPLOAD_FILE_V1;
        } else if (!AppConstant.V2.equals(apiVersion)) {
            eventName = EventName.UPLOAD_FILE_V2;
        }
        eventInterface.addText(username, eventName.getName(), AppConstant.SUCCESS,
                "", comment);
    }
}
