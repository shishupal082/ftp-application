package com.project.ftp.event;

import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.service.StaticService;

public class AddEvent {
    private final EventInterface eventInterface;
    public AddEvent(final EventInterface eventInterface) {
        this.eventInterface = eventInterface;
    }
    public void addCommonEvent(String username, String eventNameStr, String status, String reason, String comment) {
        eventInterface.addText(username, eventNameStr, status, reason, comment);
    }
    public void addFailureEvent(String username, EventName eventName,
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
        this.addCommonEvent(username, eventNameStr, AppConstant.FAILURE, errorCodeString, comment);
    }
    public void addFailureEventV2(EventName eventName, ErrorCodes errorCode, String comment) {
        this.addFailureEvent(null, eventName, errorCode, comment);
    }
    public void addSuccessEvent(String username, EventName eventName, String comment) {
        String eventNameStr = null;
        if (eventName != null) {
            eventNameStr = eventName.getName();
        }
        this.addCommonEvent(username, eventNameStr, AppConstant.SUCCESS, null, comment);
    }
    public void addSuccessEventV2(String username, EventName eventName) {
        this.addSuccessEvent(username, eventName, null);
    }

    public void addEventTextV2(String username, EventName eventName, String status, String reason, String comment) {
        String eventNameStr = null;
        if (eventName != null) {
            eventNameStr = eventName.getName();
        }
        eventInterface.addTextV2(username, eventNameStr, status, reason, comment);
    }
}
