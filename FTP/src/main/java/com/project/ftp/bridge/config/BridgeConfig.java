package com.project.ftp.bridge.config;

import com.project.ftp.bridge.roles.obj.Roles;

public class BridgeConfig {
    private final EmailConfig emailConfig;
    private final CreatePasswordEmailConfig createPasswordEmailConfig;
    private Roles roles;
    public BridgeConfig(EmailConfig emailConfig,
                        CreatePasswordEmailConfig createPasswordEmailConfig) {
        this.emailConfig = emailConfig;
        this.createPasswordEmailConfig = createPasswordEmailConfig;
    }
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }
    public CreatePasswordEmailConfig getCreatePasswordEmailConfig() {
        return createPasswordEmailConfig;
    }

    public Roles getRoles() {
        return roles;
    }
    public void setRoles(Roles roles) {
        this.roles = roles;
    }
}
