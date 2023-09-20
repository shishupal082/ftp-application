package com.project.ftp;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.intreface.AppToBridge;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.service.FileServiceV3;
import com.project.ftp.service.MSExcelService;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestMSExcelService {
    final static Logger logger = LoggerFactory.getLogger(TestMSExcelService.class);
    private AppConfig getAppConfig() {
        AppConfig appConfig = new AppConfig();
        FtpConfiguration ftpConfiguration = new FtpConfiguration();
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("serverName");
        arguments.add("false");
        arguments.add("/meta-data/app_env_config_4.yml");
        appConfig.setCmdArguments(arguments);
        appConfig.updateFinalFtpConfiguration(ftpConfiguration);
        AppToBridge appToBridge = new AppToBridge(ftpConfiguration, null);
        appConfig.setAppToBridge(appToBridge);
        appConfig.setFtpConfiguration(ftpConfiguration);
        return appConfig;
    }
    @Test
    public void testTestMSExcelServiceV1() {
        MSExcelService msExcelService = new MSExcelService(this.getAppConfig(), null);
        try {
            ApiResponse apiResponse =  msExcelService.updateMSExcelSheetData("csv-test-01");
            logger.info("{}", apiResponse);
            apiResponse =  msExcelService.updateMSExcelSheetData("csv-test-02");
            logger.info("{}", apiResponse);
            apiResponse =  msExcelService.updateMSExcelSheetData("csv-test-03");
            logger.info("{}", apiResponse);
        } catch (Exception e) {
            logger.info("{}", e.getMessage());
        }
    }
}
