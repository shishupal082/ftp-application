package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestEventTracking {
    private String event;
    private String status;
    private String reason;
    private String comment;
    private String role_id;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    @Override
    public String toString() {
        return "RequestEventTracking{" +
                "event='" + event + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", comment='" + comment + '\'' +
                ", role_id='" + role_id + '\'' +
                '}';
    }
}
