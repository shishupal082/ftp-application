package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;

import java.util.ArrayList;

public class FilepathDBParameters {
    private boolean isUpdated;
    private long id;
    private String orgUsername;
    private String entryTime;
    private String loginUsername;
    private String tableName = AppConstant.TABLE_FILE_PATH;
    private String tableUniqueId;
    private String uiEntryTime;
    private String deviceName;
    private String scanDirMappingId;
    private String type;
    private Double sizeInKb;
    private String size;
    private String scannedDate;
    private String detectedAt;
    private String editedAt;
    private String deletedAt;
    private String remark;
    private String parentPath;
    private String pathName;
    private String fileName;
    private String extension;
    private PathInfo pathInfo;
    private String filePathDetails;

    private final int orgUsernameMaxLength = 255;
    private final int entryTimeMaxLength = 31;
    private final int loginUsernameMaxLength = 255;
    private final int tableNameMaxLength = 31;
    private final int tableUniqueIdMaxLength = 63;
    private final int uiEntryTimeMaxLength = 31;
    private final int deviceNameMaxLength = 31;
    private final int scanDirMappingIdMaxLength = 511;
    private final int typeMaxLength = 31;
    private final int minSizeInKb = 0;
    private final int sizeMaxLength = 31;
    private final int scannedDateMaxLength = 31;
    private final int detectedAtMaxLength = 31;
    private final int editedAtMaxLength = 31;
    private final int deletedAtMaxLength = 31;
    private final int remarkMaxLength = 511;
    private final int parentPathMaxLength = 511;
    private final int pathNameMaxLength = 511;
    private final int fileNameMaxLength = 127;
    private final int filePathDetailsMaxLength = 1023;

    public FilepathDBParameters() {}
    public FilepathDBParameters(final PathInfo pathInfo) {
        if (pathInfo == null) {
            return;
        }
        this.pathInfo = pathInfo;
        this.detectedAt = pathInfo.getDetectedAt();
        this.pathName = pathInfo.getPath();
        this.type = pathInfo.getType();
        this.parentPath = pathInfo.getParentFolder();
        this.fileName = pathInfo.getFileName();
        this.extension = pathInfo.getExtension();
        this.size = pathInfo.getSize();
        this.sizeInKb = pathInfo.getSizeInKb();
    }
    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrgUsername() {
        return orgUsername;
    }
    public String getOrgUsernameV2() {
        return StaticService.truncateString(orgUsername, orgUsernameMaxLength);
    }
    public void setOrgUsername(String orgUsername) {
        this.orgUsername = orgUsername;
    }

