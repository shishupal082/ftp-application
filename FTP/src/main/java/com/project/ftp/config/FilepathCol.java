package com.project.ftp.config;

/**
 * Created by shishupalkumar on 18/02/17.
 */
public enum FilepathCol {
    id("id"),
    orgUsername("org_username"),
    entryTime("entry_time"),
    loginUsername("login_username"),
    colTableName("table_name"),
    tableUniqueId("table_unique_id"),
    uiEntryTime("ui_entry_time"),
    deviceName("device_name"),
    scanDirMappingId("scan_dir_mapping_id"),
    type("type"),
    sizeInKb("size_in_kb"),
    size("size"),
    scannedDate("scanned_date"),
    detectedAt("detected_at"),
    editedAt("edited_at"),
    deletedAt("deleted_at"),
    remark("remark"),
    parentPath("parent_path"),
    colPathname("pathname"),
    colFilename("filename");

    private final String filepathColumn;

    FilepathCol(String filepathColumn) {
        this.filepathColumn = filepathColumn;
    }

    public String getColumnName() {
        return filepathColumn;
    }
//    public void setPathType(String pathType) {
//        this.pathType = pathType;
//    }
}
