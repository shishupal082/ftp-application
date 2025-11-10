package com.project.ftp;

import com.project.ftp.bridge.obj.standalone.StandAloneConfig;
import com.project.ftp.bridge.service.StandAloneService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestStandAloneConfig {
    final static Logger logger = LoggerFactory.getLogger(TestStandAloneConfig.class);
    final StandAloneService standAloneService = new StandAloneService();
    @Test
    public void TestStandAloneService1() {
        StandAloneConfig standAloneConfig = standAloneService.getStandaloneConfig(null);
        Assert.assertNull(standAloneConfig);
    }
    @Test
    public void TestStandAloneService2() {
        ArrayList<String> standAloneConfigPath = new ArrayList<>();
        StandAloneConfig standAloneConfig = standAloneService.getStandaloneConfig(standAloneConfigPath);
        Assert.assertNull(standAloneConfig);
    }
    @Test
    public void TestStandAloneService3() {
        ArrayList<String> standAloneConfigPath = new ArrayList<>();
        standAloneConfigPath.add("D:/workspace/ftp-application/FTP/meta-data/config-files/standalone/config_1.yml");
        StandAloneConfig standAloneConfig = standAloneService.getStandaloneConfig(standAloneConfigPath);
        Assert.assertNotNull(standAloneConfig);
        Assert.assertEquals("sequence_1",standAloneConfig.getCurrentSequence());
        Assert.assertEquals(1,standAloneConfig.getSequence().get("sequence_1").size());
        Assert.assertEquals("id1",standAloneConfig.getSequence().get("sequence_1").get(0).getId());
        Assert.assertFalse(standAloneConfig.getSequence().get("sequence_1").get(0).getConfirmationRequired());
        Assert.assertEquals("api",standAloneConfig.getApiList().get("id1").getResource());
    }
}
