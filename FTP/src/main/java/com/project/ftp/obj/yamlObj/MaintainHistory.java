package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MaintainHistory {
    private boolean required;
    private ArrayList<String> excludeColumnName;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ArrayList<String> getExcludeColumnName() {
        return excludeColumnName;
    }

    public void setExcludeColumnName(ArrayList<String> excludeColumnName) {
        this.excludeColumnName = excludeColumnName;
    }
}
