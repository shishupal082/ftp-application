package com.project.ftp;

import com.project.ftp.bridge.mysqlTable.TableService;
import com.project.ftp.config.AppConfig;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class TestMysqlTable {
    final static Logger logger = LoggerFactory.getLogger(TestMysqlTable.class);
    @Test
    public void testMyTableTableService() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        TableService tableService = new TableService(null, null, null);
        ArrayList<HashMap<String, String>> result;
        try {
            tableService.getTableData(null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, e.getErrorCode());
        }
        appConfig.getFtpConfiguration().setTableDbConfigFilePath(null);
        tableService = appConfig.getTableService();
        try {
            tableService.getTableData(null, "get-users", null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, e.getErrorCode());
        }
        try {
            tableService.getTableData(null, "invalid-table-config-id", null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, e.getErrorCode());
        }
    }
    @Test
    public void testMyTableDb() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        TableService tableService = appConfig.getTableService();
        ArrayList<HashMap<String, String>> result;
        try {
            tableService.getTableData(null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            tableService.getTableData(null, "", null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            tableService.getTableData(null, "invalid-table-config-id", null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        result = tableService.getTableData(null, "get-users", null);
        Assert.assertNotNull(result);
        result = tableService.getTableData(null, "get-event_data", null);
        Assert.assertNotNull(result);
        ArrayList<String> filterRequest = new ArrayList<>();
        filterRequest.add("mysql_table_data");
        result = tableService.getTableData(null, "get-event_data", filterRequest);
        Assert.assertNotNull(result);

        filterRequest = new ArrayList<>();
        filterRequest.add("invalid_mysql_table_data_name");
        result = tableService.getTableData(null, "get-event_data", filterRequest);
        Assert.assertEquals(0, result.size());

        filterRequest = new ArrayList<>();
        filterRequest.add("mysql_table_data|application_start");
        result = tableService.getTableData(null, "get-event_data", filterRequest);
        Assert.assertEquals(100, result.size());

        filterRequest = new ArrayList<>();
        filterRequest.add("mysql_table_data|application_start");
        filterRequest.add("2nd filter parameter"); // 2nd filter will be ignored as in the config only one filter available
        result = tableService.getTableData(null, "get-event_data", filterRequest);
        Assert.assertEquals(100, result.size());
    }

    @Test
    public void testUpdateMySqlTableDb() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        TableService tableService = appConfig.getTableService();
        tableService.updateTableDataFromCsv(null, "csv-mysql-update-smms_assets_list");
        try {
        } catch (AppException appException) {
        }
    }
}
