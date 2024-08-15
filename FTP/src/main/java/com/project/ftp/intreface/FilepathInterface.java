package com.project.ftp.intreface;

import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.obj.FilepathDBParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class FilepathInterface {
    final static Logger logger = LoggerFactory.getLogger(FilepathInterface.class);
    public ArrayList<FilepathDBParameters> getAll() {
        return null;
    }
    public ArrayList<FilepathDBParameters> getByMultipleParameter(ArrayList<String> scan_dir_id,
                                                                  ArrayList<String> pathname,
                                                                  ArrayList<String> filetype,
                                                                  boolean pathnameExact,
                                                                  boolean logQuery) {
        return null;
    }
    public HashMap<String, Integer> updateIntoDb(FilePathDAO filePathDAO) {
        return null;
    }
    public void updateFromReqScanDir(FilePathDAO filePathDAO,
                                     ArrayList<String> scanDirListId, boolean isRecursive) {
    }

    public ArrayList<FilepathDBParameters> getByFilterParameter(String reqPathName, ArrayList<String> scanDirIdList,
                                                                ArrayList<String> fileTypeList, boolean recursive) {
        return null;
    }
}
