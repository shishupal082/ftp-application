package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CellMapping {
    private String newColumnName;
    private String defaultCellData;
    private String dateRegex;
    private Integer col_index;
    private Boolean rewrite;
    private ArrayList<CellMappingData> mappingData;

    public String getNewColumnName() {
        return newColumnName;
    }

    public void setNewColumnName(String newColumnName) {
        this.newColumnName = newColumnName;
    }

    public String getDefaultCellData() {
        return defaultCellData;
    }

    public void setDefaultCellData(String defaultCellData) {
        this.defaultCellData = defaultCellData;
    }

    public String getDateRegex() {
        return dateRegex;
    }

    public void setDateRegex(String dateRegex) {
        this.dateRegex = dateRegex;
    }

    public Integer getCol_index() {
        return col_index;
    }

    public void setCol_index(Integer col_index) {
        this.col_index = col_index;
    }

    public Boolean getRewrite() {
        return rewrite;
    }

    public void setRewrite(Boolean rewrite) {
        this.rewrite = rewrite;
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
                "newColumnName='" + newColumnName + '\'' +
                ", defaultCellData='" + defaultCellData + '\'' +
                ", dateRegex='" + dateRegex + '\'' +
                ", col_index=" + col_index +
                ", rewrite=" + rewrite +
                ", mappingData=" + mappingData +
                '}';
    }
}
