package com.project.ftp.obj;

import com.project.ftp.config.PathType;

import java.util.ArrayList;

public class ScanResult {
    private double pathSize;
    private String pathName;
    private PathType pathType;
    private String staticFolderPath;
    private String md5Hash;
    private ArrayList<ScanResult> scanResults;
    public ScanResult(String staticFolderPath, String pathName){
        this.staticFolderPath = staticFolderPath;
        this.pathName = pathName;
    }
    public ScanResult(String staticFolderPath, String pathName, PathType pathType) {
        this.staticFolderPath = staticFolderPath;
        this.pathName = pathName;
        this.pathType = pathType;
    }

    public double getPathSize() {
        return pathSize;
    }

    public void setPathSize(double pathSize) {
        this.pathSize = pathSize;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public PathType getPathType() {
        return pathType;
    }

    public void setPathType(PathType pathType) {
        this.pathType = pathType;
    }

    public String getStaticFolderPath() {
        return staticFolderPath;
    }

    public void setStaticFolderPath(String staticFolderPath) {
        this.staticFolderPath = staticFolderPath;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public ArrayList<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(ArrayList<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "pathSize=" + pathSize +
                ", pathName='" + pathName + '\'' +
                ", pathType=" + pathType +
                ", staticFolderPath='" + staticFolderPath + '\'' +
                ", md5Hash='" + md5Hash + '\'' +
                ", scanResults=" + scanResults +
                '}';
    }
}
