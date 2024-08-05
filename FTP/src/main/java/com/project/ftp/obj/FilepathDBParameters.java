package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FilepathCol;
import com.project.ftp.service.StaticService;

import java.util.ArrayList;
import java.util.HashMap;

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
    private String reqScanDirId;
    private String reqPathName;
    private String reqFileType;
    private String reqRecursive;
    private String reqCsvMappingId;

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

    public String getReqScanDirId() {
        return reqScanDirId;
    }

    public void setReqScanDirId(String reqScanDirId) {
        this.reqScanDirId = reqScanDirId;
    }

    public String getReqPathName() {
        return reqPathName;
    }

    public void setReqPathName(String reqPathName) {
        this.reqPathName = reqPathName;
    }

    public String getReqFileType() {
        return reqFileType;
    }

    public void setReqFileType(String reqFileType) {
        this.reqFileType = reqFileType;
    }

    public String getReqRecursive() {
        return reqRecursive;
    }

    public void setReqRecursive(String reqRecursive) {
        this.reqRecursive = reqRecursive;
    }

    public String getReqCsvMappingId() {
        return reqCsvMappingId;
    }

    public void setReqCsvMappingId(String reqCsvMappingId) {
        this.reqCsvMappingId = reqCsvMappingId;
    }
    public HashMap<String, String> getJsonData() {
        HashMap<String, String> result = new HashMap<>();
        result.put(FilepathCol.id.getColumnName(), Long.toString(id));
        if (StaticService.isValidString(orgUsername)) {
            result.put(FilepathCol.orgUsername.getColumnName(), orgUsername);
        }
        if (StaticService.isValidString(entryTime)) {
            result.put(FilepathCol.entryTime.getColumnName(), entryTime);
        }
        if (StaticService.isValidString(loginUsername)) {
            result.put(FilepathCol.loginUsername.getColumnName(), loginUsername);
        }
        if (StaticService.isValidString(tableName)) {
            result.put(FilepathCol.colTableName.getColumnName(), tableName);
        }
        if (StaticService.isValidString(tableUniqueId)) {
            result.put(FilepathCol.tableUniqueId.getColumnName(), tableUniqueId);
        }
        if (StaticService.isValidString(uiEntryTime)) {
            result.put(FilepathCol.uiEntryTime.getColumnName(), uiEntryTime);
        }
        if (StaticService.isValidString(deviceName)) {
            result.put(FilepathCol.deviceName.getColumnName(), deviceName);
        }
        if (StaticService.isValidString(scanDirMappingId)) {
            result.put(FilepathCol.scanDirMappingId.getColumnName(), scanDirMappingId);
        }
        if (StaticService.isValidString(type)) {
            result.put(FilepathCol.type.getColumnName(), type);
        }
        result.put(FilepathCol.sizeInKb.getColumnName(), Double.toString(sizeInKb));
        if (StaticService.isValidString(size)) {
            result.put(FilepathCol.size.getColumnName(), size);
        }
        if (StaticService.isValidString(scannedDate)) {
            result.put(FilepathCol.scannedDate.getColumnName(), scannedDate);
        }
        if (StaticService.isValidString(detectedAt)) {
            result.put(FilepathCol.detectedAt.getColumnName(), detectedAt);
        }
        if (StaticService.isValidString(editedAt)) {
            result.put(FilepathCol.editedAt.getColumnName(), editedAt);
        }
        if (StaticService.isValidString(deletedAt)) {
            result.put(FilepathCol.deletedAt.getColumnName(), deletedAt);
        }
        if (StaticService.isValidString(remark)) {
            result.put(FilepathCol.remark.getColumnName(), remark);
        }
        if (StaticService.isValidString(parentPath)) {
            result.put(FilepathCol.parentPath.getColumnName(), parentPath);
        }
        if (StaticService.isValidString(pathName)) {
            result.put(FilepathCol.colPathname.getColumnName(), pathName);
        }
        if (StaticService.isValidString(fileName)) {
            result.put(FilepathCol.colFilename.getColumnName(), fileName);
        }
        if (StaticService.isValidString(extension)) {
            result.put("extension", extension);
        }
        if (StaticService.isValidString(reqScanDirId)) {
            result.put("req_scan_dir_id", reqScanDirId);
        }
        if (StaticService.isValidString(reqPathName)) {
            result.put("req_pathname", reqPathName);
        }
        if (StaticService.isValidString(reqFileType)) {
            result.put("req_filetype", reqFileType);
        }
        if (StaticService.isValidString(reqRecursive)) {
            result.put("req_recursive", reqRecursive);
        }
        if (StaticService.isValidString(reqCsvMappingId)) {
            result.put("req_csv_mapping_id", reqCsvMappingId);
        }
        return result;
    }
    public ArrayList<String> getArrayData() {
        ArrayList<String> result = new ArrayList<>();
        result.add(Long.toString(id));
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
        result.add(reqScanDirId);
        result.add(reqPathName);
        result.add(reqFileType);
        result.add(reqRecursive);
        result.add(reqCsvMappingId);
        return result;
    }
    public ArrayList<String> getCsvData() {
        ArrayList<String> result = new ArrayList<>();
        result.add(Long.toString(id));
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
        result.add(reqScanDirId == null? "": reqScanDirId);
        result.add(reqPathName == null? "": reqPathName);
        result.add(reqFileType == null? "": reqFileType);
        result.add(reqRecursive == null? "": reqRecursive);
        result.add(reqCsvMappingId == null? "": reqCsvMappingId);
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
                ", reqScanDirId='" + reqScanDirId + '\'' +
                ", reqPathName='" + reqPathName + '\'' +
                ", reqFileType='" + reqFileType + '\'' +
                ", reqRecursive='" + reqRecursive + '\'' +
                ", reqCsvMappingId='" + reqCsvMappingId + '\'' +
                '}';
    }
}
