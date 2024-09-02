package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.yamlObj.MaintainHistory;
import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.obj.yamlObj.TableFileConfiguration;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.service.MSExcelService;
import com.project.ftp.service.MiscService;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TableService {
    final static Logger logger = LoggerFactory.getLogger(TableService.class);
    private final FtpConfiguration ftpConfiguration;
    private final MSExcelService msExcelService;
    private final TableDb tableDb;
    public TableService(final FtpConfiguration ftpConfiguration, final MSExcelService msExcelService, final TableDb tableDb) {
        this.ftpConfiguration = ftpConfiguration;
        this.msExcelService = msExcelService;
        this.tableDb = tableDb;
    }
    private boolean isAllowEmptyFilter(TableConfiguration tableConfiguration) {
        Boolean allowEmptyFilter = tableConfiguration.getAllowEmptyFilter();
        if (allowEmptyFilter != null) {
            return allowEmptyFilter;
        }
        return true;
    }
    private boolean isUpdateIfFoundEnabled(TableConfiguration tableConfiguration) {
        Boolean updateIfFound = tableConfiguration.getUpdateIfFound();
        if (updateIfFound != null) {
            return updateIfFound;
        }
        return true;
    }
    private TableConfiguration getTableConfiguration(String tableConfigId) throws AppException {
        if (ftpConfiguration == null) {
            logger.info("ftpConfiguration is null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        ArrayList<String> tableDbConfigs = ftpConfiguration.getTableDbConfigFilePath();
//        String filepath = "D:/workspace/ftp-application/FTP/meta-data/config-files/table-db/table-db-config.yml";
//        tableDbConfigs.add(filepath);
        YamlFileParser yamlFileParser = new YamlFileParser();
        TableFileConfiguration tableFileConfiguration;
        ArrayList<TableConfiguration> tableConfigurations;
        String tempTableConfigId;
        TableConfiguration resultTableConfiguration = null;
        if (tableDbConfigs == null) {
            logger.info("ftpConfiguration.tableDbConfigFilePath is null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        for(String filePath: tableDbConfigs) {
            tableFileConfiguration = yamlFileParser.tableDbConfigByConfigPath(filePath);
            if (tableFileConfiguration == null) {
                continue;
            }
            tableConfigurations = tableFileConfiguration.getTableDbConfig();
            if (tableConfigurations == null) {
                continue;
            }
            for (TableConfiguration tempTableConfiguration: tableConfigurations) {
                if (tempTableConfiguration == null) {
                    continue;
                }
                tempTableConfigId = tempTableConfiguration.getTableConfigId();
                if (tempTableConfigId == null) {
                    continue;
                }
                if (tempTableConfigId.equals(tableConfigId)) {
                    resultTableConfiguration = tempTableConfiguration;
                    break;
                }
            }
            if (resultTableConfiguration != null) {
                break;
            }
        }
        if (resultTableConfiguration == null) {
            logger.info("getTableConfiguration: tableConfigId: {}, not found", tableConfigId);
        }
        return resultTableConfiguration;
    }
    private String findColumnName(int index, ArrayList<String> filterParameter) {
        if (filterParameter == null || index < 0) {
            return null;
        }
        if (filterParameter.size() > index) {
            return filterParameter.get(index);
        }
        return null;
    }
    private HashMap<String, ArrayList<String>> getRequestFilterParameterV2 (TableConfiguration tableConfiguration,
                                                                            ArrayList<String> filterRequest) {
        if (tableConfiguration == null) {
            return null;
        }
        ArrayList<String> filterParameter = tableConfiguration.getFilterParameter();
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        String columnName;
        String filterParam;
        String[] splitResult;
        ArrayList<String> filterByColumnName;
        if (filterRequest != null) {
            for(int i=0; i<filterRequest.size(); i++) {
                filterParam = filterRequest.get(i);
                if (filterParam == null || filterParam.isEmpty()) {
                    continue;
                }
                columnName = this.findColumnName(i, filterParameter);
                if (StaticService.isInValidString(columnName)) {
                    continue;
                }
                splitResult = filterParam.split("\\|");
                filterByColumnName = new ArrayList<>();
                for(String str: splitResult) {
                    if (StaticService.isValidString(str)) {
                        filterByColumnName.add(str.trim());
                    }
                }
                result.put(columnName, filterByColumnName);
            }
        }
        return result;
    }
    private HashMap<String, ArrayList<String>> getRequestFilterParameter(TableConfiguration tableConfiguration,
                                                                         ArrayList<String> filterRequest,
                                                                         String defaultFilterMappingId) {
        if (tableConfiguration == null) {
            return null;
        }
        ArrayList<String> filterParameter = tableConfiguration.getFilterParameter();
        HashMap<String, ArrayList<String>> defaultFilterMapping = tableConfiguration.getDefaultFilterMapping();
        ArrayList<String> defaultFilter = null;
        if (defaultFilterMapping != null) {
            defaultFilter = defaultFilterMapping.get(defaultFilterMappingId);
        }
        HashMap<String, ArrayList<String>> requestData = this.getRequestFilterParameterV2(tableConfiguration, filterRequest);
        HashMap<String, ArrayList<String>> defaultData = this.getRequestFilterParameterV2(tableConfiguration, defaultFilter);
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        ArrayList<String> temp;
        if (filterParameter != null && (requestData != null || defaultData != null)) {
            if (requestData == null) {
                requestData = new HashMap<>();
            }
            if (defaultData == null) {
                defaultData = new HashMap<>();
            }
            for (String filterKey: filterParameter) {
                if (filterKey == null || filterKey.isEmpty()) {
                    continue;
                }
                temp = requestData.get(filterKey);
                if (temp != null && !temp.isEmpty()) {
                    result.put(filterKey, temp);
                    continue;
                }
                temp = defaultData.get(filterKey);
                if (temp != null && !temp.isEmpty()) {
                    result.put(filterKey, temp);
                }
            }
        }
        if (result.isEmpty()) {
            if (!this.isAllowEmptyFilter(tableConfiguration)) {
                logger.info("getRequestFilterParameter: emptyFilter not allowed: {}, {}, {}",
                        tableConfiguration, filterRequest, defaultFilterMappingId);
                throw new AppException(ErrorCodes.CONFIG_ERROR);
            }
        }
        return result;
    }
    public ArrayList<HashMap<String, String>> getTableData(HttpServletRequest request,
                                                           String tableConfigId,
                                                           ArrayList<String> filterRequest,
                                                           String defaultFilterMappingId) throws AppException {
        TableConfiguration tableConfiguration = this.getTableConfiguration(tableConfigId);
        if (tableConfiguration == null) {
            logger.info("getTableData: tableConfiguration is null for tableConfigId: {}", tableConfigId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        HashMap<String, ArrayList<String>> requestFilterParameter = this.getRequestFilterParameter(tableConfiguration, filterRequest, defaultFilterMappingId);
        return tableDb.getByMultipleParameter(tableConfiguration, requestFilterParameter, true);
    }
    public ArrayList<ArrayList<String>> getTableDataArray(HttpServletRequest request,
                                                           String tableConfigId,
                                                           ArrayList<String> filterRequest,
                                                          String defaultFilterMappingId) throws AppException {
        TableConfiguration tableConfiguration = this.getTableConfiguration(tableConfigId);
        if (tableConfiguration == null) {
            logger.info("getTableDataArray: tableConfiguration is null for tableConfigId: {}", tableConfigId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        HashMap<String, ArrayList<String>> requestFilterParameter = this.getRequestFilterParameter(tableConfiguration, filterRequest, defaultFilterMappingId);
        ArrayList<HashMap<String, String>> tableData = tableDb.getByMultipleParameter(tableConfiguration, requestFilterParameter, true);
        ArrayList<String> columnNames = tableConfiguration.getColumnName();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> arrayRowData;
        if (tableData != null) {
            for (HashMap<String, String> rowData: tableData) {
                if (rowData == null || rowData.isEmpty()) {
                    continue;
                }
                arrayRowData = new ArrayList<>();
                for(String name: columnNames) {
                    arrayRowData.add(rowData.get(name));
                }
                result.add(arrayRowData);
            }
        }
        return result;
    }
    private void saveHistory(String tableName, String uniqueColumn, String uniqueParameter,
                             String columnName, String oldValue, String newValue) {
        if (tableName == null || tableName.isEmpty()) {
            return;
        }
        HashMap<String, String> rowData = new HashMap<>();
        HistoryBookTable historyBookTable = new HistoryBookTable();
        ArrayList<String> updateColumn = historyBookTable.getUpdateColumnName();
        TableConfiguration tableConfiguration = new TableConfiguration();
        tableConfiguration.setTableName(historyBookTable.getTableName());
        tableConfiguration.setUpdateColumnName(updateColumn);
        rowData.put(updateColumn.get(0), tableName);
        rowData.put(updateColumn.get(1), uniqueColumn);
        rowData.put(updateColumn.get(2), uniqueParameter);
        rowData.put(updateColumn.get(3), columnName);
        rowData.put(updateColumn.get(4), oldValue);
        rowData.put(updateColumn.get(5), newValue);
        tableDb.addTableEntry(tableConfiguration, rowData);
    }
    private void maintainHistory(TableConfiguration tableConfiguration,
                                 HashMap<String, String> dbRowData,
                                 HashMap<String, String> currentRowData,
                                 ArrayList<String> maintainHistoryExcludedColumn) {
        if (tableConfiguration == null || dbRowData == null || currentRowData == null) {
            return;
        }
        ArrayList<String> compareBeforeUpdateColumn = tableConfiguration.getCompareBeforeUpdateColumn();
        ArrayList<ArrayList<String>> changeHistory = new ArrayList<>();
        if (compareBeforeUpdateColumn == null) {
            return;
        }
        if (maintainHistoryExcludedColumn == null) {
            maintainHistoryExcludedColumn = new ArrayList<>();
        }
        ArrayList<String> changeData;
        String oldData, newData;
        for (String columnName: compareBeforeUpdateColumn) {
            if (columnName == null || columnName.isEmpty()) {
                continue;
            }
            oldData = dbRowData.get(columnName);
            newData = currentRowData.get(columnName);
            if (Objects.equals(oldData, newData)) {
                continue;
            }
            if (oldData == null) {
                oldData = "";
            }
            if (newData == null) {
                newData = "";
            }
            changeData = new ArrayList<>();
            changeData.add(columnName);
            changeData.add(oldData);
            changeData.add(newData);
            changeHistory.add(changeData);
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        String uniqueColumn = "";
        StringBuilder uniqueParameter = new StringBuilder();
        StringBuilder columnName = new StringBuilder();
        StringBuilder oldValue = new StringBuilder();
        StringBuilder newValue = new StringBuilder();
        String uniquePatternData;
        int i=0;
        if (uniquePattern != null) {
            uniqueColumn = String.join(",", uniquePattern);
            for(String name: uniquePattern) {
                if (name == null || name.isEmpty()) {
                    continue;
                }
                uniquePatternData = dbRowData.get(name);
                if (uniquePatternData == null) {
                    uniquePatternData = "";
                }
                if (i==0) {
                    uniqueParameter = new StringBuilder(uniquePatternData);
                } else {
                    uniqueParameter.append(",").append(uniquePatternData);
                }
                i++;
            }
            i = 0;
            for(ArrayList<String> changeData2: changeHistory) {
                if (changeData2 == null || changeData2.size() != 3) {
                    continue;
                }
                if (i==0) {
                    columnName = new StringBuilder(changeData2.get(0));
                    oldValue = new StringBuilder(changeData2.get(1));
                    newValue = new StringBuilder(changeData2.get(2));
                    i++;
                } else {
                    columnName.append(",").append(changeData2.get(0));
                    oldValue.append(",").append(changeData2.get(1));
                    newValue.append(",").append(changeData2.get(2));
                }
            }
        }
        String finalColumnName = columnName.toString();
        if (maintainHistoryExcludedColumn.contains(finalColumnName)) {
            return;
        }
        this.saveHistory(tableConfiguration.getTableName(), uniqueColumn, uniqueParameter.toString(),
                finalColumnName, oldValue.toString(), newValue.toString());
        logger.info("Change History: {}", changeHistory);
    }
    private boolean isValidCurrentRowData(TableConfiguration tableConfiguration, HashMap<String, String> currentRowData) {
        if (tableConfiguration == null || currentRowData == null) {
            return false;
        }
        ArrayList<String> uniquePattern = tableConfiguration.getUniquePattern();
        if (uniquePattern == null || uniquePattern.isEmpty()) {
            return false;
        }
        String value;
        for(String columnName: uniquePattern) {
            if (columnName == null || columnName.isEmpty()) {
                return false;
            }
            value = currentRowData.get(columnName);
            if (value == null || value.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    private TableUpdateEnum getNextAction(TableConfiguration tableConfiguration, HashMap<String, String> currentRowData,
                                 boolean updateIfFound, boolean maintainHistory, ArrayList<String> maintainHistoryExcludedColumn) {
        if (currentRowData == null) {
            return TableUpdateEnum.NULL;
        }
        if (!this.isValidCurrentRowData(tableConfiguration, currentRowData)) {
            return TableUpdateEnum.NULL;
        }
        ArrayList<HashMap<String, String>> searchedData = tableDb.searchData(tableConfiguration, currentRowData);
        if (searchedData == null || searchedData.isEmpty()) {
            return TableUpdateEnum.ADD;
        }
        if (searchedData.size() > 1) {
            return TableUpdateEnum.SKIP;
        }
        if (!updateIfFound) {
            return TableUpdateEnum.SKIP_WITHOUT_LOG;
        }
        // if found unique row entry with current row data filter
        ArrayList<String> compareBeforeUpdateColumn = tableConfiguration.getCompareBeforeUpdateColumn();
        if (compareBeforeUpdateColumn == null) {
            return TableUpdateEnum.UPDATE;
        }
        HashMap<String, String> dbRowData = searchedData.get(0);
        if (dbRowData == null) {
            return TableUpdateEnum.UPDATE;
        }
        for (String columnName: compareBeforeUpdateColumn) {
            if (columnName == null || columnName.isEmpty()) {
                continue;
            }
            if (Objects.equals(dbRowData.get(columnName), currentRowData.get(columnName))) {
                continue;
            }
            if (maintainHistory) {
                this.maintainHistory(tableConfiguration, dbRowData, currentRowData, maintainHistoryExcludedColumn);
            }
            return TableUpdateEnum.UPDATE;
        }
        return TableUpdateEnum.SKIP_IGNORE;
    }
    public void updateTableDataFromCsv(HttpServletRequest request,
                                       String tableConfigId) throws AppException {
        TableConfiguration tableConfiguration = this.getTableConfiguration(tableConfigId);
        MiscService miscService = new MiscService();
        if (tableConfiguration == null) {
            logger.info("addOrUpdateTableData: tableConfiguration is null for tableConfigId: {}", tableConfigId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String excelConfigId = tableConfiguration.getExcelConfigId();
        ArrayList<ArrayList<String>> csvDataArray =msExcelService.getMSExcelSheetDataArray(request, excelConfigId);
        ArrayList<HashMap<String, String>> csvDataJson = miscService.convertArraySheetDataToJsonData(csvDataArray, tableConfiguration.getUpdateColumnName());
        int index = 1;
        int size = 0;
        int entryCount;
        int updateEntryCount = 0;
        int addEntryCount = 0;
        int skipEntryCount = 0;
        TableUpdateEnum nextAction;
        boolean updateIfFound = this.isUpdateIfFoundEnabled(tableConfiguration);
        MaintainHistory maintainHistory = tableConfiguration.isMaintainHistory();
        boolean maintainHistoryRequired = false;
        ArrayList<String> maintainHistoryExcludedColumn = null;
        if (maintainHistory != null) {
            maintainHistoryRequired = maintainHistory.isRequired();
            maintainHistoryExcludedColumn = maintainHistory.getExcludeColumnName();
        }
        if (csvDataJson != null) {
            size = csvDataJson.size();
            for(HashMap<String, String> rowData: csvDataJson) {
                nextAction = this.getNextAction(tableConfiguration, rowData, updateIfFound,
                        maintainHistoryRequired, maintainHistoryExcludedColumn);
                if (nextAction != null) {
                    switch (nextAction) {
                        case UPDATE:
                            entryCount = 1;
                            tableDb.updateEntry(tableConfiguration, rowData, entryCount);
                            updateEntryCount++;
                            logger.info("{}/{}: update completed. summary: {},{},{}: Addition, Update, Skip",
                                    index, size, addEntryCount, updateEntryCount, skipEntryCount);
                            break;
                        case ADD:
                            entryCount = 0;
                            tableDb.addEntry(tableConfiguration, rowData, entryCount);
                            addEntryCount++;
                            logger.info("{}/{}: addition completed. summary: {},{},{}: Addition, Update, Skip",
                                    index, size, addEntryCount, updateEntryCount, skipEntryCount);
                            break;
                        case SKIP:
                            skipEntryCount++;
                            logger.info("{}/{}: updateTableDataFromCsv: Multi entry exist, add " +
                                            "or update not possible. data: {}, summary: {},{},{}: Addition, Update, Skip",
                                    index, size, rowData, addEntryCount, updateEntryCount, skipEntryCount);
                            break;
                        case SKIP_WITHOUT_LOG:
                            skipEntryCount++;
                            break;
                        case SKIP_IGNORE:
                            skipEntryCount++;
                            logger.info("{}/{}: updateTableDataFromCsv: existing data same as current data, " +
                                            "update not required. summary: {},{},{}: Addition, Update, Skip",
                                    index, size, addEntryCount, updateEntryCount, skipEntryCount);
                            break;
                        case NULL:
                            skipEntryCount++;
                            logger.info("{}/{}, {}: invalid next action. data: {}", index, size, nextAction, rowData);
                            break;
                    }
                } else {
                    skipEntryCount++;
                    logger.info("{}/{}, {}: unhandled next action. data: {}", index, size, nextAction, rowData);
                    break;
                }
                index++;
            }
            logger.info("Final update summary, {}/{}/{}/{}: Addition, Update, Skip, Total",
                    addEntryCount, updateEntryCount, skipEntryCount, size);
        }
    }
}
