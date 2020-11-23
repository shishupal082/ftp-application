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
        Assert.assertFalse(rolesService.isRoleAuthorised(null, null, false));
        Assert.assertFalse(rolesService.isRoleAuthorised(null, null, true));
        Assert.assertFalse(rolesService.isRoleAuthorised("apiName", null, false));
        Assert.assertFalse(rolesService.isRoleAuthorised("apiName", null, true));
        Assert.assertFalse(rolesService.isRoleAuthorised(null, "userName", false));
        Assert.assertFalse(rolesService.isRoleAuthorised(null, "userName", true));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, false));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, true));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "username", false));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnable", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnable", "username", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnable", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnable", "username", false));


        rolesService = new RolesService(bridgeConfig, rolesFilePath);
        Assert.assertNotNull(rolesService.getRolesConfig());
        Assert.assertNotNull(rolesService.getRolesAccess());
        Assert.assertNotNull(rolesService.getApiRolesMapping());
        Assert.assertNotNull(rolesService.getRolesAccessByRoleId("admin"));
        Assert.assertNull(rolesService.getRolesAccessByRoleId("adminNotFound"));
        Assert.assertNotNull(rolesService.getRolesByApiName("isAdminUser"));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminUser", "Admin", false));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminUser", "Admin", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAdminUser", "U1", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAdminUser", "U1", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAdminUser", "userNotFound", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAdminUser", "userNotFound", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isDevUser", "U1", false));
        Assert.assertTrue(rolesService.isRoleAuthorised("isDevUser", "U1", true));

        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "", false));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "", true));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, false));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, true));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "U1", false));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "U1", true));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "InvalidUsername", true));

        Assert.assertFalse(rolesService.isRoleAuthorised("isDevUserNotFound", "U1", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isDevUserNotFound", "U1", true));

        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminOrDevUser", "U1", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminOrDevUser", "Admin", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminAndDevUser", "adminAndDev", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminOrDevOrLoginUser", "Admin1", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("inValidRoleEntry", "Admin", true));


        Assert.assertTrue(rolesService.isRoleAuthorised("isUploadFileEnable", "username", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAddTextEnable", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnable", "username", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnable", "username", false));


        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnableNotFound", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnableNotFound", "username", true));

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
