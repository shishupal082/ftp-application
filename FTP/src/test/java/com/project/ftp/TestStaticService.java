package com.project.ftp;

import com.project.ftp.common.StrUtils;
import com.project.ftp.service.FileServiceV3;
import com.project.ftp.service.StaticService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestStaticService {
    @Test
    public void testFileServiceV3ParseAssetsDirFilepath() {
        FileServiceV3 fileServiceV3 = new FileServiceV3(null, null);
        // First /./ is operated then /../
        String assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/ok/temp.txt");
        Assert.assertEquals("ok/temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/assets-dir/temp.txt");
        Assert.assertEquals("assets-dir/temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/assets-dir/assets-dir/temp.txt");
        Assert.assertEquals("assets-dir/assets-dir/temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/assets-dir/assets-dir/assets-dir/temp.txt");
        Assert.assertEquals("assets-dir/assets-dir/assets-dir/temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/../temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/./temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/..//./temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/..//../temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/../../../temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/..../temp.txt");
        Assert.assertEquals("..../temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/.././../temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
        assetsDirFilepath = fileServiceV3.parseAssetsDirFilepath("/assets-dir/./././temp.txt");
        Assert.assertEquals("temp.txt", assetsDirFilepath);
    }
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
    @Test
    public void testPathTokenize() {
        StrUtils strUtils = new StrUtils();
        boolean checkBackSlash = false;
        boolean skipEmpty = false;
        boolean removeDynamic = false;
        boolean removeTrailingEmpty = false;
        Assert.assertNull(strUtils.tokenizePath(null, checkBackSlash, skipEmpty, removeDynamic, removeTrailingEmpty));
        String str = "/data";
        ArrayList<String> tokens = strUtils.tokenizePath(str, checkBackSlash, skipEmpty, removeDynamic, removeTrailingEmpty);
        Assert.assertEquals(2, tokens.size());
        Assert.assertEquals("/data", String.join("/",tokens));
        str = "/data/../../";
        removeDynamic = true;
        tokens = strUtils.tokenizePath(str, checkBackSlash, skipEmpty, removeDynamic, removeTrailingEmpty);
        Assert.assertEquals(3, tokens.size());
        Assert.assertEquals("/data/", String.join("/",tokens));

        str = "/data/../../";
        removeDynamic = false;
        tokens = strUtils.tokenizePath(str, checkBackSlash, skipEmpty, removeDynamic, removeTrailingEmpty);
        Assert.assertEquals(5, tokens.size());
        Assert.assertEquals("/data/../../", String.join("/",tokens));

        str = "/data///";
        tokens = strUtils.tokenizePath(str, checkBackSlash, skipEmpty, removeDynamic, removeTrailingEmpty);
        Assert.assertEquals(5, tokens.size());
        Assert.assertEquals("/data///", String.join("/",tokens));
    }
    @Test
    public void testRemoveRelativePath() {
        Assert.assertNull(StaticService.removeRelativePath(null));
        Assert.assertEquals("/data", StaticService.removeRelativePath("/data"));
        Assert.assertEquals("/data", StaticService.removeRelativePath("/../data"));
        Assert.assertEquals("/data", StaticService.removeRelativePath("/.././data"));
        Assert.assertEquals("/data", StaticService.removeRelativePath("/../data"));
        Assert.assertEquals("/data", StaticService.removeRelativePath("/data/..//../"));
        Assert.assertEquals("/data.", StaticService.removeRelativePath("/../data./.././"));
        Assert.assertEquals("/data.", StaticService.removeRelativePath("/../data././../"));
        Assert.assertEquals("/data.", StaticService.removeRelativePath("/../data./././"));
        Assert.assertEquals("/.data.", StaticService.removeRelativePath("/.././.data./../../."));
        Assert.assertEquals("/.data.", StaticService.removeRelativePath("\\..\\.\\.data.\\..\\..\\."));
    }
}
