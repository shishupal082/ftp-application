package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class NdTo1dConfig {
    private ArrayList<String> sourceExcelId;
    private Integer dataStartIndex;
    private Integer dataDimension;
    private ArrayList<Integer> textColIndex;
    private ArrayList<ArrayList<ArrayList<Integer>>> dataColIndex;
    private ArrayList<String> headingField;
    private ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria;

    public ArrayList<String> getSourceExcelId() {
        return sourceExcelId;
    }

    public void setSourceExcelId(ArrayList<String> sourceExcelId) {
        this.sourceExcelId = sourceExcelId;
    }

    public Integer getDataStartIndex() {
        return dataStartIndex;
    }

    public void setDataStartIndex(Integer dataStartIndex) {
        this.dataStartIndex = dataStartIndex;
    }

    public Integer getDataDimension() {
        return dataDimension;
    }

    public void setDataDimension(Integer dataDimension) {
        this.dataDimension = dataDimension;
    }

    public ArrayList<Integer> getTextColIndex() {
        return textColIndex;
    }

    public void setTextColIndex(ArrayList<Integer> textColIndex) {
        this.textColIndex = textColIndex;
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> getDataColIndex() {
        return dataColIndex;
    }

    public void setDataColIndex(ArrayList<ArrayList<ArrayList<Integer>>> dataColIndex) {
        this.dataColIndex = dataColIndex;
    }

    public ArrayList<String> getHeadingField() {
        return headingField;
    }

    public void setHeadingField(ArrayList<String> headingField) {
        this.headingField = headingField;
    }

    public ArrayList<NdTo1dSkipRowCriteria> getSkipRowCriteria() {
        return skipRowCriteria;
    }

    public void setSkipRowCriteria(ArrayList<NdTo1dSkipRowCriteria> skipRowCriteria) {
        this.skipRowCriteria = skipRowCriteria;
    }

    @Override
    public String toString() {
        return "NdTo1dConfig{" +
                "sourceExcelId=" + sourceExcelId +
                ", dataStartIndex=" + dataStartIndex +
                ", dataDimension=" + dataDimension +
                ", textColIndex=" + textColIndex +
                ", dataColIndex=" + dataColIndex +
                ", headingField=" + headingField +
                ", skipRowCriteria=" + skipRowCriteria +
                '}';
    }
}
