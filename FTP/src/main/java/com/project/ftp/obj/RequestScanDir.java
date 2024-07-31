package com.project.ftp.obj;


import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.yamlObj.ScanDirMapping;
import com.project.ftp.service.StaticService;

import java.util.ArrayList;

public class RequestScanDir {
    private final String reqScanDirId;
    private final String reqPathName;
    private final String reqFileType;
    private final String reqRecursive;
    private final String reqCsvMappingId;
    private final ArrayList<String> scanDirIdList;
    private final ArrayList<String> fileTypeList;
    private final boolean recursive;
    private ScanDirMapping scanDirMapping;
    public RequestScanDir(String reqScanDirId, String reqPathName, String reqFileType,
                          String reqRecursive, String reqCsvMappingId) {
        this.scanDirIdList = this.getTokenizedRequestParameter(reqScanDirId);
        if (this.scanDirIdList != null) {
            this.reqScanDirId = this.combineReqParameter(this.scanDirIdList);
        } else {
            this.reqScanDirId = null;
        }
        reqPathName = StaticService.replaceBackSlashToSlash(reqPathName);
        if (reqPathName != null) {
            this.reqPathName = reqPathName.trim();
        } else {
            this.reqPathName = null;
        }
        this.fileTypeList = this.getTokenizedRequestParameter(reqFileType);
        if (this.fileTypeList != null) {
            this.reqFileType = this.combineReqParameter(this.fileTypeList);
        } else {
            this.reqFileType = null;
        }
        if (reqRecursive != null) {
            this.reqRecursive = reqRecursive.trim();
        } else {
            this.reqRecursive = null;
        }
        this.recursive = AppConstant.TRUE.equals(this.reqRecursive);
        if (reqCsvMappingId != null) {
            this.reqCsvMappingId = reqCsvMappingId.trim();
        } else {
            this.reqCsvMappingId = null;
        }
    }
    private ArrayList<String> getTokenizedRequestParameter(String requestParameter) {
        if (StaticService.isInValidString(requestParameter)) {
            return null;
        }
        String[] splitResult = requestParameter.split("\\|");
        ArrayList<String> result = new ArrayList<>();
        for (String str: splitResult) {
            if (StaticService.isValidString(str)) {
                result.add(str.trim());
            }
        }
        return result;
    }
    private String combineReqParameter(ArrayList<String> reqParam) {
        if (reqParam == null || reqParam.isEmpty()) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        for(String param: reqParam) {
            if (StaticService.isValidString(param)) {
                result.add(param);
            }
        }
        return String.join("|", result);
    }
    // Only used for reading data but not for final api response
    public ArrayList<String> getFinalFiletypeList() {
        if (this.reqFileType != null) {
            return this.fileTypeList;
        }
        if (this.scanDirMapping != null) {
            return this.getTokenizedRequestParameter(this.scanDirMapping.getFiletype());
        }
        return null;
    }
    // Only used for reading data but not for final api response
    public String getFinalCsvMappingId() {
        if (this.reqCsvMappingId != null) {
            return this.reqCsvMappingId;
        }
        if (this.scanDirMapping != null) {
            return this.scanDirMapping.getCsv_mapping_id();
        }
        return null;
    }
    // Only used for reading data but not for final api response
    public boolean getFinalRecursive() {
        if (this.recursive) {
            return true;
        }
        if (StaticService.isValidString(this.reqRecursive)) {
            return false;
        }
        if (this.scanDirMapping != null) {
            return AppConstant.TRUE.equals(this.scanDirMapping.getRecursive());
        }
        return false;
    }
    public String getReqScanDirId() {
        return reqScanDirId;
    }

    public String getReqPathName() {
        return reqPathName;
    }

    public String getReqFileType() {
        return reqFileType;
    }

    public String getReqRecursive() {
        return reqRecursive;
    }

    public String getReqCsvMappingId() {
        return reqCsvMappingId;
    }

    public ArrayList<String> getScanDirIdList() {
        return scanDirIdList;
    }

    public ArrayList<String> getFileTypeList() {
        return fileTypeList;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public ScanDirMapping getScanDirMapping() {
        return scanDirMapping;
    }

    public void setScanDirMapping(ScanDirMapping scanDirMapping) {
        this.scanDirMapping = scanDirMapping;
    }

    @Override
    public String toString() {
        return "RequestScanDir{" +
                "reqScanDirId='" + reqScanDirId + '\'' +
                ", reqPathName='" + reqPathName + '\'' +
                ", reqFileType='" + reqFileType + '\'' +
                ", reqRecursive='" + reqRecursive + '\'' +
                ", reqCsvMappingId='" + reqCsvMappingId + '\'' +
                ", scanDirIdList=" + scanDirIdList +
                ", fileTypeList=" + fileTypeList +
                ", recursive=" + recursive +
                ", scanDirMapping=" + scanDirMapping +
                '}';
    }
}
