package com.project.ftp;

import com.project.ftp.bridge.mysqlTable.TableService;
import com.project.ftp.config.AppConfig;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.service.FileServiceV3;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class TestProdData {
    private final TestMSExcelService testMSExcelService = new TestMSExcelService();
    /*
    @Test
    public void testTestMSExcelService8082() {
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        String requestId;
        AppConfig appConfig = testMSExcelService.getAppConfigProd8082(false);
        ApiResource apiResource = new ApiResource(appConfig);
        ApiResponse apiResponse;
        requestId = "smms-assets-list-rnc";
        apiResponse =  apiResource.getMSExcelDataJson(request, requestId);
        ArrayList<HashMap<String, String>> result = (ArrayList<HashMap<String, String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
    }
    @Test
    public void testProdMySqlUserTableDb() {
        AppConfig appConfig = testMSExcelService.getAppConfigProd(true);
        TableService tableService = appConfig.getTableService();
        tableService.updateTableDataFromCsv(null, "csv-mysql-update-staff-details");
    }
    @Test
    public void testGetOracleTableData() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        TableService tableService = appConfig.getTableService();
        ArrayList<HashMap<String, String>> result;
//        result = tableService.getTableData(null, "get-mysql-smms-assets-list_oracle", null, null);
//        Assert.assertNotNull(result);
//
//        result = tableService.getTableData(null, "get_smms_assets_list_view_oracle", null, "all");
//        Assert.assertNotNull(result);
//
//        result = tableService.getTableData(null, "get-oracle-smms-assets-list", null, "rnc_division");
//        Assert.assertNotNull(result);
//
//        result = tableService.getTableData(null, "get_smms_assets_list_duplicate", null, "all");
//        Assert.assertNotNull(result);

    }
    @Test
    public void testUpdateMySqlTableDb() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        TableService tableService = appConfig.getTableService();
        tableService.updateTableDataFromCsv(null, "csv-mysql-update-smms_assets_list_oracle");
    }
    @Test
    public void testMyTableDbv2() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        TableService tableService = appConfig.getTableService();
        ArrayList<HashMap<String, String>> result;
//        result = tableService.getTableData(null, "get-oracle-smms-assets-list", null, "rnc_division");
//        Assert.assertEquals(3, result.size());
//        result = tableService.getTableData(null, "get-oracle-smms-assets-list", null, "asset_unique");
//        Assert.assertEquals(2, result.size());
//        Assert.assertEquals("Modified asset code", result.get(0).get("modified_asset_code"));
    }
    */
}
