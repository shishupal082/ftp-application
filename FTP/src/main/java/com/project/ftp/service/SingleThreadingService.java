package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.SingleThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class SingleThreadingService {
    private final static Logger logger = LoggerFactory.getLogger(SingleThreadingService.class);
    private final ArrayList<SingleThread> singleThreadStatus = new ArrayList<>();
    private final FtpConfiguration ftpConfiguration;
    public SingleThreadingService(final FtpConfiguration ftpConfiguration) {
        this.ftpConfiguration = ftpConfiguration;
    }
    public boolean isSingleThreading(HttpServletRequest request) {
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
        if (!singleThreadStatus.isEmpty()) {
            logger.info("Thread already running: {}, {}", name, singleThreadStatus);
            throw new AppException(ErrorCodes.SINGLE_THREAD_BUSY);
        }
        SingleThread singleThread = new SingleThread(name);
        singleThread.setUrl(RequestService.getPathUrl(request));
        singleThreadStatus.add(singleThread);
    }
    public void clearSingleThread(HttpServletRequest request, String name) {
        if (!this.isSingleThreading(request) || StaticService.isInValidString(name)) {
            return;
        }
        if (!singleThreadStatus.isEmpty()) {
            for(SingleThread singleThread: singleThreadStatus) {
                if (singleThread != null) {
                    if (name.equals(singleThread.getName())) {
                        singleThreadStatus.remove(singleThread);
                    }
                }
            }
        }
    }
    public HashMap<String, String> getSingleThreadStatus(HttpServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        result.put("isEnabled", Boolean.toString(this.isSingleThreading(request)));
        result.put("status", singleThreadStatus.toString());
        return result;
    }
}
