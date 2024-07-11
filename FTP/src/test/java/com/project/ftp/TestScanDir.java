package com.project.ftp;

import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import com.project.ftp.bridge.roles.service.ExpressionEvaluator;
import com.project.ftp.bridge.roles.service.RolesFileParser;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.intreface.UserFile;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.PathInfoScanResult;
import com.project.ftp.obj.ScanResult;
import com.project.ftp.service.FileService;
import com.project.ftp.service.ScanDirService;
import com.project.ftp.service.StaticService;
import com.project.ftp.service.UserService;
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
        AppConfig appConfig = testMSExcelService.getAppConfig();
        UserService userService = new UserService(appConfig, new UserFile(appConfig));
        ScanDirService scanDirService = new ScanDirService(appConfig, userService);
        String path = "D:/workspace/ftp-application/FTP/";
        String recursive = null;
        ApiResponse apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        ArrayList<PathInfoScanResult> pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 20);
        recursive = "true";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 564);
    }
    @Test
    public void testReadScanDir2() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig();
        UserService userService = new UserService(appConfig, new UserFile(appConfig));
        ScanDirService scanDirService = new ScanDirService(appConfig, userService);
        String path = "D:/workspace/ftp-application/FTP/meta-data/";
        String recursive = null;
        ApiResponse apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        ArrayList<PathInfoScanResult> pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 9);
        path = "D:/workspace/ftp-application/FTP/meta-data";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 9);
        recursive = "false";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 9);
        recursive = "invalid-boolean";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 9);
        recursive = "true";
//        path = "C:/";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 63);
//        for(PathInfoScanResult pathInfoScanResult: pathInfoScanResults) {
//            if (pathInfoScanResult.getType() == AppConstant.FOLDER) {
//                logger.info("{}, {}, {}", pathInfoScanResult.getType(),
//                        pathInfoScanResult.getPath(), pathInfoScanResult.getSize());
//            }
//        }
    }
    @Test
    public void testReadScanDir3() {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfig();
        UserService userService = new UserService(appConfig, new UserFile(appConfig));
        ScanDirService scanDirService = new ScanDirService(appConfig, userService);
        String path = "D:/workspace/ftp-application/FTP/meta-data/app_env_config.yml";
        String recursive = "false";
        ApiResponse apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        ArrayList<PathInfoScanResult> pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        recursive = "true";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        path = "D:/workspace/ftp-application/FTP/meta-data/empty-folder/";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        path = "D:/workspace/ftp-application/FTP/meta-data/empty-folder";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        pathInfoScanResults = (ArrayList<PathInfoScanResult>) apiResponse.getData();
        Assert.assertEquals(pathInfoScanResults.size(), 1);
        path = "invalid-path";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        Assert.assertNull(apiResponse.getData());
        path = "";
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        Assert.assertNull(apiResponse.getData());
        path = null;
        apiResponse = scanDirService.getPathInfoDetails(path, recursive);
        Assert.assertNull(apiResponse.getData());
    }
}
