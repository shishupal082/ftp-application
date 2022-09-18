package com.project.ftp.session;

import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;

public class SessionData {
    private String sessionId;
    private Long updatedTime;
    private String orgUsername;
    private String username;
    private String visibleDate;
    private boolean isInfiniteTTL;
    public SessionData(String sessionId, Long updatedTime) {
        this.sessionId = sessionId;
        this.updatedTime = updatedTime;
        visibleDate = StaticService.getDateStrFromTimeMs(AppConstant.DateTimeFormat2, updatedTime);
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
        visibleDate = StaticService.getDateStrFromTimeMs(AppConstant.DateTimeFormat2, updatedTime);
    }

    public String getOrgUsername() {
        return orgUsername;
    }

    public void setOrgUsername(String orgUsername) {
        this.orgUsername = orgUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getVisibleDate() {
        return visibleDate;
    }

    public void setVisibleDate(String visibleDate) {
        this.visibleDate = visibleDate;
    }
    public boolean isInfiniteTTL() {
        return isInfiniteTTL;
    }

    public void setInfiniteTTL(boolean infiniteTTL) {
        isInfiniteTTL = infiniteTTL;
    }

    @Override
    public String toString() {
        return "SessionData{" +
                "sessionId='" + sessionId + '\'' +
                ", updatedTime=" + updatedTime +
                ", orgUsername='" + orgUsername + '\'' +
                ", username='" + username + '\'' +
                ", visibleDate='" + visibleDate + '\'' +
                ", isInfiniteTTL=" + isInfiniteTTL +
                '}';
    }
}
