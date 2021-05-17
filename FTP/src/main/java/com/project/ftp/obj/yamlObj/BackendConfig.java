package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class BackendConfig {
    private String forgotPasswordMessage;
    private boolean forgotPasswordEnable;
    private String loadRoleStatusOnPageLoad;
    private String eventDataFilenamePattern;
    private String userDataFilename;
    private String fileDataFilename;
    private String staticDataFilename;
    private String addTextV2TimeStamp;
    private ArrayList<String> fileNotFoundMapping;
    private ArrayList<String> rolesFileName;
    private int rateLimitThreshold;// used for register and create_password

    public BackendConfig() {}

    public String getForgotPasswordMessage() {
        return forgotPasswordMessage;
    }

    public void setForgotPasswordMessage(String forgotPasswordMessage) {
        this.forgotPasswordMessage = forgotPasswordMessage;
    }

    public boolean isForgotPasswordEnable() {
        return forgotPasswordEnable;
    }

    public void setForgotPasswordEnable(boolean forgotPasswordEnable) {
        this.forgotPasswordEnable = forgotPasswordEnable;
    }

    public String getLoadRoleStatusOnPageLoad() {
        return loadRoleStatusOnPageLoad;
    }

    public void setLoadRoleStatusOnPageLoad(String loadRoleStatusOnPageLoad) {
        this.loadRoleStatusOnPageLoad = loadRoleStatusOnPageLoad;
    }

    public ArrayList<String> getRolesFileName() {
        return rolesFileName;
    }

    public void setRolesFileName(ArrayList<String> rolesFileName) {
        this.rolesFileName = rolesFileName;
    }

    public int getRateLimitThreshold() {
        return rateLimitThreshold;
    }

    public void setRateLimitThreshold(int rateLimitThreshold) {
        this.rateLimitThreshold = rateLimitThreshold;
    }

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

    public String getAddTextV2TimeStamp() {
        return addTextV2TimeStamp;
    }

    public void setAddTextV2TimeStamp(String addTextV2TimeStamp) {
        this.addTextV2TimeStamp = addTextV2TimeStamp;
    }

    public ArrayList<String> getFileNotFoundMapping() {
        return fileNotFoundMapping;
    }

    public void setFileNotFoundMapping(ArrayList<String> fileNotFoundMapping) {
        this.fileNotFoundMapping = fileNotFoundMapping;
    }

    public String getStaticDataFilename() {
        return staticDataFilename;
    }

    public void setStaticDataFilename(String staticDataFilename) {
        this.staticDataFilename = staticDataFilename;
    }

    @Override
    public String toString() {
        return "BackendConfig{" +
                "forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", forgotPasswordEnable=" + forgotPasswordEnable +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                ", eventDataFilenamePattern='" + eventDataFilenamePattern + '\'' +
                ", userDataFilename='" + userDataFilename + '\'' +
                ", fileDataFilename='" + fileDataFilename + '\'' +
                ", staticDataFilename='" + staticDataFilename + '\'' +
                ", addTextV2TimeStamp='" + addTextV2TimeStamp + '\'' +
                ", fileNotFoundMapping=" + fileNotFoundMapping +
                ", rolesFileName=" + rolesFileName +
                ", rateLimitThreshold=" + rateLimitThreshold +
                '}';
    }
}
