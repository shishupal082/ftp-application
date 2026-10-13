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

public class TestScanDirProd {
    final static Logger logger = LoggerFactory.getLogger(TestScanDirProd.class);
    @Test
    public void testReadScanDirV2() {
        if (TestMSExcelService.isCompilerTest) {
            return;
        }
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("meta-data/config-files/file-mapping-config");
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        AppConfig appConfig = testMSExcelService.getAppConfigProd(false, arguments);
        ScanDirService scanDirService = appConfig.getScanDirService();

        String scanDirId, path;
        ArrayList<ArrayList<String>> result;
        scanDirId = "scan-fault-dir";
        path = "F:/ftp-app/OneDrive/ftp/app-data/config-files-v2/standalone/dl-faults/data/Fault-07-10-26";
        result = scanDirService.readScanDirectory(null, scanDirId, path, null, AppConstant.TRUE, null, AppConstant.DEFAULT_ROLE_ID);
        Assert.assertFalse(result.isEmpty());
    }
}
