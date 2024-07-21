package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.FilepathDBParameters;
import com.project.ftp.service.ScanDirService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestScanDir {
    final static Logger logger = LoggerFactory.getLogger(TestScanDir.class);
    @Test
    public void testReadScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = appConfig.getScanDirService();
        ApiResponse apiResponse;
        String path;
        try {
            scanDirService.readScanDirectory(null, null,null);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "",null);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "invalid-path",null);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "E:/invalid-file-or-folder/",null);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        path = "D:/workspace/ftp-application/FTP/";
        apiResponse = scanDirService.readScanDirectory(null, path, null);
        ArrayList<FilepathDBParameters> pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 20);
    }
    @Test
    public void testReadScanDir2() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String path = "D:/workspace/ftp-application/FTP/meta-data/config-files/";
        String recursive = null;
        ApiResponse apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        ArrayList<FilepathDBParameters> pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        path = "D:/workspace/ftp-application/FTP/meta-data/config-files";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "invalid-boolean";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 36);
    }
    @Test
    public void testReadScanDir3() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = new ScanDirService(appConfig, null);
        String path = "D:/workspace/ftp-application/FTP/meta-data/app_env_config.yml";
        String recursive = "false";
        ApiResponse apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        ArrayList<FilepathDBParameters> pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        path = "D:/workspace/ftp-application/FTP/meta-data/empty-folder/";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        path = "D:/workspace/ftp-application/FTP/meta-data/empty-folder";
        apiResponse = scanDirService.readScanDirectory(null, path, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
    }
    @Test
    public void testUpdateScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String path = "D:/workspace/ftp-application/FTP/meta-data/";
        ApiResponse apiResponse = scanDirService.updateScanDirectory(null, path, AppConstant.TRUE);
        Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);
    }
    @Test
    public void testGetScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String path;
        ApiResponse apiResponse;
        path = "D:/workspace/ftp-application/FTP/meta-data/";
        try {
            apiResponse = scanDirService.getScanDirectory(null, path, null, null, AppConstant.TRUE);
            Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);
        } catch (AppException e) {
            // For invalid path
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        path = "E:/app-data-v2.1/project-tracking/saved-files/Shishupal2/2024-04-02-10-20-BD's ltr 01.04.24-Plan for Reliablitiy Improvement and Maintenance Effectiveness (PRIME) 2024-25..pdf";
        apiResponse = scanDirService.getScanDirectory(null, path, null, null, AppConstant.TRUE);
        Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);
        String result = scanDirService.getScanDirectoryCsv(null, null, null, null, AppConstant.TRUE);
        Assert.assertNotNull(result);
        apiResponse = scanDirService.getScanDirectory(null, null, "pdf", null, AppConstant.TRUE);
        Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);

    }
}
