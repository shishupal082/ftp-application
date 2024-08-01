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
    public FilepathCsv(AppConfig appConfig) {}
    @Override
    public ArrayList<FilepathDBParameters> getAll() {
        return null;
    }

    @Override
    public ArrayList<FilepathDBParameters> getByMultipleParameter(ArrayList<String> scan_dir_id,
                                                                  ArrayList<String> pathname,
                                                                  ArrayList<String> filetype,
                                                                  boolean pathnameExact,
                                                                  boolean logQuery) {
        return null;
    }

    @Override
    public HashMap<String, Integer> updateIntoDb(FilePathDAO filePathDAO) {
        return null;
    }
    @Override
    public void updateFromReqScanDir(FilePathDAO filePathDAO,
                                                                ArrayList<String> scanDirListId, boolean isRecursive) {
        return;
    }

    @Override
    public ArrayList<FilepathDBParameters> getByFilterParameter(String reqPathName, ArrayList<String> scanDirIdList,
                                                         ArrayList<String> fileTypeList, boolean recursive) {
        return null;
    }
}
