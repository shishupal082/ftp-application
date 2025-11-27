package com.project.ftp.bridge.service;

import com.project.ftp.bridge.config.MappingDataType;
import com.project.ftp.bridge.obj.yamlObj.*;
import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.yamlObj.TableConfiguration;
import com.project.ftp.service.FileService;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class ExcelToCsvDataConvertServiceV2 {
    final static Logger logger = LoggerFactory.getLogger(ExcelToCsvDataConvertServiceV2.class);
    final FileService fileService;
    public ExcelToCsvDataConvertServiceV2() {
        this.fileService = new FileService();
    }
    private boolean isSkipEmptyRows(ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null) {
            return true;
        }
        Boolean skipEmptyRows = excelDataConfigById.getSkipEmptyRows();
        if (skipEmptyRows == null) {
            return true;
        }
        return skipEmptyRows;
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
                        if (!str.isEmpty()) {
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
    private String applyBasicFormatOnCellData(String cellData, boolean isOnlyTrim, String configCommaReplacer) {
        if (cellData == null) {
            return null;
        }
        if (isOnlyTrim) {
            return cellData.trim();
        }
        String commaReplacer = AppConstant.threeDotDelimiter;
        if (configCommaReplacer != null) {
            commaReplacer = configCommaReplacer;
        }
        cellData = cellData.replaceAll("\r\n", AppConstant.colonDelimiter);
        cellData = cellData.replaceAll("\n", AppConstant.colonDelimiter);
        cellData = cellData.replaceAll("\r", AppConstant.EmptyStr);
        cellData = cellData.replaceAll(AppConstant.commaDelimiter, commaReplacer);
        return cellData.trim();
    }
    public ArrayList<ArrayList<String>> formatCellData(ArrayList<ArrayList<String>> sheetData,
                                                       ExcelDataConfig excelDataConfigById) {
        String cellData;
        ArrayList<String> temp, rowData;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        int i, lastValidIndex, lastRowIndex=0;
        String configCommaReplacer = null;
        if (excelDataConfigById != null) {
            configCommaReplacer = excelDataConfigById.getCommaReplacer();
        }
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
                            cellData = applyBasicFormatOnCellData(cellData, false, configCommaReplacer);
                            if (!cellData.isEmpty()) {
                                lastValidIndex = i;
                            }
                        }
                        rowData.set(i, cellData);
                    }
                    for(i=0; i<=lastValidIndex; i++) {
                        temp.add(rowData.get(i));
                    }
                }
                if (!temp.isEmpty()) {
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
        boolean skipEmptyRows = this.isSkipEmptyRows(excelDataConfigById);
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
//        logger.info("sheetData size before skipRowCriteria: {}", sheetData.size());
        int i;
        Integer colIndex;
        String cellData;
        ArrayList<String> rowData;
        ArrayList<Integer> removeIndex = new ArrayList<>();
        for(i=0; i<sheetData.size(); i++) {
            rowData = sheetData.get(i);
            if (rowData == null) {
                continue;
            }
            for(SkipRowCriteria skipRowCriteria: skipRowCriteriaList) {
                if (skipRowCriteria == null) {
                    continue;
                }
                colIndex = skipRowCriteria.getCol_index();
                if (colIndex == null || colIndex < 0) {
                    continue;
                }
                if (colIndex >= rowData.size()) {
                    cellData = "";
                } else {
                    cellData = rowData.get(colIndex);
                }
                if (this.isValidCellData(cellData, skipRowCriteria)) {
                    removeIndex.add(i);
                }
            }
        }
        for(i=0; i<sheetData.size(); i++) {
            if (removeIndex.contains(i)) {
                continue;
            }
            result.add(sheetData.get(i));
        }
//        logger.info("sheetData size after skipRowCriteria: {}", result.size());
        return result;
    }
    private String getColumnNameFromCelIndex(Integer celIndex, ArrayList<String> columnNames) {
        if (celIndex == null || celIndex < 0 || columnNames == null) {
            return null;
        }
        if (columnNames.size() > celIndex) {
            return columnNames.get(celIndex);
        }
        return null;
    }
    private String getCellData(Integer celIndex, ArrayList<String> columnNames, HashMap<String, String> rowData) {
        if (rowData == null) {
            return null;
        }
        String columnName = this.getColumnNameFromCelIndex(celIndex, columnNames);
        if (columnName == null) {
            return null;
        }
        return rowData.get(columnName);
    }
    private boolean isValidCellData(String cellData, SkipRowCriteria skipRowCriteria) {
        if (skipRowCriteria == null) {
            return true;
        }
        String regex;
        ArrayList<String> range, notInRange;
        Boolean isEmpty = skipRowCriteria.getIs_empty();
        regex = skipRowCriteria.getRegex();
        range = skipRowCriteria.getRange();
        notInRange = skipRowCriteria.getNotInRange();
        return this.isValidCondition(cellData, range, notInRange, isEmpty, regex);
    }
    public ArrayList<HashMap<String, String>> applySkipRowCriteriaV2(ArrayList<HashMap<String, String>> tableData,
                                                              TableConfiguration tableConfiguration) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        if (tableData == null || tableConfiguration == null) {
            return tableData;
        }
        ArrayList<String> columnNames = tableConfiguration.getColumnName();
        ArrayList<SkipRowCriteria> skipRowCriteriaList = tableConfiguration.getSkipRowCriteria();
        if (skipRowCriteriaList == null || columnNames == null) {
            return tableData;
        }
