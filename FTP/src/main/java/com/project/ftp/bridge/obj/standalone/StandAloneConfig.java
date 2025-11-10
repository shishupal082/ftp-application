package com.project.ftp.bridge.obj.standalone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class StandAloneConfig {
    private String currentSequence;
    private HashMap<String, ArrayList<Sequence>> sequence;
    private HashMap<String, ApiDetail> apiList;
    public String getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(String currentSequence) {
        this.currentSequence = currentSequence;
    }

    public HashMap<String, ArrayList<Sequence>> getSequence() {
        return sequence;
    }

    public void setSequence(HashMap<String, ArrayList<Sequence>> sequence) {
        this.sequence = sequence;
    }

    public HashMap<String, ApiDetail> getApiList() {
        return apiList;
    }

    public void setApiList(HashMap<String, ApiDetail> apiList) {
        this.apiList = apiList;
    }

    @Override
    public String toString() {
        return "StandAloneConfig{" +
                "currentSequence='" + currentSequence + '\'' +
                ", sequence=" + sequence +
                ", apiList=" + apiList +
                '}';
    }
}
