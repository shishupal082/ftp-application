package com.project.ftp;

import com.project.ftp.bridge.roles.service.BinaryTree;
import com.project.ftp.bridge.roles.service.ExpressionEvaluator;
import com.project.ftp.bridge.roles.service.Stack;
import com.project.ftp.service.ConfigService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestStack {
    private final ExpressionEvaluator testRoles = new ExpressionEvaluator();
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
