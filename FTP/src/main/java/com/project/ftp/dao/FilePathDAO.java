package com.project.ftp.dao;

import com.project.ftp.obj.FilepathDBParameters;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FilePathDAO implements Dao<FilepathDBParameters> {
    private final static Logger logger = LoggerFactory.getLogger(FilePathDAO.class);
    private final ArrayList<FilepathDBParameters> filepathDBParametersList = new ArrayList<>();
    public FilePathDAO() {}

    @Override
    public FilepathDBParameters getById(long id) {
        for(FilepathDBParameters filepathDBParameter: filepathDBParametersList) {
            if (filepathDBParameter != null) {
                if (filepathDBParameter.getId() == id) {
                    return filepathDBParameter;
                }
            }
        }
        return null;
    }
    @Override
    public ArrayList<FilepathDBParameters> getAll() {
        return filepathDBParametersList;
    }
    @Override
    public void add(FilepathDBParameters dbParameter) {
        if (dbParameter != null) {
            filepathDBParametersList.add(dbParameter);
        }
    }

    @Override
    public void addAll(ArrayList<FilepathDBParameters> filepathDBParameters) {
        if (filepathDBParameters != null) {
            filepathDBParametersList.addAll(filepathDBParameters);
        }
    }

    @Override
    public FilepathDBParameters findByData(FilepathDBParameters filepathDBParameters) {
        if (filepathDBParameters == null) {
            return null;
        }
        String pathname = filepathDBParameters.getPathName();
        String scanDirMappingId = filepathDBParameters.getScanDirMappingId();
        if (StaticService.isInValidString(pathname) || StaticService.isInValidString(scanDirMappingId)) {
            return null;
        }
        for(FilepathDBParameters filepathDBParameter: filepathDBParametersList) {
            if (filepathDBParameter != null && pathname.equals(filepathDBParameter.getPathName())) {
                if (scanDirMappingId.equals(filepathDBParameter.getScanDirMappingId())) {
                    return filepathDBParameter;
                }
            }
        }
        return null;
    }

    @Override
    public void updateById(FilepathDBParameters dbParameters) {
        long rowId;
        int index = 0;
        boolean isNotAdded = true;
        if (dbParameters != null && dbParameters.getId() > 0) {
            rowId = dbParameters.getId();
            for (FilepathDBParameters filepathDBParameters1: filepathDBParametersList) {
                if (filepathDBParameters1 != null) {
                    if (rowId == filepathDBParameters1.getId()) {
                        filepathDBParametersList.set(index, dbParameters);
                        isNotAdded = false;
                        break;
                    }
                }
                index++;
            }
        }
        if (isNotAdded) {
            this.add(dbParameters);
        }
    }
}
