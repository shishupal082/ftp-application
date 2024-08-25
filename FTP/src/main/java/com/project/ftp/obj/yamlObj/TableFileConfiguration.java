package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class TableFileConfiguration {
    private ArrayList<TableConfiguration> tableDbConfig;

    public ArrayList<TableConfiguration> getTableDbConfig() {
        return tableDbConfig;
    }

    public void setTableDbConfig(ArrayList<TableConfiguration> tableDbConfig) {
        this.tableDbConfig = tableDbConfig;
    }
}
