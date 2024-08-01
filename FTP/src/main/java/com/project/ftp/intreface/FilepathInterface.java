package com.project.ftp.intreface;

import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.obj.FilepathDBParameters;

import java.util.ArrayList;
import java.util.HashMap;

public interface FilepathInterface {
    ArrayList<FilepathDBParameters> getAll();
    ArrayList<FilepathDBParameters> getByMultipleParameter(ArrayList<String> scan_dir_id,
                                        ArrayList<String> pathname,
                                        ArrayList<String> filetype,
                                        boolean pathnameExact,
                                        boolean logQuery);
    HashMap<String, Integer> updateIntoDb(FilePathDAO filePathDAO);
    void updateFromReqScanDir(FilePathDAO filePathDAO, ArrayList<String> scanDirListId, boolean isRecursive);
    ArrayList<FilepathDBParameters> getByFilterParameter(String reqPathName, ArrayList<String> scanDirIdList,
                                      ArrayList<String> fileTypeList, boolean recursive);
}
