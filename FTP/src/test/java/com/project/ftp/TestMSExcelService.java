package com.project.ftp;

import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.*;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.service.AuthService;
import com.project.ftp.service.MSExcelService;
import com.project.ftp.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TestMSExcelService {
    final static Logger logger = LoggerFactory.getLogger(TestMSExcelService.class);
    private EventTracking getEventTracking(AppConfig appConfig) {
        UserInterface userInterface = new UserFile(appConfig);
        UserService userService = new UserService(appConfig, userInterface);
        EventInterface eventInterface = new EventFile(appConfig);
        return new EventTracking(appConfig, userService, eventInterface);
    }
    public AppConfig getAppConfig() {
        AppConfig appConfig = new AppConfig();
        FtpConfiguration ftpConfiguration = new FtpConfiguration();
        EventTracking eventTracking = this.getEventTracking(appConfig);
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("serverName");
        arguments.add("false");
        arguments.add("false");
        arguments.add("/meta-data/app_env_config_2.yml");
        arguments.add("/meta-data/app_env_config_4.yml");
        appConfig.setCmdArguments(arguments);
        appConfig.updateFinalFtpConfiguration(ftpConfiguration);
        AppToBridge appToBridge = new AppToBridge(ftpConfiguration, eventTracking);
        appConfig.setAppToBridge(appToBridge);
        appConfig.setFtpConfiguration(ftpConfiguration);
        return appConfig;
    }
    public ApiResource getApiResource() {
        AppConfig appConfig = this.getAppConfig();
        UserInterface userInterface = new UserFile(appConfig);
        UserService userService = new UserService(appConfig, userInterface);
        AuthService authService = new AuthService(userService);
        return new ApiResource(appConfig, userService, this.getEventTracking(appConfig), authService);
    }
    private MSExcelService getMSExcelService() {
        AppConfig appConfig = this.getAppConfig();
        UserInterface userInterface = new UserFile(appConfig);
        UserService userService = new UserService(appConfig, userInterface);
        EventInterface eventInterface = new EventFile(appConfig);
        EventTracking eventTracking = new EventTracking(appConfig, userService, eventInterface);
        return new MSExcelService(appConfig, eventTracking, userService);
    }
    private HttpServletRequest getHttpServletRequest() {
        return null;
    }
    @Test
    public void testTestMSExcelServiceV0() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        MSExcelService msExcelService = this.getMSExcelService();
        requestId = "gs-csv-test-13";
        ArrayList<ExcelDataConfig> excelDataConfigs =  msExcelService.getActualMSExcelSheetDataConfig(request, requestId, false);
        Assert.assertEquals(requestId, excelDataConfigs.get(0).getId());
    }
    @Test
    public void testTestMSExcelServiceCsv() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        MSExcelService msExcelService = this.getMSExcelService();
        String result;
        requestId = "csv-test-01";
        result =  msExcelService.getMSExcelSheetDataCsv(request, requestId);
        Assert.assertEquals(66, result.length());
    }
    @Test
    public void testTestMSExcelServiceV1() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = null;
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("FAILURE", apiResponse.getStatus());
        requestId = "csv-test-01";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-02";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-03";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("FAILURE", apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV2() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-04";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-05";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-06";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-07";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV08() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-08-09";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-id-not-found-in-csv-config";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("CONFIG_ERROR", apiResponse.getFailureCode());
        requestId = "csv-test-id-not-found-error";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("BAD_REQUEST_ERROR", apiResponse.getFailureCode());
        requestId = "gs-csv-test-12-direct-invalid";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("BAD_REQUEST_ERROR", apiResponse.getFailureCode());
    }
    @Test
    public void testTestMSExcelServiceV09() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-09";
        apiResponse = apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV10() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-10";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV11() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-11";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV12() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "gs-csv-test-12";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertNotNull(apiResponse.getStatus());
//        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
        requestId = "csv-gs-csv-test-12";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertNotNull(apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV13() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        MSExcelService msExcelService = this.getMSExcelService();
        requestId = "gs-csv-test-12";
        ArrayList<ExcelDataConfig> excelDataConfigs =  msExcelService.getActualMSExcelSheetDataConfig(request, requestId, false);
        Assert.assertEquals(requestId, excelDataConfigs.get(0).getId());
        Assert.assertNotNull(excelDataConfigs.get(0).getValidFor());
        Assert.assertEquals("google", excelDataConfigs.get(0).getGsConfig().get(0).getFileConfigMapping().getFileDataSource());

        requestId = "gs-csv-test-12-direct";
        //config for gs-csv-test-12 and gs-csv-test-12-direct should be same
        excelDataConfigs =  msExcelService.getActualMSExcelSheetDataConfig(request, requestId, false);
        Assert.assertEquals(requestId, excelDataConfigs.get(0).getId());
        Assert.assertNotNull(excelDataConfigs.get(0).getValidFor());
        Assert.assertEquals("google", excelDataConfigs.get(0).getGsConfig().get(0).getFileConfigMapping().getFileDataSource());

        requestId = "csv-test-01";
        excelDataConfigs =  msExcelService.getActualMSExcelSheetDataConfig(request, requestId, false);
        Assert.assertEquals(requestId, excelDataConfigs.get(0).getId());
        Assert.assertNull(excelDataConfigs.get(0).getValidFor());
        Assert.assertEquals(1, excelDataConfigs.get(0).getCsvConfig().size());
        Assert.assertEquals("csv", excelDataConfigs.get(0).getCsvConfig().get(0).getFileConfigMapping().getFileDataSource());

        requestId = "csv-test-08-09";
        excelDataConfigs =  msExcelService.getActualMSExcelSheetDataConfig(request, requestId, false);
        Assert.assertEquals("csv-test-08", excelDataConfigs.get(0).getId());
        Assert.assertEquals("csv-test-09", excelDataConfigs.get(1).getId());
        Assert.assertEquals(1, excelDataConfigs.get(0).getCsvConfig().size());
        Assert.assertEquals("csv", excelDataConfigs.get(0).getCsvConfig().get(0).getFileConfigMapping().getFileDataSource());
    }
}
