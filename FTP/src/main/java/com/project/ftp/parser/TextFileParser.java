package com.project.ftp.parser;

import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TextFileParser {
    private final static Logger logger = LoggerFactory.getLogger(TextFileParser.class);
    private final String filepath;
    private final boolean isNewFile;
    public TextFileParser(final String filepath) {
        this.filepath = filepath;
        this.isNewFile = false;
    }
    public TextFileParser(final String filepath, final boolean isNewFile) {
        this.filepath = filepath;
        this.isNewFile = isNewFile;
    }
    public ArrayList<ArrayList<String>> readCsvData() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> fileData = this.readTextFile();
        if (fileData == null || fileData.isEmpty()) {
            return null;
        }
        String[] tempArr;
        for (String line: fileData) {
            tempArr = line.split(",");
            result.add(new ArrayList<>(Arrays.asList(tempArr)));
        }
        return result;
    }
    public String getTextDataV2() {
        ArrayList<String> fileData = this.readTextFile();
        if (fileData == null || fileData.isEmpty()) {
            return AppConstant.EmptyStr;
        }
        return String.join("\n", fileData);
    }
    public boolean addText(String text, boolean logFilename) {
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
            if (logFilename) {
                logger.info("Text added in: {}", filepath);
            }
            textAddStatus = true;
        } catch (Exception e) {
            logger.info("Error in adding text in filename: {}", filepath);
        }
        return textAddStatus;
    }
    public ArrayList<String> readTextFile() {
        ArrayList<String> response = new ArrayList<>();
        if (filepath == null || filepath.isEmpty()) {
            logger.info("Invalid requested file path: {}", filepath);
            return null;
        }
        File file = new File(filepath);
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), AppConstant.UTF8));
            String str;
            while ((str = in.readLine()) != null) {
                response.add(str);
            }
            in.close();
        } catch (FileNotFoundException e) {
            logger.info("FileNotFoundException, fileName: {}, {}", filepath, e.getMessage());
        } catch (Exception e) {
            logger.info("Unknown Exception, fileName: {}, {}", filepath, e.getMessage());
        }
        return response;
    }
}
