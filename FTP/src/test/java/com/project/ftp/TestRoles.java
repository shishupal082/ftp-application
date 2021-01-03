package com.project.ftp;

import com.project.ftp.bridge.BridgeConstant;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import com.project.ftp.bridge.roles.service.ExpressionEvaluator;
import com.project.ftp.bridge.roles.service.RolesFileParser;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestRoles {
    private final ExpressionEvaluator testRoles = new ExpressionEvaluator();
    @Test
    public void testRelatedUsers() {
        BridgeConfig bridgeConfig = new BridgeConfig(null, null);
        ArrayList<String> rolesFilePath = new ArrayList<>();
        rolesFilePath.add(StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles/roles.yml");
        rolesFilePath.add(StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles/roles_2.yml");
        RolesService rolesService = new RolesService(bridgeConfig, rolesFilePath);
        Assert.assertNull(rolesService.getRelatedUsers(null));
        Assert.assertEquals("U5,U4,U3,U2,U1", String.join(",", rolesService.getRelatedUsers("U1")));
        Assert.assertEquals("U5,U4,U3,U2,U1", String.join(",", rolesService.getRelatedUsers("U2")));
        Assert.assertEquals("U5,U4,U3,U2,U1", String.join(",", rolesService.getRelatedUsers("U3")));
        Assert.assertEquals("U5,U4,U3,U2,U1", String.join(",", rolesService.getRelatedUsers("U4")));
        Assert.assertEquals("U5,U4,U3,U2,U1", String.join(",", rolesService.getRelatedUsers("U5")));
        Assert.assertEquals("U6,U3", String.join(",", rolesService.getRelatedUsers("U6")));
        Assert.assertEquals("", String.join(",", rolesService.getRelatedUsers("U7")));
        Assert.assertEquals("", String.join(",", rolesService.getRelatedUsers("U8")));
        Assert.assertEquals("U9", String.join(",", rolesService.getRelatedUsers("U9")));
        Assert.assertEquals("", String.join(",", rolesService.getRelatedUsers("U10")));
        Assert.assertEquals("U3,U11", String.join(",", rolesService.getRelatedUsers("U11")));
    }
    @Test
    public void testRoleFileEntry() {
        RolesFileParser rolesFileParser = new RolesFileParser();
        Assert.assertNull(rolesFileParser.getAllRolesFileData(null));
        ArrayList<String> invalidRolesFilePath = new ArrayList<>();
        invalidRolesFilePath.add("invalid-file-name");
        Assert.assertNull(rolesFileParser.getAllRolesFileData(invalidRolesFilePath));
        ArrayList<String> rolesFilePath = new ArrayList<>();
        rolesFilePath.add(StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles/roles.yml");
        rolesFilePath.add(StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles/roles_2.yml");
        Roles roles = rolesFileParser.getAllRolesFileData(rolesFilePath);
        Assert.assertNotNull(roles);
        Assert.assertNotNull(roles.getRoleAccess());
        Assert.assertNotNull(roles.getRoleAccessMapping());
    }
    @Test
    public void testRoleService() {
        RolesService rolesService;
        ArrayList<String> invalidRolesFilePath = new ArrayList<>();
        invalidRolesFilePath.add("invalid-roles-file-path");
        rolesService = new RolesService(null, invalidRolesFilePath);
        Assert.assertNull(rolesService.getRolesConfig());

        BridgeConfig bridgeConfig = new BridgeConfig(null, null);
        rolesService = new RolesService(bridgeConfig, invalidRolesFilePath);
        Assert.assertNull(rolesService.getRolesConfig());
        Assert.assertNull(rolesService.getRolesAccess());
        Assert.assertNull(rolesService.getApiRolesMapping());
        Assert.assertEquals(0, rolesService.getAllRoles().size());
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
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "username", false));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, true));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isBooleanTrue", null, true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isBooleanTrue", null, false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isBooleanTrue", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnable", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnable", "username", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnable", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnable", "username", false));

        ArrayList<String> rolesFilePath = new ArrayList<>();
        rolesFilePath.add(StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles/roles.yml");
        rolesFilePath.add(StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles/roles_2.yml");
        rolesService = new RolesService(bridgeConfig, rolesFilePath);
        Assert.assertNotNull(rolesService.getRolesConfig());
        Assert.assertNotNull(rolesService.getRolesAccess());
        Assert.assertNotNull(rolesService.getApiRolesMapping());
        Assert.assertEquals(13, rolesService.getAllRoles().size());
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
        Assert.assertFalse(rolesService.isRoleAuthorised("isDevUser", null, true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isDevUser", "", true));

        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, false));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "", false));
        Assert.assertFalse(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "U1", false));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, null, true));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "", true));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "U1", true));
        Assert.assertTrue(rolesService.isRoleAuthorised(BridgeConstant.IS_LOGIN, "InvalidUsername", true));


        Assert.assertTrue(rolesService.isRoleAuthorised("isBooleanTrue", null, true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isBooleanTrue", null, false));
        Assert.assertTrue(rolesService.isRoleAuthorised("isBooleanTrue", "", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isBooleanTrue", "", false));
        Assert.assertTrue(rolesService.isRoleAuthorised("isBooleanTrue", "username", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isBooleanTrue", "username", false));

        Assert.assertFalse(rolesService.isRoleAuthorised("isDevUserNotFound", "U1", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isDevUserNotFound", "U1", true));

        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminOrDevUser", "U1", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminOrDevUser", "Admin", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminAndDevUser", "adminAndDev", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAdminOrDevOrLoginUser", "Admin1", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("inValidRoleEntry", "Admin", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("inValidRoleEntry2", "Admin", true));


        Assert.assertTrue(rolesService.isRoleAuthorised("isUploadFileEnable", "username", true));
        Assert.assertTrue(rolesService.isRoleAuthorised("isAddTextEnable", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnable", "username", false));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnable", "username", false));


        Assert.assertFalse(rolesService.isRoleAuthorised("isUploadFileEnableNotFound", "username", true));
        Assert.assertFalse(rolesService.isRoleAuthorised("isAddTextEnableNotFound", "username", true));

        Assert.assertEquals("((admin|dev)|(admin|dev))", rolesService.getRolesByApiName("isAdminOrDevUser"));
    }
}
