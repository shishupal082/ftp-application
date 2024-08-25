package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.jdbc.MysqlConnection;
import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class TableDb {
    private final static Logger logger = LoggerFactory.getLogger(TableDb.class);
    private final MysqlConnection mysqlConnection;
    public TableDb(DataSourceFactory dataSourceFactory) {
        this.mysqlConnection = new MysqlConnection(dataSourceFactory);
    }
    private ArrayList<HashMap<String, String>> generateTableData(TableConfiguration tableConfiguration, ResultSet rs) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> rowData;
        String value;
        if (rs == null || tableConfiguration == null || tableConfiguration.getColumnName() == null) {
            return null;
        }
        ArrayList<String> colAttributes = tableConfiguration.getColumnName();
        try {
            while (rs.next()) {
                rowData = new HashMap<>();
                for (String columnName: colAttributes) {
                    if (columnName == null || columnName.isEmpty()) {
                        continue;
                    }
                    value = rs.getString(columnName);
                    rowData.put(columnName, value);
                }
                result.add(rowData);
            }
        } catch (Exception e) {
            result = null;
            logger.info("Error in reading data parsing mysql: {}", e.toString());
        }
        return result;
    }
    public ArrayList<HashMap<String, String>> getByMultipleParameter(TableConfiguration tableConfiguration,
                                                                     boolean logQuery) {
        if (tableConfiguration == null) {
            logger.info("getByMultipleParameter: invalid tableConfiguration: null");
            return null;
        }
        String tableName = tableConfiguration.getTableName();
        if (tableConfiguration.getTableName() == null || tableConfiguration.getTableName().isEmpty()) {
            logger.info("getByMultipleParameter: invalid tableName: null");
            return null;
        }
        String filterQuery = "";
        ArrayList<String> finalQueryParam = new ArrayList<>();
        String query = "select * from " + tableName + " where deleted=0" + filterQuery + " order by id desc;";
        if (logQuery) {
            logger.info("getByMultipleParameter: Query: {}, param: {}", query, finalQueryParam);
        }
        ResultSet rs = mysqlConnection.query(query, finalQueryParam);
        return this.generateTableData(tableConfiguration, rs);
    }
    public ArrayList<HashMap<String, String>> getAll(TableConfiguration tableConfiguration) {
        return this.getByMultipleParameter(tableConfiguration, true);
    }
}
