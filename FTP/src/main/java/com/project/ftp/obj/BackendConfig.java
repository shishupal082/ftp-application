package com.project.ftp.obj;

public class BackendConfig {
    private String forgotPasswordMessage;
    private String loadRoleStatusOnPageLoad;
    private String rolesFileName;
    private Integer rateLimitThreshold;// used for register and create_password

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

    public Integer getRateLimitThreshold() {
        return rateLimitThreshold;
    }

    public void setRateLimitThreshold(Integer rateLimitThreshold) {
        this.rateLimitThreshold = rateLimitThreshold;
    }

    @Override
    public String toString() {
        return "BackendConfig{" +
                "forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                ", loadRoleStatusOnPageLoad='" + loadRoleStatusOnPageLoad + '\'' +
                ", rolesFileName='" + rolesFileName + '\'' +
                ", rateLimitThreshold=" + rateLimitThreshold +
                '}';
    }
}
