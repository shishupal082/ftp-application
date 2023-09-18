package com.project.ftp.bridge.service;

import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MSExcelServiceUtils {
    final static Logger logger = LoggerFactory.getLogger(MSExcelServiceUtils.class);
    private FormulaEvaluator evaluator;
    public MSExcelServiceUtils() {
        evaluator = null;
    }
    public ArrayList<ArrayList<String>> readExcelSheetData(String srcFilepath, String sheetName,
                                                           ExcelDataConfig excelDataConfigById) throws AppException {
        if (sheetName == null || excelDataConfigById == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
        ExcelToCsvDataConvertService excelToCsvDataConvertService = new ExcelToCsvDataConvertService();
        String cellData;
        FileInputStream file =null;
        boolean isError = false;
        File file1 = new File(srcFilepath);
        if (!file1.isFile()) {
            logger.info("Source excel filepath: {} does not exist, {}", srcFilepath, excelDataConfigById);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        try {
            file = new FileInputStream(file1);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cellData = this.parseCellData(cell, excelDataConfigById);
                    excelToCsvDataConvertService.insertData(sheetData, row.getRowNum(), cell.getColumnIndex(), cellData);
                }
            }
            file.close();
        } catch (Exception e) {
            logger.info("Error in reading excel filepath: {}, sheetName: {}, {}", srcFilepath, sheetName, excelDataConfigById);
            isError = true;
        }
        try {
            if (file != null) {
                file.close();
            }
            if (isError) {
                throw new AppException(ErrorCodes.SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.info("Error in closing excel filepath: {}, sheetName: {}, {}", srcFilepath, sheetName, excelDataConfigById);
            throw new AppException(ErrorCodes.SERVER_ERROR);
        }
        return sheetData;
    }
    private String convertNumericCellToString(Cell cell, ExcelDataConfig excelDataConfigById) {
        String result;
        double numericCellData = cell.getNumericCellValue();
        String dateFormat, timeFormat, dateTimeFormat;
        dateFormat = AppConstant.DATE_FORMAT;
        timeFormat = AppConstant.TIME_FORMAT2;
        dateTimeFormat = AppConstant.DateTimeFormat7;
        DateUtilities dateUtilities = new DateUtilities();
        if (excelDataConfigById != null) {
            if (excelDataConfigById.getDateFormat() != null) {
                dateFormat = excelDataConfigById.getDateFormat();
            }
            if (excelDataConfigById.getTimeFormat() != null) {
                timeFormat = excelDataConfigById.getTimeFormat();
            }
            if (excelDataConfigById.getDateTimeFormat() != null) {
                dateTimeFormat = excelDataConfigById.getDateTimeFormat();
            }
        }
        if (DateUtil.isCellDateFormatted(cell)) {
            Date dateCellValue = cell.getDateCellValue();
            if (numericCellData < 1) {
                result = dateUtilities.getDateStrFromDateObj(timeFormat, dateCellValue);
            } else if (numericCellData % 1 == 0) {
                result = dateUtilities.getDateStrFromDateObj(dateFormat, dateCellValue);
            } else {
                result = dateUtilities.getDateStrFromDateObj(dateTimeFormat, dateCellValue);
            }
        } else {
            if (numericCellData % 1 == 0) {
                result = Integer.toString((int) numericCellData);
            } else {
                result = Double.toString(numericCellData);
            }
        }
        return result;
    }
    private String readCellData(CellType cellType, Cell cell, ExcelDataConfig excelDataConfigById) {
        String result = "";
        switch (cellType) {
            case NUMERIC:
                result = this.convertNumericCellToString(cell, excelDataConfigById);
                break;
            case STRING:
                result = cell.getStringCellValue();
                break;
            case BOOLEAN:
                result = Boolean.toString(cell.getBooleanCellValue());
                break;
            case FORMULA:
                if (evaluator != null) {
                    switch (evaluator.evaluateFormulaCell(cell)) {
                        case BOOLEAN:
                            result = Boolean.toString(cell.getBooleanCellValue());
                            break;
                        case NUMERIC:
                            result = this.convertNumericCellToString(cell, excelDataConfigById);
                            break;
                        case STRING:
                            result = cell.getStringCellValue();
                            break;
                    }
                }
                break;
            case ERROR:
            case BLANK:
            default: break;
        }
        return result;
    }
    private String parseCellData(Cell cell, ExcelDataConfig excelDataConfigById) {
        CellType cellType;
        String result = "";
        if (cell != null) {
            cellType = cell.getCellType();
            result = this.readCellData(cellType, cell, excelDataConfigById);
        }
        return result;
    }
}
