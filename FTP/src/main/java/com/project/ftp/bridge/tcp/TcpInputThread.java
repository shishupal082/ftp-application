package com.project.ftp.bridge.tcp;

import com.project.ftp.config.AppConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class TcpInputThread implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(TcpInputThread.class);
    private String finalResponse = "TCP_SERVER_FAILED";
    private boolean isThreadRunning = true;
    private InputStream inputStream;
    public TcpInputThread() {}
    private int getResponseLength(String response) {
        int length = 1;
        if (response != null && response.length() >= 1) {
            try {
                length = response.length();
                length += Integer.parseInt(response);
            } catch (Exception e) {
                length = response.length();
                logger.info("Error in parsing response length: {}", response);
            }
        }
        logger.info("Total response length: {}", length);
        return length;
    }
    public void isInputStreamComplete(int maxTimeDelayInSec) {
        int count = 0;
        int interval = 10;
        try {
            while (isThreadRunning) {
                TimeUnit.MILLISECONDS.sleep(interval);
                if (count*interval >= maxTimeDelayInSec * 1000) {
                    logger.info("TCP Time limit exceed: {} >= {}", count*interval, maxTimeDelayInSec * 1000);
                    return;
                }
                count++;
            }
        } catch (Exception e) {
            logger.info("Error in generating delay.");
        }
    }
    public void readInput() {
        logger.info("Waiting for socket inputStream.");
        StringBuilder data = new StringBuilder();
        String response = "";
        try {
            int character;
            int responseLength = 0;
            InputStreamReader reader = new InputStreamReader(inputStream);
            while (isThreadRunning && (character = reader.read()) != -1) {
                data.append((char) character);
                //Find length, if first non digit character received
                if (responseLength == 0 && (character < 48 || character > 57)) {
                    responseLength = this.getResponseLength(response);
                }
                response = data.toString();
                finalResponse = response;
                if (responseLength > 0 && response.length() >= responseLength) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.info("Error in reading input: {}", e.getMessage());
        }
        String logResponse;
        if (response.length() < AppConstant.maxLengthLogDisplay) {
            logResponse = response;
        } else {
            logResponse = "Length " + response.length();
        }
        logger.info("TCP Server response: {}, isThreadRunning: {}", logResponse, isThreadRunning);
    }
    public void run() {
        this.readInput();
        this.stop();
    }
    public void stop() {
        if (isThreadRunning) {
            logger.info("TcpInputThread stopped.");
            isThreadRunning = false;
        } else {
            logger.info("TCP Input thread already stopped.");
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFinalResponse() {
        return finalResponse;
    }
}
