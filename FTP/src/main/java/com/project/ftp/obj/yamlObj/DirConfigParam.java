package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class DirConfigParam {
    private String fileSaveDir;
    private String staticDataFilename;
    private String configDataFilePath;
    private String fileMappingConfigFilePath;
    private String splitTextFileConfigPath;
    private ArrayList<String> standAloneConfigPath;
    private ArrayList<String> tableDbConfigFilePath;
    private String scanDirConfigFilePath;
    private String isRelativePath;
    private String assetsDir;
    private String publicDir;
    private String publicPostDir;

    public String getFileSaveDir() {
        return fileSaveDir;
    }

    public void setFileSaveDir(String fileSaveDir) {
        this.fileSaveDir = fileSaveDir;
    }

    public String getStaticDataFilename() {
        return staticDataFilename;
    }

    public void setStaticDataFilename(String staticDataFilename) {
        this.staticDataFilename = staticDataFilename;
    }

    public String getConfigDataFilePath() {
        return configDataFilePath;
    }

    public void setConfigDataFilePath(String configDataFilePath) {
        this.configDataFilePath = configDataFilePath;
    }

    public String getFileMappingConfigFilePath() {
        return fileMappingConfigFilePath;
    }

    public void setFileMappingConfigFilePath(String fileMappingConfigFilePath) {
        this.fileMappingConfigFilePath = fileMappingConfigFilePath;
    }

    public String getSplitTextFileConfigPath() {
        return splitTextFileConfigPath;
    }

    public void setSplitTextFileConfigPath(String splitTextFileConfigPath) {
        this.splitTextFileConfigPath = splitTextFileConfigPath;
    }

    public ArrayList<String> getStandAloneConfigPath() {
        return standAloneConfigPath;
    }

    public void setStandAloneConfigPath(ArrayList<String> standAloneConfigPath) {
        this.standAloneConfigPath = standAloneConfigPath;
    }

    public ArrayList<String> getTableDbConfigFilePath() {
        return tableDbConfigFilePath;
    }

    public void setTableDbConfigFilePath(ArrayList<String> tableDbConfigFilePath) {
        this.tableDbConfigFilePath = tableDbConfigFilePath;
    }

    public String getScanDirConfigFilePath() {
        return scanDirConfigFilePath;
    }

    public void setScanDirConfigFilePath(String scanDirConfigFilePath) {
        this.scanDirConfigFilePath = scanDirConfigFilePath;
    }

    public String getIsRelativePath() {
        return isRelativePath;
    }

    public void setIsRelativePath(String isRelativePath) {
        this.isRelativePath = isRelativePath;
    }

    public String getAssetsDir() {
        return assetsDir;
    }

    public void setAssetsDir(String assetsDir) {
        this.assetsDir = assetsDir;
    }

    public String getPublicDir() {
        return publicDir;
    }

    public void setPublicDir(String publicDir) {
        this.publicDir = publicDir;
    }

    public String getPublicPostDir() {
        return publicPostDir;
    }

    public void setPublicPostDir(String publicPostDir) {
        this.publicPostDir = publicPostDir;
    }

    @Override
    public String toString() {
        return "DirConfigParam{" +
                "fileSaveDir='" + fileSaveDir + '\'' +
                ", staticDataFilename='" + staticDataFilename + '\'' +
                ", configDataFilePath='" + configDataFilePath + '\'' +
                ", fileMappingConfigFilePath='" + fileMappingConfigFilePath + '\'' +
                ", splitTextFileConfigPath='" + splitTextFileConfigPath + '\'' +
                ", standAloneConfigPath=" + standAloneConfigPath +
                ", tableDbConfigFilePath=" + tableDbConfigFilePath +
                ", scanDirConfigFilePath='" + scanDirConfigFilePath + '\'' +
                ", isRelativePath='" + isRelativePath + '\'' +
                ", assetsDir='" + assetsDir + '\'' +
                ", publicDir='" + publicDir + '\'' +
                ", publicPostDir='" + publicPostDir + '\'' +
                '}';
    }
}
