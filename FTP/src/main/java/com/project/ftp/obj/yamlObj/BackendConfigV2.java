package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class BackendConfigV2 {
    private String eventDataFilenamePattern;
    private String userDataFilename;
    private String fileDataFilename;

    public BackendConfigV2() {}

    public String getEventDataFilenamePattern() {
        return eventDataFilenamePattern;
    }

    public void setEventDataFilenamePattern(String eventDataFilenamePattern) {
        this.eventDataFilenamePattern = eventDataFilenamePattern;
    }

    public String getUserDataFilename() {
        return userDataFilename;
    }

    public void setUserDataFilename(String userDataFilename) {
        this.userDataFilename = userDataFilename;
    }

    public String getFileDataFilename() {
        return fileDataFilename;
    }

    public void setFileDataFilename(String fileDataFilename) {
        this.fileDataFilename = fileDataFilename;
    }

    @Override
    public String toString() {
        return "BackendConfigV2{" +
                "eventDataFilenamePattern='" + eventDataFilenamePattern + '\'' +
                ", userDataFilename='" + userDataFilename + '\'' +
                ", fileDataFilename='" + fileDataFilename + '\'' +
                '}';
    }
}
