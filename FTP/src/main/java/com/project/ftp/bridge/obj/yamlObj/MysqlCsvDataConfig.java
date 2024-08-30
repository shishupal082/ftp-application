package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MysqlCsvDataConfig {
    private ArrayList<String> filterValues;
    private String defaultFilterMappingId;

    public ArrayList<String> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(ArrayList<String> filterValues) {
        this.filterValues = filterValues;
    }

    public String getDefaultFilterMappingId() {
        return defaultFilterMappingId;
    }

    public void setDefaultFilterMappingId(String defaultFilterMappingId) {
        this.defaultFilterMappingId = defaultFilterMappingId;
    }

    @Override
    public String toString() {
        return "MysqlCsvDataConfig{" +
                "filterValues=" + filterValues +
                ", defaultFilterMappingId='" + defaultFilterMappingId + '\'' +
                '}';
    }
}
