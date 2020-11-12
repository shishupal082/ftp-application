package com.project.ftp;

import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

public class TestStaticService {
    @Test
    public void testSplitStringOnLimit() {
        String str = "D:/workspace/project/data";
        String[] strings = StaticService.splitStringOnLimit(str, null, -1);
        Assert.assertEquals(str, strings[0]);
        strings = StaticService.splitStringOnLimit(str, "/", -1);
        Assert.assertEquals("D:", strings[0]);
        strings = StaticService.splitStringOnLimit(str, "D:/workspace/project", -1);
        Assert.assertEquals("", strings[0]);
        Assert.assertEquals("/data", strings[1]);
    }
}
