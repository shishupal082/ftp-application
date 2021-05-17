package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FtlConfig {
    private String description;
    private String keywords;
    private String author;
    private String title;
    private String headingJson;
    private String afterLoginLinkJson;
    private String footerLinkJson;
    private String footerLinkJsonAfterLogin;
    private String loginRedirectUrl;
    private String gaTrackingId;
    private String forgotPasswordPageInstruction;
    private String createPasswordOtpInstruction;
    // It is used locally
    private String tempGaEnable;
    private boolean gaTrackingEnable;

    public FtlConfig() {}
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLoginRedirectUrl() {
        return loginRedirectUrl;
    }

    public void setLoginRedirectUrl(String loginRedirectUrl) {
        this.loginRedirectUrl = loginRedirectUrl;
    }

    public String getHeadingJson() {
        return headingJson;
    }

    public void setHeadingJson(String headingJson) {
        this.headingJson = headingJson;
    }

    public String getAfterLoginLinkJson() {
        return afterLoginLinkJson;
    }

    public void setAfterLoginLinkJson(String afterLoginLinkJson) {
        this.afterLoginLinkJson = afterLoginLinkJson;
    }

    public String getFooterLinkJson() {
        return footerLinkJson;
    }

    public void setFooterLinkJson(String footerLinkJson) {
        this.footerLinkJson = footerLinkJson;
    }

    public String getFooterLinkJsonAfterLogin() {
        return footerLinkJsonAfterLogin;
    }

    public void setFooterLinkJsonAfterLogin(String footerLinkJsonAfterLogin) {
        this.footerLinkJsonAfterLogin = footerLinkJsonAfterLogin;
    }

    public boolean isGaTrackingEnable() {
        return gaTrackingEnable;
    }

    public void setGaTrackingEnable(boolean gaTrackingEnable) {
        this.gaTrackingEnable = gaTrackingEnable;
    }

    public String getGaTrackingId() {
        return gaTrackingId;
    }

    public void setGaTrackingId(String gaTrackingId) {
        this.gaTrackingId = gaTrackingId;
    }

    public String getTempGaEnable() {
        return tempGaEnable;
    }

    public String getForgotPasswordPageInstruction() {
        return forgotPasswordPageInstruction;
    }

    public void setForgotPasswordPageInstruction(String forgotPasswordPageInstruction) {
        this.forgotPasswordPageInstruction = forgotPasswordPageInstruction;
    }

    public String getCreatePasswordOtpInstruction() {
        return createPasswordOtpInstruction;
    }

    public void setCreatePasswordOtpInstruction(String createPasswordOtpInstruction) {
        this.createPasswordOtpInstruction = createPasswordOtpInstruction;
    }

    public void setTempGaEnable(String tempGaEnable) {
        if (this.gaTrackingEnable && this.gaTrackingId != null && this.gaTrackingId.length() > 0) {
            this.tempGaEnable = "true";
        } else {
            this.tempGaEnable = tempGaEnable;
        }
    }

    @Override
    public String toString() {
        return "FtlConfig{" +
                "description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", headingJson='" + headingJson + '\'' +
                ", afterLoginLinkJson='" + afterLoginLinkJson + '\'' +
                ", footerLinkJson='" + footerLinkJson + '\'' +
                ", footerLinkJsonAfterLogin='" + footerLinkJsonAfterLogin + '\'' +
                ", loginRedirectUrl='" + loginRedirectUrl + '\'' +
                ", gaTrackingId='" + gaTrackingId + '\'' +
                ", forgotPasswordPageInstruction='" + forgotPasswordPageInstruction + '\'' +
                ", createPasswordOtpInstruction='" + createPasswordOtpInstruction + '\'' +
                ", tempGaEnable='" + tempGaEnable + '\'' +
                ", gaTrackingEnable=" + gaTrackingEnable +
                '}';
    }
}
