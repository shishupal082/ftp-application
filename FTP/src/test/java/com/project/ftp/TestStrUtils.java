package com.project.ftp;

import com.project.ftp.common.StrUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class TestStrUtils {
    final static Logger logger = LoggerFactory.getLogger(TestStrUtils.class);
    @Test
    public void TestStrUtilsV1() throws Exception {
        StrUtils strUtils = new StrUtils();
        String text = "$$-5$$";
        String regex = "\\$\\$-5\\$\\$";
        Assert.assertTrue(strUtils.isMatching(text, regex));
        regex = "$$-5$$";
        Assert.assertFalse(strUtils.isMatching(text, regex));
    }
}
