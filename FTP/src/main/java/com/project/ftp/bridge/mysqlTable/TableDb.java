package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.jdbc.MysqlConnection;
import com.project.ftp.service.StaticService;
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
    public void updateTableEntry(TableConfiguration tableConfiguration, HashMap<String, String> data,
                                 HashMap<String, ArrayList<String>> requestFilterParameter) {
        if (tableConfiguration == null || data == null || StaticService.isInValidString(tableConfiguration.getTableName())) {
            return;
        }
        if (tableConfiguration.getColumnName() == null) {
            return;
        }
        ArrayList<String> setDataColumn = tableConfiguration.getColumnName();
        StringBuilder filterQuery = new StringBuilder();
        StringBuilder setDataParameter = new StringBuilder();
        String columnName;
        ArrayList<String> filterParameter;
        ArrayList<String> finalQueryParam = new ArrayList<>();
        int index = 0;
        int lastIndex = setDataColumn.size()-1;
        for(String col: setDataColumn) {
            setDataParameter.append(col).append("=?");
            if (index != lastIndex) {
                setDataParameter.append(",");
            }
            finalQueryParam.add(data.get(col));
            index++;
        }
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
                filterQuery.append(this.getEqualQuery(finalQueryParam, columnName, filterParameter));
            }
        }
        String query = "UPDATE " + tableConfiguration.getTableName() + " SET " +
                setDataParameter +
                " WHERE deleted=0 "+filterQuery + ";";
        try {
            mysqlConnection.updateQueryV2(query, finalQueryParam);
        } catch (Exception e) {
            logger.info("updateEntry: error in query: {}, {}", query, e.getMessage());
            e.printStackTrace();
        }
    }
    private void addTableEntry(TableConfiguration tableConfiguration, HashMap<String, String> data) {
        if (tableConfiguration == null || data == null || StaticService.isInValidString(tableConfiguration.getTableName())) {
            return;
        }
        if (tableConfiguration.getColumnName() == null) {
            return;
        }
        ArrayList<String> setDataColumn = tableConfiguration.getColumnName();
        StringBuilder setDataParameter = new StringBuilder();
        StringBuilder setValueParameter = new StringBuilder();
        ArrayList<String> finalQueryParam = new ArrayList<>();
        int index = 0;
        int lastIndex = setDataColumn.size()-1;
        for(String col: setDataColumn) {
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
                " VALUES(" + setValueParameter + ");";
        try {
            mysqlConnection.updateQueryV2(query, finalQueryParam);
        } catch (Exception e) {
            logger.info("addTableEntry: error in query: {}, {}", query, e.getMessage());
        }
    }
    public void addEntry(TableConfiguration tableConfiguration, HashMap<String, String> data, Integer entryCount0) {
        if (tableConfiguration == null || data == null) {
            return;
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
            return;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            logger.info("addEntry: Configuration error. uniquePattern is null or empty: {}, data: {}", uniquePattern, data);
            return;
        }
        this.addTableEntry(tableConfiguration, data);
    }
    public int getEntryCount(TableConfiguration tableConfiguration, HashMap<String, String> data) {
        if (tableConfiguration == null || data == null) {
            return 0;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            logger.info("isEntryExist: Configuration error. uniquePattern is null or empty: {}, data: {}", uniquePattern, data);
            return 0;
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
        ArrayList<HashMap<String, String>> existingData = this.getByMultipleParameter(tableConfiguration, requestFilterParameter, false);
        if (existingData == null || existingData.isEmpty()) {
            logger.info("isEntryExist: Entry not found for uniquePattern: {}, data: {}", uniquePattern, data);
            return 0;
        }
        return existingData.size();
    }
    public void updateEntry(TableConfiguration tableConfiguration, HashMap<String, String> data, Integer entryCount0) {
        if (tableConfiguration == null || data == null) {
            return;
        }
        int entryCount;
        if (entryCount0 != null) {
            entryCount = entryCount0;
        } else {
            entryCount = this.getEntryCount(tableConfiguration, data);
        }
        if (entryCount != 1) {
            logger.info("updateEntry: Unique entry not exist, update not possible.");
            return;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            return;
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
        this.updateTableEntry(tableConfiguration, data, requestFilterParameter);
    }
    public void addOrUpdateEntry(TableConfiguration tableConfiguration, HashMap<String, String> data) {
        if (tableConfiguration == null || data == null) {
            return;
        }
        int entryCount = this.getEntryCount(tableConfiguration, data);
        if (entryCount == 1) {
            logger.info("addOrUpdateEntry: Entry already exist, updating it.");
            this.updateEntry(tableConfiguration, data, entryCount);
        } else if (entryCount < 1) {
            logger.info("addOrUpdateEntry: Entry not exist, adding it.");
            this.addEntry(tableConfiguration, data, entryCount);
        } else {
            logger.info("addOrUpdateEntry: Multi entry exist, add or update not possible.");
        }
    }
}
