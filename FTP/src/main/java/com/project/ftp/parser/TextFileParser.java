package com.project.ftp.parser;

import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TextFileParser {
    final static Logger logger = LoggerFactory.getLogger(TextFileParser.class);
    final String filepath;
    public TextFileParser(final String filepath) {
        this.filepath = filepath;
    }
    public ArrayList<ArrayList<String>> getTextData() throws AppException {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        PathInfo pathInfo = StaticService.getPathInfo(filepath);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Requested file is not found: {}", filepath);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        try {
            File file = new File(filepath);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), AppConstant.UTF8));
            String str;
            String[] tempArr;
            ArrayList<String> temp;
            while ((str = in.readLine()) != null) {
                tempArr = str.split(",");
                temp = new ArrayList<>(Arrays.asList(tempArr));
                result.add(temp);
            }
            in.close();
            logger.info("Text file read success: {}", filepath);
        } catch (Exception e) {
            logger.info("Error in reading text file: {}", e.getMessage());
            throw new AppException(ErrorCodes.INVALID_FILE_DATA);
        }
        return result;
    }
    public String getTextDataV2() {
        ArrayList<String> result = new ArrayList<>();
        PathInfo pathInfo = StaticService.getPathInfo(filepath);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Requested file is not found: {}", filepath);
            return "";
        }
        try {
            File file = new File(filepath);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), AppConstant.UTF8));
            String str;
            while ((str = in.readLine()) != null) {
                result.add(str);
            }
            in.close();
            result.add("");
            logger.info("Text file read success: {}", filepath);
        } catch (Exception e) {
            logger.info("Error in reading text file: {}", e.getMessage());
            throw new AppException(ErrorCodes.INVALID_FILE_DATA);
        }
        return String.join("\n", result);
    }
    public boolean addText(String text, boolean isNewFile) {
        if (text == null) {
            text = "";
        }
        boolean textAddStatus = false;
        PathInfo pathInfo = StaticService.getPathInfo(filepath);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Requested file is not found: {}", filepath);
            return false;
        }
        try {
            File file = new File(filepath);
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), AppConstant.UTF8));
            if (!isNewFile) {
                writer.append("\n");
            }
            writer.append(text);
            writer.close();
            logger.info("Text added in: {}", filepath);
            textAddStatus = true;
        } catch (Exception e) {
            logger.info("Error in adding text in filename: {}", filepath);
        }
        return textAddStatus;
    }
}
