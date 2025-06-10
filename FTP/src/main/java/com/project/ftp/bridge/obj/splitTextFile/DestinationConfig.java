package com.project.ftp.bridge.obj.splitTextFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class DestinationConfig {
    private String destinationFileName;
    private String destinationFileDir;
    private ArrayList<ArrayList<Integer>> dataRange;

    public String getDestinationFileName() {
        return destinationFileName;
    }

    public void setDestinationFileName(String destinationFileName) {
        this.destinationFileName = destinationFileName;
    }

    public String getDestinationFileDir() {
        return destinationFileDir;
    }

    public void setDestinationFileDir(String destinationFileDir) {
        this.destinationFileDir = destinationFileDir;
    }

    public ArrayList<ArrayList<Integer>> getDataRange() {
        return dataRange;
    }

    public void setDataRange(ArrayList<ArrayList<Integer>> dataRange) {
        this.dataRange = dataRange;
    }

    @Override
    public String toString() {
        return "DestinationConfig{" +
                "destinationFileName='" + destinationFileName + '\'' +
                ", destinationFileDir='" + destinationFileDir + '\'' +
                ", dataRange=" + dataRange +
                '}';
    }
}
