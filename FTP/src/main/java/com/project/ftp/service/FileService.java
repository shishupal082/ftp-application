package com.project.ftp.service;

import com.project.ftp.config.AppConstant;
import com.project.ftp.config.PathType;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class FileService {
    private final static Logger logger = LoggerFactory.getLogger(FileService.class);
    public FileService() {}
    public PathInfo getPathInfo(String requestedPath) {
        if (requestedPath == null) {
            requestedPath = "";
        }
        PathInfo pathInfo = new PathInfo(requestedPath);
        File file = new File(requestedPath);
        if (file.isDirectory()) {
            pathInfo.setType(AppConstant.FOLDER);
        } else if (file.isFile()) {
            pathInfo.setType(AppConstant.FILE);
            pathInfo.setFileName(file.getName());
            String parentFolder = StaticService.replaceBackSlashToSlash(file.getParent());
            pathInfo.setParentFolder(parentFolder); // It does not contain "/" in the end
            pathInfo.findExtension();
            pathInfo.findMimeType();
        }
        return pathInfo;
    }

    public Boolean copyFileV2(String oldFilePath, String newFilePath) {
        boolean fileCopyStatus = false;
        InputStream is = null;
        OutputStream os = null;
        try {
            File file = new File(oldFilePath);
            if (!file.isFile()) {
                logger.info("oldFilePath is not a file: {}", oldFilePath);
            }
            File newFile = new File(newFilePath);
            is = new FileInputStream(file);
            os = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.close();
            fileCopyStatus = true;
        } catch (Exception e) {
            logger.info("Error in copy file '{}', {}", oldFilePath, e);
        }
        return fileCopyStatus;
    }

    public Boolean moveFile(String source, String destination, String filename, String ext) {
        // Here source and destination both does not contain / in the end
        if (source == null || destination == null || filename == null || ext == null) {
            logger.info("Invalid request to moveFile: {}", source + "--" + destination + "--"
                    + filename + "--" + ext);
            return false;
        }
        if (!this.isDirectory(source)) {
            logger.info("sourcePath is not a folder: {}", source);
            return false;
        }
        if (!this.isDirectory(destination)) {
            logger.info("destinationPath is not a folder: {}", destination);
            return false;
        }
        String sourceFilePath = source + "/" + filename + "." + ext;
        String destinationFilePath = destination + "/" + filename + "." + ext;
        if (this.isFile(destinationFilePath)) {
            logger.info("filename: {}, exist in the destination folder, renaming it.", destinationFilePath);
            String filename2 = filename + "-" + StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat);
            this.renameExistingFile(destination, filename + "." + ext, filename2 + "." + ext);
        }
        Boolean copyFileStatus = this.copyFileV2(sourceFilePath, destinationFilePath);
        boolean deleteStatus = false;
        if (copyFileStatus) {
            deleteStatus = this.deleteFileV2(sourceFilePath);
            if (!deleteStatus) {
                logger.info("Error in delete file: {}", sourceFilePath);
            }
        } else {
            logger.info("Error in copy file from: {} to: {}", sourceFilePath, destinationFilePath);
        }
        return deleteStatus;
    }

    public String createDir(ArrayList<String> dirs) {
        if (dirs == null || dirs.isEmpty()) {
            return null;
        }
        String dirStr = dirs.get(0);
        dirStr = StaticService.getProperDirString(dirStr);
        if (!this.isDirectory(dirStr)) {
            return null;
        }
        dirStr += "/"; // First element must contain / (slash) in the path
        for (int i = 1; i < dirs.size(); i++) {
            if (!this.isDirectory(dirStr + dirs.get(i))) {
                if (!this.createFolder(dirStr, dirs.get(i))) {
                    return null;
                }
            }
            dirStr += dirs.get(i) + "/";
        }
        dirStr = StaticService.getProperDirString(dirStr);
        return dirStr; // It will not contain / in the end
    }

    public boolean isDirectory(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.isDirectory();
    }

    public boolean isFile(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.isFile();
    }

    public PathInfo getPathInfoFromFileName(String fileName) {
        PathInfo pathInfo = new PathInfo();
        pathInfo.setType(AppConstant.FILE);
        pathInfo.setFileName(fileName);
        pathInfo.findExtension();
        pathInfo.findMimeType();
        if (fileName != null) {
            String[] strArr = fileName.split("/");
            String parentFolder = "";
            if (strArr.length > 1) {
                pathInfo.setFileName(strArr[strArr.length - 1]);
                for (int i = 0; i < strArr.length - 1; i++) {
                    if (i != 0) {
                        parentFolder += "/" + strArr[i];
                    } else {
                        parentFolder = strArr[i];
                    }
                }
                pathInfo.setParentFolder(parentFolder); // it does not contain / in the end
            }
        }
        return pathInfo;
    }

    public boolean createFolder(String existingFolder, String currentFolderName) {
        if (existingFolder == null) {
            return false;
        }
        existingFolder = StaticService.getProperDirString(existingFolder);
        File file = new File(existingFolder);
        boolean dirCreateStatus = false;
        if (file.isDirectory()) {
            file = new File((existingFolder + "/" + currentFolderName));
            if (file.mkdir()) {
                dirCreateStatus = true;
                logger.info("Directory '{}' created.", file.getPath());
            } else {
                logger.info("Error in creating directory '{}'.", file.getPath());
            }
        } else {
            logger.info("Existing path '{}' is not directory.", existingFolder);
        }
        return dirCreateStatus;
    }

    public ScanResult scanDirectory(String folderPath, String staticFolderPath, boolean isRecursive) {
        //folderPath and staticFolderPath should contain / in the end
        ScanResult finalFileScanResult = new ScanResult(staticFolderPath, folderPath);
        if (folderPath == null || staticFolderPath == null) {
            return finalFileScanResult;
        }
        ScanResult fileScanResult = null;
        try {
            File folder = new File(folderPath);
            if (folder.isFile()) {
                finalFileScanResult = new ScanResult(staticFolderPath, folderPath, PathType.FILE);
//                logger.info("Given folder path is a file returning : {}", finalFileScanResult);
                return finalFileScanResult;
            }
            finalFileScanResult = new ScanResult(staticFolderPath, folderPath);
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
//                logger.info("Files in folder : {}, {}", folderPath, listOfFiles);
                finalFileScanResult.setPathType(PathType.FOLDER);
                finalFileScanResult.setScanResults(new ArrayList<ScanResult>());
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        fileScanResult = scanDirectory(folderPath + file.getName(),
                                staticFolderPath, false);
                    } else if (file.isDirectory()) {
                        if (isRecursive) {
                            fileScanResult = scanDirectory(folderPath + file.getName() + "/",
                                    staticFolderPath, true);
                        } else {
                            fileScanResult = new ScanResult(staticFolderPath,
                                    folderPath + file.getName() + "/", PathType.FOLDER);
                        }
                    } else {
                        logger.info("Unknown file type present : {}", file.getName());
                        continue;
                    }
                    finalFileScanResult.getScanResults().add(fileScanResult);
                }
            } else {
                logger.info("No files found in folder : {}, listOfFiles=null", folderPath);
            }
        } catch (Exception e) {
            logger.info("Error fetching folder from : {}, {}", folderPath, e);
        }
