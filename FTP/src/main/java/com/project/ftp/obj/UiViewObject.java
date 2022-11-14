package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class UiViewObject {
    private String pageDescription;
    private String pageKeywords;
    private String pageAuthor;
    private String pageTitle;
    private ArrayList<String> cssFiles;
    private ArrayList<String> jsFiles;
    private String bodyClass;
    private String jQueryFilePath;
    private String baseApi;
    private String basePathName;
    private String appControlDataPath;
    private String appControlApi;
    private String projectHeading;
    private boolean forceLogin;
    private ArrayList<String> validAppControl;
    private String customPageData;
    public UiViewObject() {}
    public String getPageDescription() {
        return pageDescription;
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = pageDescription;
    }

    public String getPageKeywords() {
        return pageKeywords;
    }

    public void setPageKeywords(String pageKeywords) {
        this.pageKeywords = pageKeywords;
    }

    public String getPageAuthor() {
        return pageAuthor;
    }

    public void setPageAuthor(String pageAuthor) {
        this.pageAuthor = pageAuthor;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public ArrayList<String> getCssFiles() {
        return cssFiles;
    }

    public void setCssFiles(ArrayList<String> cssFiles) {
        this.cssFiles = cssFiles;
    }

    public ArrayList<String> getJsFiles() {
        return jsFiles;
    }

    public void setJsFiles(ArrayList<String> jsFiles) {
        this.jsFiles = jsFiles;
    }

    public String getBodyClass() {
        return bodyClass;
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public String getjQueryFilePath() {
        return jQueryFilePath;
    }

    public void setjQueryFilePath(String jQueryFilePath) {
        this.jQueryFilePath = jQueryFilePath;
    }

    public String getBaseApi() {
        return baseApi;
    }

    public void setBaseApi(String baseApi) {
        this.baseApi = baseApi;
    }

    public String getBasePathName() {
        return basePathName;
    }

    public void setBasePathName(String basePathName) {
        this.basePathName = basePathName;
    }

    public String getAppControlDataPath() {
        return appControlDataPath;
    }

    public void setAppControlDataPath(String appControlDataPath) {
        this.appControlDataPath = appControlDataPath;
    }

    public String getAppControlApi() {
        return appControlApi;
    }

    public void setAppControlApi(String appControlApi) {
        this.appControlApi = appControlApi;
    }

    public String getProjectHeading() {
        return projectHeading;
    }

    public void setProjectHeading(String projectHeading) {
        this.projectHeading = projectHeading;
    }

    public boolean isForceLogin() {
        return forceLogin;
    }

    public void setForceLogin(boolean forceLogin) {
        this.forceLogin = forceLogin;
    }

    public ArrayList<String> getValidAppControl() {
        return validAppControl;
    }

    public void setValidAppControl(ArrayList<String> validAppControl) {
        this.validAppControl = validAppControl;
    }

    public String getCustomPageData() {
        return customPageData;
    }

    public void setCustomPageData(String customPageData) {
        this.customPageData = customPageData;
    }

    @Override
    public String toString() {
        return "UiViewObject{" +
                "pageDescription='" + pageDescription + '\'' +
                ", pageKeywords='" + pageKeywords + '\'' +
                ", pageAuthor='" + pageAuthor + '\'' +
                ", pageTitle='" + pageTitle + '\'' +
                ", cssFiles=" + cssFiles +
                ", jsFiles=" + jsFiles +
                ", bodyClass='" + bodyClass + '\'' +
                ", jQueryFilePath='" + jQueryFilePath + '\'' +
                ", baseApi='" + baseApi + '\'' +
                ", basePathName='" + basePathName + '\'' +
                ", appControlDataPath='" + appControlDataPath + '\'' +
                ", appControlApi='" + appControlApi + '\'' +
                ", projectHeading='" + projectHeading + '\'' +
                ", forceLogin=" + forceLogin +
                ", validAppControl=" + validAppControl +
                ", customPageData='" + customPageData + '\'' +
                '}';
    }
}
