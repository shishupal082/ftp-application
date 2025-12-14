package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)

public class ExcelDataSortingConfig {
    private Integer index;
    private String order;
    private String dataType;
    private String defaultData;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDefaultData() {
        return defaultData;
    }

    public void setDefaultData(String defaultData) {
        this.defaultData = defaultData;
    }

    @Override
    public String toString() {
        return "ExcelDataSortingConfig{" +
                "index=" + index +
                ", order='" + order + '\'' +
                ", dataType='" + dataType + '\'' +
                ", defaultData='" + defaultData + '\'' +
                '}';
    }
}