    public String getEntryTime() {
        return entryTime;
    }
    public String getEntryTimeV2() {
        return StaticService.truncateString(entryTime, entryTimeMaxLength);
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getLoginUsername() {
        return loginUsername;
    }
    public String getLoginUsernameV2() {
        return StaticService.truncateString(loginUsername, loginUsernameMaxLength);
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getTableName() {
        return tableName;
    }
    public String getTableNameV2() {
        return StaticService.truncateString(tableName, tableNameMaxLength);
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableUniqueId() {
        return tableUniqueId;
    }
    public String getTableUniqueIdV2() {
        return StaticService.truncateString(tableUniqueId, tableUniqueIdMaxLength);
    }

    public void setTableUniqueId(String tableUniqueId) {
        this.tableUniqueId = tableUniqueId;
    }

    public String getUiEntryTime() {
        return uiEntryTime;
    }
    public String getUiEntryTimeV2() {
        return StaticService.truncateString(uiEntryTime, uiEntryTimeMaxLength);
    }

    public void setUiEntryTime(String uiEntryTime) {
        this.uiEntryTime = uiEntryTime;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public String getDeviceNameV2() {
        return StaticService.truncateString(deviceName, deviceNameMaxLength);
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getScanDirMappingId() {
        return scanDirMappingId;
    }
    public String getScanDirMappingIdV2() {
        return StaticService.truncateString(scanDirMappingId, scanDirMappingIdMaxLength);
    }

    public void setScanDirMappingId(String scanDirMappingId) {
        this.scanDirMappingId = scanDirMappingId;
    }

    public String getType() {
        return type;
    }
    public String getTypeV2() {
        return StaticService.truncateString(type, typeMaxLength);
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getSizeInKb() {
        return sizeInKb;
    }
    public Double getSizeInKbV2() {
        if (sizeInKb != null && sizeInKb >= minSizeInKb) {
            return sizeInKb;
        }
        return 0.0;
    }
    public void setSizeInKb(Double sizeInKb) {
        this.sizeInKb = sizeInKb;
    }

    public String getSize() {
        return size;
    }
    public String getSizeV2() {
        return StaticService.truncateString(size, sizeMaxLength);
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getScannedDate() {
        return scannedDate;
    }
    public String getScannedDateV2() {
        return StaticService.truncateString(scannedDate, scannedDateMaxLength);
    }
    public void setScannedDate(String scannedDate) {
        this.scannedDate = scannedDate;
    }

    public String getDetectedAt() {
        return detectedAt;
    }
    public String getDetectedAtV2() {
        return StaticService.truncateString(detectedAt, detectedAtMaxLength);
    }

    public void setDetectedAt(String detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public String getEditedAtV2() {
        return StaticService.truncateString(editedAt, editedAtMaxLength);
    }

    public void setEditedAt(String editedAt) {
        this.editedAt = editedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getDeletedAtV2() {
        return StaticService.truncateString(deletedAt, deletedAtMaxLength);
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getRemark() {
        return remark;
    }
    public String getRemarkV2() {
        return StaticService.truncateString(remark, remarkMaxLength);
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getParentPathV2() {
        return StaticService.truncateString(parentPath, parentPathMaxLength);
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getPathName() {
        return pathName;
    }
    public String getPathNameV2() {
        return StaticService.truncateString(pathName, pathNameMaxLength);
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getFileName() {
        return fileName;
    }
    public String getFileNameV2() {
        return StaticService.truncateString(fileName, fileNameMaxLength);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public PathInfo getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(PathInfo pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getFilePathDetails() {
        return filePathDetails;
    }

    public void setFilePathDetails(String filePathDetails) {
        this.filePathDetails = filePathDetails;
    }
    public ArrayList<String> getJsonData() {
        ArrayList<String> result = new ArrayList<>();
        result.add(orgUsername);
        result.add(entryTime);
        result.add(loginUsername);
        result.add(tableName);
        result.add(tableUniqueId);
        result.add(uiEntryTime);
        result.add(deviceName);
        result.add(scanDirMappingId);
        result.add(type);
        result.add(Double.toString(sizeInKb));
        result.add(size);
        result.add(scannedDate);
        result.add(detectedAt);
        result.add(editedAt);
        result.add(deletedAt);
        result.add(remark);
        result.add(parentPath);
        result.add(pathName);
        result.add(fileName);
        result.add(extension);
        return result;
    }
    public ArrayList<String> getCsvData() {
        ArrayList<String> result = new ArrayList<>();
        result.add(orgUsername == null ? "" : orgUsername);
        result.add(entryTime == null? "" : entryTime);
        result.add(loginUsername == null? "" : loginUsername);
        result.add(tableName == null? "" : tableName);
        result.add(tableUniqueId == null? "" : tableUniqueId);
        result.add(uiEntryTime == null? "" : uiEntryTime);
        result.add(deviceName == null? "" : deviceName);
        result.add(scanDirMappingId == null? "" : scanDirMappingId);
        result.add(type == null? "" : type);
        result.add(sizeInKb == null? "" : Double.toString(sizeInKb));
        result.add(size == null? "" : size);
        result.add(scannedDate == null? "" : scannedDate);
        result.add(detectedAt == null? "" : detectedAt);
        result.add(editedAt == null? "" : editedAt);
        result.add(deletedAt == null? "" : deletedAt);
        result.add(remark == null? "" : remark);
        result.add(parentPath == null? "" : parentPath);
        result.add(pathName == null? "" : pathName);
        result.add(fileName == null? "" : fileName);
        result.add(extension == null? "" : extension);
        return result;
    }

    @Override
    public String toString() {
        return "FilepathDBParameters{" +
                "isUpdated=" + isUpdated +
                ", id=" + id +
                ", orgUsername='" + orgUsername + '\'' +
                ", entryTime='" + entryTime + '\'' +
                ", loginUsername='" + loginUsername + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableUniqueId='" + tableUniqueId + '\'' +
                ", uiEntryTime='" + uiEntryTime + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", scanDirMappingId='" + scanDirMappingId + '\'' +
                ", type='" + type + '\'' +
                ", sizeInKb=" + sizeInKb +
                ", size='" + size + '\'' +
                ", scannedDate='" + scannedDate + '\'' +
                ", detectedAt='" + detectedAt + '\'' +
                ", editedAt='" + editedAt + '\'' +
                ", deletedAt='" + deletedAt + '\'' +
                ", remark='" + remark + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", pathName='" + pathName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", extension='" + extension + '\'' +
                ", pathInfo=" + pathInfo +
                ", filePathDetails='" + filePathDetails + '\'' +
                ", orgUsernameMaxLength=" + orgUsernameMaxLength +
                ", entryTimeMaxLength=" + entryTimeMaxLength +
                ", loginUsernameMaxLength=" + loginUsernameMaxLength +
                ", tableNameMaxLength=" + tableNameMaxLength +
                ", tableUniqueIdMaxLength=" + tableUniqueIdMaxLength +
                ", uiEntryTimeMaxLength=" + uiEntryTimeMaxLength +
                ", deviceNameMaxLength=" + deviceNameMaxLength +
                ", scanDirMappingIdMaxLength=" + scanDirMappingIdMaxLength +
                ", typeMaxLength=" + typeMaxLength +
                ", minSizeInKb=" + minSizeInKb +
                ", sizeMaxLength=" + sizeMaxLength +
                ", scannedDateMaxLength=" + scannedDateMaxLength +
                ", detectedAtMaxLength=" + detectedAtMaxLength +
                ", editedAtMaxLength=" + editedAtMaxLength +
                ", deletedAtMaxLength=" + deletedAtMaxLength +
                ", remarkMaxLength=" + remarkMaxLength +
                ", parentPathMaxLength=" + parentPathMaxLength +
                ", pathNameMaxLength=" + pathNameMaxLength +
                ", fileNameMaxLength=" + fileNameMaxLength +
                ", filePathDetailsMaxLength=" + filePathDetailsMaxLength +
                '}';
    }
}
