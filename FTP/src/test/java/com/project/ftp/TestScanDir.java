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
import java.util.HashMap;

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
        ArrayList<ArrayList<String>> pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, null, null);
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
        ArrayList<ArrayList<String>> pathInfoScanResults;
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 18);
        scanDirId = "workspace-ftp-config-files/";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 18);
        recursive = "false";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 18);
        recursive = "invalid-boolean";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 18);
        recursive = "true";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 40);
        scanDirId = "workspace-ftp-empty-folder";
        recursive = "true";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        scanDirId = "workspace-ftp-empty-folder/";
        recursive = "false";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(pathInfoScanResults.size(), 1);

        scanDirId = "workspace-ftp-single-file-folder";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, null, null);
        Assert.assertEquals(2, pathInfoScanResults.size());

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(5, pathInfoScanResults.size());
        recursive = "false";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(4, pathInfoScanResults.size());

        scanDirId = "scan-dir-test-folder";
        recursive = "false";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(1, pathInfoScanResults.size());
        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, "invalid-csv-mapping-id");
        Assert.assertEquals(26, csvData.split(",").length);

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(1, pathInfoScanResults.size());

        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
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
        ArrayList<ArrayList<String>> result;
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(result.size(), 3);
        recursive = "true";
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(result.size(), 3);

        scanDirId = "test-4|test-5";
        recursive = "true-ok";
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, "test-4|test-5-csv-mapping-id");
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
        result = scanDirService.readScanDirectory(null, scanDirId, null, "txt|ok", null, null);
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
    public void testReadScanDir4() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(false);
        ScanDirService scanDirService = new ScanDirService(appConfig, null);
        String scanDirId = "all-path";
        String recursive = "false";
        ArrayList<ArrayList<String>> result;
        ArrayList<HashMap<String, String>> resultJson;
        String reqPathName = "file://10.130.4.15/DSTE-RNC/joint-report/gag-for-gate-lodge-shifting.pdf";
        reqPathName = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder/readme.txt";
//        reqPathName = "//10.130.4.15/Share%20Folder%20S&T/DSTE-RNC/SpecialTermsandConditionsofcontractELBMB-1822-09-22.pdf";
        resultJson = scanDirService.readScanDirectoryJson(null, scanDirId, reqPathName, null, recursive, null);
        Assert.assertEquals(1, resultJson.size());
        Assert.assertEquals(reqPathName, resultJson.get(0).get("pathname"));
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
        ArrayList<ArrayList<String>> result;
        scanDirId = "meta-data-dir";
        result = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
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
        result = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
        Assert.assertFalse(result.isEmpty());

        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, "invalid-csv-mapping-id");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, "api-scan-dir");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(11, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path,"pdf", AppConstant.TRUE, null);
        Assert.assertNull(result);

        path = "D:/workspace/ftp-application/FTP/meta-data/config-files/file-mapping-config/";
        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null);
        Assert.assertEquals(5, result.size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "yml", AppConstant.TRUE, null);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(26, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "yml", AppConstant.TRUE, "api-scan-dir");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(11, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, "api-scan-dir");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(11, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, "invalid-csv-mapping-id");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(26, result.get(0).size());
    }
}
