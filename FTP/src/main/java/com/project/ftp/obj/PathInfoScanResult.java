package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathInfoScanResult {
    final static Logger logger = LoggerFactory.getLogger(PathInfoScanResult.class);
    private String deviceId;
    private String scanDirMappingId;
    private String scannedDate;
    private String detectedAt;
    private String editedAt;
    private String remark;
    private String path;
    private String type;
    private String parentFolder; // In case of file only
    private String fileName;
    private String filenameWithoutExt;
    private String extension;
    private String mediaType;
    private String size;
    private double sizeInKb;

    public PathInfoScanResult() {}
    public PathInfoScanResult(final PathInfo pathInfo) {
        if (pathInfo == null) {
            return;
        }
        this.detectedAt = pathInfo.getDetectedAt();
        this.path = pathInfo.getPath();
        this.type = pathInfo.getType();
        this.parentFolder = pathInfo.getParentFolder();
        this.fileName = pathInfo.getFileName();
        this.filenameWithoutExt = pathInfo.getFilenameWithoutExt();
        this.extension = pathInfo.getExtension();
        this.mediaType =pathInfo.getMediaType();
        this.size = pathInfo.getSize();
        this.sizeInKb = pathInfo.getSizeInKb();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getScanDirMappingId() {
        return scanDirMappingId;
    }

    public void setScanDirMappingId(String scanDirMappingId) {
        this.scanDirMappingId = scanDirMappingId;
    }

    public String getScannedDate() {
        return scannedDate;
    }

    public void setScannedDate(String scannedDate) {
        this.scannedDate = scannedDate;
    }

    public String getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(String detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(String editedAt) {
        this.editedAt = editedAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilenameWithoutExt() {
        return filenameWithoutExt;
    }

    public void setFilenameWithoutExt(String filenameWithoutExt) {
        this.filenameWithoutExt = filenameWithoutExt;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getSizeInKb() {
        return sizeInKb;
    }

    public void setSizeInKb(double sizeInKb) {
        this.sizeInKb = sizeInKb;
    }

    @Override
    public String toString() {
        return "PathInfoScanResult{" +
                "deviceId='" + deviceId + '\'' +
                ", scanDirMappingId='" + scanDirMappingId + '\'' +
                ", scannedDate='" + scannedDate + '\'' +
                ", detectedAt='" + detectedAt + '\'' +
                ", editedAt='" + editedAt + '\'' +
                ", remark='" + remark + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", parentFolder='" + parentFolder + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filenameWithoutExt='" + filenameWithoutExt + '\'' +
                ", extension='" + extension + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", size='" + size + '\'' +
                ", sizeInKb=" + sizeInKb +
                '}';
    }
}