//        logger.info("tableData size before skipRowCriteria: {}", tableData.size());
        int i;
        Integer colIndex;
        String cellData;
        HashMap<String, String> rowData;
        ArrayList<Integer> removeIndex = new ArrayList<>();
        for(i=0; i<tableData.size(); i++) {
            rowData = tableData.get(i);
            if (rowData == null) {
                continue;
            }
            for(SkipRowCriteria skipRowCriteria: skipRowCriteriaList) {
                if (skipRowCriteria == null) {
                    continue;
                }
                colIndex = skipRowCriteria.getCol_index();
                if (colIndex == null || colIndex < 0) {
                    continue;
                }
                cellData = this.getCellData(colIndex, columnNames, rowData);
                if (this.isValidCellData(cellData, skipRowCriteria)) {
                    removeIndex.add(i);
                }
            }
        }
        for(i=0; i<tableData.size(); i++) {
            if (removeIndex.contains(i)) {
                continue;
            }
            result.add(tableData.get(i));
        }
//        logger.info("tableData size after skipRowCriteria: {}", result.size());
        return result;
    }
    public ArrayList<ArrayList<String>> applySkipRowEntry(int lineIndex,ArrayList<ArrayList<String>> sheetData,
                                                      ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null || sheetData == null || sheetData.isEmpty()) {
            return sheetData;
        }
        int lastRowIndex = sheetData.size()-1;
        ArrayList<Integer> skipRowsIndex = this.getSkipRowIndexes(lineIndex,excelDataConfigById, lastRowIndex);
        if (skipRowsIndex == null || skipRowsIndex.isEmpty()) {
            return sheetData;
        }
        if (lineIndex >= 0) {
            if (skipRowsIndex.contains(lineIndex)) {
                return null;
            } else {
                return sheetData;
            }
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
    private ArrayList<Integer> getSkipRowIndexes(int lineIndex,ExcelDataConfig excelDataConfigById, int lastRowIndex) {
        ArrayList<Integer> result = new ArrayList<>();
        if (excelDataConfigById == null || lastRowIndex < 0) {
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
                    if (lineIndex >= 0) {
                        if (firstIndex <= lineIndex) {
                            if (!result.contains(lineIndex)) {
                                result.add(lineIndex);
                            }
                        }
                    }
                    if (firstIndex <= lastRowIndex) {
                        for(int i=firstIndex; i<=lastRowIndex; i++) {
                            if (!result.contains(i)) {
                                result.add(i);
                            }
                        }
                    }
                } else if (firstIndex <= lastIndex) {
                    if (lineIndex >= 0) {
                        if (firstIndex <= lineIndex && lastIndex >= lineIndex) {
                            if (!result.contains(lineIndex)) {
                                result.add(lineIndex);
                            }
                        }
                    }
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
            if (cellMappings != null && rowDataFinal != null && !rowDataFinal.isEmpty()) {
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
        if (startIndex >= 0 && endIndex >= 0) {
            if (startIndex <= endIndex && startIndex < subString.length() && endIndex < subString.length()) {
                subString = subString.substring(startIndex, endIndex+1).trim();
            } else {
                subString = AppConstant.EmptyStr;
            }
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
    // For excel, csv and api
    private String getFormatedCellData(String sheetName, String srcFilepath, ArrayList<String> rowData,
                                       String defaultCellData, Integer colIndex, String dateRegex,
                                       int sheetDataIndex, int externalIndex) {
        DateUtilities dateUtilities = new DateUtilities();
        if (AppConstant.NOW.equals(defaultCellData) && dateRegex != null) {
            defaultCellData = dateUtilities.getDateStrFromPattern(dateRegex, defaultCellData);
        }
        String cellData = defaultCellData;
        if (colIndex == null) {
            return cellData;
        } else if (colIndex >= 0) {
            if (colIndex < rowData.size()) {
                cellData = rowData.get(colIndex);
            }
        } else if (colIndex == -1) {
            cellData = defaultCellData;
        } else if (colIndex == -2) {
            cellData = sheetName;
        } else if (colIndex == -3) {
            cellData = this.getFileName(srcFilepath, defaultCellData);
        } else if (colIndex == -4) {
            cellData = srcFilepath;
        } else if (colIndex == -5) {
            if (externalIndex >= 0) {
                cellData = Integer.toString(externalIndex);
            } else {
                cellData = Integer.toString(sheetDataIndex);
            }

        }
        if (cellData == null) {
            cellData = "";
        }
        return cellData;
    }
    // For tableData
    private String getFormatedCellDataV2(String requestTableConfigId, String requestDefaultFilterMappingId,
                                         TableConfiguration tableConfiguration, HashMap<String, String> rowData,
                                       String defaultCellData, Integer colIndex, String dateRegex) {
        if (tableConfiguration == null) {
            return null;
        }
        DateUtilities dateUtilities = new DateUtilities();
        if (AppConstant.NOW.equals(defaultCellData) && dateRegex != null) {
            defaultCellData = dateUtilities.getDateStrFromPattern(dateRegex, defaultCellData);
        }
        String cellData = defaultCellData;
        if (colIndex == null) {
            return cellData;
        } else if (colIndex >= 0) {
            cellData = this.getCellData(colIndex, tableConfiguration.getColumnName(), rowData);
        } else if (colIndex == -1) {
            cellData = defaultCellData;
        } else if (colIndex == -2) {
            cellData = requestTableConfigId;
        } else if (colIndex == -3) {
            cellData = requestDefaultFilterMappingId;
        } else if (colIndex == -4) {
            cellData = tableConfiguration.getTableConfigId();
        } else if (colIndex == -5) {
            cellData = tableConfiguration.getTableName();
        } else if (colIndex == -6) {
            cellData = tableConfiguration.getDbType();
        } else if (colIndex == -7) {
            cellData = tableConfiguration.getDbIdentifier();
        } else if (colIndex == -8) {
            cellData = tableConfiguration.getExcelConfigId();
        }
        return cellData;
    }
    private MappingDataType getMappingDataType(String cellData2,
                                                CellMappingData cellMappingData) {
        Boolean isEmpty = cellMappingData.getIs_empty();
        ArrayList<String> range = cellMappingData.getRange();
        ArrayList<String> notInRange = cellMappingData.getNotInRange();
        String regex = cellMappingData.getRegex();
        ArrayList<Integer> subStringConfig = cellMappingData.getSubStringConfig();
        if (range != null && range.contains(cellData2)) {
            return MappingDataType.Range;
        } else if (notInRange != null && !notInRange.contains(cellData2)) {
            return MappingDataType.NotInRange;
        } else if (isEmpty != null && isEmpty && (cellData2==null || cellData2.isEmpty())) {
            return MappingDataType.IsEmptyTrue;
        } else if (isEmpty != null && !isEmpty && cellData2!=null && !cellData2.isEmpty()) {
            return MappingDataType.IsEmptyFalse;
        } else if (regex != null && StaticService.isPatternMatching(cellData2, regex, false)) {
            return MappingDataType.Regex;
        }
        if (subStringConfig != null) {
            cellData2 = this.getSubStringTextFromCellData(subStringConfig, cellData2);
            if (range != null && range.contains(cellData2)) {
                return MappingDataType.RangeForSubString;
            } else if (notInRange != null && !notInRange.contains(cellData2)) {
                return MappingDataType.NotInRangeForSubString;
            } else if (regex == null) {
                return MappingDataType.SubStringWithRegexNull;
            }
        }
        return null;
    }
    private String getFinalUpdatedCellData(String cellData, String cellData2,
                                           CellMappingData cellMappingData, MappingDataType mappingDataType) {
        DateUtilities dateUtilities = new DateUtilities();
        String value = cellMappingData.getValue();
        ArrayList<Integer> subStringConfig = cellMappingData.getSubStringConfig();
        String dateRegex = cellMappingData.getDateRegex();
        String oldDateText;
        if (AppConstant.ValueSameAsColIndexData.equals(value)) {
            value = cellData;
        } else if (AppConstant.ValueSameAsColIndexData2.equals(value)) {
            value = cellData2;
        }
        if (mappingDataType != null) {
            switch (mappingDataType) {
                case Range:
                case NotInRange:
                    cellData = value;
                    if (subStringConfig != null) {
                        cellData = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                    }
                    break;
                case RangeForSubString:
                case NotInRangeForSubString:
                case IsEmptyTrue:
                case IsEmptyFalse:
                    cellData = value;
                    break;
                case SubStringWithRegexNull:
                    if (subStringConfig != null) {
                        cellData = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                    }
                    break;
                case Regex:
                    if (dateRegex != null) {
                        oldDateText = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                        cellData = dateUtilities.getDateStrInNewPattern(value, dateRegex, oldDateText, oldDateText);
                    } else {
                        cellData = value;
                        if (subStringConfig != null) {
                            cellData = this.getSubStringTextFromCellData(subStringConfig, cellData2);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return cellData;
    }
    public ArrayList<ArrayList<String>> applyCellMapping(ArrayList<ArrayList<String>> sheetData,
                                                         ExcelDataConfig excelDataConfigById,
                                                         String srcFilepath, String sheetName, int externalIndex) {
        if (sheetData == null || excelDataConfigById == null) {
            return sheetData;
        }
        ArrayList<CellMapping> cellMappings = excelDataConfigById.getCellMapping();
        ArrayList<ArrayList<Integer>> appendCellDataIndex = excelDataConfigById.getAppendCellDataIndex();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        CellMapping cellMapping;
        ArrayList<CellMappingData> cellsMappingData;
        Integer colIndex, colIndex2;
        int sheetDataIndex = -1;
        String defaultCellData, cellData, cellData2, dateRegex;
        ArrayList<String> rowDataFinal;
        Boolean rewrite;
        MappingDataType mappingDataType;
        for(ArrayList<String> rowData: sheetData) {
            sheetDataIndex++;
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
                        cellData = this.getFormatedCellData(sheetName, srcFilepath, rowData,
                                defaultCellData, colIndex, dateRegex, sheetDataIndex, externalIndex);
                        if (cellsMappingData != null) {
                            for (CellMappingData cellMappingData : cellsMappingData) {
                                if (cellMappingData != null) {
                                    colIndex2 = cellMappingData.getCol_index();
                                    cellData2 = this.getFormatedCellData(sheetName, srcFilepath,
                                            rowData, cellData, colIndex2, null, sheetDataIndex, externalIndex);
                                    mappingDataType = this.getMappingDataType(cellData2, cellMappingData);
                                    if (mappingDataType != null) {
                                        cellData = this.getFinalUpdatedCellData(cellData, cellData2, cellMappingData, mappingDataType);
                                        break;
                                    }
                                }
                            }
                        }
                        if (rewrite != null && rewrite && colIndex != null && colIndex >= 0) {
                            if (colIndex >= rowData.size() && cellData != null && !cellData.isEmpty()) {
                                for (int i=rowData.size(); i<=colIndex; i++) {
                                    rowData.add("");
                                }
                            }
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
    public ArrayList<HashMap<String, String>> applyCellMappingV2(String requestTableConfigId,
                                                                 String requestDefaultFilterMappingId,
                                                                 ArrayList<HashMap<String, String>> tableData,
                                                                 TableConfiguration tableConfiguration) {
        if (tableData == null || tableConfiguration == null) {
            return tableData;
        }
        ArrayList<String> columnNames = tableConfiguration.getColumnName();
        ArrayList<CellMapping> cellMappings = tableConfiguration.getCellMapping();
        CellMapping cellMapping;
        ArrayList<CellMappingData> cellsMappingData;
        Integer colIndex, colIndex2;
        String newColumnName, defaultCellData, cellData, cellData2, dateRegex;
        Boolean rewrite;
        MappingDataType mappingDataType;
        for(HashMap<String, String> rowData: tableData) {
            if (rowData != null) {
                if (cellMappings != null) {
                    for (CellMapping mapping : cellMappings) {
                        cellMapping = mapping;
                        newColumnName = cellMapping.getNewColumnName();
                        rewrite = cellMapping.getRewrite();
                        colIndex = cellMapping.getCol_index();
                        if (rewrite != null && rewrite) {
                            newColumnName = this.getColumnNameFromCelIndex(colIndex, columnNames);
                        }
                        if (newColumnName == null || newColumnName.isEmpty()) {
                            continue;
                        }
                        defaultCellData = cellMapping.getDefaultCellData();
                        dateRegex = cellMapping.getDateRegex();
                        cellsMappingData = cellMapping.getMappingData();
                        cellData = this.getFormatedCellDataV2(requestTableConfigId, requestDefaultFilterMappingId,
                                tableConfiguration, rowData, defaultCellData, colIndex, dateRegex);
                        if (cellsMappingData != null) {
                            for (CellMappingData cellMappingData : cellsMappingData) {
                                if (cellMappingData != null) {
                                    colIndex2 = cellMappingData.getCol_index();
                                    cellData2 = this.getFormatedCellDataV2(requestTableConfigId,
                                            requestDefaultFilterMappingId, tableConfiguration, rowData, cellData,
                                            colIndex2, null);
                                    mappingDataType = this.getMappingDataType(cellData2, cellMappingData);
                                    if (mappingDataType != null) {
                                        cellData = this.getFinalUpdatedCellData(cellData, cellData2, cellMappingData, mappingDataType);
                                        break;
                                    }
                                }
                            }
                        }
                        rowData.put(newColumnName, cellData);
                    }
                }
            }
        }
        return tableData;
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
        String finalCellData;
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
                    finalCellData = StaticService.replaceString(rowData.get(index), find, replace);
                    finalCellData = this.applyBasicFormatOnCellData(finalCellData, true, null);
                    rowData.set(index, finalCellData);
                }
            }
        }
    }
    private boolean isValidCondition(String cellData, ArrayList<String> range, ArrayList<String> notInRange,
                                     Boolean isEmpty, String regex) {
        if (range != null && range.contains(cellData)) {
            return true;
        }
        if (notInRange != null && !notInRange.contains(cellData)) {
            return true;
        }
        if (regex != null && StaticService.isPatternMatching(cellData, regex, false)) {
            return true;
        }
        if (isEmpty != null) {
            if (isEmpty) {
                return cellData.isEmpty();
            } else {
                return !cellData.isEmpty();
            }
        }
        return false;
    }
    private boolean isValidMergeColumnConfigCondition(ArrayList<String> rowData,
                                                      ArrayList<MergeConfigCondition> conditions) {
        if (conditions == null) {
            return true;
        }
        Integer colIndex;
        ArrayList<String> range;
        ArrayList<String> notInRange;
        Boolean isEmpty;
        String regex;
        String cellData;
        boolean currentStatus;
        for (MergeConfigCondition condition: conditions) {
            if (condition == null) {
                continue;
            }
            colIndex = condition.getCol_index();
            if (colIndex == null) {
                continue;
            }
            if (colIndex < 0 || colIndex >= rowData.size()) {
                continue;
            }
            cellData = rowData.get(colIndex);
            range = condition.getRange();
            notInRange = condition.getNotInRange();
            regex = condition.getRegex();
            isEmpty = condition.getIs_empty();
            currentStatus = this.isValidCondition(cellData, range, notInRange, isEmpty, regex);
            if (currentStatus) {
                return true;
            }
        }
        return false;
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
        ArrayList<String> tempFinalData;
        ArrayList<String> rowData, updatedRowData;
        ArrayList<MergeConfigCondition> conditions = mergeColumnConfig.getConditions();
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
            if (conditions != null) {
                if (!this.isValidMergeColumnConfigCondition(rowData, conditions)) {
                    continue;
                }
            }
            tempFinalData = new ArrayList<>();
            for (Integer index: sourceIndex) {
                if (index != null && index >= 0 && index < rowData.size()) {
                    tempFinalData.add(rowData.get(index));
                }
            }
            if (i < updatedSheetData.size()) {
                updatedRowData = updatedSheetData.get(i);
                if (finalIndex >= updatedRowData.size()) {
                    for(j=updatedRowData.size(); j<=finalIndex; j++) {
                        updatedRowData.add(AppConstant.EmptyStr);
                    }
                }
                updatedRowData.set(finalIndex, String.join(join, tempFinalData));
            } else {
                logger.info("sheetData and updatedSheetData mismatch in row.");
            }
        }
    }
    public ArrayList<ArrayList<String>> applyMergeColumnMapping(ArrayList<ArrayList<String>> sheetData,
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
                    if (!sourceIndex.isEmpty()) {
                        tempIndex = sourceIndex.get(sourceIndex.size()-1);
                        for (int j=tempIndex+1; j < maxColCount; j++) {
                            sourceIndex.add(j);
                        }
                    }
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
    public ArrayList<ArrayList<String>> applyHeadingField(ArrayList<ArrayList<String>> sheetData, ExcelDataConfig excelDataConfigById) {
        if (excelDataConfigById == null || sheetData == null || sheetData.isEmpty()) {
            return sheetData;
        }
        ArrayList<String> headingField = excelDataConfigById.getHeadingField();
        if (headingField == null || headingField.isEmpty()) {
            return sheetData;
        }
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(headingField);
        result.addAll(sheetData);
        return result;
    }
}
