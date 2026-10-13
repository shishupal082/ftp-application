package com.project.ftp;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class TestStandAloneService {
    final static Logger logger = LoggerFactory.getLogger(TestStandAloneService.class);
    @Test
    public void TestStandAloneOracleV1() throws Exception {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("F:/ftp-app/OneDrive/ftp/app-data/config-files-v2/standalone/dl-faults/test/1-env_config_1.yml");
        String[] args = testMSExcelService.getAppStandAloneConfig(false, arguments);
//        FtpApplication.main(args);
    }
}
