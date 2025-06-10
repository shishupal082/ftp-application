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
    public TextFileParser() {
        this.filepath = null;
        this.isNewFile = false;
    }
    public TextFileParser(final String filepath) {
        this.filepath = filepath;
        this.isNewFile = false;
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
    public boolean writeTextData(String destinationFilePath, String text, boolean isNewFile2) {
        if (text == null) {
            text = "";
        }
        boolean textAddStatus = false;
        PathInfo pathInfo = StaticService.getPathInfo(destinationFilePath);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("writeTextData: Destination file not found: {}", destinationFilePath);
            return textAddStatus;
        }
        try {
            File file = new File(destinationFilePath);
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), AppConstant.UTF8));
            if (!isNewFile2) {
                writer.append("\n");
            }
            writer.append(text);
            writer.close();
            textAddStatus = true;
        } catch (Exception e) {
            logger.info("writeTextData: Error in adding text in filename: {}", destinationFilePath);
        }
        return textAddStatus;
    }
    public void readAndWriteTextFile(String sourceFilePath, String destinationFilePath,
                                                Integer startIndex, Integer endIndex, boolean isNewFile2) {
        logger.info("readAndWriteTextFile request: {},{},{},{}", startIndex, endIndex, sourceFilePath, destinationFilePath);
        if (sourceFilePath == null) {
            logger.info("readAndWriteTextFile: Invalid sourceFilePath: null");
            return;
        }
        if (destinationFilePath == null) {
            logger.info("readAndWriteTextFile: Invalid destinationFilePath: null");
            return;
        }
        if (startIndex == null || startIndex < 0) {
            startIndex = -1;
        }
        if (endIndex == null || endIndex < 0) {
            endIndex = -1;
        }
        File file = new File(sourceFilePath);
        int lineIndex = -1;
        boolean isValidLineIndex = false;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), AppConstant.UTF8));
            File file2 = new File(destinationFilePath);
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file2, true), AppConstant.UTF8));
            String str;
            while ((str = in.readLine()) != null) {
                isValidLineIndex = false;
                lineIndex++;
                if (endIndex >= 0) {
                    if (startIndex >= 0) {
                        if (lineIndex >= startIndex && lineIndex <= endIndex) {
                            isValidLineIndex = true;
                        }
                    }
                } else if (startIndex >= 0) {
                    if (lineIndex >= startIndex) {
                        isValidLineIndex = true;
                    }
                } else {
                    isValidLineIndex = true;
                }
                if (isValidLineIndex) {
                    if (!isNewFile2) {
                        writer.append("\n");
                    }
                    writer.append(str);
                    isNewFile2 = false;
                }
                if (lineIndex > endIndex && endIndex >= 0) {
                    break;
                }
            }
            in.close();
            writer.close();
        } catch (FileNotFoundException e) {
            logger.info("readAndWriteTextFile: FileNotFoundException, fileName: {}, {}", filepath, e.getMessage());
        } catch (Exception e) {
            logger.info("readAndWriteTextFile: Unknown Exception, fileName: {}, {}", filepath, e.getMessage());
        }
    }
}
