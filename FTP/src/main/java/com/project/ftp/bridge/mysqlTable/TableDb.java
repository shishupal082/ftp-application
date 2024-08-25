package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.jdbc.MysqlConnection;
import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableDb {
    private final static Logger logger = LoggerFactory.getLogger(TableDb.class);
    private final MysqlConnection mysqlConnection;
    public TableDb(DataSourceFactory dataSourceFactory) {
        this.mysqlConnection = new MysqlConnection(dataSourceFactory);
    }
    private String getEqualQuery(ArrayList<String> finalQueryParam, String colName, ArrayList<String> filterValues) {
        if (filterValues == null || filterValues.isEmpty()) {
            return null;
        }
        if (finalQueryParam == null) {
            finalQueryParam = new ArrayList<>();
        }
        String filterQuery = " and (";
        int i=0;
        for (String str : filterValues) {
            if (i==0) {
                filterQuery = filterQuery.concat(colName + "=?");
            } else {
                filterQuery = filterQuery.concat(" or " + colName + "=?");
            }
            finalQueryParam.add(str);
            i++;
        }
        filterQuery = filterQuery.concat(")");
        return filterQuery;
    }
    private String getLikeQuery(ArrayList<String> finalQueryParam, String colName, ArrayList<String> filterValues) {
        if (filterValues == null || filterValues.isEmpty()) {
            return null;
        }
        if (finalQueryParam == null) {
            finalQueryParam = new ArrayList<>();
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
            finalQueryParam.add(str);
            i++;
        }
        filterQuery = filterQuery.concat(")");
        return filterQuery;
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
                                                                     HashMap<String, ArrayList<String>> requestFilterParameter,
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
        ArrayList<String> finalQueryParam = new ArrayList<>();
        StringBuilder filterQuery = new StringBuilder();
        ArrayList<String> likeParameters = tableConfiguration.getLikeParameter();
        if (likeParameters == null) {
            likeParameters = new ArrayList<>();
        }
        String columnName;
        ArrayList<String> filterParameter;
        if (requestFilterParameter != null) {
            for(Map.Entry<String, ArrayList<String>> set: requestFilterParameter.entrySet()) {
                if (set == null) {
                    continue;
                }
                columnName = set.getKey();
                filterParameter = set.getValue();
                if (columnName == null || columnName.isEmpty() || filterParameter == null) {
                    continue;
                }
                if (likeParameters.contains(columnName)) {
                    filterQuery.append(this.getLikeQuery(finalQueryParam, columnName, filterParameter));
                } else {
                    filterQuery.append(this.getEqualQuery(finalQueryParam, columnName, filterParameter));
                }
            }
        }
        String limitQuery = "";
        String orderByQuery = "";
        int limit;
        String limitParam = tableConfiguration.getLimit();
        String orderByParam = tableConfiguration.getOrderBy();
        String query = "select * from " + tableName + " where deleted=0" + filterQuery;
        if (limitParam != null && !limitParam.isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit > 0) {
                    limitQuery = "limit " + limit;
                }
            } catch (Exception ignore) {}
        }
        if (orderByParam != null && !orderByParam.isEmpty()) {
            orderByQuery = "order by " + orderByParam;
        }
        if (!orderByQuery.isEmpty()) {
            query = query + " " + orderByQuery;
        }
        if (!limitQuery.isEmpty()) {
            query = query + " " + limitQuery;
        }
        query = query + ";";
        if (logQuery) {
            logger.info("getByMultipleParameter: Query: {}, param: {}", query, finalQueryParam);
        }
        ResultSet rs = mysqlConnection.query(query, finalQueryParam);
        return this.generateTableData(tableConfiguration, rs);
    }
    public ArrayList<HashMap<String, String>> getAll(TableConfiguration tableConfiguration) {
        return this.getByMultipleParameter(tableConfiguration, null, true);
    }
}
