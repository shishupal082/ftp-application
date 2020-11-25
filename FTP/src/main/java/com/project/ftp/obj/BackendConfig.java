package com.project.ftp.obj;

public class BackendConfig {
    private String forgotPasswordMessage;
    private String loadRoleStatusOnPageLoad;

    public BackendConfig() {}

    public String getForgotPasswordMessage() {
        return forgotPasswordMessage;
    }

    public void setForgotPasswordMessage(String forgotPasswordMessage) {
        this.forgotPasswordMessage = forgotPasswordMessage;
    }

    public String getLoadRoleStatusOnPageLoad() {
        return loadRoleStatusOnPageLoad;
    }

    public void setLoadRoleStatusOnPageLoad(String loadRoleStatusOnPageLoad) {
        this.loadRoleStatusOnPageLoad = loadRoleStatusOnPageLoad;
    }

    @Override
    public String toString() {
        return "BackendConfig{" +
                "forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                '}';
    }
}
