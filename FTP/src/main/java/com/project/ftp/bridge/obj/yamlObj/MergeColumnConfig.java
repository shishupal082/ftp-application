package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MergeColumnConfig {
    private Integer finalIndex;
    private ArrayList<Integer> sourceIndex;
    private ArrayList<MergeConfigCondition> conditions;
    private String join;

    public Integer getFinalIndex() {
        return finalIndex;
    }

    public void setFinalIndex(Integer finalIndex) {
        this.finalIndex = finalIndex;
    }

    public ArrayList<Integer> getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(ArrayList<Integer> sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public ArrayList<MergeConfigCondition> getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList<MergeConfigCondition> conditions) {
        this.conditions = conditions;
    }

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    @Override
    public String toString() {
        return "MergeColumnConfig{" +
                "finalIndex=" + finalIndex +
                ", sourceIndex=" + sourceIndex +
                ", conditions=" + conditions +
                ", join='" + join + '\'' +
                '}';
    }
}
