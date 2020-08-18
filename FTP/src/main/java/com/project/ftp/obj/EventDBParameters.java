package com.project.ftp.obj;

import com.project.ftp.service.StaticService;

public class EventDBParameters {
    private final String username;
    private final String event;
    private final String status;
    private final String reason;
    private final String comment;

    public EventDBParameters(String username, String event, String status, String reason, String comment) {
        int usernameMaxLength = 255;
        int eventMaxLength = 127;
        int statusMaxLength = 63;
        int reasonMaxLength = 255;
        int commentMaxLength = 511;
        if (StaticService.isInValidString(username)) {
            this.username = null;
        } else {
            this.username = StaticService.truncateString(username, usernameMaxLength);
        }
        if (StaticService.isInValidString(event)) {
            this.event = null;
        } else {
            this.event = StaticService.truncateString(event, eventMaxLength);
        }
        if (StaticService.isInValidString(status)) {
            this.status = null;
        } else {
            this.status = StaticService.truncateString(status, statusMaxLength);
        }
        if (StaticService.isInValidString(reason)) {
            this.reason = null;
        } else {
            this.reason = StaticService.truncateString(reason, reasonMaxLength);
        }
        if (StaticService.isInValidString(comment)) {
            this.comment = null;
        } else {
            this.comment = StaticService.truncateString(comment, commentMaxLength);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getEvent() {
        return event;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "EventDBParameters{" +
                "username='" + username + '\'' +
                ", event='" + event + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
