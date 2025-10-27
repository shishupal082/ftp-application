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
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        apiResponse = scanDirService.getScanDirectoryConfig(null, "test-with-null-path-index", null);
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertEquals(1, scanDirMapping.size());
        Assert.assertEquals("test-with-null-path-index", scanDirMapping.get(0).getId());
        Assert.assertNull(scanDirMapping.get(0).getPathIndex());

        scanDirService.getScanDirectoryConfig(null, "test-with-null-path-index", "invalid-path");
        scanDirMapping = (ArrayList<ScanDirMapping>) apiResponse.getData();
        Assert.assertEquals(1, scanDirMapping.size());
        Assert.assertEquals("test-with-null-path-index", scanDirMapping.get(0).getId());
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
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            scanDirService.readScanDirectory(null, "",null, null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            scanDirService.readScanDirectory(null, "invalid-path",null, null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            scanDirService.readScanDirectory(null, null, "E:/invalid-file-or-folder/", null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        scanDirId = "d-workspace-ftp-application-ftp";
        ArrayList<ArrayList<String>> pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, null, null);
        Assert.assertEquals(20, pathInfoScanResults.size());
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
        Assert.assertEquals(19, pathInfoScanResults.size());
        scanDirId = "workspace-ftp-config-files/";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(19, pathInfoScanResults.size());
        recursive = "false";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(19, pathInfoScanResults.size());
        recursive = "invalid-boolean";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(19, pathInfoScanResults.size());
        recursive = "true";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(58, pathInfoScanResults.size());
        scanDirId = "workspace-ftp-empty-folder";
        recursive = "true";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(1, pathInfoScanResults.size());
        scanDirId = "workspace-ftp-empty-folder/";
        recursive = "false";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(1, pathInfoScanResults.size());

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
        Assert.assertEquals(28, csvData.split(",").length);

        scanDirId = "scan-dir-test-folder";
        recursive = "true";
        path = "D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/readme.txt";
        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(1, pathInfoScanResults.size());

        pathInfoScanResults = scanDirService.readScanDirectory(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(1, pathInfoScanResults.size());

        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, null);
        Assert.assertEquals(27, csvData.split(",").length);

        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, "api-scan-dir");
        Assert.assertEquals(11, csvData.split(",").length);
        Assert.assertEquals("readme.txt", csvData.split(",")[10]);


        csvData = scanDirService.readScanDirectoryCsv(null, scanDirId, path, null, recursive, "invalid-csv-mapping-id");
        Assert.assertEquals(28, csvData.split(",").length);
        Assert.assertEquals("txt", csvData.split(",")[22]);
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
        Assert.assertEquals(3, result.size());
        recursive = "true";
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, null);
        Assert.assertEquals(3, result.size());

        scanDirId = "test-4|test-5";
        recursive = "true-ok";
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, recursive, "test-4|test-5-csv-mapping-id");
        Assert.assertEquals(3, result.size());

        Assert.assertEquals("test-4|test-5-csv-mapping-id", result.get(1).get(27));//reqCsvMappingId
        Assert.assertEquals("true-ok", result.get(1).get(26));//reqRecursive
        Assert.assertNull(result.get(1).get(25));//reqFileType
        Assert.assertNull(result.get(1).get(24));//reqPathName
        Assert.assertEquals("test-4|test-5", result.get(1).get(23));//reqScanDirId

        Assert.assertEquals("txt", result.get(1).get(22));//file extension
        Assert.assertEquals("readme.txt", result.get(1).get(20));//file name
        Assert.assertEquals(AppConstant.TABLE_FILE_PATH, result.get(1).get(4));//table name
        Assert.assertEquals("dev-laptop-4", result.get(1).get(7));//device name
        Assert.assertEquals("test-4", result.get(1).get(8));//scan_dir_mapping_id
        Assert.assertEquals(AppConstant.FILE, result.get(1).get(9));//type
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder", result.get(1).get(18));//parent path
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder/readme.txt", result.get(1).get(19));//path name

        scanDirId = "invalid-1|test-5";
        result = scanDirService.readScanDirectory(null, scanDirId, null, "txt|ok", null, null);
        Assert.assertEquals(1, result.size());

        Assert.assertNull(result.get(0).get(27));//reqCsvMappingId
        Assert.assertNull(result.get(0).get(26));//reqRecursive
        Assert.assertEquals("txt|ok", result.get(0).get(25));//reqFileType
        Assert.assertNull(result.get(0).get(24));//reqPathName
        Assert.assertEquals("invalid-1|test-5", result.get(0).get(23));//reqScanDirId

        Assert.assertEquals("txt", result.get(0).get(22));//file extension
        Assert.assertEquals("readme", result.get(0).get(21));//filename without extension
        Assert.assertEquals("readme.txt", result.get(0).get(20));//file name
        Assert.assertEquals(AppConstant.TABLE_FILE_PATH, result.get(0).get(4));//table name
        Assert.assertEquals("dev-laptop-5", result.get(0).get(7));//device name
        Assert.assertEquals("test-5", result.get(0).get(8));//scan_dir_mapping_id
        Assert.assertEquals(AppConstant.FILE, result.get(0).get(9));//type
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder", result.get(0).get(18));//parent path
        Assert.assertEquals("D:/workspace/ftp-application/FTP/meta-data/scan-dir-test-folder/single-file-folder/readme.txt", result.get(0).get(19));//path name

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
        if (TestMSExcelService.isCompilerTest) {
            return;
        }
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String scanDirId;;
        ApiResponse apiResponse;
        try {
            scanDirService.updateScanDirectory(null, null, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        scanDirId = "meta-data-dir";
        apiResponse = scanDirService.updateScanDirectory(null, scanDirId, AppConstant.TRUE);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());

        scanDirId = "test-4|test-5";
        apiResponse = scanDirService.updateScanDirectory(null, scanDirId, AppConstant.TRUE);
        Assert.assertEquals(AppConstant.SUCCESS, apiResponse.getStatus());
    }
    @Test
    public void testGetScanDir() {
        if (TestMSExcelService.isCompilerTest) {
            return;
        }
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
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        try {
            scanDirService.getScanDirectory(null, null, null, null, AppConstant.TRUE, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        String path = "D:\\workspace\\ftp-application\\FTP\\meta-data\\config-files\\file-mapping-config\\readme.txt";
        try {
            scanDirService.getScanDirectory(null, null, path, null, AppConstant.TRUE, null);
            Assert.assertEquals(0, 1);
        } catch (AppException e) {
            Assert.assertEquals(ErrorCodes.BAD_REQUEST_ERROR, e.getErrorCode());
        }
        scanDirId = "meta-data-dir";
        result = scanDirService.getScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
        Assert.assertFalse(result.isEmpty());

        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(27, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, "invalid-csv-mapping-id");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(27, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, "api-scan-dir");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(11, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(28, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path,"pdf", AppConstant.TRUE, null);
        Assert.assertNull(result);

        path = "D:/workspace/ftp-application/FTP/meta-data/config-files/file-mapping-config/";
        result = scanDirService.getScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null);
        Assert.assertEquals(6, result.size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "yml", AppConstant.TRUE, null);
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(28, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(27, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "yml", AppConstant.TRUE, "api-scan-dir");
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(11, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, "api-scan-dir");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(11, result.get(0).size());

        result = scanDirService.getScanDirectory(null, scanDirId, path, "txt", AppConstant.TRUE, "invalid-csv-mapping-id");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(27, result.get(0).size());
    }
    @Test
    public void testReadScanDirV2() {
        if (TestMSExcelService.isCompilerTest) {
            return;
        }
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig(true);
        ScanDirService scanDirService = appConfig.getScanDirService();
        String scanDirId, path;
        ArrayList<ArrayList<String>> result;
        scanDirId = "meta-data-dir";
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, AppConstant.TRUE, null);
        Assert.assertFalse(result.isEmpty());

        result = scanDirService.readScanDirectory(null, scanDirId, null, null, AppConstant.FALSE, null);
        Assert.assertFalse(result.isEmpty());

        path = "D:/workspace/ftp-application/FTP/meta-data/smms-data/";
        result = scanDirService.readScanDirectory(null, scanDirId, path, null, null, null);
        Assert.assertEquals(28, result.get(0).size());

        path = "D:/workspace/ftp-application/FTP/meta-data/test-data/";
        result = scanDirService.readScanDirectory(null, scanDirId, path, null, AppConstant.FALSE, null);
        Assert.assertEquals(28, result.get(0).size());
    }
    @Test
    public void testReadScanDirV3() {
        TestAppConfig testAppConfig = new TestAppConfig();
        AppConfig appConfig = testAppConfig.getAppConfigV2();
        ScanDirService scanDirService = appConfig.getScanDirService();
        String scanDirId, path;
        ArrayList<ArrayList<String>> result;
        scanDirId = "test-data-md5-dir";
        result = scanDirService.readScanDirectory(null, scanDirId, null, null, null, null);
        Assert.assertEquals(10, result.size());
        Assert.assertEquals(28, result.get(0).size());
        Assert.assertEquals("1.bmp", result.get(1).get(20));
        Assert.assertEquals("258a5aab652db23fa8de7c2649ff9220", result.get(1).get(12));
        Assert.assertEquals("1.docx", result.get(2).get(20));
        Assert.assertEquals("81f8e9334be195ffc7186288627d620d", result.get(2).get(12));
        Assert.assertEquals("1.txt", result.get(3).get(20));
        Assert.assertEquals("c4ca4238a0b923820dcc509a6f75849b", result.get(3).get(12));
        Assert.assertEquals("1.xlsx", result.get(4).get(20));
        Assert.assertEquals("f2bf6ff199fbe3400d50a41a01b3d380", result.get(4).get(12));
        Assert.assertEquals("2.bmp", result.get(5).get(20));
        Assert.assertEquals("37834f4cd8abd5963e8d13cffb45229c", result.get(5).get(12));
        Assert.assertEquals("2.docx", result.get(6).get(20));
        Assert.assertEquals("5dd625e7ef7e3a4d254934816a7b7b68", result.get(6).get(12));
        Assert.assertEquals("2.txt", result.get(7).get(20));
        Assert.assertEquals("c4ca4238a0b923820dcc509a6f75849b", result.get(7).get(12));
        Assert.assertEquals("2.xlsx", result.get(8).get(20));
        Assert.assertEquals("8e0c33cb67bff268445ab73aa43b788e", result.get(8).get(12));
        Assert.assertEquals("readme.txt", result.get(9).get(20));
        Assert.assertEquals("198b90c503ef2936e5d22bcaf116c357", result.get(9).get(12));
    }
}
