package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CellMapping {
    private String defaultCellData;
    private Integer col_index;
    private ArrayList<CellMappingData> mappingData;

    public String getDefaultCellData() {
        return defaultCellData;
    }

    public void setDefaultCellData(String defaultCellData) {
        this.defaultCellData = defaultCellData;
    }

    public Integer getCol_index() {
        return col_index;
    }

    public void setCol_index(Integer col_index) {
        this.col_index = col_index;
    }

    public ArrayList<CellMappingData> getMappingData() {
        return mappingData;
    }

    public void setMappingData(ArrayList<CellMappingData> mappingData) {
        this.mappingData = mappingData;
    }

    @Override
    public String toString() {
        return "CellMapping{" +
                "defaultCellData='" + defaultCellData + '\'' +
                ", col_index=" + col_index +
                ", mappingData=" + mappingData +
                '}';
    }
}
