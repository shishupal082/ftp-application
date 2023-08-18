package com.project.ftp.bridge.service;

import com.project.ftp.bridge.obj.yamlObj.CellMapping;
import com.project.ftp.bridge.obj.yamlObj.CellMappingData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class ExcelToCsvDataConvertService {
    final static Logger logger = LoggerFactory.getLogger(ExcelToCsvDataConvertService.class);
    public ArrayList<ArrayList<String>> formatCellData(ArrayList<ArrayList<String>> sheetData) {
        String cellData;
        ArrayList<String> temp, rowData;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        int i, lastValidIndex, lastRowIndex=0;
        if (sheetData != null) {
            for (int j=0; j<sheetData.size(); j++) {
                rowData = sheetData.get(j);
                temp = new ArrayList<>();
                if (rowData != null) {
                    lastValidIndex=-1;
                    for(i=0; i< rowData.size(); i++) {
                        cellData = rowData.get(i);
                        if (cellData != null) {
                            cellData = cellData.replaceAll("\r\n", ";");
                            cellData = cellData.replaceAll("\n", ";");
                            cellData = cellData.replaceAll("\r", "");
                            cellData = cellData.replaceAll(",", "...");
                            cellData = cellData.trim();
                            if (!cellData.equals("")) {
                                lastValidIndex = i;
                            }
                        }
                        rowData.set(i, cellData);
                    }
                    for(i=0; i<=lastValidIndex; i++) {
                        temp.add(rowData.get(i));
                    }
                }
                if (temp.size() > 0) {
                    lastRowIndex = j;
                }
                sheetData.set(j, temp);
            }
            for (i=0; i<=lastRowIndex; i++) {
                result.add(sheetData.get(i));
            }
        }
        return result;
    }
    public ArrayList<ArrayList<String>> skipEmptyRows(ArrayList<ArrayList<String>> sheetData,
                                                      ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null) {
            return sheetData;
        }
        boolean skipEmptyRows = excelDataConfigById.isSkipEmptyRows();
        if (!skipEmptyRows) {
            return sheetData;
        }
        boolean isValidRow;
        ArrayList<ArrayList<String>> sheetDataUpdated = new ArrayList<>();
        if (sheetData != null) {
            for (ArrayList<String> rowData: sheetData) {
                if (rowData != null) {
                    isValidRow = false;
                    for (String cellData : rowData) {
                        if (cellData != null && !cellData.isEmpty()) {
                            isValidRow = true;
                            break;
                        }
                    }
                    if (isValidRow) {
                        sheetDataUpdated.add(rowData);
                    }
                }
            }
        }
        sheetData = sheetDataUpdated;
        return sheetData;
    }
    public ArrayList<ArrayList<String>> applySkipRowEntry(ArrayList<ArrayList<String>> sheetData,
                                                      ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null || sheetData == null) {
            return sheetData;
        }
        int lastRowIndex = sheetData.size()-1;
        ArrayList<Integer> skipRowsIndex = this.getSkipRowIndexes(excelDataConfigById, lastRowIndex);
        if (skipRowsIndex == null || skipRowsIndex.size() == 0) {
            return sheetData;
        }
        ArrayList<ArrayList<String>> sheetDataUpdated = new ArrayList<>();
        for (int i=0; i<=lastRowIndex; i++) {
            if (skipRowsIndex.contains(i)) {
                continue;
            }
            sheetDataUpdated.add(sheetData.get(i));
        }
        sheetData = sheetDataUpdated;
        return sheetData;
    }
    private ArrayList<Integer> getSkipRowIndexes(ExcelDataConfig excelDataConfigById, int lastRowIndex) {
        ArrayList<Integer> result = new ArrayList<>();
        if (excelDataConfigById == null || lastRowIndex < 1) {
            return null;
        }
        ArrayList<ArrayList<Integer>> skipRowIndex = excelDataConfigById.getSkipRowIndex();
        Integer firstIndex, lastIndex;
        if (skipRowIndex == null) {
            return null;
        }
        for (ArrayList<Integer> indexes : skipRowIndex) {
            if (indexes != null && indexes.size() == 2) {
                firstIndex = indexes.get(0);
                lastIndex = indexes.get(1);
                if (firstIndex == null || lastIndex == null || firstIndex < 0) {
                    // Wrong configuration, all rows are required
                    return null;
                } else if (lastIndex == -1) {
                    // All rows after firstIndex are not required
                    if (firstIndex <= lastRowIndex) {
                        for(int i=firstIndex; i<=lastRowIndex; i++) {
                            if (!result.contains(i)) {
                                result.add(i);
                            }
                        }
                    }
                } else if (firstIndex <= lastIndex) {
                    for(int i=firstIndex; i<=lastIndex; i++) {
                        if (i>lastRowIndex) {
                            break;
                        }
                        if (!result.contains(i)) {
                            result.add(i);
                        }
                    }
                }
            }
        }
        return result;
    }
    public void insertData(ArrayList<ArrayList<String>> sheetData, int rowIndex, int colIndex, String cellData)
            throws AppException {
        if (sheetData == null) {
            throw new AppException(ErrorCodes.SERVER_ERROR);
        }
        if (sheetData.size() <= rowIndex) {
            for (int i=sheetData.size(); i<=rowIndex; i++) {
                sheetData.add(new ArrayList<>());
            }
        }
        ArrayList<String> rowData = sheetData.get(rowIndex);
        if (rowData == null) {
            throw new AppException(ErrorCodes.SERVER_ERROR);
        }
        if (rowData.size() <= colIndex) {
            for (int i=rowData.size(); i<colIndex; i++) {
                rowData.add(AppConstant.EmptyStr);
            }
        }
        rowData.add(cellData);
        sheetData.set(rowIndex, rowData);
    }
    public void copyCellDataIndex(ArrayList<ArrayList<String>> sheetData, ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null || sheetData == null) {
            return;
        }
        ArrayList<Integer> copyCellDataIndex = excelDataConfigById.getCopyCellDataIndex();
        HashMap<Integer, String> previousCellData = new HashMap<>();
        if (copyCellDataIndex == null) {
            return;
        }
        for(Integer index: copyCellDataIndex) {
            if (index != null && index >= 0) {
                previousCellData.put(index, "");
            }
        }
        for(ArrayList<String> rowData: sheetData) {
            if (rowData != null) {
                for(Integer colIndex: copyCellDataIndex) {
                    if (colIndex == null || colIndex < 0) {
                        continue;
                    }
                    if (colIndex < rowData.size()) {
                        if (rowData.get(colIndex) != null && !rowData.get(colIndex).isEmpty()) {
                            previousCellData.put(colIndex, rowData.get(colIndex));
                        }
                    }
                    if (colIndex >= rowData.size()) {
                        for (int i=rowData.size(); i<=colIndex; i++) {
                            rowData.add("");
                        }
                    }
                    rowData.set(colIndex, previousCellData.get(colIndex));
                }
            }
        }
    }
    private ArrayList<String> appendCellDataIndex(ArrayList<String> rowData, ArrayList<String> rowDataFinal,
                                     ArrayList<CellMapping> cellMappings,
                                     ArrayList<ArrayList<Integer>> appendCellDataIndex) {
        if (appendCellDataIndex == null) {
            if (cellMappings != null) {
                return rowDataFinal;
            } else {
                return rowData;
            }
        }
        Integer startIndex, endIndex;
        for (ArrayList<Integer> indexRange: appendCellDataIndex) {
            if (indexRange == null || indexRange.size() != 2 ||
                    indexRange.get(0) == null || indexRange.get(1) == null) {
                continue;
            }
            startIndex = indexRange.get(0);
            endIndex = indexRange.get(1);
            if (startIndex > endIndex || startIndex >= rowData.size()) {
                continue;
            }
            if (endIndex == -1 || endIndex >= rowData.size()) {
                endIndex = rowData.size()-1;
            }
            for(int i=startIndex; i<=endIndex; i++) {
                rowDataFinal.add(rowData.get(i));
            }
        }
        return rowDataFinal;
    }
    public ArrayList<ArrayList<String>> applyCellMapping(ArrayList<ArrayList<String>> sheetData,
                                                         ExcelDataConfig excelDataConfigById) {
        if (sheetData == null || excelDataConfigById == null) {
            return sheetData;
        }
        ArrayList<CellMapping> cellMappings = excelDataConfigById.getCellMapping();
        ArrayList<ArrayList<Integer>> appendCellDataIndex = excelDataConfigById.getAppendCellDataIndex();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        CellMapping cellMapping;
        ArrayList<CellMappingData> cellsMappingData;
        Integer colIndex;
        String defaultCellData, cellData, value;
        ArrayList<String> rowDataFinal, range;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData != null) {
                rowDataFinal = new ArrayList<>();
                if (cellMappings != null) {
                    for (CellMapping mapping : cellMappings) {
                        cellData = "";
                        cellMapping = mapping;
                        colIndex = cellMapping.getCol_index();
                        defaultCellData = cellMapping.getDefaultCellData();
                        cellsMappingData = cellMapping.getMappingData();
                        if (defaultCellData != null) {
                            cellData = defaultCellData;
                        }
                        if (colIndex != null && colIndex >= 0 && rowData.size() > colIndex) {
                            cellData = rowData.get(colIndex);
                        }
                        if (cellsMappingData != null) {
                            for (CellMappingData cellMappingData : cellsMappingData) {
                                if (cellMappingData != null) {
                                    colIndex = cellMappingData.getCol_index();
                                    value = cellMappingData.getValue();
                                    range = cellMappingData.getRange();
                                    if (value == null) {
                                        value = "";
                                    }
                                    if (colIndex != null && range != null) {
                                        if (colIndex >= 0 && colIndex < rowData.size()
                                                && range.contains(rowData.get(colIndex))) {
                                            cellData = value;
                                        }
                                    }
                                }
                            }
                        }
                        rowDataFinal.add(cellData);
                    }
                }
                rowDataFinal = this.appendCellDataIndex(rowData, rowDataFinal, cellMappings, appendCellDataIndex);
                result.add(rowDataFinal);
            }
        }
        return result;
    }
}
