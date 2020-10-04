package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class UiBackendConfig {
    private boolean forgotPasswordEnable;

    public UiBackendConfig() {}

    public boolean isForgotPasswordEnable() {
        return forgotPasswordEnable;
    }

    public void setForgotPasswordEnable(boolean forgotPasswordEnable) {
        this.forgotPasswordEnable = forgotPasswordEnable;
    }

    @Override
    public String toString() {
        return "UiBackendConfig{" +
                "forgotPasswordEnable=" + forgotPasswordEnable +
                '}';
    }
}
