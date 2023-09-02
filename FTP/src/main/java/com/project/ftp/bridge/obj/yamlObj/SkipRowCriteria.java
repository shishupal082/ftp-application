package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SkipRowCriteria {
    private Integer col_index;
    private Boolean is_empty;
    private String regex;
    private String notRegex;
    private ArrayList<String> range;
    private ArrayList<String> notInRange;

    public Integer getCol_index() {
        return col_index;
    }

    public void setCol_index(Integer col_index) {
        this.col_index = col_index;
    }

    public Boolean getIs_empty() {
        return is_empty;
    }

    public void setIs_empty(Boolean is_empty) {
        this.is_empty = is_empty;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getNotRegex() {
        return notRegex;
    }

    public void setNotRegex(String notRegex) {
        this.notRegex = notRegex;
    }

    public ArrayList<String> getRange() {
        return range;
    }

    public void setRange(ArrayList<String> range) {
        this.range = range;
    }

    public ArrayList<String> getNotInRange() {
        return notInRange;
    }

    public void setNotInRange(ArrayList<String> notInRange) {
        this.notInRange = notInRange;
    }

    @Override
    public String toString() {
        return "SkipRowCriteria{" +
                "col_index=" + col_index +
                ", is_empty=" + is_empty +
                ", regex='" + regex + '\'' +
                ", notRegex='" + notRegex + '\'' +
                ", range=" + range +
                ", notInRange=" + notInRange +
                '}';
    }
}
