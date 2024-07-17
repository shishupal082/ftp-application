package com.project.ftp.intreface;

import com.project.ftp.config.AppConfig;
import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.obj.FilepathDBParameters;
import com.project.ftp.obj.LoginUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilepathCsv {
    private final static Logger logger = LoggerFactory.getLogger(FilepathCsv.class);
    private final AppConfig appConfig;
    public FilepathCsv(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void addFilepath(LoginUserDetails loginUserDetails, FilepathDBParameters pathInfoScanResult) {
    }

    public void updateFilepath(FilePathDAO filePathDAO, LoginUserDetails loginUserDetails, FilepathDBParameters pathInfoScanResult) {

    }
}
