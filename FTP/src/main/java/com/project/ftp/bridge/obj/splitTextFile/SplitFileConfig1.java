package com.project.ftp.bridge.obj.splitTextFile;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SplitFileConfig1 {
    private ArrayList<String> splitTextFileConfigPath;

    public ArrayList<String> getSplitTextFileConfigPath() {
        return splitTextFileConfigPath;
    }

    public void setSplitTextFileConfigPath(ArrayList<String> splitTextFileConfigPath) {
        this.splitTextFileConfigPath = splitTextFileConfigPath;
    }

    @Override
    public String toString() {
        return "SplitFileConfig{" +
                "splitTextFileConfigPath=" + splitTextFileConfigPath +
                '}';
    }
}
