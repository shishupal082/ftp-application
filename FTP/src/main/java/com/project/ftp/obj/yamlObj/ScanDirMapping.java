package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ScanDirMapping {
    private String device_name;
    private String id;
    private String filetype;
    private String recursive;
    private String csv_mapping_id;
    private ArrayList<String> pathIndex;

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getRecursive() {
        return recursive;
    }

    public void setRecursive(String recursive) {
        this.recursive = recursive;
    }

    public String getCsv_mapping_id() {
        return csv_mapping_id;
    }

    public void setCsv_mapping_id(String csv_mapping_id) {
        this.csv_mapping_id = csv_mapping_id;
    }

    public ArrayList<String> getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(ArrayList<String> pathIndex) {
        this.pathIndex = pathIndex;
    }

    @Override
    public String toString() {
        return "ScanDirMapping{" +
                "device_name='" + device_name + '\'' +
                ", id='" + id + '\'' +
                ", filetype='" + filetype + '\'' +
                ", recursive='" + recursive + '\'' +
                ", csv_mapping_id='" + csv_mapping_id + '\'' +
                ", pathIndex=" + pathIndex +
                '}';
    }
}
