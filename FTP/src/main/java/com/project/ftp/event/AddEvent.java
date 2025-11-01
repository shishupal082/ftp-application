package com.project.ftp.event;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.obj.yamlObj.EventConfig;
import com.project.ftp.service.StaticService;

public class AddEvent {
    private final AppConfig appConfig;
    private final EventInterface eventInterface;
    public AddEvent(final AppConfig appConfig, final EventInterface eventInterface) {
        this.appConfig = appConfig;
        this.eventInterface = eventInterface;
    }
    private boolean isEventLoggingDisabled() {
        EventConfig eventConfig = appConfig.getFtpConfiguration().getEventConfig();
        return eventConfig == null || !eventConfig.isEnabled();
    }
    public void addCommonEvent(String configFilePath, String username, String eventNameStr, String status, String reason, String comment) {
        if (this.isEventLoggingDisabled()) {
            return;
        }
        eventInterface.addText(configFilePath, username, eventNameStr, status, reason, comment);
    }
    public void addFailureEvent(String configFilePath, String username, EventName eventName,
                                ErrorCodes errorCode, String comment) {
        String errorCodeString = null;
        if (errorCode != null) {
            errorCodeString = errorCode.getErrorCode();
            if (StaticService.isInValidString(comment)) {
                comment = errorCode.getErrorString();
            }
        }
        String eventNameStr = null;
        if (eventName != null) {
            eventNameStr = eventName.getName();
        }
        this.addCommonEvent(configFilePath, username, eventNameStr, AppConstant.FAILURE, errorCodeString, comment);
    }
    public void addFailureEventV2(String configFilePath, EventName eventName, ErrorCodes errorCode, String comment) {
        this.addFailureEvent(configFilePath,null, eventName, errorCode, comment);
    }
    public void addSuccessEvent(String configFilePath, String username, EventName eventName, String comment) {
        String eventNameStr = null;
        if (eventName != null) {
            eventNameStr = eventName.getName();
        }
        this.addCommonEvent(configFilePath, username, eventNameStr, AppConstant.SUCCESS, null, comment);
    }

    public void addSuccessEventV2(String configFilePath, String username, EventName eventName) {
        this.addSuccessEvent(configFilePath, username, eventName, null);
    }

    public void addSuccessEventV3(String configFilePath, String username, EventName eventName, String reason, String comment) {
        String eventNameStr = null;
        if (eventName != null) {
            eventNameStr = eventName.getName();
        }
        this.addCommonEvent(configFilePath, username, eventNameStr, AppConstant.SUCCESS, reason, comment);
    }

    public void addEventTextV2(String configFilePath, String username, EventName eventName, String status, String reason, String comment) {
        if (this.isEventLoggingDisabled()) {
            return;
        }
        String eventNameStr = null;
        if (eventName != null) {
            eventNameStr = eventName.getName();
        }
        eventInterface.addTextV2(configFilePath, username, eventNameStr, status, reason, comment);
    }

    public void addCommonEventV2(String configFilePath, String username, String eventNameStr, String status, String reason, String comment) {
        if (this.isEventLoggingDisabled()) {
            return;
        }
        eventInterface.addTextV2(configFilePath, username, eventNameStr, status, reason, comment);
    }
}
