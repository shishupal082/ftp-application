package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
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
        apiResponse = scanDirService.getScanDirectoryConfig(null, null, null);
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertFalse(scanDirMapping.isEmpty());// It will return all scanDirMapping data
        apiResponse = scanDirService.getScanDirectoryConfig(null, "", null);
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertFalse(scanDirMapping.isEmpty());
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


        apiResponse = scanDirService.getScanDirectoryConfig(null, "test-1|test-2", null);
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertEquals(2, scanDirMapping.size());
        Assert.assertEquals("test-1", scanDirMapping.get(0).getId());
        Assert.assertEquals("test-2", scanDirMapping.get(1).getId());
    }
    @Test
    public void testReadScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = appConfig.getScanDirService();
        ApiResponse apiResponse;
        String scanDirId, path;
        try {
            scanDirService.readScanDirectory(null, null,null, null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "",null, null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, "invalid-path",null, null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.readScanDirectory(null, null, "E:/invalid-file-or-folder/", null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        scanDirId = "d-workspace-ftp-application-ftp";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, null, null);
        ArrayList<ArrayList<String>> pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
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
        ApiResponse apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        ArrayList<ArrayList<String>> pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        scanDirId = "workspace-ftp-config-files/";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "invalid-boolean";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 17);
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 37);
        scanDirId = "workspace-ftp-empty-folder";
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        scanDirId = "workspace-ftp-empty-folder/";
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);

        scanDirId = "workspace-ftp-single-file-folder";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, null, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(2, pathInfoScanResults.size());

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(5, pathInfoScanResults.size());
        recursive = "false";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(4, pathInfoScanResults.size());

        scanDirId = "scan-dir-test-folder";
        recursive = "false";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, pathInfoScanResults.size());
        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, "invalid-csv-mapping-id");
        Assert.assertEquals(26, csvData.split(",").length);

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, pathInfoScanResults.size());

        apiResponse = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        pathInfoScanResults = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, pathInfoScanResults.size());

        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(25, csvData.split(",").length);

        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, "api-scan-dir");
        Assert.assertEquals(11, csvData.split(",").length);
        Assert.assertEquals("txt", csvData.split(",")[10]);


        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, "invalid-csv-mapping-id");
        Assert.assertEquals(26, csvData.split(",").length);
        Assert.assertEquals("txt", csvData.split(",")[20]);
    }
    @Test
    public void testReadScanDir3() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = new ScanDirService(appConfig, null);
        String scanDirId = "app-env-config-file";
        String recursive = "false";
        ApiResponse apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(result.size(), 3);
        recursive = "true";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(result.size(), 3);

        scanDirId = "test-4|test-5";
        recursive = "true-ok";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, "test-4|test-5-csv-mapping-id");
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(result.size(), 3);

        Assert.assertEquals("test-4|test-5-csv-mapping-id", result.get(1).get(25));//reqCsvMappingId
        Assert.assertEquals("true-ok", result.get(1).get(24));//reqRecursive
        Assert.assertNull(result.get(1).get(23));//reqFileType
        Assert.assertNull(result.get(1).get(22));//reqPathName
        Assert.assertEquals("test-4|test-5", result.get(1).get(21));//reqScanDirId

        Assert.assertEquals("txt", result.get(1).get(20));//file extension
        Assert.assertEquals("readme.txt", result.get(1).get(19));//file name
        Assert.assertEquals(AppConstant.TABLE_FILE_PATH, result.get(1).get(4));//table name
        Assert.assertEquals("dev-laptop-4", result.get(1).get(7));//device name
        Assert.assertEquals("test-4", result.get(1).get(8));//scan_dir_mapping_id
        Assert.assertEquals(AppConstant.FILE, result.get(1).get(9));//type
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder", result.get(1).get(17));//parent path
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder/readme.txt", result.get(1).get(18));//path name

        scanDirId = "invalid-1|test-5";
        apiResponse = scanDirService.readScanDirectory(null, scanDirId, null, "txt|ok", null, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(result.size(), 1);

        Assert.assertNull(result.get(0).get(25));//reqCsvMappingId
        Assert.assertNull(result.get(0).get(24));//reqRecursive
        Assert.assertEquals("txt|ok", result.get(0).get(23));//reqFileType
        Assert.assertNull(result.get(0).get(22));//reqPathName
        Assert.assertEquals("invalid-1|test-5", result.get(0).get(21));//reqScanDirId

        Assert.assertEquals("txt", result.get(0).get(20));//file extension
        Assert.assertEquals("readme.txt", result.get(0).get(19));//file name
        Assert.assertEquals(AppConstant.TABLE_FILE_PATH, result.get(0).get(4));//table name
        Assert.assertEquals("dev-laptop-5", result.get(0).get(7));//device name
        Assert.assertEquals("test-5", result.get(0).get(8));//scan_dir_mapping_id
        Assert.assertEquals(AppConstant.FILE, result.get(0).get(9));//type
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder", result.get(0).get(17));//parent path
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder/readme.txt", result.get(0).get(18));//path name

    }
    @Test
    public void testUpdateScanDir() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String scanDirId;;
        ApiResponse apiResponse;
        try {
            scanDirService.updateScanDirectory(null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        scanDirId = "meta-data-dir";
        apiResponse = scanDirService.updateScanDirectory(null, scanDirId, AppConstant.TRUE);
        Assert.assertEquals(apiResponse.getStatus(), AppConstant.SUCCESS);

        scanDirId = "test-4|test-5";
        apiResponse = scanDirService.updateScanDirectory(null, scanDirId, AppConstant.TRUE);
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
        apiResponse = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
        ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertFalse(result.isEmpty());
        scanDirId = "invalid-id";
        try {
            scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        try {
            scanDirService.getScanDirectory(null, null, null, null, AppConstant.TRUE, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        String path = "D:\\workspace\\ftp-application\\FTP\\meta-data\\config-files\\file-mapping-config\\readme.txt";
        try {
            scanDirService.getScanDirectory(null, null, path, null, AppConstant.TRUE, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCodes.BAD_REQUEST_ERROR);
        }
        scanDirId = "meta-data-dir";
        apiResponse = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();// All row where scanDirId is matched
        Assert.assertFalse(result.isEmpty());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, "invalid-csv-mapping-id");
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, "api-scan-dir");
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(11, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path,"pdf", AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertNull(result);

        path = "D:/workspace/ftp-application/FTP/meta-data/config-files/file-mapping-config/";
        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();// All row where scanDirId and pathName is matched
        Assert.assertEquals(5, result.size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, "yml", AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(26, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, null);
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, "yml", AppConstant.TRUE, "api-scan-dir");
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(11, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, "api-scan-dir");
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(11, result.get(0).size());

        apiResponse = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, "invalid-csv-mapping-id");
        result = (ArrayList<ArrayList<String>>) apiResponse.getData();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());
    }
}
