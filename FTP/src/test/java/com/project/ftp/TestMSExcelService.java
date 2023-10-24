package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.*;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.resources.ApiResource;
import com.project.ftp.service.AuthService;
import com.project.ftp.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TestMSExcelService {
    final static Logger logger = LoggerFactory.getLogger(TestMSExcelService.class);
    private AppConfig getAppConfig() {
        AppConfig appConfig = new AppConfig();
        FtpConfiguration ftpConfiguration = new FtpConfiguration();
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("serverName");
        arguments.add("false");
        arguments.add("/meta-data/app_env_config_2.yml");
        arguments.add("/meta-data/app_env_config_4.yml");
        appConfig.setCmdArguments(arguments);
        appConfig.updateFinalFtpConfiguration(ftpConfiguration);
        AppToBridge appToBridge = new AppToBridge(ftpConfiguration, null);
        appConfig.setAppToBridge(appToBridge);
        appConfig.setFtpConfiguration(ftpConfiguration);
        return appConfig;
    }
    public ApiResource getApiResource() {
        AppConfig appConfig = this.getAppConfig();
        UserInterface userInterface = new UserFile(appConfig);
        EventInterface eventInterface = new EventFile(appConfig);
        UserService userService = new UserService(appConfig, userInterface);
        AuthService authService = new AuthService(userService);
        EventTracking eventTracking = new EventTracking(appConfig, userService, eventInterface);
        return new ApiResource(appConfig, userService, eventTracking, authService);
    }
    private HttpServletRequest getHttpServletRequest() {
        return null;
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
        requestId = "csv-test-08";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-09";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
        requestId = "csv-test-id-not-found-in-csv-config";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("CONFIG_ERROR", apiResponse.getFailureCode());
        requestId = "csv-test-id-not-found-in-env_config-excel-ms_yml";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("BAD_REQUEST_ERROR", apiResponse.getFailureCode());
        requestId = "csv-test-id-not-found-error";
        apiResponse =  apiResource.getMSExcelData(request, requestId);
        Assert.assertEquals("CONFIG_ERROR", apiResponse.getFailureCode());
    }
    @Test
    public void testTestMSExcelServiceV10() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-10";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
    }
    @Test
    public void testTestMSExcelServiceV11() {
        HttpServletRequest request = this.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        ApiResource apiResource = this.getApiResource();
        requestId = "csv-test-11";
        apiResponse =  apiResource.updateMSExcelData(request, requestId);
        Assert.assertEquals("SUCCESS", apiResponse.getStatus());
    }
}
