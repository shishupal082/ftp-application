package com.project.ftp.intreface;

import com.project.ftp.config.AppConfig;
import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.obj.FilepathDBParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class FilepathCsv implements FilepathInterface {
    private final static Logger logger = LoggerFactory.getLogger(FilepathCsv.class);
    private final AppConfig appConfig;
    public FilepathCsv(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    @Override
    public ArrayList<FilepathDBParameters> getAll() {
        return null;
    }

    @Override
    public ArrayList<FilepathDBParameters> getByMultipleParameter(ArrayList<String> scan_dir_id, ArrayList<String> pathname, ArrayList<String> filetype, boolean pathnameExact, boolean logQuery) {
        return null;
    }

    @Override
    public HashMap<String, Integer> updateIntoDb(FilePathDAO filePathDAO) {
        return null;
    }
}
