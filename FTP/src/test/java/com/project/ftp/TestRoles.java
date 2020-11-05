package com.project.ftp;

import com.project.ftp.bridge.BridgeConstant;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import com.project.ftp.bridge.roles.service.ExpressionEvaluator;
import com.project.ftp.bridge.roles.service.RolesFileParser;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.service.ConfigService;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

public class TestRoles {
    private final ExpressionEvaluator testRoles = new ExpressionEvaluator();
    private final String rolesFilePath = StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles.yml";
    @Test
    public void testEvaluateBinary() {
        Assert.assertTrue(testRoles.evaluateBinaryExpression("((true&true&true&true)&(~false))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&((false|true)&(true|false)))"));
        Assert.assertNull(testRoles.evaluateBinaryExpression(null));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&(false|true))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&true&true&true&true)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&(true&true&true&true))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("((true&true&true&true)&true)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("(true&(false&true))"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("(true&true1)"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("(true1&true)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&~false)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true|false)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("(true&false)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("(~true)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("~true"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("true"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(~false)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("~false"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("false"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("((~false))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("((~false)&(~false))"));


        // Assert.assertNull(testRoles.evaluateBinaryExpression("(false&~)"));
    }
    @Test
    public void testEvaluateNumeric() {
        Assert.assertNull(testRoles.evaluateNumericExpression(null));
        Assert.assertNull(testRoles.evaluateNumericExpression("(2/0)"));
        Assert.assertNull(testRoles.evaluateNumericExpression("(p/q)"));
        Assert.assertNull(testRoles.evaluateNumericExpression("((2p+(2*2))/2)"));
        Assert.assertEquals("3.0", testRoles.evaluateNumericExpression("((2+(2*2))/2)"));
        Assert.assertEquals("6.0", testRoles.evaluateNumericExpression("(2+4)"));
        Assert.assertEquals("8.0", testRoles.evaluateNumericExpression("(2*(2+2))"));
        Assert.assertEquals("6.0", testRoles.evaluateNumericExpression("(2+(2*2))"));
        Assert.assertEquals("1.0", testRoles.evaluateNumericExpression("((2+(2-2))/2)"));
    }
    @Test
    public void testRoleFileEntry() {
        RolesFileParser rolesFileParser = new RolesFileParser();
        Assert.assertNull(rolesFileParser.getRolesFileData(null));
        Assert.assertNull(rolesFileParser.getRolesFileData("invalid-file-name"));
        Roles roles = rolesFileParser.getRolesFileData(rolesFilePath);
        Assert.assertNotNull(roles);
        Assert.assertNotNull(roles.getRoleAccess());
        Assert.assertNotNull(roles.getRoleAccessMapping());
    }
    @Test
    public void testRoleService() {
        RolesService rolesService;
        rolesService = new RolesService(null, "invalid-roles-file-path");
        Assert.assertNull(rolesService.getRolesConfig());

        BridgeConfig bridgeConfig = new BridgeConfig(null, null);
        rolesService = new RolesService(bridgeConfig, "invalid-roles-file-path");
        Assert.assertNull(rolesService.getRolesConfig());
        Assert.assertNull(rolesService.getRolesAccess());
        Assert.assertNull(rolesService.getApiRolesMapping());
        Assert.assertNull(rolesService.getRolesAccessByRoleId(null));
        Assert.assertNull(rolesService.getRolesAccessByRoleId("admin"));
        Assert.assertNull(rolesService.getRolesByApiName(null));
        Assert.assertNull(rolesService.getRolesByApiName("getPublicFiles"));
        Assert.assertFalse(rolesService.isApiAuthorised(null, null, false));
        Assert.assertFalse(rolesService.isApiAuthorised(null, null, true));
        Assert.assertFalse(rolesService.isApiAuthorised("apiName", null, false));
        Assert.assertFalse(rolesService.isApiAuthorised("apiName", null, true));
        Assert.assertFalse(rolesService.isApiAuthorised(null, "userName", false));
        Assert.assertFalse(rolesService.isApiAuthorised(null, "userName", true));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, null, false));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, null, true));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "username", false));
        Assert.assertTrue(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "username", true));

        rolesService = new RolesService(bridgeConfig, rolesFilePath);
        Assert.assertNotNull(rolesService.getRolesConfig());
        Assert.assertNotNull(rolesService.getRolesAccess());
        Assert.assertNotNull(rolesService.getApiRolesMapping());
        Assert.assertNotNull(rolesService.getRolesAccessByRoleId("admin"));
        Assert.assertNull(rolesService.getRolesAccessByRoleId("adminNotFound"));
        Assert.assertNotNull(rolesService.getRolesByApiName("isAdminUser"));
        Assert.assertTrue(rolesService.isApiAuthorised("isAdminUser", "Admin", false));
        Assert.assertTrue(rolesService.isApiAuthorised("isAdminUser", "Admin", true));
        Assert.assertFalse(rolesService.isApiAuthorised("isAdminUser", "U1", false));
        Assert.assertFalse(rolesService.isApiAuthorised("isAdminUser", "U1", true));
        Assert.assertFalse(rolesService.isApiAuthorised("isAdminUser", "userNotFound", false));
        Assert.assertFalse(rolesService.isApiAuthorised("isAdminUser", "userNotFound", true));
        Assert.assertTrue(rolesService.isApiAuthorised("isDevUser", "U1", false));
        Assert.assertTrue(rolesService.isApiAuthorised("isDevUser", "U1", true));

        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "", false));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "", true));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, null, false));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, null, true));
        Assert.assertFalse(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "U1", false));
        Assert.assertTrue(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "U1", true));
        Assert.assertTrue(rolesService.isApiAuthorised(BridgeConstant.IS_LOGIN, "InvalidUsername", true));

        Assert.assertFalse(rolesService.isApiAuthorised("isDevUserNotFound", "U1", false));
        Assert.assertFalse(rolesService.isApiAuthorised("isDevUserNotFound", "U1", true));

        Assert.assertTrue(rolesService.isApiAuthorised("isAdminOrDevUser", "U1", true));
        Assert.assertTrue(rolesService.isApiAuthorised("isAdminOrDevUser", "Admin", true));
        Assert.assertTrue(rolesService.isApiAuthorised("isAdminAndDevUser", "adminAndDev", true));
    }
    @Test
    public void testConfigService() {
        ConfigService configService = new ConfigService(null);
        String sys = "F:/ftp-app/ftp-app-6.0.0-stable";
        String pub = "../../..";
        String pubPost = "D:/workspace/project";
        String calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("D:/workspace/project", calculatedStr);

        calculatedStr = configService.getValidPublicDir(null, null, null);
        Assert.assertEquals("", calculatedStr);

        sys = "D:\\workspace\\ftp-application\\FTP";
        pub = "../..";
        pubPost = "/project";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("D:/workspace/project", calculatedStr);

        sys = "/D:/workspace/ftp-application/FTP";
        pub = "../..";
        pubPost = "/project";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("/D:/workspace/project", calculatedStr);

        sys = "///D:/workspace/ftp-application/FTP";
        pub = "../..";
        pubPost = "/project";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("/D:/workspace/project", calculatedStr);

        sys = "D:////workspace/ftp-application/FTP";
        pub = "../..";
        pubPost = "/project";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("D:/workspace/project", calculatedStr);


        sys = "D:/workspace//ftp-application/FTP//";
        pub = "../..";
        pubPost = "/project";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("D:/workspace/ftp-application/FTP/project", calculatedStr);


        sys = "D:/workspace/ftp-application/FTP";
        pubPost = "/project";

        pub = "../../";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("D:/workspace/project", calculatedStr);

        pub = "../../../..";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("/project", calculatedStr);

        pub = "../../../../../";
        calculatedStr = configService.getValidPublicDir(sys, pub, pubPost);
        Assert.assertEquals("/project", calculatedStr);
    }
}
