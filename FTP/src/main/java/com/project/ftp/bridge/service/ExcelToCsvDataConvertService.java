package com.project.ftp.bridge.service;

import com.project.ftp.bridge.obj.yamlObj.*;
import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.FileService;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class ExcelToCsvDataConvertService {
    final static Logger logger = LoggerFactory.getLogger(ExcelToCsvDataConvertService.class);
    final FileService fileService;
    public ExcelToCsvDataConvertService() {
        this.fileService = new FileService();
    }
    public ArrayList<ArrayList<String>> removeFirstEmptyRow(ArrayList<ArrayList<String>> csvData) {
        if (csvData == null) {
            return null;
        }
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        String str;
        boolean isEmptyRow = true;
        for(ArrayList<String> strings: csvData) {
            if (strings != null) {
                for(int i=0; i<strings.size(); i++) {
                    str = strings.get(i);
                    if (str != null) {
                        str = str.trim();
                        if (str.length() > 0) {
                            isEmptyRow = false;
                        }
                    }
                    strings.set(i, str);
                }
                if (!isEmptyRow) {
                    result.add(strings);
                }
            }
        }
        return result;
    }
    public ArrayList<ArrayList<String>> formatCellData(ArrayList<ArrayList<String>> sheetData) {
        String cellData;
        ArrayList<String> temp, rowData;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        int i, lastValidIndex, lastRowIndex=0;
        if (sheetData != null) {
            sheetData = this.removeFirstEmptyRow(sheetData);
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
                if (i < sheetData.size()) {
                    result.add(sheetData.get(i));
                }
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
    public ArrayList<ArrayList<String>> applySkipRowCriteria (ArrayList<ArrayList<String>> sheetData,
                                                              ExcelDataConfig excelDataConfigById) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        if (excelDataConfigById == null || sheetData == null) {
            return sheetData;
        }
        ArrayList<SkipRowCriteria> skipRowCriteriaList = excelDataConfigById.getSkipRowCriteria();
        if (skipRowCriteriaList == null) {
            return sheetData;
        }
        logger.info("SheetData size before skipRowCriteria: {}", sheetData.size());
        int i;
        Integer colIndex;
        Boolean isEmpty;
        String regex, notRegex, cellData;
        ArrayList<String> range, notInRange, rowData;
        ArrayList<Integer> removeIndex = new ArrayList<>();
        for(SkipRowCriteria skipRowCriteria: skipRowCriteriaList) {
            if (skipRowCriteria == null) {
                continue;
            }
            colIndex = skipRowCriteria.getCol_index();
            if (colIndex == null) {
                continue;
            }
            isEmpty = skipRowCriteria.getIs_empty();
            regex = skipRowCriteria.getRegex();
            notRegex = skipRowCriteria.getNotRegex();
            range = skipRowCriteria.getRange();
            notInRange = skipRowCriteria.getNotInRange();
            for(i=0; i<sheetData.size(); i++) {
                rowData = sheetData.get(i);
                if (rowData == null) {
                    removeIndex.add(i);
                    continue;
                }
                if (colIndex < 0) {
                    continue;
                }
                if (colIndex >= rowData.size()) {
                    cellData = "";
                } else {
                    cellData = rowData.get(colIndex);
                }
                if (isEmpty != null && isEmpty) {
                    if (cellData == null || cellData.isEmpty()) {
                        removeIndex.add(i);
                        continue;
                    }
                }
                if (range != null && range.contains(cellData)) {
                    removeIndex.add(i);
                    continue;
                }
                if (notInRange != null && !notInRange.contains(cellData)) {
                    removeIndex.add(i);
                    continue;
                }
                if (regex != null) {
                    if (StaticService.isPatternMatching(cellData, regex, false)) {
                        removeIndex.add(i);
                        continue;
                    }
                }
                if (notRegex != null) {
                    if (!StaticService.isPatternMatching(cellData, notRegex, false)) {
                        removeIndex.add(i);
                    }
                }
            }
        }
        for(i=0; i<sheetData.size(); i++) {
            if (removeIndex.contains(i)) {
                continue;
            }
            result.add(sheetData.get(i));
        }
        logger.info("SheetData size after skipRowCriteria: {}", result.size());
        return result;
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
            if (cellMappings != null && rowDataFinal != null && rowDataFinal.size() > 0) {
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
            if (startIndex < 0 || startIndex >= rowData.size() || endIndex < -1) {
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
    private String getSubStringTextFromCellData(ArrayList<Integer> subStringConfig, String cellData) {
        String subString = cellData;
        Integer start, length, end;
        int startIndex = -1, endIndex = -1;
        if (subString == null || subStringConfig == null || subStringConfig.size() < 3) {
            return subString;
        }
        subString = subString.trim();
        if (subString.isEmpty()) {
            return subString;
        }
        start = subStringConfig.get(0);
        length = subStringConfig.get(1);
        end = subStringConfig.get(2);
        if (start == null || length == null || end == null) {
            return subString;
        }
        if (start >= 0) {
            startIndex = start;
            if (length > 0) {
                endIndex = start + length - 1;//endIndex >= 0 && subString.length() >= 1
                if (endIndex >= subString.length()) {
                    endIndex = subString.length() - 1;
                }
            } else if (end >= 0) {
                endIndex = subString.length() - end - 1;
            }
        } else if (end >= 0 && length > 0) {
            startIndex = subString.length()-length-end;
            endIndex = subString.length()-end-1;
        }
        if (startIndex >= 0 && endIndex >= 0 && startIndex < endIndex && startIndex < subString.length() && endIndex < subString.length()) {
            subString = subString.substring(startIndex, endIndex+1);
        }
        return subString;
    }
    private String getFileName(String srcFilepath, String defaultCellData) {
        String filename = "";
        if (defaultCellData != null) {
            filename = defaultCellData;
        }
        PathInfo pathInfo = fileService.getPathInfo(srcFilepath);
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            filename = pathInfo.getFilenameWithoutExt();
        }
        return filename;
    }
    private String getFormatedCellData(String sheetName, String srcFilepath, ArrayList<String> rowData,
                                       String defaultCellData, Integer colIndex, String dateRegex) {
        DateUtilities dateUtilities = new DateUtilities();
        String result = null;
        if (AppConstant.NOW.equals(defaultCellData) && dateRegex != null) {
            defaultCellData = dateUtilities.getDateStrFromPattern(dateRegex, defaultCellData);
        }
        result = defaultCellData;
        if (colIndex != null && colIndex >= 0 && rowData.size() > colIndex) {
            result = rowData.get(colIndex);
        } else if (colIndex != null && colIndex == -2) {
            result = sheetName;
        }else if (colIndex != null && colIndex == -3) {
            result = this.getFileName(srcFilepath, defaultCellData);
        }
        if (result == null) {
            result = "";
        }
        return result;
    }
    private String getUpdatedCellData(String sheetName, String srcFilepath, ArrayList<String> rowData,
                                      String cellData, CellMappingData cellMappingData) {
        if (cellMappingData == null) {
            return cellData;
        }
        DateUtilities dateUtilities = new DateUtilities();
        Integer colIndex2 = cellMappingData.getCol_index();
        String value = cellMappingData.getValue();
        ArrayList<String> range = cellMappingData.getRange();
        String regex = cellMappingData.getRegex();
        ArrayList<Integer> subStringConfig = cellMappingData.getSubStringConfig();
        String dateRegex = cellMappingData.getDateRegex();
        String dateText;
        String cellData2 = this.getFormatedCellData(sheetName, srcFilepath, rowData, cellData, colIndex2, null);
        if (colIndex2 != null && colIndex2 != -1 && colIndex2 >= -3) {
            if (dateRegex != null) {
                if (regex != null && StaticService.isPatternMatching(cellData2, regex, false)) {
                    dateText = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                    cellData = dateUtilities.getDateStrInNewPattern(value, dateRegex, dateText, dateText);
                }
            } else if (range != null && range.contains(cellData2)) {
                cellData = value;
                if (subStringConfig != null) {
                    cellData = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                }
            } else if (regex != null && StaticService.isPatternMatching(cellData2, regex, false)) {
                cellData = value;
                if (subStringConfig != null) {
                    cellData = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                }
            }
        }
        return cellData;
    }
    public ArrayList<ArrayList<String>> applyCellMapping(ArrayList<ArrayList<String>> sheetData,
                                                         ExcelDataConfig excelDataConfigById,
                                                         String srcFilepath, String sheetName) {
        if (sheetData == null || excelDataConfigById == null) {
            return sheetData;
        }
        ArrayList<CellMapping> cellMappings = excelDataConfigById.getCellMapping();
        ArrayList<ArrayList<Integer>> appendCellDataIndex = excelDataConfigById.getAppendCellDataIndex();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        CellMapping cellMapping;
        ArrayList<CellMappingData> cellsMappingData;
        Integer colIndex;
        String defaultCellData, cellData, cellData2, dateRegex;
        ArrayList<String> rowDataFinal;
        Boolean rewrite;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData != null) {
                rowDataFinal = new ArrayList<>();
                if (cellMappings != null) {
                    for (CellMapping mapping : cellMappings) {
                        cellMapping = mapping;
                        colIndex = cellMapping.getCol_index();
                        defaultCellData = cellMapping.getDefaultCellData();
                        dateRegex = cellMapping.getDateRegex();
                        cellsMappingData = cellMapping.getMappingData();
                        rewrite = cellMapping.getRewrite();
                        cellData = this.getFormatedCellData(sheetName, srcFilepath, rowData, defaultCellData, colIndex, dateRegex);
                        if (cellsMappingData != null) {
                            for (CellMappingData cellMappingData : cellsMappingData) {
                                if (cellMappingData != null) {
                                    cellData2 = this.getUpdatedCellData(sheetName, srcFilepath,
                                            rowData, cellData, cellMappingData);
                                    if (cellData2 != null && !cellData2.equals(cellData)) {
                                        cellData = cellData2;
                                        break;
                                    }
                                }
                            }
                        }
                        if (rewrite != null && rewrite && colIndex != null && colIndex >= 0) {
                            if (colIndex < rowData.size()) {
                                rowData.set(colIndex, cellData);
                            }
                        } else {
                            rowDataFinal.add(cellData);
                        }
                    }
                }
                rowDataFinal = this.appendCellDataIndex(rowData, rowDataFinal, cellMappings, appendCellDataIndex);
                result.add(rowDataFinal);
            }
        }
        return result;
    }
    public void applyReplaceCellString(ArrayList<ArrayList<String>> sheetData, ExcelDataConfig excelDataConfigById) {
        if (sheetData == null || excelDataConfigById == null) {
            return;
        }
        ArrayList<ReplaceCellDataMapping> replaceCellDataMappings = excelDataConfigById.getReplaceCellString();
        if (replaceCellDataMappings == null) {
            return;
        }
        Integer index;
        String find, replace;
        for(ReplaceCellDataMapping replaceCellDataMapping: replaceCellDataMappings) {
            if (replaceCellDataMapping == null) {
                continue;
            }
            index = replaceCellDataMapping.getIndex();
            find = replaceCellDataMapping.getFind();
            replace = replaceCellDataMapping.getReplace();
            if (index == null || find == null || replace == null) {
                continue;
            }
            for(ArrayList<String> rowData: sheetData) {
                if (rowData == null) {
                    continue;
                }
                if (index < rowData.size()) {
                    rowData.set(index, StaticService.replaceString(rowData.get(index), find, replace));
                }
            }
        }
    }
    private void applyCellMerging(ArrayList<ArrayList<String>> sheetData,
                                  ArrayList<ArrayList<String>> updatedSheetData,
                                  MergeColumnConfig mergeColumnConfig) {
        if (sheetData == null || mergeColumnConfig == null) {
            return;
        }
        Integer finalIndex = mergeColumnConfig.getFinalIndex();
        ArrayList<Integer> sourceIndex = mergeColumnConfig.getSourceIndex();
        String join = mergeColumnConfig.getJoin();
        ArrayList<String> temp;
        ArrayList<String> rowData, updatedRowData;
        if (join == null) {
            join = "";
        }
        if (finalIndex == null || finalIndex < 0 || sourceIndex == null) {
            return;
        }
        int j;
        for(int i=0; i<sheetData.size(); i++) {
            rowData = sheetData.get(i);
            if (rowData == null) {
                continue;
            }
            temp = new ArrayList<>();
            for (Integer index: sourceIndex) {
                if (index != null && index >= 0 && index < rowData.size()) {
                    temp.add(rowData.get(index));
                }
            }
            if (i < updatedSheetData.size()) {
                updatedRowData = updatedSheetData.get(i);
                if (finalIndex >= updatedRowData.size()) {
                    for(j=updatedRowData.size(); j<=finalIndex; j++) {
                        updatedRowData.add(AppConstant.EmptyStr);
                    }
                }
                updatedRowData.set(finalIndex, String.join(join, temp));
            } else {
                logger.info("sheetData and updatedSheetData mismatch in row.");
            }
        }
    }
    public ArrayList<ArrayList<String>> applyColumnMapping(ArrayList<ArrayList<String>> sheetData,
                                                           ExcelDataConfig excelDataConfigById) {
        if (sheetData == null || excelDataConfigById == null) {
            return sheetData;
        }
        ArrayList<MergeColumnConfig> mergeColumnConfigs = excelDataConfigById.getMergeColumnConfig();
        if (mergeColumnConfigs == null) {
            return  sheetData;
        }
        int maxColCount = 0;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData.size() > maxColCount) {
                maxColCount = rowData.size();
            }
        }
        Integer tempIndex;
        ArrayList<Integer> sourceIndex, tempSourceIndex;
        for (MergeColumnConfig mergeColumnConfig: mergeColumnConfigs) {
            if (mergeColumnConfig == null) {
                continue;
            }
            tempSourceIndex = mergeColumnConfig.getSourceIndex();
            sourceIndex = new ArrayList<>();
            if (tempSourceIndex == null) {
                continue;
            }
            for(Integer index: tempSourceIndex) {
                if (index == null) {
                    continue;
                }
                if (index >= 0) {
                    sourceIndex.add(index);
                } else if (index == -1) {
                    if (sourceIndex.size() > 0) {
                        tempIndex = sourceIndex.get(sourceIndex.size()-1);
                        for (int j=tempIndex+1; j < maxColCount; j++) {
                            sourceIndex.add(j);
                        }
                    }
                    mergeColumnConfig.setSourceIndex(sourceIndex);
                    break;
                }
            }
            mergeColumnConfig.setSourceIndex(sourceIndex);
        }
        ArrayList<String> updatedRowData;
        ArrayList<ArrayList<String>> updatedSheetData = new ArrayList<>();
        for(ArrayList<String> rowData: sheetData) {
            updatedRowData = new ArrayList<>(rowData);
            updatedSheetData.add(updatedRowData);
        }
        for (MergeColumnConfig mergeColumnConfig: mergeColumnConfigs) {
            if (mergeColumnConfig == null) {
                continue;
            }
            this.applyCellMerging(sheetData, updatedSheetData, mergeColumnConfig);
        }
        return updatedSheetData;
    }
    public ArrayList<ArrayList<String>> applyRemoveColumnConfig(ArrayList<ArrayList<String>> sheetData,
                                                                ExcelDataConfig excelDataConfigById) {
        if (sheetData == null || excelDataConfigById == null) {
            return sheetData;
        }
        ArrayList<Integer> removeColumnConfig = excelDataConfigById.getRemoveColumnConfig();
        if (removeColumnConfig == null) {
            return sheetData;
        }
        int i;
        ArrayList<Integer> finalRemoveIndex = new ArrayList<>();
        int maxColCount = 0, previousIndex = -1;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData.size() > maxColCount) {
                maxColCount = rowData.size();
            }
        }
        for(Integer index: removeColumnConfig) {
            if (index >= 0) {
                previousIndex = index;
                if (!finalRemoveIndex.contains(index)) {
                    finalRemoveIndex.add(index);
                }
                continue;
            }
            if (index == -1) {
                for(i=previousIndex+1; i<maxColCount; i++) {
                    if (!finalRemoveIndex.contains(i)) {
                        finalRemoveIndex.add(i);
                    }
                }
                break;
            }
        }
        ArrayList<ArrayList<String>> finalSheetData = new ArrayList<>();
        ArrayList<String> finalRowData;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData == null) {
                continue;
            }
            finalRowData = new ArrayList<>();
            for (i=0; i<rowData.size(); i++) {
                if (!finalRemoveIndex.contains(i)) {
                    finalRowData.add(rowData.get(i));
                }
            }
            finalSheetData.add(finalRowData);
        }
        return finalSheetData;
    }
    public ArrayList<ArrayList<String>> applyUniqueEntry(ArrayList<ArrayList<String>> sheetData,
                                                         ExcelDataConfig excelDataConfigById,
                                                         ArrayList<String> tempUniqueStrings) {
        if (sheetData == null || excelDataConfigById == null) {
            return sheetData;
        }
        ArrayList<Integer> uniqueEntry = excelDataConfigById.getUniqueEntry();
        if (uniqueEntry == null) {
            return sheetData;
        }
        if (tempUniqueStrings == null) {
            tempUniqueStrings = new ArrayList<>();
        }
        StringBuilder temp;
        ArrayList<ArrayList<String>> finalSheetData = new ArrayList<>();
        boolean isUniqueRow;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData == null) {
                continue;
            }
            isUniqueRow = false;
            temp = new StringBuilder();
            for(Integer i: uniqueEntry) {
                if (i == null) {
                    continue;
                }
                if (i < rowData.size()) {
                    temp.append(rowData.get(i));
                } else {
                    isUniqueRow = true;
                    break;
                }
            }
            if (!isUniqueRow) {
                if (!tempUniqueStrings.contains(temp.toString())) {
                    isUniqueRow = true;
                    tempUniqueStrings.add(temp.toString());
                }
            }
            if (isUniqueRow) {
                finalSheetData.add(rowData);
            }
        }
        return finalSheetData;
    }
}
