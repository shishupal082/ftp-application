package com.project.ftp;

import com.project.ftp.config.ApiIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRoles {
    @Test
    public void testAuthorisationV1() {
        List<ApiIdentifier> list = Arrays.asList(ApiIdentifier.values());
        ArrayList<ApiIdentifier> apiIdentifiers = new ArrayList<>(list);
        Assert.assertEquals(57, apiIdentifiers.size());
    }
}
