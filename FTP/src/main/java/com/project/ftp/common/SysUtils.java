package com.project.ftp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.UUID;


public class SysUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(SysUtils.class);
    public Long getTimeInMsLong() {
        return System.currentTimeMillis();
    }
    public String getProjectWorkingDir() {
        return System.getProperty("user.dir");
    }
    public void printLog(Object logStr) {
        System.out.println(logStr);
    }
    public Boolean deleteFileContent(String filePath) {
        boolean deleteFileContentStatus = false;
        try {
            PrintWriter pw = new PrintWriter(filePath);
            pw.close();
            deleteFileContentStatus = true;
        } catch (Exception e) {
            LOGGER.info("Error in deleting file content: filePath= {}, {}", filePath, e.toString());
        }
        return deleteFileContentStatus;
    }
    public String createUUIDNumber() {
        return UUID.randomUUID().toString();
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
