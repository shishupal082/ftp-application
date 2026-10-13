package com.project.ftp;

import com.project.ftp.bridge.service.ExcelToCsvDataConvertServiceV2;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestMSExcelToCsvDataConvertServiceV2 {
    private final ExcelToCsvDataConvertServiceV2 excelToCsvDataConvertServiceV2 = new ExcelToCsvDataConvertServiceV2();
    @Test
    public void testDataConvertV1() {
        ArrayList<ArrayList<String>> inputData, outputData;
        inputData = null;
        outputData = excelToCsvDataConvertServiceV2.removeFirstEmptyRow(null);
        Assert.assertNull(outputData);
    }
    @Test
    public void testDataConvertV2() {
        ArrayList<ArrayList<String>> inputData, outputData;
        inputData = null;
        outputData = excelToCsvDataConvertServiceV2.skipEmptyRows(null, null);
        Assert.assertNull(outputData);
    }
}
