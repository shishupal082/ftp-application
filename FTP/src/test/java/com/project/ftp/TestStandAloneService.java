package com.project.ftp;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestStandAloneService {
    final static Logger logger = LoggerFactory.getLogger(TestStandAloneService.class);
    @Test
    public void TestStandAloneOracleV1() throws Exception {
        TestMSExcelService testMSExcelService = new TestMSExcelService();
        String[] args = testMSExcelService.getAppStandAloneConfig(false);
//        FtpApplication.main(args);
    }
}