//        logger.info("Scan folder result for folder : {}, {}", folderPath, finalFileScanResult);
        logger.info("Scan complete for folder : {}", folderPath);
        return finalFileScanResult;
    }

    public ArrayList<String> getAvailableFiles(String directory) {
        //directory should contain / in the end
        ArrayList<String> availableFiles = new ArrayList<>();
        ScanResult scanResult = this.scanDirectory(directory, directory, false);
        ArrayList<ScanResult> scanResults = scanResult.getScanResults();
        if (scanResults != null && scanResults.size() > 0) {
            for (ScanResult scanResult1 : scanResults) {
                availableFiles.add(StaticService.replaceBackSlashToSlash(scanResult1.getPathName()));
            }
        }
        return availableFiles;
    }

    private void renameExistingFile(String dir, String fileName, String renameFilename) {
        String logStr = "File rename request: dir=" + dir + ", filename=" + fileName;
        logger.info("{}, renameFilename={}", logStr, renameFilename);
        if (dir == null || fileName == null || renameFilename == null) {
            logger.info("Invalid request for file rename.");
            return; // return false;
        }
        File file = new File(dir + "/" + fileName);
        if (!file.isFile()) {
            logger.info("Requested src file does not exist.");
            return;//return false;
        }
        String renameFilePath = dir + "/" + renameFilename;
        File newFile = new File(renameFilePath);
        if (newFile.isFile()) {
            logger.info("Requested destination file already exist.");
            return;//return false;
        }
//        boolean renameStatus = false;
        try {
            if (file.renameTo(newFile)) {
                logStr = "file rename in dir={}, from: {}";
                logger.info(logStr, dir, fileName + " to " + renameFilename);
            } else {
                logger.info("File rename fail: from {}, to {}", file.getPath(), newFile.getPath());
            }
//            renameStatus = true;
        } catch (Exception e) {
            logger.info("Error in renaming file from '{}', to '{}'", file.getPath(), newFile.getPath());
        }
        //return renameStatus;
    }

    public PathInfo searchIndexHtmlInFolder(PathInfo pathInfo) {
        if (AppConstant.FOLDER.equals(pathInfo.getType())) {
            File file1 = new File(pathInfo.getPath() + "index.html");
            File file2 = new File(pathInfo.getPath() + "/index.html");
            File file = null;
            if (file1.isFile()) {
                file = file1;
            } else if (file2.isFile()) {
                file = file2;
            }
            if (file != null) {
                String pre = pathInfo.toString();
                pathInfo = this.getPathInfo(file.getPath());
                logger.info("PathInfo changes from folder to file: {} to {}", pre, pathInfo.toString());
            }
        }
        return pathInfo;
    }

    public boolean deleteFileV2(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        boolean fileDeleteStatus = false;
        if (file.isFile()) {
            if (file.delete()) {
                fileDeleteStatus = true;
                logger.info("File deleted: {}", filePath);
            } else {
                logger.info("Error in file delete: {}", filePath);
            }
        } else {
            logger.info("Requested in file delete: {}, does not exist.", filePath);
        }
        return fileDeleteStatus;
    }

    public PathInfo uploadFile(InputStream uploadedInputStream, String filePath, Integer maxFileSize) throws AppException {
        logger.info("uploadFile request: {}", filePath);
        if (maxFileSize == null || maxFileSize < 1) {
            logger.info("Invalid maxFileSize in ftp env configuration: {}", maxFileSize);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        try {
            int read;
            int fileSize = 0;
            final int BUFFER_LENGTH = 1024;
            final byte[] buffer = new byte[BUFFER_LENGTH];
            OutputStream out = new FileOutputStream(new File(filePath));
            while ((read = uploadedInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                fileSize += read;
                if (fileSize >= maxFileSize) {
                    logger.info("fileSize = {} exceed than maxFileSize = {}", fileSize, maxFileSize);
                    out.flush();
                    out.close();
                    this.deleteFileV2(filePath);
                    throw new AppException(ErrorCodes.FILE_SIZE_EXCEEDED);
                }
            }
            out.flush();
            out.close();
            logger.info("fileSize = {}, uploaded file: {}", fileSize, filePath);
        } catch (FileNotFoundException e1) {
            logger.info("FileNotFoundException in saving file: {}", e1.getMessage());
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        } catch (IOException e2) {
            logger.info("IOException in saving file: {}", e2.getMessage());
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        return getPathInfo(filePath);
    }

    public boolean createNewFile(String filePath) {
        File file = new File(filePath);
        try {
            return file.createNewFile();
        } catch (Exception e) {
            logger.info("Error in creating file: {}", filePath);
        }
        return false;
    }
}
