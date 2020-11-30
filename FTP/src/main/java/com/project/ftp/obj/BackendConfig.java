package com.project.ftp.obj;

public class BackendConfig {
    private String forgotPasswordMessage;
    private String loadRoleStatusOnPageLoad;
    private String rolesFileName;

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

    public String getRolesFileName() {
        return rolesFileName;
    }

    public void setRolesFileName(String rolesFileName) {
        this.rolesFileName = rolesFileName;
    }

    @Override
    public String toString() {
        return "BackendConfig{" +
                "forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                ", rolesFileName='" + rolesFileName + '\'' +
                '}';
    }
}
