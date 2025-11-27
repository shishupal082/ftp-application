package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.yamlObj.NdTo1dConfig;
import com.project.ftp.service.MSExcelService;
import com.project.ftp.service.NdTo1dService;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TestNdTo1d {
    @Test
    public void testNdTo1DConfig() {
        AppConfig appConfig = TestAppConfig.getAppConfigV2_1();
        NdTo1dService ndTo1dService = new NdTo1dService(appConfig);
        NdTo1dConfig ndTo1dConfig;
        try {
            ndTo1dService.getNdTo1dConfigV1(null, null, null);
            Assert.assertEquals(1, 0);
        } catch (AppException ae) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, ae.getErrorCode());
        }
        ndTo1dConfig = ndTo1dService.getNdTo1dConfigV1(null, "invalidRequestId", null);
        Assert.assertNull(ndTo1dConfig);
        try {
            appConfig.getFtpConfiguration().setDirConfigParam(null);
            ndTo1dService.getNdTo1dConfigV1(null, "invalidRequestId", null);
            Assert.assertEquals(1, 0);
        } catch (AppException ae) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, ae.getErrorCode());
        }
    }
    @Test
    public void testNdTo1dConfigData() {
        AppConfig appConfig = TestAppConfig.getAppConfigV2_1();
        NdTo1dService ndTo1dService = new NdTo1dService(appConfig);
        String requestId = "invalidRequestId";
        String roleId = "";
        NdTo1dConfig ndTo1dConfig = ndTo1dService.getNdTo1dConfigV1(null, requestId, roleId);
        Assert.assertNull(ndTo1dConfig);
        requestId = "id_2dTo1d";
        ndTo1dConfig = ndTo1dService.getNdTo1dConfigV1(null, requestId, roleId);
        Assert.assertEquals(2, Integer.parseInt(ndTo1dConfig.getDataDimension().toString()));
    }
    @Test
    public void testNdTo1dV1() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        AppConfig appConfig = TestAppConfig.getAppConfigV2_1();
        NdTo1dService ndTo1dService = new NdTo1dService(appConfig);
        String requestId = "id_2dTo1d";
        String roleId = "defaultRole";
        ArrayList<ArrayList<String>> excelData = ndTo1dService.getNdTo1dData(request, requestId, roleId);
        Assert.assertEquals(9,excelData.size());
        Assert.assertEquals(5,excelData.get(0).size());
        Assert.assertEquals("Population",excelData.get(0).get(1));
        Assert.assertEquals("Bihar",excelData.get(0).get(3));
        Assert.assertEquals("20",excelData.get(0).get(4));
        Assert.assertEquals("Population",excelData.get(1).get(1));
        Assert.assertEquals("Jharkhand",excelData.get(1).get(3));
        Assert.assertEquals("18",excelData.get(1).get(4));
        Assert.assertEquals("Area",excelData.get(3).get(1));
        Assert.assertEquals("Bihar",excelData.get(3).get(3));
        Assert.assertEquals("80",excelData.get(3).get(4));

        requestId = "id_3dTo1d";
        excelData = ndTo1dService.getNdTo1dData(request, requestId, roleId);
        Assert.assertEquals(12,excelData.size());
        Assert.assertEquals(6,excelData.get(0).size());
        Assert.assertEquals("UP",excelData.get(11).get(3));
        Assert.assertEquals("Winter",excelData.get(11).get(4));
        Assert.assertEquals("Rain",excelData.get(11).get(5));
    }
    @Test
    public void testNdTo1dV2() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        AppConfig appConfig = TestAppConfig.getAppConfigV2_1();
        NdTo1dService ndTo1dService = new NdTo1dService(appConfig);
        String requestId = "id_1d_1To1d";
        String roleId = "defaultRole";
        //Configuration error due to dimension < 1 (0)
        try {
            ndTo1dService.getNdTo1dData(request, requestId, roleId);
            Assert.assertEquals(1, 0);
        } catch (AppException ae) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, ae.getErrorCode());
        }
        //Data null due to start index < 0 (-1)
        requestId = "id_1d_2To1d";
        ArrayList<ArrayList<String>> data = ndTo1dService.getNdTo1dData(request, requestId, roleId);
        Assert.assertNull(data);
        //Configuration error due to sourceExcelId is invalid
        requestId = "id_1d_3To1d";
        try {
            ndTo1dService.getNdTo1dData(request, requestId, roleId);
            Assert.assertEquals(1, 0);
        } catch (AppException ae) {
            Assert.assertEquals(ErrorCodes.CONFIG_ERROR, ae.getErrorCode());
        }
        //Data null to sourceExcelId is "invalid"
        requestId = "id_1d_4To1d";
        data = ndTo1dService.getNdTo1dData(request, requestId, roleId);
        Assert.assertNull(data);
        //Data null to textColIndex and dataColIndex is null
        requestId = "id_1d_5To1d";
        data = ndTo1dService.getNdTo1dData(request, requestId, roleId);
        Assert.assertNull(data);
        //Test pass
        requestId = "id_1dTo1d";
        data = ndTo1dService.getNdTo1dData(request, requestId, roleId);
        Assert.assertEquals(4, data.size());
    }
    @Test
    public void testNdTo1dV1Update() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        MSExcelService msExcelService = testMSExcelService.getMSExcelService();
        String requestId = "2dTo1dFinal";
        ApiResponse apiResponse = msExcelService.updateMSExcelSheetData(request, requestId, null);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
        requestId = "3dTo1dFinal";
        apiResponse = msExcelService.updateMSExcelSheetData(request, requestId, null);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
        requestId = "2d_1To1dFinal";
        apiResponse = msExcelService.updateMSExcelSheetData(request, requestId, null);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
        requestId = "1dTo1dFinal";
        apiResponse = msExcelService.updateMSExcelSheetData(request, requestId, null);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
}
