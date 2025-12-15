package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)

public class ExcelDataSortingConfig2 {
    private ArrayList<Integer> skipRowIndex;
    private ArrayList<ExcelDataSortingConfig> sortingDetails;

    public ArrayList<Integer> getSkipRowIndex() {
        return skipRowIndex;
    }

    public void setSkipRowIndex(ArrayList<Integer> skipRowIndex) {
        this.skipRowIndex = skipRowIndex;
    }

    public ArrayList<ExcelDataSortingConfig> getSortingDetails() {
        return sortingDetails;
    }

    public void setSortingDetails(ArrayList<ExcelDataSortingConfig> sortingDetails) {
        this.sortingDetails = sortingDetails;
    }

    @Override
    public String toString() {
        return "ExcelDataSortingConfig2{" +
                "skipRowIndex=" + skipRowIndex +
                ", sortingDetails=" + sortingDetails +
                '}';
    }
}
