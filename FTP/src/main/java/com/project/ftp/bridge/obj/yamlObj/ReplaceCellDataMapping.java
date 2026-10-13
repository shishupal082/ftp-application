package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ReplaceCellDataMapping {
    private ArrayList<ArrayList<Integer>> index;
    private Integer finalIndex;
    private Boolean upper;
    private Boolean lower;
    private String find;
    private String replace;

    public ArrayList<ArrayList<Integer>> getIndex() {
        return index;
    }

    public void setIndex(ArrayList<ArrayList<Integer>> index) {
        this.index = index;
    }

    public Integer getFinalIndex() {
        return finalIndex;
    }

    public void setFinalIndex(Integer finalIndex) {
        this.finalIndex = finalIndex;
    }

    public Boolean getUpper() {
        return upper;
    }

    public void setUpper(Boolean upper) {
        this.upper = upper;
    }

    public Boolean getLower() {
        return lower;
    }

    public void setLower(Boolean lower) {
        this.lower = lower;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public String getReplace() {
        return replace;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    @Override
    public String toString() {
        return "ReplaceCellDataMapping{" +
                "index=" + index +
                ", destination=" + finalIndex +
                ", upper=" + upper +
                ", lower=" + lower +
                ", find='" + find + '\'' +
                ", replace='" + replace + '\'' +
                '}';
    }
}
