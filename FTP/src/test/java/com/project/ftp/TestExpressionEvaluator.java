package com.project.ftp;

import com.project.ftp.bridge.roles.service.BinaryTree;
import com.project.ftp.bridge.roles.service.ExpressionEvaluator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestExpressionEvaluator {
    private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
    @Test
    public void testEvaluateBinary() {
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("((true&true&true&true)&(~false))"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("(true&((false|true)&(true|false)))"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("(true&(false|true))"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("(true&true&true&true&true)"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("(true&(true&true&true&true))"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("((true&true&true&true)&true)"));
        Assert.assertFalse(expressionEvaluator.evaluateBinaryExpression("(true&(false&true))"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("(true&~false)"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("(true|false)"));
        Assert.assertFalse(expressionEvaluator.evaluateBinaryExpression("(true&false)"));
        Assert.assertFalse(expressionEvaluator.evaluateBinaryExpression("~true"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("true"));
        Assert.assertTrue(expressionEvaluator.evaluateBinaryExpression("~false"));
        Assert.assertFalse(expressionEvaluator.evaluateBinaryExpression("false"));

        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression(null));
        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression("(~false)"));
        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression("((~false))"));
        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression("(~true)"));
        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression("(true&true1)"));
        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression("(true1&true)"));
        Assert.assertNull(expressionEvaluator.evaluateBinaryExpression("((~false)&(~false))"));

        // Assert.assertNull(testRoles.evaluateBinaryExpression("(false&~)"));
    }
    @Test
    public void testEvaluateNumeric() {
        Assert.assertNull(expressionEvaluator.evaluateNumericExpression(null));
        Assert.assertNull(expressionEvaluator.evaluateNumericExpression("(2/0)"));
        Assert.assertNull(expressionEvaluator.evaluateNumericExpression("(p/q)"));
        Assert.assertNull(expressionEvaluator.evaluateNumericExpression("((2p+(2*2))/2)"));

        Assert.assertEquals("3.0", expressionEvaluator.evaluateNumericExpression("((2+(2*2))/2)"));
        Assert.assertEquals("6.0", expressionEvaluator.evaluateNumericExpression("(2+4)"));
        Assert.assertEquals("8.0", expressionEvaluator.evaluateNumericExpression("(2*(2+2))"));
        Assert.assertEquals("6.0", expressionEvaluator.evaluateNumericExpression("(2+(2*2))"));
        Assert.assertEquals("1.0", expressionEvaluator.evaluateNumericExpression("((2+(2-2))/2)"));
    }
}
