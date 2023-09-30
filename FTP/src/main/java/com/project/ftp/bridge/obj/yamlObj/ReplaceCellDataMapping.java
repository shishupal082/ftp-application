package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ReplaceCellDataMapping {
    private Integer index;
    private String find;
    private String replace;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
                ", find='" + find + '\'' +
                ", replace='" + replace + '\'' +
                '}';
    }
}
