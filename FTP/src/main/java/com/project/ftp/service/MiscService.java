package com.project.ftp.service;

import com.project.ftp.config.AppConstant;
import java.util.ArrayList;
import java.util.HashMap;

public class MiscService {
    public MiscService() {}
    private ArrayList<String> getTableIndex(ArrayList<String> configTableIndex, int maxColCount) {
        if (maxColCount < 1) {
            return null;
        }
        if (configTableIndex == null) {
            configTableIndex = new ArrayList<>();
            configTableIndex.add("col-1");
        }
        ArrayList<String> finalTableIndex = new ArrayList<>();
        String colIndex;
        for(int i=0; i<maxColCount; i++) {
            if (configTableIndex.size() > i) {
                colIndex = configTableIndex.get(i);
            } else {
                colIndex = "col-" + (i+1);
            }
            if (!finalTableIndex.contains(colIndex)) {
                finalTableIndex.add(colIndex);
            } else if (!finalTableIndex.contains(colIndex + "-1")) {
                finalTableIndex.add(colIndex+"-1");
            } else if (!finalTableIndex.contains(colIndex + "-2")) {
                finalTableIndex.add(colIndex+"-2");
            } else {
                finalTableIndex.add(colIndex+"-"+StaticService.createUUIDNumber());
            }
        }
        return finalTableIndex;
    }
    private HashMap<String, String> getRowJsonData(ArrayList<String> rowData, ArrayList<String> colIndex) {
        if (colIndex == null || rowData == null) {
            return null;
        }
        HashMap<String, String> result = new HashMap<>();
        String value;
        for(int i=0; i<colIndex.size(); i++) {
            if (i < rowData.size()) {
                value = rowData.get(i);
                if (StaticService.isValidString(value)) {
                    result.put(colIndex.get(i), value);
                }
            }
        }
        return result;
    }
    public void insertData(ArrayList<ArrayList<String>> sheetData, int rowIndex, int colIndex, String cellData) {
        if (sheetData == null) {
            return;
        }
        if (sheetData.size() <= rowIndex) {
            for (int i=sheetData.size(); i<=rowIndex; i++) {
                sheetData.add(new ArrayList<>());
            }
        }
        ArrayList<String> rowData = sheetData.get(rowIndex);
        if (rowData == null) {
            return;
        }
        if (rowData.size() <= colIndex) {
            for (int i=rowData.size(); i<colIndex; i++) {
                rowData.add(AppConstant.EmptyStr);
            }
        }
        rowData.add(cellData);
        sheetData.set(rowIndex, rowData);
    }
    public ArrayList<HashMap<String, String>> convertArraySheetDataToJsonData(ArrayList<ArrayList<String>> sheetData,
                                                                              ArrayList<String> tableMappingIndex) {
        if (sheetData == null) {
            return null;
        }
        int maxColCount = 0;
        for(ArrayList<String> row: sheetData) {
            if (row == null) {
                continue;
            }
            if (row.size() > maxColCount) {
                maxColCount = row.size();
            }
        }
        tableMappingIndex = this.getTableIndex(tableMappingIndex, maxColCount);
        ArrayList<HashMap<String, String>> finalSheetData = new ArrayList<>();
        HashMap<String, String> rowJsonData;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData == null) {
                continue;
            }
            rowJsonData = this.getRowJsonData(rowData, tableMappingIndex);
            if (rowJsonData == null) {
                continue;
            }
            finalSheetData.add(rowJsonData);
        }
        return finalSheetData;
    }
}
