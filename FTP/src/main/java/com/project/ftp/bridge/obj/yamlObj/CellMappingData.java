package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CellMappingData {
    private Integer col_index;
    private String value;
    private ArrayList<String> range;

    public Integer getCol_index() {
        return col_index;
    }

    public void setCol_index(Integer col_index) {
        this.col_index = col_index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getRange() {
        return range;
    }

    public void setRange(ArrayList<String> range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "CellMappingData{" +
                "col_index=" + col_index +
                ", value='" + value + '\'' +
                ", range=" + range +
                '}';
    }
}
