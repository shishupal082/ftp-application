package com.project.ftp;

import com.project.ftp.bridge.obj.splitTextFile.SplitTextFileConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.EventFile;
import com.project.ftp.intreface.EventInterface;
import com.project.ftp.intreface.UserFile;
import com.project.ftp.intreface.UserInterface;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.service.SplitTextFileService;
import com.project.ftp.service.UserService;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

public class TestSplitText {
    private final TestMSExcelService testMSExcelService = new TestMSExcelService();
    private SplitTextFileService getSplitTextFileService() {
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        UserInterface userInterface = new UserFile(appConfig);
        UserService userService = new UserService(appConfig, userInterface);
        EventInterface eventInterface = new EventFile(appConfig);
        EventTracking eventTracking = new EventTracking(appConfig, userService, eventInterface);
        return new SplitTextFileService(appConfig, eventTracking, userService);
    }
    @Test
    public void testTestSplitTextConfig() {
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        String requestId;
        SplitTextFileConfig splitTextFileConfig;
        SplitTextFileService splitTextFileService = this.getSplitTextFileService();

        requestId = null;
        splitTextFileConfig =  splitTextFileService.getSplitTextFileConfigV2(request, requestId);
        Assert.assertNull(splitTextFileConfig);

        requestId = "invalid-id";
        splitTextFileConfig =  splitTextFileService.getSplitTextFileConfigV2(request, requestId);
        Assert.assertNull(splitTextFileConfig);

        requestId = "split-csv-file";
        splitTextFileConfig =  splitTextFileService.getSplitTextFileConfigV2(request, requestId);
        Assert.assertEquals(requestId, splitTextFileConfig.getId());
    }
    @Test
    public void testTestSplitTextFile1() {
        HttpServletRequest request = testMSExcelService.getHttpServletRequest();
        String requestId;
        ApiResponse apiResponse;
        SplitTextFileService splitTextFileService = this.getSplitTextFileService();

        requestId = "invalid-id";
        apiResponse =  splitTextFileService.splitTextFile(request, requestId);
        Assert.assertEquals(AppConstant.FAILURE, apiResponse.getStatus());

        requestId = "split-csv-file";
//        requestId = "split-csv-file-v3";
        apiResponse =  splitTextFileService.splitTextFile(request, requestId);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
}
