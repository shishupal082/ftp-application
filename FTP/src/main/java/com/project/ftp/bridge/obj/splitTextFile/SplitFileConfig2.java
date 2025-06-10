package com.project.ftp.bridge.obj.splitTextFile;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SplitFileConfig2 {
    private HashMap<String, SplitTextFileConfig> splitTextFileConfig;

    public HashMap<String, SplitTextFileConfig> getSplitTextFileConfig() {
        return splitTextFileConfig;
    }

    public void setSplitTextFileConfig(HashMap<String, SplitTextFileConfig> splitTextFileConfig) {
        this.splitTextFileConfig = splitTextFileConfig;
    }

    @Override
    public String toString() {
        return "SplitFileConfig2{" +
                "splitTextFileConfig=" + splitTextFileConfig +
                '}';
    }
}
