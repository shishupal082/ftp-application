package com.project.ftp;

import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import com.project.ftp.bridge.roles.service.ExpressionEvaluator;
import com.project.ftp.bridge.roles.service.RolesFileParser;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

public class TestRoles {
    private final ExpressionEvaluator testRoles = new ExpressionEvaluator();
    private final String projectWorkingDir = StaticService.getProjectWorkingDir();
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
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(~false)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("~false"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("((~false))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("((~false)&(~false))"));
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
        String roleFileName = projectWorkingDir + "/meta-data/config-files/roles.yml";
        Assert.assertNull(rolesFileParser.getRolesFileData(null));
        Assert.assertNull(rolesFileParser.getRolesFileData("invalid-file-name"));
        Roles roles = rolesFileParser.getRolesFileData(roleFileName);
        Assert.assertNotNull(roles);
    }
    @Test
    public void testRoleService() {
        RolesService rolesService;
        String roleFileName;
        BridgeConfig bridgeConfig = new BridgeConfig(null, null);
        roleFileName = "invalid-file-name";
        rolesService = new RolesService(bridgeConfig, roleFileName);
        Assert.assertNull(rolesService.getRolesConfig());
        Assert.assertNull(rolesService.getRolesAccess());
        Assert.assertNull(rolesService.getApiRolesMapping());
        Assert.assertNull(rolesService.getRolesAccessByRoleId(null));
        Assert.assertNull(rolesService.getRolesAccessByRoleId("admin"));
        Assert.assertNull(rolesService.getRolesByApiName(null));
        Assert.assertNull(rolesService.getRolesByApiName("getPublicFiles"));
        Assert.assertFalse(rolesService.isApiAuthorised(null, null));
        Assert.assertFalse(rolesService.isApiAuthorised("apiName", null));
        Assert.assertFalse(rolesService.isApiAuthorised(null, "userName"));

        roleFileName = projectWorkingDir + "/meta-data/config-files/roles.yml";
        rolesService = new RolesService(bridgeConfig, roleFileName);
        Assert.assertNotNull(rolesService.getRolesConfig());
        Assert.assertNotNull(rolesService.getRolesAccess());
        Assert.assertNotNull(rolesService.getApiRolesMapping());
        Assert.assertNotNull(rolesService.getRolesAccessByRoleId("admin"));
        Assert.assertNull(rolesService.getRolesAccessByRoleId("adminNotFound"));
        Assert.assertNotNull(rolesService.getRolesByApiName("isAdminUser"));
        Assert.assertTrue(rolesService.isApiAuthorised("isAdminUser", "Admin"));
        Assert.assertFalse(rolesService.isApiAuthorised("isAdminUser", "U1"));
        Assert.assertFalse(rolesService.isApiAuthorised("isAdminUser", "userNotFound"));
        Assert.assertTrue(rolesService.isApiAuthorised("isDevUser", "U1"));
        Assert.assertFalse(rolesService.isApiAuthorised("isDevUserNotFound", "U1"));
    }
}
