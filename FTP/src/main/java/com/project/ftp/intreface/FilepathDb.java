package com.project.ftp.intreface;

import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FilepathCol;
import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.jdbc.JdbcQueryStatus;
import com.project.ftp.jdbc.MysqlConnection;
import com.project.ftp.obj.FilepathDBParameters;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.StaticService;
import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class FilepathDb extends FilepathInterface {
    private final static Logger logger = LoggerFactory.getLogger(FilepathDb.class);
    private final MysqlConnection mysqlConnection;
    private final String tableName = AppConstant.TABLE_FILE_PATH;
    public FilepathDb(DataSourceFactory dataSourceFactory) {
        this.mysqlConnection = new MysqlConnection(dataSourceFactory);
    }
    private ArrayList<FilepathDBParameters> generateFilepathDBParameters(ResultSet rs) {
        ArrayList<FilepathDBParameters> result = new ArrayList<>();
        if (rs == null) {
            return null;
        }
        try {
            while (rs.next()) {
                FilepathDBParameters dbParameters = new FilepathDBParameters();
                dbParameters.setId(rs.getInt(FilepathCol.id.getColumnName()));

                dbParameters.setOrgUsername(rs.getString(FilepathCol.orgUsername.getColumnName()));
                dbParameters.setEntryTime(rs.getString(FilepathCol.entryTime.getColumnName()));
                dbParameters.setLoginUsername(rs.getString(FilepathCol.loginUsername.getColumnName()));
                dbParameters.setTableName(rs.getString(FilepathCol.colTableName.getColumnName()));

                dbParameters.setTableUniqueId(rs.getString(FilepathCol.tableUniqueId.getColumnName()));
                dbParameters.setUiEntryTime(rs.getString(FilepathCol.uiEntryTime.getColumnName()));

                dbParameters.setDeviceName(rs.getString(FilepathCol.deviceName.getColumnName()));
                dbParameters.setScanDirMappingId(rs.getString(FilepathCol.scanDirMappingId.getColumnName()));
                dbParameters.setType(rs.getString(FilepathCol.type.getColumnName()));
                dbParameters.setSizeInKb(rs.getDouble(FilepathCol.sizeInKb.getColumnName()));
                dbParameters.setSize(rs.getString(FilepathCol.size.getColumnName()));

                dbParameters.setScannedDate(rs.getString(FilepathCol.scannedDate.getColumnName()));
                dbParameters.setDetectedAt(rs.getString(FilepathCol.detectedAt.getColumnName()));
                dbParameters.setEditedAt(rs.getString(FilepathCol.editedAt.getColumnName()));
                dbParameters.setDeletedAt(rs.getString(FilepathCol.deletedAt.getColumnName()));
                dbParameters.setRemark(rs.getString(FilepathCol.remark.getColumnName()));
                dbParameters.setParentPath(rs.getString(FilepathCol.parentPath.getColumnName()));
                dbParameters.setPathName(rs.getString(FilepathCol.colPathname.getColumnName()));
                dbParameters.setFileName(rs.getString(FilepathCol.colFilename.getColumnName()));
                PathInfo pathInfo = new PathInfo(dbParameters.getType(), dbParameters.getFileName());
                pathInfo.findExtension();
                dbParameters.setExtension(pathInfo.getExtension());
                result.add(dbParameters);
            }
        } catch (Exception e) {
            logger.info("Error in reading data parsing mysql");
        }
        return result;
    }
    private String getEqualQuery(String colName, ArrayList<String> filterValues) {
        if (filterValues == null || filterValues.isEmpty()) {
            return null;
        }
        String filterQuery = " and (";
        int i=0;
        for (String str : filterValues) {
            if (i==0) {
                filterQuery = filterQuery.concat(colName + "=?");
            } else {
                filterQuery = filterQuery.concat(" or " + colName + "=?");
            }
            i++;
        }
        filterQuery = filterQuery.concat(")");
        return filterQuery;
    }
    private String getLikeQuery(String colName, ArrayList<String> filterValues) {
        if (filterValues == null || filterValues.isEmpty()) {
            return null;
        }
        //For reading .pdf or .csv file
        String filterQuery = " and (";
        int i=0;
        for (String str : filterValues) {
            if (i==0) {
                filterQuery = filterQuery.concat(colName + " like ?");
            } else {
                filterQuery = filterQuery.concat(" or " + colName + " like ?");
            }
            i++;
        }
        filterQuery = filterQuery.concat(")");
        return filterQuery;
    }
    public ArrayList<FilepathDBParameters> getByFilterParameter(String pathname, ArrayList<String> scanDirId,
                                                                ArrayList<String> fileType, boolean recursive) {
        ArrayList<String> pathnameParam = new ArrayList<>();
        ArrayList<String> filetypeParam = new ArrayList<>();
        ArrayList<String> scanDirMappingIdParam = new ArrayList<>();
        String[] splitResult;
        if (pathname != null) {
            splitResult = pathname.split("\\|");
            for(String str: splitResult) {
                if (StaticService.isValidString(str)) {
                    pathnameParam.add(str.trim());
                }
            }
        }
        if (fileType != null) {
            filetypeParam = fileType;
        }
        if (scanDirId != null) {
            scanDirMappingIdParam = scanDirId;
        }
        ArrayList<FilepathDBParameters> filepathDBParameters =
                this.getByMultipleParameter(scanDirMappingIdParam, pathnameParam,
                        filetypeParam, false, true);
        if (filepathDBParameters != null) {
            logger.info("getByFilterParameter: DB result count: {}", filepathDBParameters.size());
        }
        if (recursive) {
            return filepathDBParameters;
        } else if (pathnameParam.isEmpty()) {
            return filepathDBParameters;
        } else if (filepathDBParameters == null) {
            return null;
        }
        ArrayList<FilepathDBParameters> result = new ArrayList<>();
        String filename;
        String requiredFilename;
        for(FilepathDBParameters dbParameters: filepathDBParameters) {
            for(String requestedPathname: pathnameParam) {
                filename = dbParameters.getFileName();
                requiredFilename = StaticService.getProperDirString(requestedPathname+"/"+filename);
                if (filename != null) {
                    if (requiredFilename.equals(dbParameters.getPathName())) {
                        result.add(dbParameters);
                        break;
                    }
                }
            }
        }
        logger.info("getByFilterParameter: final result count: {}", result.size());
        return result;
    }
    @Override
    public ArrayList<FilepathDBParameters> getByMultipleParameter(ArrayList<String> scan_dir_id,
                                                                  ArrayList<String> pathname,
                                                                  ArrayList<String> filetype,
                                                                  boolean pathnameExact,
                                                                  boolean logQuery) {
        String filterQuery = "";
        ArrayList<String> finalQueryParam = new ArrayList<>();
        ArrayList<String> tempParam;
        if (scan_dir_id != null && !scan_dir_id.isEmpty()) {
            filterQuery = filterQuery.concat(this.getEqualQuery(FilepathCol.scanDirMappingId.getColumnName(), scan_dir_id));
            finalQueryParam.addAll(scan_dir_id);
        }
        if (pathname != null && !pathname.isEmpty()) {
            tempParam = new ArrayList<>();
            for (String str: pathname) {
                if (pathnameExact) {
                    tempParam.add(str);
                } else {
                    tempParam.add(str+"%");
                }
            }
            if (pathnameExact) {
                filterQuery = filterQuery.concat(this.getEqualQuery(FilepathCol.colPathname.getColumnName(), tempParam));
            } else {
                filterQuery = filterQuery.concat(this.getLikeQuery(FilepathCol.colPathname.getColumnName(), tempParam));
            }
            finalQueryParam.addAll(tempParam);
        }
        if (filetype != null && !filetype.isEmpty()) {
            tempParam = new ArrayList<>();
            for (String str: filetype) {
                tempParam.add("%."+str);
            }
            filterQuery = filterQuery.concat(this.getLikeQuery(FilepathCol.colFilename.getColumnName(), tempParam));
            finalQueryParam.addAll(tempParam);
        }
        String query = "select * from " + tableName + " where deleted=0" + filterQuery + " order by id desc;";
        if (logQuery) {
            logger.info("getByMultipleParameter: Query: {}, param: {}", query, finalQueryParam);
        }
        ResultSet rs = mysqlConnection.query(query, finalQueryParam);
        return this.generateFilepathDBParameters(rs);
    }
    private ArrayList<String> getDbTableParameter(FilepathDBParameters dbParameters) {
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(dbParameters.getOrgUsernameV2());
        parameters.add(dbParameters.getEntryTimeV2());
        parameters.add(dbParameters.getLoginUsernameV2());

        parameters.add(dbParameters.getTableNameV2());
        parameters.add(dbParameters.getTableUniqueIdV2());
        parameters.add(dbParameters.getUiEntryTimeV2());

        parameters.add(dbParameters.getDeviceNameV2());
        parameters.add(dbParameters.getScanDirMappingIdV2());
        parameters.add(dbParameters.getTypeV2());
        parameters.add(String.valueOf(dbParameters.getSizeInKbV2()));
        parameters.add(dbParameters.getSizeV2());

        parameters.add(dbParameters.getScannedDateV2());
        parameters.add(dbParameters.getDetectedAtV2());
        parameters.add(dbParameters.getEditedAtV2());
        parameters.add(dbParameters.getDeletedAtV2());

        parameters.add(dbParameters.getRemarkV2());
        parameters.add(dbParameters.getParentPathV2());
        parameters.add(dbParameters.getPathNameV2());
        parameters.add(dbParameters.getFileNameV2());
        return parameters;
    }
    private JdbcQueryStatus addEntry(FilepathDBParameters dbParameters) {
        String query = "INSERT INTO " + tableName + " (org_username,entry_time,login_username," +
                "table_name,table_unique_id,ui_entry_time," +
                "device_name,scan_dir_mapping_id,type,size_in_kb,size," +
                "scanned_date,detected_at,edited_at,deleted_at," +
                "remark,parent_path,pathname,filename)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            return mysqlConnection.updateQueryV2(query, this.getDbTableParameter(dbParameters));
        } catch (Exception e) {
            logger.info("addEntry: error in query: {}, {}", query, e.getMessage());
        }
        return null;
    }
    private JdbcQueryStatus updateEntry(FilepathDBParameters dbParameters) {
        if (dbParameters == null || dbParameters.getId() < 1) {
            return null;
        }
        String query = "UPDATE " + tableName + " SET " +
                "org_username=?,entry_time=?,login_username=?," +
                "table_name=?,table_unique_id=?,ui_entry_time=?," +
                "device_name=?,scan_dir_mapping_id=?,type=?,size_in_kb=?,size=?," +
                "scanned_date=?,detected_at=?,edited_at=?,deleted_at=?," +
                "remark=?,parent_path=?,pathname=?,filename=?" +
                " WHERE id="+dbParameters.getId();
        try {
            return mysqlConnection.updateQueryV2(query, this.getDbTableParameter(dbParameters));
        } catch (Exception e) {
            logger.info("updateEntry: error in query: {}, {}", query, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public HashMap<String, Integer> updateIntoDb(FilePathDAO filePathDAO) {
        ArrayList<FilepathDBParameters> filepathDBParameters = filePathDAO.getAll();
        HashMap<String, Integer> result = new HashMap<>();
        int addedEntrySuccess = 0;
        int addedEntryFailure = 0;
        int updatedEntrySuccess = 0;
        int updatedEntryFailure = 0;
        int skippedUpdate = 0;
        JdbcQueryStatus jdbcQueryStatus;
        for(FilepathDBParameters dbParameters: filepathDBParameters) {
            if (dbParameters != null && dbParameters.isUpdated()) {
                if (dbParameters.getId() > 0) {
                    jdbcQueryStatus = this.updateEntry(dbParameters);
                    if (jdbcQueryStatus != null && AppConstant.SUCCESS.equals(jdbcQueryStatus.getStatus())) {
                        updatedEntrySuccess++;
                    } else {
                        updatedEntryFailure++;
                    }
                } else {
                    jdbcQueryStatus = this.addEntry(dbParameters);
                    if (jdbcQueryStatus != null && AppConstant.SUCCESS.equals(jdbcQueryStatus.getStatus())) {
                        addedEntrySuccess++;
                    } else {
                        addedEntryFailure++;
                    }
                }
            } else {
                skippedUpdate++;
            }
        }
        result.put("addedEntrySuccess", addedEntrySuccess);
        result.put("addedEntryFailure", addedEntryFailure);
        result.put("updatedEntrySuccess", updatedEntrySuccess);
        result.put("updatedEntryFailure", updatedEntryFailure);
        result.put("skippedUpdate", skippedUpdate);
        logger.info("updateIntoDb completed, result: {}", result);
        return result;
    }
    @Override
    public ArrayList<FilepathDBParameters> getAll() {
        return this.getByMultipleParameter(null, null, null, false, true);
    }
    @Override
    public void updateFromReqScanDir(FilePathDAO filePathDAO, ArrayList<String> scanDirListId, boolean isRecursive) {
        ArrayList<FilepathDBParameters> filepathDBParameters = this.getByFilterParameter(null, scanDirListId, null, isRecursive);
        if (filepathDBParameters != null) {
            filePathDAO.addAll(filepathDBParameters);
        }

    }
}
