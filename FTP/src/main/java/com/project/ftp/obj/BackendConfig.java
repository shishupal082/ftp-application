package com.project.ftp.obj;

public class BackendConfig {
    private String forgotPasswordMessage;

    public BackendConfig() {}

    public String getForgotPasswordMessage() {
        return forgotPasswordMessage;
    }

    public void setForgotPasswordMessage(String forgotPasswordMessage) {
        this.forgotPasswordMessage = forgotPasswordMessage;
    }

    @Override
    public String toString() {
        return "BackendConfig{" +
                "forgotPasswordMessage='" + forgotPasswordMessage + '\'' +
                '}';
    }
}
