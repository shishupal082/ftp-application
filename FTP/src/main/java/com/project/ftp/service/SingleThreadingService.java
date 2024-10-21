package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.SingleThread;
import com.project.ftp.obj.SingleThreadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class SingleThreadingService {
    private final static Logger logger = LoggerFactory.getLogger(SingleThreadingService.class);
    private ArrayList<SingleThread> singleThreadItem = new ArrayList<>();
    private final FtpConfiguration ftpConfiguration;
    private Boolean isStopped;
    private SingleThreadStatus singleThreadStatus;
    public SingleThreadingService(final FtpConfiguration ftpConfiguration) {
        this.ftpConfiguration = ftpConfiguration;
    }

    public SingleThreadStatus getSingleThreadStatus() {
        return singleThreadStatus;
    }

    public void setSingleThreadStatus(SingleThreadStatus singleThreadStatus) {
        this.singleThreadStatus = singleThreadStatus;
    }

    public boolean getStopped() {
        if (isStopped == null) {
            return false;
        }
        return isStopped;
    }

    public void setStopped(Boolean stopped) {
        isStopped = stopped;
    }

    private boolean isSingleThreading(HttpServletRequest request) {
        Boolean singleThreadingEnable = ftpConfiguration.getSingleThreadingEnable();
        if (singleThreadingEnable == null) {
            return false;
        }
        return singleThreadingEnable.equals(true);
    }
    public void checkSingleThreadStatus(HttpServletRequest request, String name) throws AppException {
        if (!this.isSingleThreading(request)) {
            return;
        }
        if (StaticService.isInValidString(name)) {
            logger.info("Invalid name checking singleThreadStatus: {}", name);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (!singleThreadItem.isEmpty()) {
            logger.info("Thread already running: {}, {}, {}", name, singleThreadItem, singleThreadStatus);
            throw new AppException(ErrorCodes.SINGLE_THREAD_BUSY);
        }
        SingleThread singleThread = new SingleThread(name);
        singleThread.setUrl(RequestService.getPathUrl(request));
        singleThreadItem.add(singleThread);
    }
    public void clearSingleThread(HttpServletRequest request, String name) {
        if (!this.isSingleThreading(request) || StaticService.isInValidString(name)) {
            return;
        }
        if (!singleThreadItem.isEmpty()) {
            singleThreadItem = new ArrayList<>();
        }
    }
    public HashMap<String, String> getSingleThreadStatus(HttpServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        result.put("isEnabled", Boolean.toString(this.isSingleThreading(request)));
        result.put("item", singleThreadItem.toString());
        result.put("stopped", Boolean.toString(this.getStopped()));
        if (singleThreadStatus == null) {
            result.put("status", null);
        } else {
            result.put("status", singleThreadStatus.toString());
        }
        return result;
    }
}
