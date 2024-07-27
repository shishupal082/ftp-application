package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ScanDirConfig {
    private ArrayList<ScanDirMapping> scanDirMapping;

    public ArrayList<ScanDirMapping> getScanDirMapping() {
        return scanDirMapping;
    }

    public void setScanDirMapping(ArrayList<ScanDirMapping> scanDirMapping) {
        this.scanDirMapping = scanDirMapping;
    }

    @Override
    public String toString() {
        return "ScanDirConfig{" +
                "scanDirMapping=" + scanDirMapping +
                '}';
    }
}
