package com.project.ftp.bridge.config;

public class BridgeConfig {
    private final EmailConfig emailConfig;
    private final CreatePasswordEmailConfig createPasswordEmailConfig;
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
}
