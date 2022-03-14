package com.project.ftp.obj;

public class AppStaticData {
    private String appVersion;
    private String uploadFileApiVersion;
    private String headingJson;
    private String afterLoginLinkJson;
    private String pageNotFoundJson;
    private String footerLinkJson;
    private String footerLinkJsonAfterLogin;
    private String title;
    private boolean loginWithGmail;
    private String googleLoginClientId;
    private Object jsonFileData;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
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

    public String getPageNotFoundJson() {
        return pageNotFoundJson;
    }

    public void setPageNotFoundJson(String pageNotFoundJson) {
        this.pageNotFoundJson = pageNotFoundJson;
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

    public String getUploadFileApiVersion() {
        return uploadFileApiVersion;
    }

    public void setUploadFileApiVersion(String uploadFileApiVersion) {
        this.uploadFileApiVersion = uploadFileApiVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLoginWithGmail() {
        return loginWithGmail;
    }

    public void setLoginWithGmail(boolean loginWithGmail) {
        this.loginWithGmail = loginWithGmail;
    }

    public String getGoogleLoginClientId() {
        return googleLoginClientId;
    }

    public void setGoogleLoginClientId(String googleLoginClientId) {
        this.googleLoginClientId = googleLoginClientId;
    }

    public Object getJsonFileData() {
        return jsonFileData;
    }

    public void setJsonFileData(Object jsonFileData) {
        this.jsonFileData = jsonFileData;
    }

    @Override
    public String toString() {
        return "AppStaticData{" +
                "appVersion='" + appVersion + '\'' +
                ", uploadFileApiVersion='" + uploadFileApiVersion + '\'' +
                ", headingJson='" + headingJson + '\'' +
                ", afterLoginLinkJson='" + afterLoginLinkJson + '\'' +
                ", pageNotFoundJson='" + pageNotFoundJson + '\'' +
                ", footerLinkJson='" + footerLinkJson + '\'' +
                ", footerLinkJsonAfterLogin='" + footerLinkJsonAfterLogin + '\'' +
                ", title='" + title + '\'' +
                ", loginWithGmail=" + loginWithGmail +
                ", googleLoginClientId='" + googleLoginClientId + '\'' +
                ", jsonFileData=" + jsonFileData +
                '}';
    }
}
