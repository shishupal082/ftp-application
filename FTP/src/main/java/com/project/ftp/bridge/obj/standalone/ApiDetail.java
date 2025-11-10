package com.project.ftp.bridge.obj.standalone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)


public class ApiDetail {
    private String resource;
    private String path;
    private ArrayList<String> params;
    private Boolean confirmationRequired;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public Boolean getConfirmationRequired() {
        return confirmationRequired;
    }

    public void setConfirmationRequired(Boolean confirmationRequired) {
        this.confirmationRequired = confirmationRequired;
    }

    @Override
    public String toString() {
        return "ApiDetail{" +
                "resource='" + resource + '\'' +
                ", path='" + path + '\'' +
                ", params=" + params +
                ", confirmationRequired=" + confirmationRequired +
                '}';
    }
}
