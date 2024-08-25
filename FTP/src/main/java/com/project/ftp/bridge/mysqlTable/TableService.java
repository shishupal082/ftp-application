package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.obj.yamlObj.TableFileConfiguration;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class TableService {
    final static Logger logger = LoggerFactory.getLogger(TableService.class);
    private final FtpConfiguration ftpConfiguration;
    private final TableDb tableDb;
    public TableService(final FtpConfiguration ftpConfiguration, final TableDb tableDb) {
        this.ftpConfiguration = ftpConfiguration;
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
    public ArrayList<HashMap<String, String>> getTableData(HttpServletRequest request, String tableConfigId) throws AppException {
        TableConfiguration tableConfiguration = this.getTableConfiguration(tableConfigId);
        if (tableConfiguration == null) {
            logger.info("getTableData: tableConfiguration is null for tableConfigId: {}", tableConfigId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        return tableDb.getAll(tableConfiguration);
    }
}
