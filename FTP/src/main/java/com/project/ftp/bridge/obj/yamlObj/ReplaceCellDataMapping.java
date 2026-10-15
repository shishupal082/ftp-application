package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ReplaceCellDataMapping {
    private ArrayList<ArrayList<Integer>> index;
    private Integer finalIndex;
    private Boolean upper;
    private Boolean lower;
    private Boolean trimBoth;
    private Boolean trimLeft;
    private Boolean trimRight;
    private Boolean copy;
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

    public Boolean getTrimBoth() {
        return trimBoth;
    }

    public void setTrimBoth(Boolean trimBoth) {
        this.trimBoth = trimBoth;
    }

    public Boolean getTrimLeft() {
        return trimLeft;
    }

    public void setTrimLeft(Boolean trimLeft) {
        this.trimLeft = trimLeft;
    }

    public Boolean getTrimRight() {
        return trimRight;
    }

    public void setTrimRight(Boolean trimRight) {
        this.trimRight = trimRight;
    }

    public Boolean getCopy() {
        return copy;
    }

    public void setCopy(Boolean copy) {
        this.copy = copy;
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
                ", finalIndex=" + finalIndex +
                ", upper=" + upper +
                ", lower=" + lower +
                ", trimBoth=" + trimBoth +
                ", trimLeft=" + trimLeft +
                ", trimRight=" + trimRight +
                ", copy=" + copy +
                ", find='" + find + '\'' +
                ", replace='" + replace + '\'' +
                '}';
    }
}
