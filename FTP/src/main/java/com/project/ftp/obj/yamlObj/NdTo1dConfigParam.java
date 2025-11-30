package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class NdTo1dConfigParam {
    private HashMap<String, NdTo1dConfig> ndTo1dConfig;

    public HashMap<String, NdTo1dConfig> getNdTo1dConfig() {
        return ndTo1dConfig;
    }

    public void setNdTo1dConfig(HashMap<String, NdTo1dConfig> ndTo1dConfig) {
        this.ndTo1dConfig = ndTo1dConfig;
    }

    @Override
    public String toString() {
        return "NdTo1dConfigParam{" +
                "ndTo1dConfig=" + ndTo1dConfig +
                '}';
    }
}
