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
        TableService tableService = new TableService(null, null);
        ArrayList<HashMap<String, String>> result;
        try {
            tableService.getTableData(null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, e.getErrorCode());
        }
        appConfig.getFtpConfiguration().setTableDbConfigFilePath(null);
        tableService = appConfig.getTableService();
        try {
            tableService.getTableData(null, "get-users");
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, e.getErrorCode());
        }
        try {
            tableService.getTableData(null, "invalid-table-config-id");
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
            tableService.getTableData(null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            tableService.getTableData(null, "");
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            tableService.getTableData(null, "invalid-table-config-id");
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        result = tableService.getTableData(null, "get-users");
        Assert.assertNotNull(result);
        result = tableService.getTableData(null, "get-event_data");
        Assert.assertNotNull(result);
    }
}
