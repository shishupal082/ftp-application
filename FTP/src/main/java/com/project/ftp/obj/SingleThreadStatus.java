package com.project.ftp.obj;

import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConstant;


public class SingleThreadStatus {
    private String startedTime;
    private String runningItem;
    private String lastUpdatedTime;
    private String lastStatus;

    public SingleThreadStatus(String startedTime, String runningItem, String lastStatus) {
        DateUtilities dateUtilities = new DateUtilities();
        this.startedTime = startedTime;
        this.runningItem = runningItem;
        this.lastUpdatedTime = dateUtilities.getDateStrFromPattern(AppConstant.DateTimeFormat6, "");
        this.lastStatus = lastStatus;
    }

    public String getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(String startedTime) {
        this.startedTime = startedTime;
    }

    public String getRunningItem() {
        return runningItem;
    }

    public void setRunningItem(String runningItem) {
        this.runningItem = runningItem;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    @Override
    public String toString() {
        return "{" +
                "lastStatus='" + lastStatus + '\'' +
                ", lastUpdatedTime='" + lastUpdatedTime + '\'' +
                ", startedTime='" + startedTime + '\'' +
                ", runningItem='" + runningItem + '\'' +
                '}';
    }
}
