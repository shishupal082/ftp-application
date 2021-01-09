package com.project.ftp.obj;

import java.util.ArrayList;

public class BackendConfig {
    private String forgotPasswordMessage;
    private boolean forgotPasswordEnable;
    private String loadRoleStatusOnPageLoad;
    private ArrayList<String> rolesFileName;
    private Integer rateLimitThreshold;// used for register and create_password
    private String eventDataFilenamePattern;

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

    public Integer getRateLimitThreshold() {
        return rateLimitThreshold;
    }

    public void setRateLimitThreshold(Integer rateLimitThreshold) {
        this.rateLimitThreshold = rateLimitThreshold;
    }

    public String getEventDataFilenamePattern() {
        return eventDataFilenamePattern;
    }

    public void setEventDataFilenamePattern(String eventDataFilenamePattern) {
        this.eventDataFilenamePattern = eventDataFilenamePattern;
    }

    @Override
    public String toString() {
        return "BackendConfig{" +
                "forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", forgotPasswordEnable=" + forgotPasswordEnable +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                ", rolesFileName=" + rolesFileName +
                ", rateLimitThreshold=" + rateLimitThreshold +
                ", eventDataFilenamePattern='" + eventDataFilenamePattern + '\'' +
                '}';
    }
}
