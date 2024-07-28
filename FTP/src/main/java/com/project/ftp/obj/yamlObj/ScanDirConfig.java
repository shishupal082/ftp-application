package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ScanDirConfig {
    private ArrayList<ScanDirMapping> scanDirConfig;

    public ArrayList<ScanDirMapping> getScanDirConfig() {
        return scanDirConfig;
    }

    public void setScanDirConfig(ArrayList<ScanDirMapping> scanDirConfig) {
        this.scanDirConfig = scanDirConfig;
    }

    @Override
    public String toString() {
        return "ScanDirConfig{" +
                "scanDirConfig=" + scanDirConfig +
                '}';
    }
}
