package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ScanDirMapping {
    private String id;
    private ArrayList<String> pathIndex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", pathIndex=" + pathIndex +
                '}';
    }
}
