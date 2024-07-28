package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.FilepathDBParameters;
import com.project.ftp.obj.yamlObj.ScanDirMapping;
import com.project.ftp.service.ScanDirService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestScanDir {
    final static Logger logger = LoggerFactory.getLogger(TestScanDir.class);
    @Test
    public void testScanDirConfig() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = appConfig.getScanDirService();
        ApiResponse apiResponse;
        ArrayList<ScanDirMapping> scanDirMapping;
        try {
            scanDirService.getScanDirectoryConfig(null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.getScanDirectoryConfig(null, "", null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.getScanDirectoryConfig(null, "invalid-scan-dir-id", null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        apiResponse = scanDirService.getScanDirectoryConfig(null, "test-with-null-path-index", null);
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertEquals(1, scanDirMapping.size());
        Assert.assertEquals(scanDirMapping.get(0).getId(), "test-with-null-path-index");
        Assert.assertNull(scanDirMapping.get(0).getPathIndex());

        scanDirService.getScanDirectoryConfig(null, "test-with-null-path-index", "invalid-path");
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertEquals(1, scanDirMapping.size());
        Assert.assertEquals(scanDirMapping.get(0).getId(), "test-with-null-path-index");
        Assert.assertNull(scanDirMapping.get(0).getPathIndex());

        apiResponse = scanDirService.getScanDirectoryConfig(null, "test-1", null);
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertEquals(1, scanDirMapping.size());
        Assert.assertEquals("test-1", scanDirMapping.get(0).getId());
        Assert.assertEquals(2, scanDirMapping.get(0).getPathIndex().size());
    }
    @Test
    public void testReadScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = appConfig.getScanDirService();
        ApiResponse apiResponse;
        String scanDirId, path;
        try {
            scanDirService.readScanDirectory(null, null,null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "",null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "invalid-path",null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, null, "E:/invalid-file-or-folder/", null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        scanDirId = "d-workspace-ftp-application-ftp";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, null);
        ArrayList<FilepathDBParameters> pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 20);
    }
    @Test
    public void testReadScanDir2() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String scanDirId = "workspace-ftp-config-files";
        String path;
        String recursive = null;
        String csvData;
        ApiResponse apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        ArrayList<FilepathDBParameters> pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        scanDirId = "workspace-ftp-config-files/";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "invalid-boolean";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 37);
        scanDirId = "workspace-ftp-empty-folder";
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        scanDirId = "workspace-ftp-empty-folder/";
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);

        scanDirId = "workspace-ftp-single-file-folder";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, null);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(2, pathInfoScanResults.size());

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(5, pathInfoScanResults.size());
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(4, pathInfoScanResults.size());

        scanDirId = "scan-dir-test-folder";
        recursive = "false";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(1, pathInfoScanResults.size());
        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, AppConstant.FALSE);
        Assert.assertEquals(20, csvData.split(",").length);

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(1, pathInfoScanResults.size());
        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, AppConstant.FALSE);
        Assert.assertEquals(20, csvData.split(",").length);

        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(11, csvData.split(",").length);
        Assert.assertEquals("txt", csvData.split(",")[10]);
    }
    @Test
    public void testReadScanDir3() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = new ScanDirService(appConfig, null);
        String scanDirId = "app-env-config-file";
        String recursive = "false";
        ApiResponse apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        ArrayList<FilepathDBParameters> pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 3);
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive);
        pathInfoScanResults = (ArrayList<FilepathDBParameters>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 3);
    }
    @Test
    public void testUpdateScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String path = "meta-data-dir";
        ApiResponse apiResponse = scanDirService.updateScanDirectory(null, path, AppConstant.TRUE);
        Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);
    }
    @Test
    public void testGetScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String scanDirId;
        ApiResponse apiResponse;
        scanDirId = "meta-data-dir";
        apiResponse = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE);
        ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);
        scanDirId = "invalid-id";
        apiResponse = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(result.size(), 0);
        scanDirId = null;// "e-app-data-v2.1-saved-files-shishupal2";
        String path = "E:/app-data-v2.1/project-tracking/saved-files/Shishupal2/2024-04-02-10-20-BD's ltr 01.04.24-Plan for Reliablitiy Improvement and Maintenance Effectiveness (PRIME) 2024-25..pdf";
        apiResponse = scanDirService.getScanDirectory(null, null, path, null, AppConstant.TRUE);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertTrue(!result.isEmpty());
        String resultStr = scanDirService.getScanDirectoryCsv(null, null, path, null, AppConstant.TRUE);
        Assert.assertTrue(!resultStr.isEmpty());
        apiResponse = scanDirService.getScanDirectory(null, null, null,"pdf", AppConstant.TRUE);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertTrue(!result.isEmpty());
    }
}
