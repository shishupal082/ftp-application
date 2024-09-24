package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.jdbc.JdbcQueryStatus;
import com.project.ftp.jdbc.MysqlConnection;
import com.project.ftp.obj.yamlObj.OracleDatabaseConfig;
import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.service.StaticService;
import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableMysqlDb implements TableDb {
    private final static Logger logger = LoggerFactory.getLogger(TableMysqlDb.class);
    private final MysqlConnection mysqlConnection;
    private final HashMap<String, OracleDatabaseConfig> oracleDatabaseConfigs;
    private final HashMap<String, MysqlConnection> oracleConnections;
    private int closeCount = 0;
    public TableMysqlDb(DataSourceFactory dataSourceFactory, HashMap<String, OracleDatabaseConfig> oracleDatabaseConfigs) {
        this.mysqlConnection = new MysqlConnection(dataSourceFactory.getDriverClass(), dataSourceFactory.getUrl(),
                dataSourceFactory.getUser(), dataSourceFactory.getPassword());
        if (oracleDatabaseConfigs == null) {
            oracleDatabaseConfigs = new HashMap<>();
        }
        this.oracleDatabaseConfigs = oracleDatabaseConfigs;
        this.oracleConnections = new HashMap<>();
    }
    private String getOracleDbIdentifier(TableConfiguration tableConfiguration) {
        String oracleDbIdentifier = tableConfiguration.getDbIdentifier();
        if (oracleDbIdentifier == null || oracleDbIdentifier.isEmpty()) {
            oracleDbIdentifier = "oracle";
        }
        return oracleDbIdentifier;
    }
    private MysqlConnection getDBConnection(TableConfiguration tableConfiguration) {
        String oracleDbIdentifier;
        MysqlConnection oracleCon = null;
        OracleDatabaseConfig oracleDatabaseConfig;
        if ("oracle".equals(tableConfiguration.getDbType())) {
            oracleDbIdentifier = this.getOracleDbIdentifier(tableConfiguration);
            oracleCon = this.oracleConnections.get(oracleDbIdentifier);
            if (oracleCon == null) {
                oracleDatabaseConfig = this.oracleDatabaseConfigs.get(oracleDbIdentifier);
                if (oracleDatabaseConfig == null) {
                    logger.info("getDBConnection: oracleDatabaseConfig is null for dbIdentifier: {}, {}",
                            oracleDbIdentifier, this.oracleDatabaseConfigs);
                }
                logger.info("getDBConnection: new connection created: {},{}", oracleDatabaseConfig, tableConfiguration);
                oracleCon = new MysqlConnection(oracleDatabaseConfig);
                this.oracleConnections.put(oracleDbIdentifier, oracleCon);
            }
            return oracleCon;
        }
        return mysqlConnection;
    }
    public void closeIfOracle(TableConfiguration tableConfiguration) {
        String oracleDbIdentifier;
        int connectionResetCount = 30;
        if ("oracle".equals(tableConfiguration.getDbType())) {
            oracleDbIdentifier = this.getOracleDbIdentifier(tableConfiguration);
            OracleDatabaseConfig oracleDatabaseConfig = this.oracleDatabaseConfigs.get(oracleDbIdentifier);
            if (oracleDatabaseConfig != null) {
                connectionResetCount = oracleDatabaseConfig.getConnectionResetCount();
            }
            closeCount++;
            closeCount = closeCount % connectionResetCount;
            if (closeCount == 0) {
                this.getDBConnection(tableConfiguration).close();
            }
        }
    }
    private String getEqualQuery(ArrayList<String> finalQueryParam, String colName, ArrayList<String> filterValues) {
        if (filterValues == null || filterValues.isEmpty()) {
            return null;
        }
        if (finalQueryParam == null) {
            finalQueryParam = new ArrayList<>();
        }
        String filterQuery = "";
        if (filterValues.size() > 1) {
            filterQuery = "(";
        }
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
        if (filterValues.size() > 1) {
            filterQuery = filterQuery.concat(")");
        }
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
        String filterQuery = "";
        if (filterValues.size() > 1) {
            filterQuery = "(";
        }
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
        if (filterValues.size() > 1) {
            filterQuery = filterQuery.concat(")");
        }
        return filterQuery;
    }
    private ArrayList<HashMap<String, String>> generateTableData(TableConfiguration tableConfiguration, ResultSet rs) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> rowData;
        String value;
        if (rs == null || tableConfiguration == null) {
            return null;
        }
        ArrayList<String> columnNames = tableConfiguration.getColumnName();
        if (columnNames == null) {
            columnNames = tableConfiguration.getUniquePattern();
            if (columnNames == null) {
                columnNames = tableConfiguration.getUpdateColumnName();
                if (columnNames == null) {
                    return null;
                }
            }
        }
        try {
            while (rs.next()) {
                rowData = new HashMap<>();
                for (String columnName: columnNames) {
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
    private boolean isOnlyValid(TableConfiguration tableConfiguration) {
        Boolean onlyValid = tableConfiguration.getIncludeDeleted();
        if (onlyValid == null) {
            onlyValid = true;
        }
        return onlyValid;
    }
    private String getDefaultDeletedValue(TableConfiguration tableConfiguration) {
        String defaultDeletedValue = tableConfiguration.getDefaultDeletedValue();
        if (defaultDeletedValue != null) {
            return defaultDeletedValue;
        }
        return "0";
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
        boolean isOnlyValid = this.isOnlyValid(tableConfiguration);
        if (isOnlyValid) {
            if (requestFilterParameter == null) {
                requestFilterParameter = new HashMap<>();
            }
            ArrayList<String> temp = new ArrayList<>();
            temp.add(this.getDefaultDeletedValue(tableConfiguration));
            requestFilterParameter.put("deleted", temp);
        }
        StringBuilder filterQuery = new StringBuilder();
        ArrayList<String> likeParameters = tableConfiguration.getLikeParameter();
        if (likeParameters == null) {
            likeParameters = new ArrayList<>();
        }
        String columnName;
        ArrayList<String> filterParameter;
        String tempFilterQuery;
        boolean isAndRequired = false;
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
                    tempFilterQuery = this.getLikeQuery(finalQueryParam, columnName, filterParameter);
                } else {
                    tempFilterQuery = this.getEqualQuery(finalQueryParam, columnName, filterParameter);
                }
                if (tempFilterQuery != null && !tempFilterQuery.isEmpty()) {
                    if (isAndRequired) {
                        filterQuery.append(" and ");
                    }
                    filterQuery.append(tempFilterQuery);
                    isAndRequired = true;
                }
            }
        }
        String selectColumnNames = "";
        ArrayList<String> requiredSelectColumn = tableConfiguration.getSelectColumnName();
        if (requiredSelectColumn == null || requiredSelectColumn.isEmpty()) {
            selectColumnNames = "*";
        } else {
            selectColumnNames = String.join(",", requiredSelectColumn);
        }
        String joinQuery = "";
        String groupByQuery = "";
        String orderByQuery = "";
        String limitQuery = "";
        String joinParam = tableConfiguration.getJoinParam();
        ArrayList<String> groupByParam = tableConfiguration.getGroupBy();
        String limitParam = tableConfiguration.getLimit();
        String orderByParam = tableConfiguration.getOrderBy();

        String whereClause = "";
        if (!filterQuery.toString().isEmpty()) {
            whereClause = " where (" + filterQuery + ")";
        }
        if (groupByParam != null && !groupByParam.isEmpty()) {
            groupByQuery = "group by " + String.join(",",groupByParam);
        }
        if (orderByParam != null && !orderByParam.isEmpty()) {
            orderByQuery = "order by " + orderByParam;
        }
        if (joinParam != null && !joinParam.isEmpty()) {
            joinQuery = joinParam;
        }
        if (limitParam != null && !limitParam.isEmpty()) {
            limitQuery = limitParam;
        }
        String query = "select " + selectColumnNames + " from " + tableName;
        if (!joinQuery.isEmpty()) {
            query = query + " " + joinQuery;
        }
        if (!whereClause.isEmpty()) {
            query = query + " " + whereClause;
        }
        if (!groupByQuery.isEmpty()) {
            query = query + " " + groupByQuery;
        }
        if (!orderByQuery.isEmpty()) {
            query = query + " " + orderByQuery;
        }
        if (!limitQuery.isEmpty()) {
            query = query + " " + limitQuery;
        }
        if (logQuery) {
            logger.info("getByMultipleParameter: Query: {}, param: {}", query, finalQueryParam);
        }
        MysqlConnection dbConnection = this.getDBConnection(tableConfiguration);
        ResultSet rs = dbConnection.query(query, finalQueryParam);
        return this.generateTableData(tableConfiguration, rs);
    }
    public ArrayList<HashMap<String, String>> getAll(TableConfiguration tableConfiguration) {
        return this.getByMultipleParameter(tableConfiguration, null, true);
    }
    public JdbcQueryStatus updateTableEntry(TableConfiguration tableConfiguration, HashMap<String, String> data,
                                             HashMap<String, ArrayList<String>> requestFilterParameter) {
        if (tableConfiguration == null || data == null || StaticService.isInValidString(tableConfiguration.getTableName())) {
            return null;
        }
        ArrayList<String> updateColumnName = tableConfiguration.getUpdateColumnName();
        if (updateColumnName == null) {
            return null;
        }
        boolean onlyValid = this.isOnlyValid(tableConfiguration);
        StringBuilder filterQuery = new StringBuilder();
        StringBuilder setDataParameter = new StringBuilder();
        String columnName;
        ArrayList<String> filterParameter;
        ArrayList<String> finalQueryParam = new ArrayList<>();
        if (!onlyValid) {
            if (requestFilterParameter == null) {
                requestFilterParameter = new HashMap<>();
            }
            ArrayList<String> temp = new ArrayList<>();
            temp.add(this.getDefaultDeletedValue(tableConfiguration));
            requestFilterParameter.put("deleted", temp);
        }
        int index = 0;
        int lastIndex = updateColumnName.size()-1;
        for(String col: updateColumnName) {
            setDataParameter.append(col).append("=?");
            if (index != lastIndex) {
                setDataParameter.append(",");
            }
            finalQueryParam.add(data.get(col));
            index++;
        }
        String tempFilterQuery;
        boolean isAndRequired = false;
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
                tempFilterQuery = this.getEqualQuery(finalQueryParam, columnName, filterParameter);
                if (tempFilterQuery != null && !tempFilterQuery.isEmpty()) {
                    if (isAndRequired) {
                        filterQuery.append(" and ");
                    }
                    filterQuery.append(tempFilterQuery);
                    isAndRequired = true;
                }
            }
        }
        String query = "UPDATE " + tableConfiguration.getTableName() + " SET " +
                setDataParameter +
                " WHERE " + filterQuery;
        MysqlConnection dbConnection = this.getDBConnection(tableConfiguration);
        try {
            return dbConnection.updateQueryV2(query, finalQueryParam);
        } catch (Exception e) {
            logger.info("updateEntry: error in query: {}, {}", query, e.getMessage());
        }
        return null;
    }
    public JdbcQueryStatus addTableEntry(TableConfiguration tableConfiguration, HashMap<String, String> data) {
        if (tableConfiguration == null || data == null || StaticService.isInValidString(tableConfiguration.getTableName())) {
            return null;
        }
        ArrayList<String> updateColumnName = tableConfiguration.getUpdateColumnName();
        if (updateColumnName == null) {
            return null;
        }
        StringBuilder setDataParameter = new StringBuilder();
        StringBuilder setValueParameter = new StringBuilder();
        ArrayList<String> finalQueryParam = new ArrayList<>();
        int index = 0;
        int lastIndex = updateColumnName.size()-1;
        for(String col: updateColumnName) {
            setDataParameter.append(col);
            setValueParameter.append("?");
            if (index != lastIndex) {
                setDataParameter.append(",");
                setValueParameter.append(",");
            }
            finalQueryParam.add(data.get(col));
            index++;
        }
        String query = "INSERT INTO " + tableConfiguration.getTableName() + " (" + setDataParameter + ")" +
                " VALUES(" + setValueParameter + ")";
        MysqlConnection dbConnection = this.getDBConnection(tableConfiguration);
        try {
            return dbConnection.updateQueryV2(query, finalQueryParam);
        } catch (Exception e) {
            logger.info("addTableEntry: error in query: {}, {}, {}", query, finalQueryParam, e.getMessage());
        }
        return null;
    }
    public JdbcQueryStatus addEntry(TableConfiguration tableConfiguration, HashMap<String, String> data, Integer entryCount0) {
        if (tableConfiguration == null || data == null) {
            return null;
        }
        int entryCount;
        if (entryCount0 != null) {
            entryCount = entryCount0;
        } else {
            entryCount = this.getEntryCount(tableConfiguration, data);
        }
        if (entryCount > 0) {
            logger.info("addEntry: Entry already exist for data, add not possible. " +
                    "tableConfiguration: {},  data: {}", tableConfiguration, data);
            return null;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            logger.info("addEntry: Configuration error. uniquePattern is null or empty: {}, data: {}", uniquePattern, data);
            return null;
        }
        return this.addTableEntry(tableConfiguration, data);
    }
    public ArrayList<HashMap<String, String>> searchData(TableConfiguration tableConfiguration, HashMap<String, String> data) {
        if (tableConfiguration == null || data == null) {
            return null;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            logger.info("searchData: Configuration error. uniquePattern is null or empty: {}, data: {}", uniquePattern, data);
            return null;
        }
        HashMap<String, ArrayList<String>> requestFilterParameter = new HashMap<>();
        ArrayList<String> filterParam;
        String columnValue;
        for(String columnName: uniquePattern) {
            columnValue = data.get(columnName);
            filterParam = new ArrayList<>();
            filterParam.add(columnValue);
            requestFilterParameter.put(columnName, filterParam);
        }
        return this.getByMultipleParameter(tableConfiguration, requestFilterParameter, false);
    }
    public int getEntryCount(TableConfiguration tableConfiguration, HashMap<String, String> data) {
        ArrayList<HashMap<String, String>> existingData = this.searchData(tableConfiguration, data);
        if (existingData == null || existingData.isEmpty()) {
            return 0;
        }
        return existingData.size();
    }
    public JdbcQueryStatus updateEntry(TableConfiguration tableConfiguration, HashMap<String, String> data, Integer entryCount0) {
        if (tableConfiguration == null || data == null) {
            return null;
        }
        int entryCount;
        if (entryCount0 != null) {
            entryCount = entryCount0;
        } else {
            entryCount = this.getEntryCount(tableConfiguration, data);
        }
        if (entryCount != 1) {
            logger.info("updateEntry: Unique entry not exist, update not possible.");
            return null;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            return null;
        }
        HashMap<String, ArrayList<String>> requestFilterParameter = new HashMap<>();
        ArrayList<String> filterParam;
        String columnValue;
        for(String columnName: uniquePattern) {
            columnValue = data.get(columnName);
            filterParam = new ArrayList<>();
            filterParam.add(columnValue);
            requestFilterParameter.put(columnName, filterParam);
        }
        return this.updateTableEntry(tableConfiguration, data, requestFilterParameter);
    }
}
