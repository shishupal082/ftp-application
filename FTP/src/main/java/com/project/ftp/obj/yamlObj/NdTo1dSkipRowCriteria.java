package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.ftp.bridge.obj.yamlObj.SkipRowCriteria;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class NdTo1dSkipRowCriteria {
    private ArrayList<ArrayList<Integer>> dataColIndex;
    private String operation;
    private ArrayList<SkipRowCriteria> criteria;

    public ArrayList<ArrayList<Integer>> getDataColIndex() {
        return dataColIndex;
    }

    public void setDataColIndex(ArrayList<ArrayList<Integer>> dataColIndex) {
        this.dataColIndex = dataColIndex;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ArrayList<SkipRowCriteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(ArrayList<SkipRowCriteria> criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "NdTo1dSkipRowCriteria{" +
                "dataColIndex=" + dataColIndex +
                ", operation='" + operation + '\'' +
                ", criteria=" + criteria +
                '}';
    }
}
