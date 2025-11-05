package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.FileServiceV2;
import org.junit.Assert;
import org.junit.Test;

public class TestFileService {
    @Test
    public void testFileServiceV2PathInfo() {
        TestAppConfig testAppConfig = new TestAppConfig();
        AppConfig appConfig = testAppConfig.getAppConfigV2();
        FileServiceV2 fileServiceV2 = new FileServiceV2(appConfig, appConfig.getUserService());
        LoginUserDetails userDetails = appConfig.getUserService().getLoginUserDetails(null);
        //1
        PathInfo pathInfo = fileServiceV2.getFileResponse("", userDetails, "prodRole");
        Assert.assertNull(pathInfo);
        //2
        pathInfo = fileServiceV2.getFileResponse(null, userDetails, "prodRole");
        Assert.assertNull(pathInfo);
        //valid publicDir and invalid file name
        pathInfo = fileServiceV2.getFileResponse("invalid-filename", userDetails, "prodRole");
        Assert.assertNotNull(pathInfo);
        Assert.assertNull(pathInfo.getFileName());
        //Invalid publicDir
        pathInfo = fileServiceV2.getFileResponse("invalid-filename", userDetails, "testRoleV1");
        Assert.assertNotNull(pathInfo);
        //4
        pathInfo = fileServiceV2.getFileResponse("/dipesh.txt", userDetails, "prodRole");
        Assert.assertEquals("dipesh.txt", pathInfo.getFileName());
        //5
        pathInfo = fileServiceV2.getFileResponse("/account/json/appControlDataTeam03.json", userDetails, "prodRoleV2");
        Assert.assertEquals("appControlDataTeam03.json", pathInfo.getFileName());
    }
}
