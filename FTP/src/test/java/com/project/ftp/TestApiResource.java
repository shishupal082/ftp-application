package com.project.ftp;

import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.resources.ApiResource;
import org.junit.Assert;
import org.junit.Test;

public class TestApiResource {
    @Test
    public void testGetPathInfo() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        ApiResource apiResource = testMSExcelService.getApiResource();
        ApiResponse apiResponse;
        String path;
        path = "filepath";
        apiResponse = apiResource.getPathInfo(null, path, null, null);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
}
