package com.project.ftp.dao;

import com.project.ftp.intreface.FilepathInterface;
import com.project.ftp.obj.FilepathDBParameters;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FilePathDAO implements Dao<FilepathDBParameters> {
    private final static Logger logger = LoggerFactory.getLogger(FilePathDAO.class);
    private final ArrayList<FilepathDBParameters> filepathDBParametersList = new ArrayList<>();
    private final FilepathInterface filepathInterface;
    public FilePathDAO(final FilepathInterface filepathInterface) {
        this.filepathInterface = filepathInterface;
    }

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

    public FilepathDBParameters getByPathname(String pathname) {
        ArrayList<String> pathnameParam = new ArrayList<>();
        if (!StaticService.isValidString(pathname)) {
            return null;
        }
        pathnameParam.add(pathname);
        for(FilepathDBParameters filepathDBParameter: filepathDBParametersList) {
            if (filepathDBParameter != null) {
                if (pathname.equals(filepathDBParameter.getPathName())) {
                    return filepathDBParameter;
                }
            }
        }
        ArrayList<FilepathDBParameters>  dbParameters = filepathInterface.getByMultipleParameter(null,
                pathnameParam, null, true, false);
        for(FilepathDBParameters filepathDBParameter: dbParameters) {
            if (filepathDBParameter != null) {
                if (pathname.equals(filepathDBParameter.getPathName())) {
                    return filepathDBParameter;
                }
            }
        }
        return null;
    }
    public ArrayList<FilepathDBParameters> getByFilterParameter(String pathname, String fileType,
                                                                String scanDirId, boolean recursive) {
        ArrayList<String> pathnameParam = new ArrayList<>();
        ArrayList<String> filetypeParam = new ArrayList<>();
        ArrayList<String> scanDirMappingIdParam = new ArrayList<>();
        String[] splitResult;
        if (pathname != null) {
            splitResult = pathname.split("\\|");
            for(String str: splitResult) {
                if (StaticService.isValidString(str)) {
                    pathnameParam.add(str.trim());
                }
            }
        }
        if (fileType != null) {
            splitResult = fileType.split("\\|");
            for(String str: splitResult) {
                if (StaticService.isValidString(str)) {
                    filetypeParam.add(str.trim());
                }
            }
        }
        if (scanDirId != null) {
            splitResult = scanDirId.split("\\|");
            for(String str: splitResult) {
                if (StaticService.isValidString(str)) {
                    scanDirMappingIdParam.add(str.trim());
                }
            }
        }
        ArrayList<FilepathDBParameters> filepathDBParameters =
                filepathInterface.getByMultipleParameter(scanDirMappingIdParam, pathnameParam,
                        filetypeParam, false, true);
        if (filepathDBParameters != null) {
            logger.info("getByFilterParameter: DB result count: {}", filepathDBParameters.size());
        }
        if (recursive) {
            return filepathDBParameters;
        } else if (pathnameParam.isEmpty()) {
            return filepathDBParameters;
        } else if (filepathDBParameters == null) {
            return null;
        }
        ArrayList<FilepathDBParameters> result = new ArrayList<>();
        String filename;
        String requiredFilename;
        for(FilepathDBParameters dbParameters: filepathDBParameters) {
            for(String requestedPathname: pathnameParam) {
                filename = dbParameters.getFileName();
                requiredFilename = StaticService.getProperDirString(requestedPathname+"/"+filename);
                if (filename != null) {
                    if (requiredFilename.equals(dbParameters.getPathName())) {
                        result.add(dbParameters);
                        break;
                    }
                }
            }
        }
        logger.info("getByFilterParameter: final result count: {}", result.size());
        return result;
    }

    @Override
    public ArrayList<FilepathDBParameters> getAll() {
        return filepathDBParametersList;
    }

    @Override
    public void save(FilepathDBParameters dbParameter) {
        if (dbParameter != null) {
            filepathDBParametersList.add(dbParameter);
        }
    }

    @Override
    public void update() {
        ArrayList<FilepathDBParameters> filepathDBParameters = filepathInterface.getAll();
        if (filepathDBParameters != null) {
            filepathDBParametersList.addAll(filepathDBParameters);
        }
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
            this.save(dbParameters);
        }
    }
}
