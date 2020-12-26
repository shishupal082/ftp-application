package com.project.ftp;

import com.project.ftp.bridge.BridgeConstant;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import com.project.ftp.bridge.roles.service.*;
import com.project.ftp.service.ConfigService;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestRoles {
    private final ExpressionEvaluator testRoles = new ExpressionEvaluator();
    private final String rolesFilePath = StaticService.getProjectWorkingDir()+"/meta-data/config-files/roles.yml";
    @Test
    public void testRelatedUsers() {
        BridgeConfig bridgeConfig = new BridgeConfig(null, null);
        RolesService rolesService = new RolesService(bridgeConfig, rolesFilePath);
        Assert.assertNull(rolesService.getRelatedUsers(null));
        Assert.assertEquals("U5,U4,U1,U3,U2", String.join(",", rolesService.getRelatedUsers("U1")));
        Assert.assertEquals("U5,U4,U1,U3,U2", String.join(",", rolesService.getRelatedUsers("U2")));
        Assert.assertEquals("U5,U4,U1,U3,U2", String.join(",", rolesService.getRelatedUsers("U3")));
        Assert.assertEquals("U5,U4,U1,U3,U2", String.join(",", rolesService.getRelatedUsers("U4")));
        Assert.assertEquals("U1,U2,U3,U5,U4", String.join(",", rolesService.getRelatedUsers("U5")));
        Assert.assertEquals("U3,U6", String.join(",", rolesService.getRelatedUsers("U6")));
        Assert.assertEquals("", String.join(",", rolesService.getRelatedUsers("U7")));
        Assert.assertEquals("", String.join(",", rolesService.getRelatedUsers("U8")));
        Assert.assertEquals("U9", String.join(",", rolesService.getRelatedUsers("U9")));
        Assert.assertEquals("", String.join(",", rolesService.getRelatedUsers("U10")));
        Assert.assertEquals("U3,U11", String.join(",", rolesService.getRelatedUsers("U11")));
    }
    @Test
    public void testStack() {
        Stack stack = new Stack();
        Assert.assertEquals(-1, stack.getTop());
        stack.push("Str");
        Assert.assertEquals(0, stack.getTop());
        String str = (String) stack.pop();
        Assert.assertEquals("Str", str);
        Assert.assertEquals(-1, stack.getTop());
    }
    @Test
    public void testBinaryTree() {
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

        String str = "(one&two)";
        ArrayList<String> strings = expressionEvaluator.tokenizeBinary(str);
        BinaryTree binaryTree = BinaryTree.createBinaryTree(strings);
        ArrayList<String> post = binaryTree.getPostOrder(binaryTree);
        Assert.assertEquals(3, post.size());

        str = "(~one)"; // Invalid expression
        strings = expressionEvaluator.tokenizeBinary(str);
        binaryTree = BinaryTree.createBinaryTree(strings);
        post = binaryTree.getPostOrder(binaryTree);
        Assert.assertEquals(3, post.size());


        str = "~one";
        strings = expressionEvaluator.tokenizeBinary(str);
        binaryTree = BinaryTree.createBinaryTree(strings);
        post = binaryTree.getPostOrder(binaryTree);
        Assert.assertEquals(2, post.size());

        str = "(~one&two)";
        strings = expressionEvaluator.tokenizeBinary(str);
        binaryTree = BinaryTree.createBinaryTree(strings);
        post = binaryTree.getPostOrder(binaryTree);
        Assert.assertEquals(4, post.size());

        str = "((~one)&two)"; // Invalid expression
        strings = expressionEvaluator.tokenizeBinary(str);
        binaryTree = BinaryTree.createBinaryTree(strings);
        post = binaryTree.getPostOrder(binaryTree);
        Assert.assertEquals(5, post.size());

        str = "(S12/13-AU_R_S&(2/3-ZU_R_R&(12-TPR&(13-TPR&(5-U_R_LR&(~5-U_N_LR&(5A-TPR&(2/3-TPR&((5-ADUCR&(ML-ZU_R_R&(M-TPR&(4-NWKR&(10/11-TPR&(4A-TPR&(OV11-Z2U_R_R&(~ML-ZU_N_R&(S11-RECR|S11-HECR|S11-DECR)))))))))|(5-BDUCR&(5B-TPR&(~LL-ZU_N_R&(L-TPR&(4B-TPR&(((4-NWKR&~OV10/1-Z2U_N_R&OV10/1-Z2U_R_R)|(4-RWKR&10/11-TPR&4A-TPR&~OV10/2-Z2U_N_R&OV10/2-Z2U_R_R))&(S10-RECR|S10-HECR))))))))))))))))";
        strings = expressionEvaluator.tokenizeBinary(str);
        binaryTree = BinaryTree.createBinaryTree(strings);
        post = binaryTree.getPostOrder(binaryTree);
        Assert.assertEquals(72, post.size());
    }
    @Test
    public void testEvaluateBinary() {
        Assert.assertTrue(testRoles.evaluateBinaryExpression("((true&true&true&true)&(~false))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&((false|true)&(true|false)))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&(false|true))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&true&true&true&true)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&(true&true&true&true))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("((true&true&true&true)&true)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("(true&(false&true))"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true&~false)"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("(true|false)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("(true&false)"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("~true"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("true"));
        Assert.assertTrue(testRoles.evaluateBinaryExpression("~false"));
        Assert.assertFalse(testRoles.evaluateBinaryExpression("false"));


        Assert.assertNull(testRoles.evaluateBinaryExpression(null));
        Assert.assertNull(testRoles.evaluateBinaryExpression("(~false)"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("((~false))"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("(~true)"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("(true&true1)"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("(true1&true)"));
        Assert.assertNull(testRoles.evaluateBinaryExpression("((~false)&(~false))"));

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


        rolesService = new RolesService(bridgeConfig, rolesFilePath);
        Assert.assertNotNull(rolesService.getRolesConfig());
        Assert.assertNotNull(rolesService.getRolesAccess());
        Assert.assertNotNull(rolesService.getApiRolesMapping());
        Assert.assertEquals(12, rolesService.getAllRoles().size());
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
