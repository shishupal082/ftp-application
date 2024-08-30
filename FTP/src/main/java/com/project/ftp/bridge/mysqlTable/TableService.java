package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
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
            if (!tableConfiguration.isAllowEmptyFilter()) {
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
                if (rowData == null && rowData.isEmpty()) {
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
        Boolean updateIfFound = tableConfiguration.getUpdateIfFound();
        if (updateIfFound == null) {
            updateIfFound = true;
        }
        if (csvDataJson != null) {
            size = csvDataJson.size();
            for(HashMap<String, String> rowData: csvDataJson) {
                entryCount = tableDb.getEntryCount(tableConfiguration, rowData);
                if (entryCount == 1) {
                    if (updateIfFound) {
                        tableDb.updateEntry(tableConfiguration, rowData, entryCount);
                        updateEntryCount++;
                        logger.info("{}/{}: update completed. summary: {},{},{}: Addition, Update, Skip",
                                index, size, addEntryCount, updateEntryCount, skipEntryCount);
                    } else {
                        skipEntryCount++;
                    }
                } else if (entryCount == 0) {
                    tableDb.addEntry(tableConfiguration, rowData, entryCount);
                    addEntryCount++;
                    logger.info("{}/{}: addition completed. summary: {},{},{}: Addition, Update, Skip",
                            index, size, addEntryCount, updateEntryCount, skipEntryCount);
                } else {
                    skipEntryCount++;
                    logger.info("{}/{}: updateTableDataFromCsv: Multi entry exist, add " +
                            "or update not possible. data: {}, summary: {},{},{}: Addition, Update, Skip",
                            index, size, rowData, addEntryCount, updateEntryCount, skipEntryCount);
                }
                index++;
            }
            logger.info("Final update summary, {}/{}/{}/{}: Addition, Update, Skip, Total",
                    addEntryCount, updateEntryCount, skipEntryCount, size);
        }
    }
}
