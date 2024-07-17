package com.project.ftp.intreface;

import com.project.ftp.dao.FilePathDAO;
import com.project.ftp.obj.FilepathDBParameters;

import java.util.ArrayList;

public interface FilepathInterface {
    ArrayList<FilepathDBParameters> getAll();
    ArrayList<FilepathDBParameters> getByMultipleParameter(ArrayList<String> scan_dir_id,
                                                           ArrayList<String> pathname,
                                                           ArrayList<String> filetype,
                                                           boolean pathnameExact,
                                                           boolean logQuery);
    void updateIntoDb(FilePathDAO filePathDAO);
}
