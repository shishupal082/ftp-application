package com.project.ftp;

import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.EventFile;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.intreface.UserFile;
import com.project.ftp.intreface.UserInterface;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.service.MSExcelService;
import com.project.ftp.service.UserService;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class TestMSExcelScanDirService {
    @Test
    public void testMSExcelScanDirV1Config() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        String requestId;
        MSExcelService msExcelService = testMSExcelService.getMSExcelService();
        requestId = "csv-scan-dir-md5-test-data";
        ArrayList<ExcelDataConfig> excelDataConfigs =  msExcelService.getActualMSExcelSheetDataConfig(request, requestId, false);
        Assert.assertEquals(requestId, excelDataConfigs.get(0).getId());
        Assert.assertEquals("test-data-md5-dir", excelDataConfigs.get(0).getApiConfig().get(0).getSource());
        Assert.assertEquals("", excelDataConfigs.get(0).getApiConfig().get(0).getSheetName());
    }
    @Test
    public void testMSExcelScanDirV1Data() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        String requestId;
        MSExcelService msExcelService = testMSExcelService.getMSExcelService();
        requestId = "csv-scan-dir-md5-test-data";
        ArrayList<ArrayList<String>> sheetData =  msExcelService.getMSExcelSheetDataArray(request, requestId);
        Assert.assertEquals(10, sheetData.size());
        Assert.assertEquals("1.bmp", sheetData.get(1).get(2));
        Assert.assertEquals("258a5aab652db23fa8de7c2649ff9220", sheetData.get(1).get(5));
    }
    @Test
    public void testMSExcelScanDirV1Update() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        ApiResource apiResource = testMSExcelService.getApiResource();
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        String requestId;
        requestId = "csv-scan-dir-md5-test-data";
        ApiResponse apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
}
